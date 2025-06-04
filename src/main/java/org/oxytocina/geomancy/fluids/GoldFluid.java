package org.oxytocina.geomancy.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class GoldFluid extends LavalikeFluid {
    @Override
    public Fluid getStill() {
        return ModFluids.STILL_GOLD;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_GOLD;
    }

    @Override
    public Item getBucketItem() {
        return ModFluids.GOLD_BUCKET;
    }

    @Override
    public BlockState toBlockState(FluidState fluidState) {
        return ModFluids.GOLD.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
    }

    public static class Flowing extends GoldFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends GoldFluid {
        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}