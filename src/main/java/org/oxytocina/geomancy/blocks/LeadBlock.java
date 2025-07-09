package org.oxytocina.geomancy.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public class LeadBlock extends Block implements ILeadPoisoningBlock {
    public final float poisoningSpeed;

    public LeadBlock(Settings settings, float poisoningSpeed) {
        super(settings);
        this.poisoningSpeed=poisoningSpeed;
    }

    @Override
    public float getAmbientPoisoningSpeed() {
        return poisoningSpeed;
    }
}
