package org.oxytocina.geomancy.blocks;

import net.minecraft.block.Block;

public class OctanguliteBlock extends Block implements IOctanguliteBlock {
    public final float maddeningSpeed;

    public OctanguliteBlock(Settings settings, float maddeningSpeed) {
        super(settings);
        this.maddeningSpeed = maddeningSpeed;
    }

    @Override
    public float getAmbientMaddeningSpeed() {
        return maddeningSpeed;
    }
}
