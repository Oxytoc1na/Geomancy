package org.oxytocina.geomancy.items;

import net.minecraft.item.Item;

public class OctanguliteItem extends Item implements IMaddeningItem{

    public final float maddeningSpeed;

    public OctanguliteItem(Settings settings, float maddeningSpeed) {
        super(settings);
        this.maddeningSpeed =maddeningSpeed;
    }


    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
    }
}
