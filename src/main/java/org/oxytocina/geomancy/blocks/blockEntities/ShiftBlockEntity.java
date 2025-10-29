package org.oxytocina.geomancy.blocks.blockEntities;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.ModBlocks;

/**
 * A piston block entity represents the block being pushed by a piston.
 */
public class ShiftBlockEntity extends BlockEntity {
	private BlockState pushedBlock = Blocks.AIR.getDefaultState();
	private Direction facing;
	private static final ThreadLocal<Direction> entityMovementDirection = ThreadLocal.withInitial(() -> null);
	private float progress;
	private float lastProgress;
	private long savedWorldTime;
	private int clientDestroyTimer;

	public ShiftBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SHIFT, pos, state);
	}

	public ShiftBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing) {
		this(pos, state);
		this.pushedBlock = pushedBlock;
		this.facing = facing;
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}

	public Direction getFacing() {
		return this.facing;
	}

	public float getProgress(float tickDelta) {
		if (tickDelta > 1.0F) {
			tickDelta = 1.0F;
		}

		return MathHelper.lerp(tickDelta, this.lastProgress, this.progress);
	}

	public float getRenderOffsetX(float tickDelta) {
		return this.facing.getOffsetX() * this.getAmountExtended(movementCurve(this.getProgress(tickDelta)));
	}

	public float getRenderOffsetY(float tickDelta) {
		return this.facing.getOffsetY() * this.getAmountExtended(movementCurve(this.getProgress(tickDelta)));
	}

	public float getRenderOffsetZ(float tickDelta) {
		return this.facing.getOffsetZ() * this.getAmountExtended(movementCurve(this.getProgress(tickDelta)));
	}

	public static float movementCurve(float progress){
		return (float)((1-Math.cos(progress*Math.PI))/2f);
	}

	private float getAmountExtended(float progress) {
		return progress - 1.0F;
	}

	private BlockState getHeadBlockState() {
		return this.pushedBlock;
	}

	private static void pushEntities(World world, BlockPos pos, float progress, ShiftBlockEntity blockEntity) {
		Direction direction = blockEntity.getMovementDirection();
		double deltaProgress = progress - blockEntity.progress;
		VoxelShape voxelShape = blockEntity.getHeadBlockState().getCollisionShape(world, pos);
		if (!voxelShape.isEmpty()) {
			Box box = offsetHeadBox(pos, voxelShape.getBoundingBox(), blockEntity);
			List<Entity> entities = world.getOtherEntities(null, Boxes.stretch(box, direction, deltaProgress).union(box));
			if (!entities.isEmpty()) {
				List<Box> list2 = voxelShape.getBoundingBoxes();
				boolean pushedIsSlime = blockEntity.pushedBlock.isOf(Blocks.SLIME_BLOCK);
				Iterator var12 = entities.iterator();

				while (true) {
					Entity entity;
					while (true) {
						if (!var12.hasNext()) {
							return;
						}

						entity = (Entity)var12.next();
						if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
							if (!pushedIsSlime) {
								break;
							}

							if (!(entity instanceof ServerPlayerEntity)) {
								Vec3d vec3d = entity.getVelocity();
								double e = vec3d.x;
								double g = vec3d.y;
								double h = vec3d.z;
								switch (direction.getAxis()) {
									case X:
										e = direction.getOffsetX();
										break;
									case Y:
										g = direction.getOffsetY();
										break;
									case Z:
										h = direction.getOffsetZ();
								}

								entity.setVelocity(e, g, h);
								break;
							}
						}
					}

					double i = 0.0;

					for (Box box2 : list2) {
						Box box3 = Boxes.stretch(offsetHeadBox(pos, box2, blockEntity), direction, deltaProgress);
						Box box4 = entity.getBoundingBox();
						if (box3.intersects(box4)) {
							i = Math.max(i, getIntersectionSize(box3, direction, box4));
							if (i >= deltaProgress) {
								break;
							}
						}
					}

					if (!(i <= 0.0)) {
						i = Math.min(i, deltaProgress) + 0.01;
						moveEntity(direction, entity, i, direction);
						//push(pos, entity, direction, d);
					}
				}
			}
		}
	}

	public static final float Y_MOVEMENT_SPEED_MULTIPLIER = 1.3f;
	private static void moveEntity(Direction direction, Entity entity, double distance, Direction movementDirection) {
		entityMovementDirection.set(direction);
		var vel = entity.getVelocity();
		entity.setVelocity(vel.x,Math.max(vel.y,0),vel.z);
		entity.move(
			MovementType.PISTON,
			new Vec3d(distance * movementDirection.getOffsetX(), distance * movementDirection.getOffsetY() * Y_MOVEMENT_SPEED_MULTIPLIER, distance * movementDirection.getOffsetZ())
		);
		entityMovementDirection.set(null);
	}

	private static void moveEntitiesInHoneyBlock(World world, BlockPos pos, float f, ShiftBlockEntity blockEntity) {
		if (blockEntity.isPushingHoneyBlock()) {
			Direction direction = blockEntity.getMovementDirection();
			if (direction.getAxis().isHorizontal()) {
				double d = blockEntity.pushedBlock.getCollisionShape(world, pos).getMax(Direction.Axis.Y);
				Box box = offsetHeadBox(pos, new Box(0.0, d, 0.0, 1.0, 1.5000010000000001, 1.0), blockEntity);
				double e = f - blockEntity.progress;

				for (Entity entity : world.getOtherEntities((Entity)null, box, entityx -> canMoveEntity(box, entityx, pos))) {
					moveEntity(direction, entity, e, direction);
				}
			}
		}
	}

	private static boolean canMoveEntity(Box box, Entity entity, BlockPos pos) {
		return entity.getPistonBehavior() == PistonBehavior.NORMAL
			&& entity.isOnGround()
			&& (entity.isSupportedBy(pos) || entity.getX() >= box.minX && entity.getX() <= box.maxX && entity.getZ() >= box.minZ && entity.getZ() <= box.maxZ);
	}

	private boolean isPushingHoneyBlock() {
		return this.pushedBlock.isOf(Blocks.HONEY_BLOCK);
	}

	public Direction getMovementDirection() {
		return this.facing;
	}

	private static double getIntersectionSize(Box box, Direction direction, Box box2) {
		switch (direction) {
			case EAST:
				return box.maxX - box2.minX;
			case WEST:
				return box2.maxX - box.minX;
			case UP:
			default:
				return box.maxY - box2.minY;
			case DOWN:
				return box2.maxY - box.minY;
			case SOUTH:
				return box.maxZ - box2.minZ;
			case NORTH:
				return box2.maxZ - box.minZ;
		}
	}

	private static Box offsetHeadBox(BlockPos pos, Box box, ShiftBlockEntity blockEntity) {
		double d = blockEntity.getAmountExtended(blockEntity.progress);
		return box.offset(
			pos.getX() + d * blockEntity.facing.getOffsetX(), pos.getY() + d * blockEntity.facing.getOffsetY(), pos.getZ() + d * blockEntity.facing.getOffsetZ()
		);
	}

	private static void push(BlockPos pos, Entity entity, Direction direction, double amount) {
		Box box = entity.getBoundingBox();
		Box box2 = VoxelShapes.fullCube().getBoundingBox().offset(pos);
		if (box.intersects(box2)) {
			Direction direction2 = direction.getOpposite();
			double d = getIntersectionSize(box2, direction2, box) + 0.01;
			double e = getIntersectionSize(box2, direction2, box.intersection(box2)) + 0.01;
			if (Math.abs(d - e) < 0.01) {
				d = Math.min(d, amount) + 0.01;
				moveEntity(direction, entity, d, direction2);
			}
		}
	}

	public BlockState getPushedBlock() {
		return this.pushedBlock;
	}

	public void finish() {
		if (this.world != null && (this.lastProgress < 1.0F || this.world.isClient)) {
			this.progress = 1.0F;
			this.lastProgress = this.progress;
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.world.getBlockState(this.pos).isOf(ModBlocks.SHIFT)) {
				BlockState blockState;
				blockState = Block.postProcessState(this.pushedBlock, this.world, this.pos);

				this.world.setBlockState(this.pos, blockState, Block.NOTIFY_ALL);
				this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
			}
		}
	}

	public static final float PROGRESS_PER_TICK = 0.3f;
	public static void tick(World world, BlockPos pos, BlockState state, ShiftBlockEntity blockEntity) {
		blockEntity.savedWorldTime = world.getTime();
		blockEntity.lastProgress = blockEntity.progress;
		if (blockEntity.lastProgress >= 1.0F) {
			if (world.isClient && blockEntity.clientDestroyTimer < 5) {
				blockEntity.clientDestroyTimer++;
			} else {
				world.removeBlockEntity(pos);
				blockEntity.markRemoved();
				if (world.getBlockState(pos).isOf(ModBlocks.SHIFT)) {
					BlockState blockState = Block.postProcessState(blockEntity.pushedBlock, world, pos);
					if (blockState.isAir()) {
						world.setBlockState(pos, blockEntity.pushedBlock, Block.NO_REDRAW | Block.FORCE_STATE | Block.MOVED);
						Block.replace(blockEntity.pushedBlock, blockState, world, pos, 3);
					} else {
						if (blockState.contains(Properties.WATERLOGGED) && (Boolean)blockState.get(Properties.WATERLOGGED)) {
							blockState = blockState.with(Properties.WATERLOGGED, false);
						}

						world.setBlockState(pos, blockState, Block.NOTIFY_ALL | Block.MOVED);
						world.updateNeighbor(pos, blockState.getBlock(), pos);
					}
				}
			}
		} else {
			float newProgress = blockEntity.progress + PROGRESS_PER_TICK;
			pushEntities(world, pos, newProgress, blockEntity);
			moveEntitiesInHoneyBlock(world, pos, newProgress, blockEntity);
			blockEntity.progress = newProgress;
			if (blockEntity.progress >= 1.0F) {
				blockEntity.progress = 1.0F;
			}
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		RegistryEntryLookup<Block> registryEntryLookup = (RegistryEntryLookup<Block>)(this.world != null
			? this.world.createCommandRegistryWrapper(RegistryKeys.BLOCK)
			: Registries.BLOCK.getReadOnlyWrapper());
		this.pushedBlock = NbtHelper.toBlockState(registryEntryLookup, nbt.getCompound("blockState"));
		this.facing = Direction.byId(nbt.getInt("facing"));
		this.progress = nbt.getFloat("progress");
		this.lastProgress = this.progress;
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
		nbt.putInt("facing", this.facing.getId());
		nbt.putFloat("progress", this.lastProgress);
	}

	public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
		VoxelShape voxelShape;
		voxelShape = VoxelShapes.empty();

		Direction direction = (Direction)entityMovementDirection.get();
		if (this.progress < 1.0 && direction == this.getMovementDirection()) {
			return voxelShape;
		} else {
			float f = this.getAmountExtended(this.progress);
			double d = this.facing.getOffsetX() * f;
			double e = this.facing.getOffsetY() * f;
			double g = this.facing.getOffsetZ() * f;
			return VoxelShapes.union(voxelShape, this.pushedBlock.getCollisionShape(world, pos).offset(d, e, g));
		}
	}

	public long getSavedWorldTime() {
		return this.savedWorldTime;
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		if (world.createCommandRegistryWrapper(RegistryKeys.BLOCK).getOptional(this.pushedBlock.getBlock().getRegistryEntry().registryKey()).isEmpty()) {
			this.pushedBlock = Blocks.AIR.getDefaultState();
		}
	}
}
