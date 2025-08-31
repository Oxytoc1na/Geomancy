package org.oxytocina.geomancy.items.tools;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.ICustomRarityItem;
import org.oxytocina.geomancy.items.ISoulStoringItem;

import java.util.List;

public class SoulStorageItem extends Item implements ISoulStoringItem, ICustomRarityItem {

    public final float capacity;
    public final float rechargeSpeedMultiplier;

    public SoulStorageItem(Settings settings, float capacity, float rechargeSpeedMultiplier) {
        super(settings);
        this.capacity=capacity;
        this.rechargeSpeedMultiplier=rechargeSpeedMultiplier;
    }

    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return capacity;
    }

    @Override
    public float getRechargeSpeedMultiplier(World world, ItemStack stack, LivingEntity entity) {
        return rechargeSpeedMultiplier* ISoulStoringItem.super.getRechargeSpeedMultiplier(world, stack, entity);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ((ISoulStoringItem)stack.getItem()).getBarColor(stack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getItemBarStep(ItemStack stack) {
        if(MinecraftClient.getInstance()==null) return 0;

        var world = MinecraftClient.getInstance().world;
        return Math.round(getMana(world,stack) * 13.0F / getCapacity(world,stack));
    }

    @Override
    public Text getName(ItemStack stack) {
        return colorizeName(stack,super.getName(stack));
    }

    @Override
    public ICustomRarityItem.Rarity getRarity() {
        return ICustomRarityItem.Rarity.Octangulite;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> list, TooltipContext context) {
        super.appendTooltip(stack, world, list, context);
        ISoulStoringItem.super.addManaTooltip(world,stack,list);
    }
}
