package org.oxytocina.geomancy.blocks.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
    public DefaultParticleType getSplashParticle() {
        return null;
    }

    @Override
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

        if(otherState.isIn(FluidTags.WATER))
        {
            // extinguish
            return Blocks.GOLD_BLOCK.getDefaultState();
        }

        if(otherState.isIn(FluidTags.LAVA)){
            // ???
            if(otherState.isOf(Fluids.LAVA))
                return Blocks.RAW_GOLD_BLOCK.getDefaultState();
            else
                return ModBlocks.GILDED_DEEPSLATE.getDefaultState();
        }

        return Blocks.GOLD_ORE.getDefaultState();
    }
}
