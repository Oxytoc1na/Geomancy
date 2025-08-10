package org.oxytocina.geomancy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

public class MaddeningStairsBlock extends StairsBlock implements IMaddeningBlock {
    public final float maddeningSpeed;

    public MaddeningStairsBlock(BlockState baseBlockState, Settings settings, float maddeningSpeed) {
        super(baseBlockState, settings);
        this.maddeningSpeed=maddeningSpeed;
    }

    @Override
    public float getAmbientMaddeningSpeed() {
        return maddeningSpeed;
    }
}
