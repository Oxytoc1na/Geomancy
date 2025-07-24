package org.oxytocina.geomancy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

public class LeadStairsBlock extends StairsBlock implements ILeadPoisoningBlock {
    public final float poisoningSpeed;

    public LeadStairsBlock(BlockState baseBlockState, Settings settings,float poisoningSpeed) {
        super(baseBlockState, settings);
        this.poisoningSpeed=poisoningSpeed;
    }


    @Override
    public float getAmbientPoisoningSpeed() {
        return poisoningSpeed;
    }
}
