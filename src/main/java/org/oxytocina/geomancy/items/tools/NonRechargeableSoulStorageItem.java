package org.oxytocina.geomancy.items.tools;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class NonRechargeableSoulStorageItem extends SoulStorageItem {

    public NonRechargeableSoulStorageItem(Settings settings, float capacity) {
        super(settings,capacity,0);
    }

    @Override
    public float getInitialMana(ItemStack base) {
        return getBaseSoulCapacity(base);
    }

    @Override
    public float getRechargeSpeedMultiplier(World world, ItemStack stack, LivingEntity entity) {
        return 0;
    }

    @Override
    public void onDepleted(ItemStack stack) {
        stack.decrement(1);
    }

    @Override
    public int depletionPriority(ItemStack stack) {
        return 10;
    }
}
