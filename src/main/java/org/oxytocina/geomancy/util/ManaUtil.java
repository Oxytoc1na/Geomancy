package org.oxytocina.geomancy.util;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.oxytocina.geomancy.entity.ManaStoringItemData;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.items.IManaStoringItem;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModBiomeTags;

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
        buf.writeUuid(data.uuid);
        buf.writeFloat(data.mana);
        buf.writeFloat(data.maxMana);

        ModMessages.sendToAllClients(svw.getServer(),ModMessages.ITEM_MANA_SYNC,buf);

    }

    public static float getAmbientSoulsPerBlock(Entity entity){
        return getAmbientSoulsPerBlock(entity.getWorld(),entity.getBlockPos());
    }
    public static float getAmbientSoulsPerBlock(World world, BlockPos pos){
        if(cachedAmbientSouls.containsKey(pos)) return cachedAmbientSouls.get(pos);

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
        // TODO

        cachedAmbientSouls.put(pos,res);
        return res;
    }

    public static float getRegenSpeedMultiplier(ServerPlayerEntity player){
        float res = JewelryItem.getManaRegenMultiplier(player);
        return res;
    }

    private static boolean tickMana(ServerPlayerEntity player){
        // channeling souls
        float ambientMana = getAmbientSoulsPerBlock(player);
        float regenSpeed = getRegenSpeedMultiplier(player);
        boolean changed = false;

        var items = getAllSoulStoringItems(player);
        for(var i : items){
            changed=changed|| tickMana(i,player,ambientMana,regenSpeed);
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

        // per tick
        float actualRegenSpeed = 0.001f * regenSpeed * ambientMana;

        // regen from ambiance
        float newMana = Toolbox.clampF(data.mana + actualRegenSpeed,0,data.maxMana);
        if(newMana!=data.mana)
        {
            data.mana = newMana;
            changed=true;
        }

        if(changed)
            syncItemMana(player.getWorld(),soulStorage);
        return changed;
    }

}
