package org.oxytocina.geomancy.blocks;

import net.minecraft.block.*;
import net.minecraft.block.enums.Thickness;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class NullSpikeBlock extends Block implements LandingBlock, Waterloggable {
    public static final DirectionProperty VERTICAL_DIRECTION;
    public static final BooleanProperty WATERLOGGED;
    private static final VoxelShape UP_TIP_SHAPE;
    private static final VoxelShape DOWN_TIP_SHAPE;

    public NullSpikeBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(VERTICAL_DIRECTION, Direction.UP).with(WATERLOGGED, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VERTICAL_DIRECTION, WATERLOGGED);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return canPlaceAtWithDirection(world, pos, (Direction)state.get(VERTICAL_DIRECTION));
    }

    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BlockPos blockPos = hit.getBlockPos();
        if (!world.isClient && projectile.canModifyAt(world, blockPos) && projectile instanceof TridentEntity && projectile.getVelocity().length() > 0.6) {
            world.breakBlock(blockPos, true);
        }

    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (state.get(VERTICAL_DIRECTION) == Direction.UP) {
            entity.handleFallDamage(fallDistance + 3.0F, 3.0F, world.getDamageSources().stalagmite());
        } else {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        }

    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (isPointingUp(state) && !this.canPlaceAt(state, world, pos)) {
            world.breakBlock(pos, true);
        } else {
            spawnFallingBlock(state, world, pos);
        }

    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {

    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        WorldAccess worldAccess = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction direction = ctx.getVerticalPlayerLookDirection().getOpposite();
        Direction direction2 = getDirectionToPlaceAt(worldAccess, blockPos, direction);
        if (direction2 == null) {
            return null;
        } else {
            boolean bl = !ctx.shouldCancelInteraction();
            Thickness thickness = getThickness(worldAccess, blockPos, direction2, bl);
            return thickness == null ? null : this.getDefaultState().with(VERTICAL_DIRECTION, direction2).with(WATERLOGGED, worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER);
        }
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Thickness thickness = Thickness.TIP;
        VoxelShape voxelShape;
        if (state.get(VERTICAL_DIRECTION) == Direction.DOWN) {
            voxelShape = DOWN_TIP_SHAPE;
        } else {
            voxelShape = UP_TIP_SHAPE;
        }
        Vec3d vec3d = state.getModelOffset(world, pos);
        return voxelShape.offset(vec3d.x, (double)0.0F, vec3d.z);
    }

    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public float getMaxHorizontalModelOffset() {
        return 0.125F;
    }

    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.syncWorldEvent(1045, pos, 0);
        }

    }

    public DamageSource getDamageSource(Entity attacker) {
        return attacker.getDamageSources().fallingStalactite(attacker);
    }

    private static void spawnFallingBlock(BlockState state, ServerWorld world, BlockPos pos) {
        BlockPos.Mutable mutable = pos.mutableCopy();

        for(BlockState blockState = state; isPointingDown(blockState); blockState = world.getBlockState(mutable)) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, mutable, blockState);
            int i = Math.max(1 + pos.getY() - mutable.getY(), 6);
            float f = 1.0F * (float)i;
            fallingBlockEntity.setHurtEntities(f, 40);
            break;
        }

    }

    @Nullable
    private static Direction getDirectionToPlaceAt(WorldView world, BlockPos pos, Direction direction) {
        Direction direction2;
        if (canPlaceAtWithDirection(world, pos, direction)) {
            direction2 = direction;
        } else {
            if (!canPlaceAtWithDirection(world, pos, direction.getOpposite())) {
                return null;
            }

            direction2 = direction.getOpposite();
        }

        return direction2;
    }

    private static Thickness getThickness(WorldView world, BlockPos pos, Direction direction, boolean tryMerge) {
        return Thickness.TIP;
    }

    private static boolean canGrow(BlockState state, ServerWorld world, BlockPos pos) {
        Direction direction = (Direction)state.get(VERTICAL_DIRECTION);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.getFluidState().isEmpty()) {
            return false;
        } else {
            return blockState.isAir() ? true : isTip(blockState, direction.getOpposite());
        }
    }

    private static boolean canPlaceAtWithDirection(WorldView world, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSideSolidFullSquare(world, blockPos, direction) || isPointedDripstoneFacingDirection(blockState, direction);
    }

    private static boolean isTip(BlockState state, Direction direction) {
        return state.get(VERTICAL_DIRECTION) == direction;
    }

    private static boolean isPointingDown(BlockState state) {
        return isPointedDripstoneFacingDirection(state, Direction.DOWN);
    }

    private static boolean isPointingUp(BlockState state) {
        return isPointedDripstoneFacingDirection(state, Direction.UP);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    private static boolean isPointedDripstoneFacingDirection(BlockState state, Direction direction) {
        return state.isOf(Blocks.POINTED_DRIPSTONE) && state.get(VERTICAL_DIRECTION) == direction;
    }

    static {
        VERTICAL_DIRECTION = Properties.VERTICAL_DIRECTION;
        WATERLOGGED = Properties.WATERLOGGED;
        UP_TIP_SHAPE = Block.createCuboidShape((double)5.0F, (double)0.0F, (double)5.0F, (double)11.0F, (double)11.0F, (double)11.0F);
        DOWN_TIP_SHAPE = Block.createCuboidShape((double)5.0F, (double)5.0F, (double)5.0F, (double)11.0F, (double)16.0F, (double)11.0F);
    }
}
