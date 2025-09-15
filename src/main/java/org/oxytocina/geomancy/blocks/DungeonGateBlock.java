package org.oxytocina.geomancy.blocks;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.util.Toolbox;

public class DungeonGateBlock extends GlassBlock {
    public static final BooleanProperty VANISHED;
    public static final BooleanProperty DESIRE_VANISHED;

    public DungeonGateBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(VANISHED,false).with(DESIRE_VANISHED,false));
    }


    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    public void propagateDesireVanished(BlockState state, World world, BlockPos pos, BlockPos sourcePos, boolean desiredVanished){
        if(state.get(DESIRE_VANISHED) == desiredVanished) return;

        world.setBlockState(pos, state.with(DESIRE_VANISHED,desiredVanished), 2);

        // propagate
        for(var dir : DIRECTIONS){
            BlockPos newP = pos.add(dir.getVector());
            BlockState newState = world.getBlockState(newP);
            if(newState.getBlock() instanceof DungeonGateBlock dgb){
                dgb.propagateDesireVanished(newState,world,newP,sourcePos,desiredVanished);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // turn desired vanished into vanished
        if(state.get(DESIRE_VANISHED) != state.get(VANISHED))
        {
            world.setBlockState(pos, state.with(VANISHED,state.get(DESIRE_VANISHED)), 2);

            // propagate
            for(var dir : DIRECTIONS){
                BlockPos newP = pos.add(dir.getVector());
                BlockState newState = world.getBlockState(newP);
                if(newState.getBlock() instanceof DungeonGateBlock){
                    world.scheduleBlockTick(newP, this, 2);
                }
            }

            Toolbox.playSound(state.get(DESIRE_VANISHED) ? SoundEvents.BLOCK_PISTON_CONTRACT : SoundEvents.BLOCK_PISTON_EXTEND, world,pos, SoundCategory.BLOCKS,0.5f,Toolbox.randomPitch());
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VANISHED,DESIRE_VANISHED);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(VANISHED)? VoxelShapes.empty(): state.getOutlineShape(world, pos);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    static {
        VANISHED = BooleanProperty.of("vanished");
        DESIRE_VANISHED = BooleanProperty.of("desire_vanished");
    }
}
