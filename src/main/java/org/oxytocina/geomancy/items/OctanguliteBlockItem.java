package org.oxytocina.geomancy.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class OctanguliteBlockItem extends BlockItem implements IMaddeningItem{

    public final float maddeningSpeed;

    public OctanguliteBlockItem(Block block, Settings settings, float maddeningSpeed) {
        super(block, settings);
        this.maddeningSpeed =maddeningSpeed;
    }

    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
    }
}
