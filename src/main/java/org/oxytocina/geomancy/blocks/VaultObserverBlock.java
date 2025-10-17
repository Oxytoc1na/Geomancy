package org.oxytocina.geomancy.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class VaultObserverBlock extends FacingBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty INVERTED = Properties.INVERTED;

    public VaultObserverBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.SOUTH).with(POWERED, false).with(INVERTED,false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED,INVERTED);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public void invert(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(INVERTED, !state.get(INVERTED)), Block.NOTIFY_LISTENERS);
        this.scheduleTick(world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        var observedState = world.getBlockState(pos.offset(state.get(FACING)));
        boolean powered = state.get(POWERED);
        boolean newPowered = false;

        var block = observedState.getBlock();
        if(block instanceof VaultLampBlock) newPowered = observedState.get(VaultLampBlock.LIT);
        else if(block instanceof RedstoneLampBlock) newPowered = observedState.get(RedstoneLampBlock.LIT);
        else if(block instanceof FurnaceBlock) newPowered = observedState.get(FurnaceBlock.LIT);
        else if(block instanceof DoorBlock) newPowered = observedState.get(DoorBlock.OPEN);
        else if(block instanceof TrapdoorBlock) newPowered = observedState.get(TrapdoorBlock.OPEN);
        else if(block instanceof FenceGateBlock) newPowered = observedState.get(FenceGateBlock.OPEN);

        if(state.get(INVERTED)) newPowered=!newPowered;

        if(newPowered!=powered)
        {
            world.setBlockState(pos, state.with(POWERED, newPowered), Block.NOTIFY_LISTENERS);
        }
        this.updateNeighbors(world, pos, state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if (state.get(FACING) == direction) {
            this.scheduleTick(world, pos);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private void scheduleTick(WorldAccess world, BlockPos pos) {
        if (!world.isClient() && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.scheduleBlockTick(pos, this, 0);
        }
    }

    protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos redstoneOutputPos = pos.offset(direction.getOpposite());
        world.updateNeighbor(redstoneOutputPos, this, pos);
        world.updateNeighborsExcept(redstoneOutputPos, this, direction);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && state.get(FACING) == direction ? 15 : 0;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!state.isOf(oldState.getBlock())) {
            if (!world.isClient() && (Boolean)state.get(POWERED) && !world.getBlockTickScheduler().isQueued(pos, this)) {
                BlockState blockState = state.with(POWERED, false);
                world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                this.updateNeighbors(world, pos, blockState);
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (!world.isClient && (Boolean)state.get(POWERED) && world.getBlockTickScheduler().isQueued(pos, this)) {
                this.updateNeighbors(world, pos, state.with(POWERED, false));
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.getStackInHand(hand).getItem()== Items.REDSTONE_TORCH){
            if(world instanceof ServerWorld sw)
                invert(state,sw,pos);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state,world,pos,player,hand,hit);
    }
}

