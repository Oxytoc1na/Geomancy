package org.oxytocina.geomancy.util;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.AutocasterBlockEntity;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.oxytocina.geomancy.entity.SoulStoringItemData;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.items.ISoulStoringItem;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModBiomeTags;
import org.oxytocina.geomancy.registries.ModBlockTags;
import org.oxytocina.geomancy.spells.SpellContext;

import java.util.*;

public class SoulUtil {

    private static final ArrayList<PlayerEntity> queuedRecalcs = new ArrayList<>();
    // cache recently calculated ambient soul values to reduce overhead
    // clears every 40 ticks
    private static final HashMap<BlockPos,Float> cachedAmbientSouls = new HashMap<>();

    /// DOESNT sync mana
    public static boolean setSoul(PlayerEntity player, float amount){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.mana;
        float mana = Toolbox.clampF(amount,0,data.maxMana);
        if(old==mana) return false;
        data.mana = mana;
        return true;
    }

    /// DOESNT sync mana
    public static boolean setSoulCap(PlayerEntity player, float newMaxMana){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.maxMana;
        if(old==newMaxMana) return false;
        data.maxMana = newMaxMana;
        return true;
    }

    public static float getSoul(PlayerEntity player){
        return PlayerData.from(player).mana;
    }

    public static float getSoul(World world, Inventory inv){
        var items = getAllSoulStoringItems(inv);
        float res = 0;
        for (ItemStack s : items) {
            res += ((ISoulStoringItem) s.getItem()).getMana(world, s);
        }
        return res;
    }

    public static float getMaxSoul(PlayerEntity player){
        return PlayerData.from(player).maxMana;
    }

    /// call after equipping/unequipping mana storing items, casting spells, etc.
    /// syncs mana
    private static void recalculateSoul(PlayerEntity player){
        recalculateSoul(player,true);
    }
    private static void recalculateSoul(PlayerEntity player, boolean sync){
        if(player==null || !(player instanceof ServerPlayerEntity)) return;

        float cap = 0;
        float mana = 0;

        var items = getAllSoulStoringItems(player);
        for(var item : items){
            if(item.getItem() instanceof ISoulStoringItem storer){
                cap += storer.getCapacity(player.getWorld(),item);
                mana += storer.getMana(player.getWorld(),item);
            }
        }

        setSoulCap(player,cap);
        setSoul(player,mana);

        if(sync)
            syncSoul(player);
    }

    private static int cacheClearTimer = 0;
    public static void tick(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            SoulUtil.tickPlayerSoul(player);
        }

        // recalc mana
        for(PlayerEntity entity : queuedRecalcs){
            recalculateSoul(entity);
        }
        queuedRecalcs.clear();

