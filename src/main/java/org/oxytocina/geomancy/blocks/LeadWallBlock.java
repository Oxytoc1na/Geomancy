package org.oxytocina.geomancy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;

public class LeadWallBlock extends WallBlock implements ILeadPoisoningBlock {
    public final float poisoningSpeed;

    public LeadWallBlock(Settings settings,float poisoningSpeed) {
        super(settings);
        this.poisoningSpeed=poisoningSpeed;
    }


    @Override
    public float getAmbientPoisoningSpeed() {
        return poisoningSpeed;
    }
}
