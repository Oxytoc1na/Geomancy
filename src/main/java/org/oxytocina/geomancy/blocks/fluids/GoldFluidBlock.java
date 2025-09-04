package org.oxytocina.geomancy.blocks.fluids;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.registries.ModFluidTags;

public class GoldFluidBlock extends ModFluidBlock {

    public GoldFluidBlock(ModFluid fluid, BlockState ultrawarmReplacementBlockState, Settings settings) {
        super(fluid, ultrawarmReplacementBlockState, settings);
    }


    @Override
    @Environment(EnvType.CLIENT)
    public DefaultParticleType getSplashParticle() {
        return null;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Pair<DefaultParticleType, DefaultParticleType> getFishingParticles() {
        return null;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public @Nullable BlockState handleFluidCollision(World world, @NotNull FluidState state, @NotNull FluidState otherState) {
        // unobstructed
        if(otherState.isEmpty() || otherState.isIn(ModFluidTags.MOLTEN_GOLD))
            return null;

        boolean full = state.isStill();
        boolean otherFull = otherState.isStill();

        if(otherState.isIn(FluidTags.WATER))
        {
            // extinguish
            return (full?Blocks.GOLD_BLOCK:Blocks.CALCITE).getDefaultState();
        }

        if(otherState.isIn(FluidTags.LAVA)){
            // ???
            if(otherFull)
                return (full?Blocks.GOLD_BLOCK:ModBlocks.GILDED_DEEPSLATE).getDefaultState();
            else
                return Blocks.TUFF.getDefaultState();
        }

        return Blocks.CALCITE.getDefaultState();
    }
}
