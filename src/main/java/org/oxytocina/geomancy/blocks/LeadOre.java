package org.oxytocina.geomancy.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.util.math.intprovider.IntProvider;

public class LeadOre extends ExperienceDroppingBlock implements ILeadPoisoningBlock {
    public final float poisoningSpeed;

    public LeadOre(Settings settings, IntProvider experience, float poisoningSpeed) {
        super(settings,experience);
        this.poisoningSpeed = poisoningSpeed;
    }

    @Override
    public float getAmbientPoisoningSpeed() {
        return poisoningSpeed;
    }
}
