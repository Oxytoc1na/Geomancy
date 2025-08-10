package org.oxytocina.geomancy.util;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.IMaddeningBlock;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.items.IMaddeningItem;
import org.oxytocina.geomancy.networking.ModMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.LeadUtil.HeldInfluenceItem;
import oshi.util.tuples.Triplet;

public class MadnessUtil {

    private static final ArrayList<PlayerEntity> queuedRecalcs = new ArrayList<>();

    private static final HashMap<BlockPos,Float> cachedAmbientMadness = new HashMap<>();

    /// DOESNT sync poisoning
    public static boolean setMadness(PlayerEntity player, float amount){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.madness;
        if(old==amount) return false;
        data.madness = amount;
        return true;
    }

    /// DOESNT sync poisoning
    public static boolean setMaddeningSpeed(PlayerEntity player, float newVal){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.madnessSpeed;
        if(old==newVal) return false;
        data.madnessSpeed = newVal;
        return true;
    }

    public static float getMadness(PlayerEntity player){
        return PlayerData.from(player).madness;
    }

    // returns only the poisoning speed from a players inventory, without ambiance added in
    public static float getMadnessSpeed(PlayerEntity player){
        return PlayerData.from(player).madnessSpeed;
    }

    /// call after equipping/unequipping lead items
    /// syncs poisoning
    private static void recalculateMadnessSpeed(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity)) return;

        float speed = 0;

        var items = getAllMaddeningItems(player);
        for(var item : items){
            if(item.stack.getItem() instanceof IMaddeningItem maddener){
                if(item.inHand)
                    speed += maddener.getInHandMaddeningSpeed()*item.stack.getCount();
                else if(item.worn)
                    speed += maddener.getWornMaddeningSpeed()*item.stack.getCount();
                else speed += maddener.getInInventoryMaddeningSpeed()*item.stack.getCount();
            }
        }

        setMaddeningSpeed(player,speed);

        syncMadness(player);
    }

    private static long clearCacheCounter = 0;
    private static long madnessEffectsCounter = 0;
    private static long whisperCounter = 0;
    public static void tick(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            MadnessUtil.tickMadness(player);
        }

        // recalc mana
        for(PlayerEntity entity : queuedRecalcs){
            recalculateMadnessSpeed(entity);
        }
        queuedRecalcs.clear();

        if(++clearCacheCounter > 60){
            cachedAmbientMadness.clear();
            clearCacheCounter=0;
        }

        if(++madnessEffectsCounter > 20*60){
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tryMadnessEffects(player);
            }
            madnessEffectsCounter =0;
        }

        if(++whisperCounter > 20*2){
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tryWhisper(player);
            }
            whisperCounter =0;
        }
    }

    private static void tryWhisper(ServerPlayerEntity player) {
        if(player.isCreative() || player.isDead()) return;

        float madness = getMadness(player);
        float ambientMadness = getAmbientMadness(player);

        float combined = madness+ambientMadness;

        float chance = combined/(combined+400);
        if(Toolbox.random.nextFloat()>chance) return;

        Toolbox.playSound(ModSoundEvents.WHISPERS,player.getWorld(),player.getBlockPos(), SoundCategory.AMBIENT,chance,0.8f+Toolbox.random.nextFloat()*0.4f);

    }

    private static void tryMadnessEffects(ServerPlayerEntity player) {
        if(player.isCreative() || player.isDead()) return;

        float madness = getMadness(player);
        // effect chance asymptotically approaches 100%.
        // at 200, there is a 20% chance.
        // at 800, there is a 50% chance.
        float chance = madness/(madness+800);
        if(Toolbox.random.nextFloat()>chance) return;

        // effects are defined with severity, which affects at which level of poisoning they can happen,
        // weight, if it is added to the pool, and the function that happens when it is picked
        ArrayList<Triplet<Float,Integer, Consumer<ServerPlayerEntity>>> effects = new ArrayList<>();

        // Regret
        effects.add(new Triplet<>(10f,1, p -> {
            p.addStatusEffect(new StatusEffectInstance(ModStatusEffects.REGRETFUL,20*60,0));
            p.sendMessage(Text.translatable("geomancy.message.madness.regret"),true);
        }));

        // Mourning
        effects.add(new Triplet<>(20f,1, p -> {
            p.addStatusEffect(new StatusEffectInstance(ModStatusEffects.MOURNING,20*60,0));
            p.sendMessage(Text.translatable("geomancy.message.madness.mourning"),true);
        }));

        // Ecstasy
        effects.add(new Triplet<>(30f,1, p -> {
            p.addStatusEffect(new StatusEffectInstance(ModStatusEffects.ECSTATIC,20*60,0));
            p.sendMessage(Text.translatable("geomancy.message.madness.ecstasy"),true);
        }));

        // Nausea
        effects.add(new Triplet<>(40f,1, p -> {
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA,20*20,0));
            p.sendMessage(Text.translatable("geomancy.message.madness.nausea"),true);
        }));

        // Paranoia
        effects.add(new Triplet<>(40f,1, p -> {
            p.addStatusEffect(new StatusEffectInstance(ModStatusEffects.PARANOIA,20*20,0));
            p.sendMessage(Text.translatable("geomancy.message.madness.paranoia"),true);
        }));

        // add to available pool
        HashMap<Triplet<Float,Integer, Consumer<ServerPlayerEntity>>,Integer> pickedEffects = new HashMap<>();
        for (Triplet<Float, Integer, Consumer<ServerPlayerEntity>> t : effects) {
            if (t.getA() < madness) {
                pickedEffects.put(t, t.getB());
            }
        }

        var picked = Toolbox.selectWeightedRandomIndex(pickedEffects,null);
        if(picked!=null){
            picked.getC().accept(player);
            // trigger madness advancement
            AdvancementHelper.grantAdvancementCriterion(player,"main/simple_maddened","simple_maddened");
        }

    }

    public static void queueRecalculateMadnessSpeed(PlayerEntity player){
        if(!queuedRecalcs.contains(player))
            queuedRecalcs.add(player);
    }

    public static void syncMadness(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity spe)) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(getMadnessSpeed(player));
        buf.writeFloat(getMadness(player));
        ServerPlayNetworking.send(spe,ModMessages.MADNESS_SYNC,buf);
    }

    public static float getAmbientMadness(Entity entity){
        return getAmbientMadness(entity.getWorld(),entity.getBlockPos());
    }
    public static float getAmbientMadness(World world, BlockPos pos){
        if(cachedAmbientMadness.containsKey(pos)) return cachedAmbientMadness.get(pos);

        final float checkRadius = 4;

        float res = 0;

        List<BlockPos> checkedBlockPos = new ArrayList<>();
        BlockPos.stream(Box.from(new BlockBox(pos)).expand(checkRadius))
                .filter(pos3 -> isMaddening(world.getBlockState(pos3))).forEach((blockPos -> checkedBlockPos.add(blockPos.mutableCopy())));
        for(var pos2 : checkedBlockPos)
        {
            BlockState state = world.getBlockState(pos2);
            if(state.getBlock() instanceof IMaddeningBlock maddeningBlock){
                float maddeningness = maddeningBlock.getAmbientMaddeningSpeed();
                double dist = pos.getSquaredDistance(pos2);
                res+=maddeningness/Math.max(1,(float)(Math.sqrt(dist)));
            }
        }

        cachedAmbientMadness.put(pos,res);
        return res;
    }

    private static boolean tickMadness(ServerPlayerEntity player){
        if(player.isCreative() || player.isDead()) return false;

        boolean changed = false;

        float prevMadness = getMadness(player);
        float playerMaddeningSpeed = getMadnessSpeed(player);
        float ambientMaddeningSpeed = getAmbientMadness(player);

        float effectiveMaddeningSpeed = playerMaddeningSpeed+ambientMaddeningSpeed;

        // make extreme values have less of an impact
        final double g = 0.03;
        effectiveMaddeningSpeed = (float)Toolbox.log(1+g,effectiveMaddeningSpeed*g+1);

        // slow maddening down a lot
        effectiveMaddeningSpeed *= 0.001f;

        // prevents maddening from climbing endlessly
        // healing is much less effective if you're still exposed
        final float lerpPerTick = 0.0001f / (1+effectiveMaddeningSpeed);

        float newMadness = Toolbox.Lerp(prevMadness,0,lerpPerTick) + effectiveMaddeningSpeed;

        if(newMadness!=prevMadness){
            changed=true;
            setMadness(player,newMadness);
        }

        if(changed) syncMadness(player);

        return changed;
    }

    public static boolean isMaddening(ItemStack stack){
        return stack.getItem() instanceof IMaddeningItem;
    }

    public static boolean isMaddening(BlockState block){
        return block.getBlock() instanceof IMaddeningBlock;
    }

    public static List<HeldInfluenceItem> getAllMaddeningItems(PlayerEntity player){
        List<HeldInfluenceItem> res = new ArrayList<>();
        var inv = player.getInventory();
        var trinketComp = TrinketsApi.getTrinketComponent(player);
        var trinketInv = trinketComp.map(TrinketComponent::getAllEquipped).orElse(null);
        if(inv!=null){
            for(ItemStack s : inv.main) if(isMaddening(s)) res.add(new HeldInfluenceItem(s,player.getMainHandStack() == s,false));
            for(ItemStack s : inv.armor) if(isMaddening(s)) res.add(new HeldInfluenceItem(s,false,true));
            for(ItemStack s : inv.offHand) if(isMaddening(s)) res.add(new HeldInfluenceItem(s,true,false));
        }
        if(trinketInv!=null){
            for(var pair : trinketInv) if(isMaddening(pair.getRight())) res.add(new HeldInfluenceItem(pair.getRight(),false,true));
        }
        return res;
    }

}
