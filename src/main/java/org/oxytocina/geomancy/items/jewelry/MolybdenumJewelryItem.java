package org.oxytocina.geomancy.items.jewelry;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.items.IMaddeningItem;
import org.oxytocina.geomancy.util.MadnessUtil;

public class MolybdenumJewelryItem extends JewelryItem implements IMaddeningItem {

    public float maddeningSpeed;
    public float maddeningSpeedWorn;

    public MolybdenumJewelryItem(Settings settings, JewelryItemSettings jewelryItemSettings, float maddeningSpeed, float maddeningSpeedWorn) {
        super(settings, jewelryItemSettings);
        this.maddeningSpeed=maddeningSpeed;
        this.maddeningSpeedWorn=maddeningSpeedWorn;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);

        if(entity instanceof ServerPlayerEntity player)
        {
            MadnessUtil.queueRecalculateMadnessSpeed(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);

        if(entity instanceof ServerPlayerEntity player)
        {
            MadnessUtil.queueRecalculateMadnessSpeed(player);
        }
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
