package org.oxytocina.geomancy.blocks.fluids;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.oxytocina.geomancy.registries.ModCauldronBehaviors;

/**
 * A cauldron filled with lava.
 */
public class MoltenGoldCauldronBlock extends AbstractCauldronBlock {
    public MoltenGoldCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, ModCauldronBehaviors.GOLD_CAULDRON_BEHAVIOR);
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    @Override
    public boolean isFull(BlockState state) {
        return true;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (this.isEntityTouchingFluid(state, pos, entity)) {
            ModFluids.MOLTEN_GOLD.onEntityCollision(state,world,pos,entity);
        }
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }
}
