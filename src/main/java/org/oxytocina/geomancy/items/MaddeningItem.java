package org.oxytocina.geomancy.items;

import net.minecraft.item.Item;

public class MaddeningItem extends Item implements IMaddeningItem{

    public final float maddeningSpeed;

    public MaddeningItem(Settings settings, float maddeningSpeed) {
        super(settings);
        this.maddeningSpeed =maddeningSpeed;
    }


    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
    }
}
