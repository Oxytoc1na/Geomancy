package org.oxytocina.geomancy.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class LeadBlockItem extends BlockItem implements ILeadPoisoningItem{

    public final float poisoningSpeed;

    public LeadBlockItem(Block block, Settings settings, float poisoningSpeed) {
        super(block, settings);
        this.poisoningSpeed=poisoningSpeed;
    }

    @Override
    public float getInInventoryPoisoningSpeed() {
        return poisoningSpeed;
    }
}
