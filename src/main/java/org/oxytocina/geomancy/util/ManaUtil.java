package org.oxytocina.geomancy.util;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ILeadPoisoningBlock;
import org.oxytocina.geomancy.effects.ModStatusEffect;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.oxytocina.geomancy.entity.ManaStoringItemData;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.items.IManaStoringItem;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModBiomeTags;
import org.oxytocina.geomancy.registries.ModBlockTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManaUtil {

    private static final ArrayList<PlayerEntity> queuedRecalcs = new ArrayList<>();
    // cache recently calculated ambient soul values to reduce overhead
    // clears every 40 ticks
    private static final HashMap<BlockPos,Float> cachedAmbientSouls = new HashMap<>();

    /// DOESNT sync mana
    public static boolean setMana(PlayerEntity player, float amount){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.mana;
        float mana = Toolbox.clampF(amount,0,data.maxMana);
        if(old==mana) return false;
        data.mana = mana;
        return true;
    }

    /// DOESNT sync mana
    public static boolean setManaCap(PlayerEntity player, float newMaxMana){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.maxMana;
        if(old==newMaxMana) return false;
        data.maxMana = newMaxMana;
        return true;
    }

    public static float getMana(PlayerEntity player){
        return PlayerData.from(player).mana;
    }

    public static float getMaxMana(PlayerEntity player){
        return PlayerData.from(player).maxMana;
    }

    /// call after equipping/unequipping mana storing items, casting spells, etc.
    /// syncs mana
    private static void recalculateMana(PlayerEntity player){
        recalculateMana(player,true);
    }
    private static void recalculateMana(PlayerEntity player, boolean sync){
        if(player==null || !(player instanceof ServerPlayerEntity)) return;

        float cap = 0;
        float mana = 0;

        var items = getAllSoulStoringItems(player);
        for(var item : items){
            if(item.getItem() instanceof IManaStoringItem storer){
                cap += storer.getCapacity(player.getWorld(),item);
                mana += storer.getMana(player.getWorld(),item);
            }
        }

        setManaCap(player,cap);
        setMana(player,mana);

        if(sync)
            syncMana(player);
    }

    private static int cacheClearTimer = 0;
    public static void tick(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ManaUtil.tickMana(player);
        }

        // recalc mana
        for(PlayerEntity entity : queuedRecalcs){
            recalculateMana(entity);
        }
        queuedRecalcs.clear();

        if(++cacheClearTimer > 40){
            cachedAmbientSouls.clear();
            cacheClearTimer=0;
        }
    }

    public static void queueRecalculateMana(PlayerEntity player){
        if(!queuedRecalcs.contains(player))
            queuedRecalcs.add(player);
    }

    public static void syncMana(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity spe)) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(getMaxMana(player));
        buf.writeFloat(getMana(player));
        ServerPlayNetworking.send(spe,ModMessages.MANA_SYNC,buf);
    }

    public static void syncItemMana(World world, ItemStack stack){
        if(!(world instanceof ServerWorld svw)) return;

        ManaStoringItemData data = ManaStoringItemData.from(world,stack, IManaStoringItem.getUUID(stack));

        PacketByteBuf buf = PacketByteBufs.create();
        data.writeBuf(buf);

        ModMessages.sendToAllClients(svw.getServer(),ModMessages.ITEM_MANA_SYNC,buf);

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

    private static boolean tickMana(ServerPlayerEntity player){
        // channeling souls
        float ambientMana = getAmbientSoulsPerBlock(player);
        float playerRegenSpeed = getRegenSpeedMultiplier(player);
        boolean changed = false;

        var items = getAllSoulStoringItems(player);
        for(var i : items){
            changed=tickMana(i,player,ambientMana,playerRegenSpeed)||changed;
        }

        if(changed) queueRecalculateMana(player);

        return changed;
    }

    public static boolean canStoreMana(ItemStack stack){
        return stack.getItem() instanceof IManaStoringItem;// stack.hasNbt() && stack.getNbt().contains("soul");
    }

    public static ItemStack setItemManaCapacity(World world,ItemStack stack,float capacity){
        if(stack.getItem() instanceof IManaStoringItem storingItem){
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
            for(ItemStack s : inv.main) if(canStoreMana(s)) res.add(s);
            for(ItemStack s : inv.armor) if(canStoreMana(s)) res.add(s);
            for(ItemStack s : inv.offHand) if(canStoreMana(s)) res.add(s);
        }
        if(trinketInv!=null){
            for(var pair : trinketInv) if(canStoreMana(pair.getRight())) res.add(pair.getRight());
        }
        return res;
    }

    private static boolean tickMana(ItemStack soulStorage, ServerPlayerEntity player, float ambientMana, float regenSpeed){
        boolean changed = false;

        var data = IManaStoringItem.getData(player.getWorld(),soulStorage);
        var item = (IManaStoringItem) soulStorage.getItem();

        // per tick
        float actualRegenSpeed = 0.0005f *
                (regenSpeed
                        +item.getRechargeSpeedMultiplier(player.getWorld(),soulStorage,player))
                * ambientMana;

        // make regen less effective the fuller the item is
        // at 0%, 100% speed
        // at 100%, 50% speed
        actualRegenSpeed *= 1-0.5f*(data.mana/Math.max(data.maxMana,1));

        // regen from ambiance
        float newMana = Toolbox.clampF(data.mana + actualRegenSpeed,0,data.maxMana);
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
            syncItemMana(player.getWorld(),soulStorage);
        return changed;
    }

    /// equally takes mana from all equipped mana storing items
    public static boolean tryConsumeMana(LivingEntity entity, float amount){
        // TODO: living entity mana consumption
        if(!(entity instanceof ServerPlayerEntity player)) return true;

        if(getMana(player) < amount) return false;

        var storers = getAllSoulStoringItems(player);
        float left = amount;
        boolean changed=true;
        World world = player.getWorld();
        while(left>0){
            if(!changed) break;
            changed=false;
            int stacksWithMana = 0;
            for(var stack : storers){if(((IManaStoringItem)stack.getItem()).getMana(world,stack) > 0 ) stacksWithMana++;}
            float amountPerStack = left/stacksWithMana;
            for(var stack : storers){
                if(!(stack.getItem() instanceof IManaStoringItem storer)) continue;
                float mana = storer.getMana(world,stack);
                if(mana<=0) continue;
                float taken = Math.min(mana,amountPerStack);
                left-=taken;
                storer.setMana(world,stack,mana-taken);
                changed=true;
            }
        }

        recalculateMana(player,false);

        return true;
    }
}
