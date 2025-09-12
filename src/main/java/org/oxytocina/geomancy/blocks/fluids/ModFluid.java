package org.oxytocina.geomancy.blocks.fluids;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.oxytocina.geomancy.blocks.MultiblockCrafter;
import org.oxytocina.geomancy.inventories.AutoCraftingInventory;
import org.oxytocina.geomancy.recipe.FluidConvertingRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.oxytocina.geomancy.util.RecipeUtil;

import java.util.Collections;

public abstract class ModFluid extends FlowableFluid {

    /**
     * @return whether the given fluid an instance of this fluid
     */
    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    /**
     * @return whether the fluid is infinite (which means can be infinitely created like water). In vanilla, it depends on the game rule.
     */
    @Override
    protected boolean isInfinite(World world) {
        return false;
    }

    /**
     * Perform actions when the fluid flows into a replaceable block. Water drops
     * the block's loot table. Lava plays the "block.lava.extinguish" sound.
     */
    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    /**
     * Lava returns true if it's FluidState is above a certain height and the
     * Fluid is Water.
     *
     * @return whether the given Fluid can flow into this FluidState
     */
    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return false;
    }

    /**
     * Possibly related to the distance checks for flowing into nearby holes?
     * Water returns 4. Lava returns 2 in the Overworld and 4 in the Nether.
     */
    @Override
    protected int getFlowSpeed(WorldView worldView) {
        return 2;
    }

    /**
     * Water returns 1. Lava returns 2 in the Overworld and 1 in the Nether.
     */
    @Override
    protected int getLevelDecreasePerBlock(WorldView worldView) {
        return 2;
    }

    /**
     * Water returns 5. Lava returns 30 in the Overworld and 10 in the Nether.
     */
    @Override
    public int getTickRate(WorldView worldView) {
        return 20;
    }

    /**
     * Water and Lava both return 100.0F.
     */
    @Override
    protected float getBlastResistance() {
        return 100.0F;
    }

    @Environment(EnvType.CLIENT)
    public abstract ParticleEffect getSplashParticle();

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {

        if (!world.isClient) {
            if (entity instanceof ItemEntity itemEntity && !itemEntity.cannotPickup() && !itemEntity.isRemoved()) {
                if (world.random.nextInt(100) == 0) {
                    ItemStack itemStack = itemEntity.getStack();
                    FluidConvertingRecipe recipe = RecipeUtil.getConversionRecipeFor(getDippingRecipeType(), world, itemStack);
                    if (recipe != null && !recipe.getOutput(world.getRegistryManager()).isOf(itemStack.getItem())) { // do not try to convert items into itself for performance reasons
                        world.playSound(null, itemEntity.getBlockPos(), SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.NEUTRAL, 1.0F, 0.9F + world.getRandom().nextFloat() * 0.2F);

                        ItemStack result = RecipeUtil.craft(recipe, itemStack, world);
                        int count = result.getCount() * itemStack.getCount();
                        result.setCount(count);

                        itemEntity.discard();
                        MultiblockCrafter.spawnItemStackAsEntitySplitViaMaxCount(world, itemEntity.getPos(), result, count, Vec3d.ZERO, false, itemEntity.getOwner());
                    }
                }
            }
        }
    }

    public abstract RecipeType<? extends FluidConvertingRecipe> getDippingRecipeType();
}