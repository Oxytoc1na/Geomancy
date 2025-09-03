package org.oxytocina.geomancy.blocks.blockEntities;


import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AutocasterBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING;
    public static final BooleanProperty TRIGGERED;
    private static final Map<Item, DispenserBehavior> BEHAVIORS;
    private static final int SCHEDULED_TICK_DELAY = 4;

    public static void registerBehavior(ItemConvertible provider, DispenserBehavior behavior) {
        BEHAVIORS.put(provider.asItem(), behavior);
    }

    public AutocasterBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AutocasterBlockEntity) {
                player.openHandledScreen((AutocasterBlockEntity)blockEntity);
            }

            return ActionResult.CONSUME;
        }
    }

    protected void cast(ServerWorld world, BlockPos pos) {
        BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
        AutocasterBlockEntity casterBlockEntity = (AutocasterBlockEntity)blockPointerImpl.getBlockEntity();
        casterBlockEntity.cast();
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean receivingRedstone = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean isTriggered = (Boolean)state.get(TRIGGERED);
        if (receivingRedstone && !isTriggered) {
            if(world instanceof ServerWorld sw)
                this.cast(sw, pos);
            //world.scheduleBlockTick(pos, this, SCHEDULED_TICK_DELAY);
            world.setBlockState(pos, (BlockState)state.with(TRIGGERED, true), 2|4);
        } else if (!receivingRedstone && isTriggered) {
            world.setBlockState(pos, (BlockState)state.with(TRIGGERED, false), 2|4);
        }

    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //this.cast(world, pos);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutocasterBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AutocasterBlockEntity) {
                ((AutocasterBlockEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }

    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AutocasterBlockEntity) {
                ItemScatterer.spawn(world, pos, (AutocasterBlockEntity)blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public static Position getOutputLocation(BlockPointer pointer) {
        Direction direction = (Direction)pointer.getBlockState().get(FACING);
        double d = pointer.getX() + 0.7 * (double)direction.getOffsetX();
        double e = pointer.getY() + 0.7 * (double)direction.getOffsetY();
        double f = pointer.getZ() + 0.7 * (double)direction.getOffsetZ();
        return new PositionImpl(d, e, f);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, TRIGGERED});
    }

    static {
        FACING = FacingBlock.FACING;
        TRIGGERED = Properties.TRIGGERED;
        BEHAVIORS = (Map)Util.make(new Object2ObjectOpenHashMap(), (map) -> map.defaultReturnValue(new ItemDispenserBehavior()));
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.AUTOCASTER_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }
}