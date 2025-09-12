package org.oxytocina.geomancy.blocks.fluids;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.registries.ModDamageTypes;
import org.oxytocina.geomancy.particles.ModParticleTypes;
import org.oxytocina.geomancy.recipe.FluidConvertingRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import net.minecraft.particle.ParticleEffect;
import org.oxytocina.geomancy.util.Toolbox;

public abstract class GoldFluid extends ModFluid {
    @Override
    public Fluid getStill() {
        return ModFluids.MOLTEN_GOLD;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_MOLTEN_GOLD;
    }

    @Override
    public Item getBucketItem() {
        return ModFluids.MOLTEN_GOLD_BUCKET;
    }

    @Override
    public BlockState toBlockState(FluidState fluidState) {
        return ModBlocks.MOLTEN_GOLD.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return ModParticleTypes.DRIPPING_MOLTEN_GOLD;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ParticleEffect getSplashParticle() {
        return ModParticleTypes.MOLTEN_GOLD_SPLASH;
    }

    /**
     * Entities colliding with liquid crystal will get a slight regeneration effect
     */
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        // creating more fluid gold
        if (!world.isClient && state.getFluidState().isOf(ModFluids.FLOWING_MOLTEN_GOLD)) {
            if (entity instanceof ItemEntity itemEntity && !itemEntity.isRemoved()) {
                ItemStack itemStack = itemEntity.getStack();
                if (itemStack.getItem() == Items.GOLD_BLOCK) {

                    if(world.setBlockState(pos,ModFluids.MOLTEN_GOLD.getDefaultState().getBlockState())){
                        world.playSound(null, itemEntity.getBlockPos(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 0.9F + world.getRandom().nextFloat() * 0.2F);
                        itemEntity.discard();
                    }
                }
            }
        }

        if (!world.isClient && entity instanceof LivingEntity livingEntity) {
            // damage!!
            if(livingEntity.damage(ModDamageTypes.of(world, ModDamageTypes.MOLTEN_GOLD), 6.0F))
                livingEntity.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + Toolbox.random.nextFloat() * 0.4F);
            if(!livingEntity.isFireImmune()){
                livingEntity.setOnFireFor(15);
            }
        }
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