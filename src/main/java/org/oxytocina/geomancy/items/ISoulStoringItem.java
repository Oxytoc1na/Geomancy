package org.oxytocina.geomancy.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.enchantments.ModEnchantments;
import org.oxytocina.geomancy.entity.SoulStoringItemData;
import org.oxytocina.geomancy.spells.SpellContext;

import java.util.List;
import java.util.UUID;

public interface ISoulStoringItem {
    static void init(World world, ItemStack stack){
        if(stack.getSubNbt("soul")!=null) return;
        NbtCompound soul = new NbtCompound();
        soul.putUuid("uuid", SoulStoringItemData.getNextUUID());
        stack.setSubNbt("soul",soul);
    }

    static SoulStoringItemData getData(World world, ItemStack stack){
        init(world,stack);
        return SoulStoringItemData.from(world,stack,getUUID(stack));
    }

    default float getInitialMana(ItemStack base){
        return 0;
    }

    default void onDepleted(ItemStack stack){

    }

    /// lower priority items deplete first
    default int depletionPriority(ItemStack stack){
        return 0;
    }

    default float getCapacityMultiplier(ItemStack stack){
        return 1+ ModEnchantments.getLevel(stack,ModEnchantments.MESMERIZING)*0.5f;
    }

    default float getCapacity(World world, ItemStack stack){
        init(world,stack);
        return getData(world,stack).maxMana * getCapacityMultiplier(stack);
    }
    default float getRechargeSpeedMultiplier(World world, ItemStack stack, LivingEntity entity){
        init(world,stack);
        return getData(world,stack).speedMultiplier;
    }
    default void setRechargeSpeedMultiplier(World world, ItemStack stack, float speed){
        init(world,stack);
        getData(world,stack).speedMultiplier = speed;
    }
    default float getMana(World world, ItemStack stack){
        init(world,stack);

        float mana = getData(world,stack).mana;
        if(Float.isNaN(mana))
        {
            Geomancy.logError("item mana was NaN!");
            setMana(world,stack,0);
        }

        return getData(world,stack).mana;
    }
    default void setCapacity(World world, ItemStack stack, float capacity){
        init(world,stack);
        getData(world,stack).maxMana = capacity;
    }
    default void setMana(World world, ItemStack stack, float mana){
        init(world,stack);
        getData(world,stack).mana = mana;
    }
    static void setUUID(ItemStack stack, UUID uuid){
        NbtCompound soul = new NbtCompound();
        soul.putUuid("uuid",uuid);
        stack.setSubNbt("soul",soul);
    }
    static UUID getUUID(ItemStack stack){
        //init(stack);
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
        float cap = getCapacity(world,stack);
        float progress = getMana(world,stack)/Math.max(cap,1);

        return ModColorizationHandler.octanguliteItemBarNoise(progress);
    }

    float getBaseSoulCapacity(ItemStack stack);

    @Environment(EnvType.CLIENT)
    default void addManaTooltip(World world, ItemStack stack, List<Text> tooltip){
        if(world==null) world=MinecraftClient.getInstance().world;
        if(world==null) return;
        float mana = getMana(world,stack);
        float cap = getCapacity(world,stack);
        if(cap<=0) return;
        float fraction = mana/cap;
        tooltip.add(Text.translatable("geomancy.soul_storage.tooltip",Math.round(mana),Math.round(cap),Math.round(fraction*100)).formatted(Formatting.DARK_GRAY));
    }

    default void takeSoul(World world, ItemStack left, float taken, @Nullable SpellContext ctx){
        setMana(world,left,getMana(world,left)-taken);
    }
}
