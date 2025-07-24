package org.oxytocina.geomancy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;

public class LeadSlabBlock extends SlabBlock implements ILeadPoisoningBlock {
    public final float poisoningSpeed;

    public LeadSlabBlock(Settings settings, float poisoningSpeed) {
        super(settings);
        this.poisoningSpeed=poisoningSpeed;
    }

    @Override
    public float getAmbientPoisoningSpeed() {
        return poisoningSpeed;
    }
}
