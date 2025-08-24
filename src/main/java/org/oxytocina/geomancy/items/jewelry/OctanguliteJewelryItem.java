package org.oxytocina.geomancy.items.jewelry;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.ICustomRarityItem;
import org.oxytocina.geomancy.items.IMaddeningItem;
import org.oxytocina.geomancy.items.IManaStoringItem;
import org.oxytocina.geomancy.util.MadnessUtil;
import org.oxytocina.geomancy.util.ManaUtil;

import java.util.List;

public class OctanguliteJewelryItem extends JewelryItem implements IManaStoringItem, IMaddeningItem, ICustomRarityItem {

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
        LivingEntity wearer = null;
        if(stack.getHolder() instanceof LivingEntity le) wearer = le;
        return baseSoulCapacity * getCapacityMultiplier(world,stack,wearer);
    }

    @Override
    public float getRechargeSpeedMultiplier(World world, ItemStack stack, LivingEntity entity) {
        return IJewelryItem.getManaRegenMultiplier(stack,entity);
    }

    public float getCapacityMultiplier(World world, ItemStack stack, LivingEntity entity) {
        return IJewelryItem.getManaCapacityMultiplier(stack,entity);
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

    @Override
    public Text getName(ItemStack stack) {
        return colorizeName(stack,super.getName(stack));
    }

    @Override
    public Rarity getRarity() {
        return Rarity.Octangulite;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> list, TooltipContext context) {
        super.appendTooltip(stack, world, list, context);
        IManaStoringItem.super.addManaTooltip(world,stack,list);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        IManaStoringItem.init(world,stack);
    }
}
