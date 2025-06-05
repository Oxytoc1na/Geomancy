package org.oxytocina.geomancy.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.MultiblockCrafter;
import org.oxytocina.geomancy.recipe.FluidConvertingRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

public abstract class GoldFluid extends ModFluid {
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



    public RecipeType<? extends FluidConvertingRecipe> getDippingRecipeType(){
        return ModRecipeTypes.GOLD_CONVERTING;
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

        @Override
        public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
            super.onEntityCollision(state, world, pos, entity);


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