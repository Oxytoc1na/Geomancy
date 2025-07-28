package org.oxytocina.geomancy.items.jewelry;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.oxytocina.geomancy.items.IMaddeningItem;
import org.oxytocina.geomancy.items.IManaStoringItem;
import org.oxytocina.geomancy.util.MadnessUtil;
import org.oxytocina.geomancy.util.ManaUtil;

public class OctanguliteJewelryItem extends JewelryItem implements IManaStoringItem, IMaddeningItem {

    public float baseSoulCapacity;
    public float maddeningSpeed;
    public float maddeningSpeedWorn;

    public OctanguliteJewelryItem(Settings settings, JewelryItemSettings jewelryItemSettings, float baseSoulCapacity, float maddeningSpeed, float maddeningSpeedWorn) {
        super(settings, jewelryItemSettings);
        this.baseSoulCapacity = baseSoulCapacity;
        this.maddeningSpeed=maddeningSpeed;
        this.maddeningSpeedWorn=maddeningSpeedWorn;
    }

    @Override
    public float getCapacity(World world, ItemStack stack) {
        return baseSoulCapacity;
    }

    @Override
    public float getRechargeSpeedMultiplier(World world, ItemStack stack, LivingEntity entity) {
        return getManaRegenMultiplier(stack,entity);
    }

    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return baseSoulCapacity;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);

        if(entity instanceof ServerPlayerEntity player)
        {
            ManaUtil.queueRecalculateMana(player);
            MadnessUtil.queueRecalculateMadnessSpeed(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);

        if(entity instanceof ServerPlayerEntity player)
        {
            ManaUtil.queueRecalculateMana(player);
            MadnessUtil.queueRecalculateMadnessSpeed(player);
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ((IManaStoringItem)stack.getItem()).getBarColor(stack);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if(MinecraftClient.getInstance()==null) return 0;

        var world = MinecraftClient.getInstance().world;
        return Math.round(getMana(world,stack) * 13.0F / getCapacity(world,stack));
    }

    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
    }

    @Override
    public float getWornMaddeningSpeed() {
        return maddeningSpeedWorn;
    }
}
