package org.oxytocina.geomancy.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class MaddeningBlockItem extends BlockItem implements IMaddeningItem{

    public final float maddeningSpeed;

    public MaddeningBlockItem(Block block, Settings settings, float maddeningSpeed) {
        super(block, settings);
        this.maddeningSpeed =maddeningSpeed;
    }

    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
    }
}
