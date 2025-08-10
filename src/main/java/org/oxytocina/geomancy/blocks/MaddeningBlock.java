package org.oxytocina.geomancy.blocks;

import net.minecraft.block.Block;

public class MaddeningBlock extends Block implements IMaddeningBlock {
    public final float maddeningSpeed;

    public MaddeningBlock(Settings settings, float maddeningSpeed) {
        super(settings);
        this.maddeningSpeed = maddeningSpeed;
    }

    @Override
    public float getAmbientMaddeningSpeed() {
        return maddeningSpeed;
    }
}
