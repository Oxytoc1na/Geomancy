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
import org.oxytocina.geomancy.spells.SpellContext;

import java.util.List;
import java.util.UUID;

public interface ISoulStoringItem {
    static void init(ItemStack stack){
        var soulNbt = getSoulNbt(stack);
        if(soulNbt==null){
            stack.setSubNbt("soul",new NbtCompound());
            soulNbt = getSoulNbt(stack);
        }
        if(!(stack.getItem() instanceof ISoulStoringItem storer)) return;
        if(!soulNbt.contains("soul")) soulNbt.putFloat("soul",storer.getInitialMana(stack));
        if(!soulNbt.contains("cap")) soulNbt.putFloat("cap",storer.getBaseSoulCapacity(stack));
        if(!soulNbt.contains("speed")) soulNbt.putFloat("speed",storer.getBaseRechargeSpeed(stack));
    }

    static NbtCompound getSoulNbt(ItemStack stack){
        return stack.getSubNbt("soul");
    }

    default float getInitialMana(ItemStack base){
        return 0;
    }

    default void onDepleted(ItemStack stack){

    }

    default void onToppedUp(ItemStack stack){

    }

    /// lower priority items deplete first
    default int depletionPriority(ItemStack stack){
        return 0;
    }

    default float getCapacityMultiplier(ItemStack stack){
        return 1+ ModEnchantments.getLevel(stack,ModEnchantments.MESMERIZING)*0.5f;
    }

    default float getCapacity(World world, ItemStack stack){
        init(stack);
        return getSoulNbt(stack).getFloat("cap") * getCapacityMultiplier(stack);
    }
    default float getRechargeSpeedMultiplier(World world, ItemStack stack, LivingEntity entity){
        init(stack);
        return getSoulNbt(stack).getFloat("speed");
    }
    default void setRechargeSpeedMultiplier(World world, ItemStack stack, float speed){
        init(stack);
        getSoulNbt(stack).putFloat("speed",speed);
    }
    default float getMana(World world, ItemStack stack){
        init(stack);

        float mana = getSoulNbt(stack).getFloat("soul");
        if(Float.isNaN(mana))
        {
            Geomancy.logError("item mana was NaN!");
            setMana(world,stack,0);
            return 0;
        }

        return mana;
    }
    default void setCapacity(World world, ItemStack stack, float capacity){
        init(stack);
        getSoulNbt(stack).putFloat("cap",capacity);
    }
    default void setMana(World world, ItemStack stack, float mana){
        init(stack);
        getSoulNbt(stack).putFloat("soul",mana);
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
    default float getBaseRechargeSpeed(ItemStack stack) {return 1;}

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

    default void changeSoul(World world, ItemStack stack, float taken, @Nullable SpellContext ctx){
        if(taken>0) addSoul(world,stack,taken,ctx);
        else if(taken <0) removeSoul(world,stack,-taken,ctx);
    }

    default boolean canRemoveSoulFrom(World world, ItemStack stack, @Nullable SpellContext ctx){return true;}
    default void removeSoul(World world, ItemStack stack, float taken, @Nullable SpellContext ctx){
        setMana(world,stack,getMana(world,stack)-taken);
    }

    default boolean canAddSoulTo(World world, ItemStack stack, @Nullable SpellContext ctx){return true;}
    default void addSoul(World world, ItemStack stack, float taken, @Nullable SpellContext ctx){
        setMana(world,stack,getMana(world,stack)+taken);
    }
}