        if(++cacheClearTimer > 40){
            cachedAmbientSouls.clear();
            cacheClearTimer=0;
        }
    }

    public static void queueRecalculateSoul(PlayerEntity player){
        if(!queuedRecalcs.contains(player))
            queuedRecalcs.add(player);
    }

    public static void syncSoul(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity spe)) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(getMaxSoul(player));
        buf.writeFloat(getSoul(player));
        ServerPlayNetworking.send(spe,ModMessages.MANA_SYNC,buf);
    }

    public static void syncItemSoul(World world, ItemStack stack){
        if(!(world instanceof ServerWorld svw)) return;
        if(!(stack.getItem() instanceof ISoulStoringItem)) return;
        for(var p : svw.getPlayers())
            syncItemSoul(world,stack,p);
    }


    public static void syncItemSoul(World world, ItemStack stack, ServerPlayerEntity with){
        if(with==null) return;
        if(!(world instanceof ServerWorld svw)) return;
        if(!(stack.getItem() instanceof ISoulStoringItem)) return;

        ISoulStoringItem.init(world,stack);
        SoulStoringItemData data = SoulStoringItemData.from(world,stack, ISoulStoringItem.getUUID(stack));

        PacketByteBuf buf = PacketByteBufs.create();
        data.writeBuf(buf);

        // TODO: dont send unneeded packets!!
        ServerPlayNetworking.send(with,ModMessages.ITEM_MANA_SYNC,buf);

    }

    public static float getAmbientSoulsPerBlock(Entity entity){
        return getAmbientSoulsPerBlock(entity.getWorld(),entity.getBlockPos());
    }
    public static float getAmbientSoulsPerBlock(World world, BlockPos pos){
        if(cachedAmbientSouls.containsKey(pos)) return Toolbox.ifNotNullThenElse(cachedAmbientSouls.get(pos),0f);

        float res = 100;

        // from biome
        RegistryEntry<Biome> biome = world.getBiome(pos);
        if(biome.isIn(ModBiomeTags.VPB_INSANE)) res *= 10;
        else if(biome.isIn(ModBiomeTags.VPB_HIGHEST)) res *= 5;
        else if(biome.isIn(ModBiomeTags.VPB_HIGHER)) res *= 3.5f;
        else if(biome.isIn(ModBiomeTags.VPB_HIGH)) res *= 2;
        else if(biome.isIn(ModBiomeTags.VPB_NONE)) res *= 0;
        else if(biome.isIn(ModBiomeTags.VPB_LOWEST)) res /= 5;
        else if(biome.isIn(ModBiomeTags.VPB_LOWER)) res /= 3.5f;
        else if(biome.isIn(ModBiomeTags.VPB_LOW)) res /= 2f;

        // from nearby living blocks
        res+= getAmbientSPBFromBlocks(world,pos);

        cachedAmbientSouls.put(pos,res);
        return res;
    }

    public static float getAmbientSPBFromBlocks(World world, BlockPos pos){
        final float checkRadius = 4;

        float res = 0;

        List<BlockPos> checkedBlockPos = new ArrayList<>();
        BlockPos.stream(Box.from(new BlockBox(pos)).expand(checkRadius))
                .filter(pos3 -> addsAmbientSouls(world.getBlockState(pos3))).forEach((blockPos -> checkedBlockPos.add(blockPos.mutableCopy())));
        for(var pos2 : checkedBlockPos)
        {
            BlockState state = world.getBlockState(pos2);
            double dist = pos.getSquaredDistance(pos2);
            float div = Math.max(1,(float)(Math.sqrt(dist)));

            if(state.isIn(ModBlockTags.ADDS_SOULS_NORMAL)) res += 3/div;
            else if(state.isIn(ModBlockTags.ADDS_SOULS_FEW)) res += 1/div;
            else if(state.isIn(ModBlockTags.ADDS_SOULS_MANY)) res += 10/div;
        }

        return res;
    }

    public static boolean addsAmbientSouls(BlockState state){
        return state.isIn(ModBlockTags.ADDS_SOULS);

    }

    public static float getRegenSpeedMultiplier(ServerPlayerEntity player){
        float res = IJewelryItem.getManaRegenMultiplier(player);

        if(player.hasStatusEffect(ModStatusEffects.MOURNING))
        {
            var amp = player.getStatusEffect(ModStatusEffects.MOURNING).getAmplifier();
            res *= Toolbox.clampF(1-(amp+1)*0.2f,0,1);
        }

        return res;
    }

    private static boolean tickPlayerSoul(ServerPlayerEntity player){
        // channeling souls
        var storers = getAllSoulStoringItems(player);
        boolean changed = false;
        // trinkets
        for (var stack : storers){
            changed = tickStack(player.getWorld(),stack,player.getBlockPos(),player) || changed;
        }

        if(!changed) return changed;

        // calculate available mana
        queueRecalculateSoul(player);
        return changed;
    }

    public static boolean canStoreSoul(ItemStack stack){
        return stack.getItem() instanceof ISoulStoringItem;// stack.hasNbt() && stack.getNbt().contains("soul");
    }

    public static ItemStack setItemSoulCapacity(World world, ItemStack stack, float capacity){
        if(stack.getItem() instanceof ISoulStoringItem storingItem){
            storingItem.setCapacity(world,stack,capacity);
        }
        return stack;
    }

    public static List<ItemStack> getAllSoulStoringItems(PlayerEntity player){
        List<ItemStack> res = new ArrayList<>();
        var inv = player.getInventory();
        var trinketComp = TrinketsApi.getTrinketComponent(player);
        var trinketInv = trinketComp.map(TrinketComponent::getAllEquipped).orElse(null);
        if(inv!=null){
            for(ItemStack s : inv.main) if(canStoreSoul(s)) res.add(s);
            for(ItemStack s : inv.armor) if(canStoreSoul(s)) res.add(s);
            for(ItemStack s : inv.offHand) if(canStoreSoul(s)) res.add(s);
        }
        if(trinketInv!=null){
            for(var pair : trinketInv) if(canStoreSoul(pair.getRight())) res.add(pair.getRight());
        }
        return res;
    }

    public static List<ItemStack> getAllSoulStoringItems(Inventory inv){
        List<ItemStack> res = new ArrayList<>();
        if(inv!=null){
            for(int i = 0; i < inv.size();i++)
            {
                var s = inv.getStack(i);
                if(canStoreSoul(s)) res.add(s);
            }
        }
        return res;
    }

    private static boolean tickSoulRegen(ItemStack stack, World world, float ambientMana, float regenSpeed, ServerPlayerEntity player){
        var changed = false;
        var item = (ISoulStoringItem) stack.getItem();
        var data = ISoulStoringItem.getData(world,stack);
        float max = item.getCapacity(world,stack);

        // per tick
        float actualRegenSpeed = 0.0005f *
                (regenSpeed
                        +item.getRechargeSpeedMultiplier(world,stack,player))
                * ambientMana;

        // make regen less effective the fuller the item is
        // at 0%, 100% speed
        // at 100%, 50% speed
        actualRegenSpeed *= 1-0.5f*(data.mana/Math.max(max,1));

        // regen from ambiance
        float newMana = Toolbox.clampF(data.mana + actualRegenSpeed,0,max);
        if(newMana!=data.mana)
        {
            if(Float.isNaN(newMana))
            {
                Geomancy.logError("setting new mana as NaN!");
                newMana=0;
            }
            data.mana = newMana;
            changed=true;
        }

        if(changed)
            syncItemSoul(world,stack,player);
        return changed;
    }

    /// equally takes mana from all equipped mana storing items
    public static boolean tryConsumeSoul(LivingEntity entity, float amount, @Nullable SpellContext ctx){
        // TODO: living entity mana consumption
        if(!(entity instanceof ServerPlayerEntity player)) return true;

        if(getSoul(player) < amount) return false;
        var storers = getAllSoulStoringItems(player);
        removeSoul(storers,amount,player.getWorld(),ctx);
        recalculateSoul(player,false);

        return true;
    }

    public static boolean tryConsumeSoul(AutocasterBlockEntity casterBlock, float amount, @Nullable SpellContext ctx) {
        if(getSoul(casterBlock.getWorld(),casterBlock) < amount) return false;
        var storers = getAllSoulStoringItems(casterBlock);
        removeSoul(storers,amount,casterBlock.getWorld(),ctx);

        if(ctx.caster instanceof  ServerPlayerEntity spe)
        for(var storer:storers)
            syncItemSoul(casterBlock.getWorld(),storer,spe);

        return true;
    }

    private static float removeSoul(List<ItemStack> storers, float amount, World world, @Nullable SpellContext ctx){

        // generate prioritized lists
        HashMap<Integer,List<Pair<ItemStack, ISoulStoringItem>>> storerPriorityMap = new HashMap<>();
        for(var storer : storers){
            var pair = new Pair<>(storer,(ISoulStoringItem)storer.getItem());
            int priority = pair.getRight().depletionPriority(storer);
            if(!storerPriorityMap.containsKey(priority)) storerPriorityMap.put(priority,new ArrayList<>());
            storerPriorityMap.get(priority).add(pair);
        }
        List<Integer> keys = new ArrayList<>(storerPriorityMap.keySet());
        keys.sort(Comparator.comparingInt(o -> o));

        float left = amount;
        for (int i = 0; i < keys.size(); i++) {
            if(left<=0) break;
            var prioStorers = storerPriorityMap.get(keys.get(i));
            boolean changed=true;
            while(left>0){
                if(!changed) break;
                changed=false;
                int stacksWithMana = 0;
                for(var pair : prioStorers){if(pair.getRight().getMana(world,pair.getLeft()) > 0 ) stacksWithMana++;}
                float amountPerStack = left/stacksWithMana;
                for(var pair : prioStorers){
                    float mana = pair.getRight().getMana(world,pair.getLeft());
                    if(mana<=0) continue;
                    float taken = Math.min(mana,amountPerStack);
                    left-=taken;
                    pair.getRight().takeSoul(world,pair.getLeft(),taken,ctx);
                    if(mana-taken<=0) pair.getRight().onDepleted(pair.getLeft());
                    changed=true;
                }
            }
        }

        return left;
    }

    public static float getMaxSoul(World world, AutocasterBlockEntity casterBlock) {
        float res = 0;
        var storers = getAllSoulStoringItems(casterBlock);
        for(var storer:storers)
        {
            res += ((ISoulStoringItem) storer.getItem()).getCapacity(world,storer);
        }
        return res;
    }

    public static boolean tickStorage(World world, Inventory inv,BlockPos pos) {
        boolean res = false;
        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getStack(i);
            res = tickStack(world,stack,pos,null) || res;
        }
        return res;
    }

    private static boolean tickStack(World world, ItemStack stack,BlockPos pos,@Nullable ServerPlayerEntity player) {
        if(!canStoreSoul(stack)) return false;

        return tickSoulRegen(stack,world,getAmbientSoulsPerBlock(world,pos),1,player);
    }

    public static void syncItemSoul(ServerPlayerEntity player) {
        var items = getAllSoulStoringItems(player);
        for (var stack : items)
            syncItemSoul(player.getWorld(),stack,player);
    }
}
