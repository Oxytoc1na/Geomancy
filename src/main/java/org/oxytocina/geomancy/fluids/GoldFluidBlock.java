package org.oxytocina.geomancy.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.oxytocina.geomancy.Geomancy;

public class GoldFluidBlock extends FluidBlock {

    private ModFluid modfluid;

    public GoldFluidBlock(ModFluid fluid, Settings settings) {
        super(fluid, settings);
        modfluid=fluid;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        modfluid.onEntityCollision(state,world,pos,entity);

        if (state.getFluidState().isOf(ModFluids.FLOWING_GOLD) && !world.isClient) {
            if (entity instanceof ItemEntity itemEntity && !itemEntity.isRemoved()) {
                ItemStack itemStack = itemEntity.getStack();
                if (itemStack.getItem() == Items.GOLD_BLOCK) {

                    if(world.setBlockState(pos,ModFluids.STILL_GOLD.getDefaultState().getBlockState())){
                        world.playSound(null, itemEntity.getBlockPos(), SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.NEUTRAL, 1.0F, 0.9F + world.getRandom().nextFloat() * 0.2F);


                        if (itemEntity.getOwner() instanceof ServerPlayerEntity serverPlayerEntity) {
                            //SpectrumAdvancementCriteria.FLUID_DIPPING.trigger(serverPlayerEntity, (ServerWorld) world, pos, itemStack, result);
                        }

                        itemEntity.discard();
                    }
                }
            }
        }
    }
}
