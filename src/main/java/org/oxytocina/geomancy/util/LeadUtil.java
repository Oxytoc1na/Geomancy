package org.oxytocina.geomancy.util;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ILeadPoisoningBlock;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.items.ILeadPoisoningItem;
import org.oxytocina.geomancy.items.IManaStoringItem;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModBiomeTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeadUtil {

    private static final ArrayList<PlayerEntity> queuedRecalcs = new ArrayList<>();

    private static final HashMap<BlockPos,Float> cachedAmbientPoison = new HashMap<>();

    /// DOESNT sync poisoning
    public static boolean setPoisoning(PlayerEntity player, float amount){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.leadPoisoning;
        if(old==amount) return false;
        data.leadPoisoning = amount;
        return true;
    }

    /// DOESNT sync poisoning
    public static boolean setPoisoningSpeed(PlayerEntity player, float newVal){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.leadPoisoningSpeed;
        if(old==newVal) return false;
        data.leadPoisoningSpeed = newVal;
        return true;
    }

    public static float getPoisoning(PlayerEntity player){
        return PlayerData.from(player).leadPoisoning;
    }

    // returns only the poisoning speed from a players inventory, without ambiance added in
    public static float getPoisoningSpeed(PlayerEntity player){
        return PlayerData.from(player).leadPoisoningSpeed;
    }

    /// call after equipping/unequipping lead items
    /// syncs poisoning
    private static void recalculatePoisoningSpeed(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity)) return;

        float speed = 0;

        var items = getAllPoisoningItems(player);
        for(var item : items){
            if(item.stack.getItem() instanceof ILeadPoisoningItem poisoner){
                if(item.inHand)
                    speed += poisoner.getInHandPoisoningSpeed()*item.stack.getCount();
                else if(item.worn)
                    speed += poisoner.getWornPoisoningSpeed()*item.stack.getCount();
                else speed += poisoner.getInInventoryPoisoningSpeed()*item.stack.getCount();
            }
        }

        setPoisoningSpeed(player,speed);

        syncPoisoning(player);
    }

    private static long clearCacheCounter = 0;
    public static void tick(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            LeadUtil.tickLeadPoisoning(player);
        }

        // recalc mana
        for(PlayerEntity entity : queuedRecalcs){
            recalculatePoisoningSpeed(entity);
        }
        queuedRecalcs.clear();

        if(++clearCacheCounter > 60){
            cachedAmbientPoison.clear();
            clearCacheCounter=0;
        }
    }

    public static void queueRecalculatePoisoningSpeed(PlayerEntity player){
        if(!queuedRecalcs.contains(player))
            queuedRecalcs.add(player);
    }

    public static void syncPoisoning(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity spe)) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(getPoisoningSpeed(player));
        buf.writeFloat(getPoisoning(player));
        ServerPlayNetworking.send(spe,ModMessages.LEAD_POISONING_SYNC,buf);
    }

    public static float getAmbientPoisoning(Entity entity){
        return getAmbientPoisoning(entity.getWorld(),entity.getBlockPos());
    }
    public static float getAmbientPoisoning(World world, BlockPos pos){
        if(cachedAmbientPoison.containsKey(pos)) return cachedAmbientPoison.get(pos);

        final float checkRadius = 2;

        float res = 0;

        List<BlockPos> checkedBlockPos = BlockPos.stream(Box.from(new BlockBox(pos)).expand(checkRadius))
                /*.filter(pos3 -> isPoisonous(world.getBlockState(pos3)))*/.toList();
        for(var pos2 : checkedBlockPos)
        {
            BlockState state = world.getBlockState(pos2);
            if(state.getBlock() instanceof ILeadPoisoningBlock poisoningBlock){
                float poisonousness = poisoningBlock.getAmbientPoisoningSpeed();
                double dist = pos.getSquaredDistance(pos2);
                res+=poisonousness/Math.max(1,(float)(Math.sqrt(dist)));
            }
        }

        cachedAmbientPoison.put(pos,res);
        return res;
    }

    private static boolean tickLeadPoisoning(ServerPlayerEntity player){
        boolean changed = false;

        float prevPoisoning = getPoisoning(player);
        float playerPoisoningSpeed = getPoisoningSpeed(player);
        float ambientPoisoningSpeed = getAmbientPoisoning(player);

        float effectivePoisoningSpeed = playerPoisoningSpeed+ambientPoisoningSpeed;

        effectivePoisoningSpeed *= 0.01f;

        // prevents poisoning from climbing endlessly
        final float lerpPerTick = 0.001f;

        float newPoisoning = Toolbox.Lerp(prevPoisoning,0,lerpPerTick) + effectivePoisoningSpeed;

        if(newPoisoning!=prevPoisoning){
            changed=true;
            setPoisoning(player,newPoisoning);
        }

        if(changed) syncPoisoning(player);

        return changed;
    }

    public static boolean isPoisonous(ItemStack stack){
        return stack.getItem() instanceof ILeadPoisoningItem;
    }

    public static boolean isPoisonous(BlockState block){
        return block.getBlock() instanceof ILeadPoisoningBlock;
    }

    public static List<HeldInfluenceItem> getAllPoisoningItems(PlayerEntity player){
        List<HeldInfluenceItem> res = new ArrayList<>();
        var inv = player.getInventory();
        var trinketComp = TrinketsApi.getTrinketComponent(player);
        var trinketInv = trinketComp.map(TrinketComponent::getAllEquipped).orElse(null);
        if(inv!=null){
            for(ItemStack s : inv.main) if(isPoisonous(s)) res.add(new HeldInfluenceItem(s,player.getMainHandStack() == s,false));
            for(ItemStack s : inv.armor) if(isPoisonous(s)) res.add(new HeldInfluenceItem(s,false,true));
            for(ItemStack s : inv.offHand) if(isPoisonous(s)) res.add(new HeldInfluenceItem(s,true,false));
        }
        if(trinketInv!=null){
            for(var pair : trinketInv) if(isPoisonous(pair.getRight())) res.add(new HeldInfluenceItem(pair.getRight(),false,true));
        }
        return res;
    }

    public static class HeldInfluenceItem {
        public final ItemStack stack;
        public final boolean inHand;
        public final boolean worn;

        public HeldInfluenceItem(ItemStack stack, boolean inHand, boolean worn){
            this.stack=stack;
            this.inHand = inHand;
            this.worn = worn;
        }
    }

}
