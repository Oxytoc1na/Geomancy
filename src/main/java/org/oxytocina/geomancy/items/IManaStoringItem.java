package org.oxytocina.geomancy.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.entity.ManaStoringItemData;

import java.util.UUID;

public interface IManaStoringItem {
    static void init(ItemStack stack){
        if(stack.getSubNbt("soul")!=null) return;
        NbtCompound soul = new NbtCompound();
        soul.putUuid("uuid", UUID.randomUUID());
        stack.setSubNbt("soul",soul);
    }

    static ManaStoringItemData getData(World world, ItemStack stack){
        init(stack);
        return ManaStoringItemData.from(world,stack,getUUID(stack));
    }

    default float getCapacity(World world, ItemStack stack){
        init(stack);
        return getData(world,stack).maxMana;
    }
    default float getRechargeSpeedMultiplier(World world, ItemStack stack, LivingEntity entity){
        init(stack);
        return getData(world,stack).speedMultiplier;
    }
    default void setRechargeSpeedMultiplier(World world, ItemStack stack, float speed){
        init(stack);
        getData(world,stack).speedMultiplier = speed;
    }
    default float getMana(World world, ItemStack stack){
        init(stack);

        float mana = getData(world,stack).mana;
        if(Float.isNaN(mana))
        {
            Geomancy.logError("item mana was NaN!");
            setMana(world,stack,0);
        }

        return getData(world,stack).mana;
    }
    default void setCapacity(World world, ItemStack stack, float capacity){
        init(stack);
        getData(world,stack).maxMana = capacity;
    }
    default void setMana(World world, ItemStack stack, float mana){
        init(stack);
        getData(world,stack).mana = mana;
    }
    static void setUUID(ItemStack stack, UUID uuid){
        NbtCompound soul = new NbtCompound();
        soul.putUuid("uuid",uuid);
        stack.setSubNbt("soul",soul);
    }
    static UUID getUUID(ItemStack stack){
        init(stack);
        NbtCompound soul = stack.getOrCreateSubNbt("soul");
        UUID uuid = soul.containsUuid("uuid")? soul.getUuid("uuid") : null;
        if(uuid==null){
            uuid = UUID.randomUUID();
            setUUID(stack,uuid);
        }
        return uuid;
    }

    @Environment(EnvType.CLIENT)
    default int getBarColor(ItemStack stack){
        if(MinecraftClient.getInstance()==null) return 0xFFFFFFFF;

        var world = MinecraftClient.getInstance().world;
        float progress = getMana(world,stack)/getCapacity(world,stack);

        return ModColorizationHandler.octanguliteItemBarNoise(progress);
    }

    float getBaseSoulCapacity(ItemStack stack);
}
