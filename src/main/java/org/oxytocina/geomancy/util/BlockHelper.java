package org.oxytocina.geomancy.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.blockEntities.ShiftBlockEntity;
import org.oxytocina.geomancy.networking.packet.S2C.ShiftBlockS2CPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BlockHelper {

    public static boolean breakBlock(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Predicate<BlockState> filter, boolean drop) {
        if(world.isClient) return false;
        ChunkPos chunkPos = world.getChunk(pos).getPos();
        if (!world.isChunkLoaded(chunkPos.x, chunkPos.z)) return false;
        BlockState blockstate = world.getBlockState(pos);
        if(blockstate.isAir()) return false;

        if(player==null){
            return tryBreakBlock((ServerWorld) world,pos,stack,drop);
        }

        if(!drop){
            return tryBreakBlockAsPlayer((ServerPlayerEntity) player,pos,stack,drop);
        }

        if (blockstate.calcBlockBreakingDelta(player, world, pos) > 0 && filter.test(blockstate)) {
            ItemStack save = player.getMainHandStack();
            player.setStackInHand(Hand.MAIN_HAND, stack);
            ((ServerPlayerEntity) player).networkHandler.sendPacket(new WorldEventS2CPacket(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(blockstate), false));
            ((ServerPlayerEntity) player).interactionManager.tryBreakBlock(pos);
            player.setStackInHand(Hand.MAIN_HAND, save);
            return true;
        }
        return false;
    }

    public static boolean replaceBlockWithDrops(PlayerEntity player, ItemStack stack, World world, BlockPos pos, BlockState newState, Predicate<BlockState> filter) {
        if(player==null){
            tryBreakBlock((ServerWorld) world,pos,stack,true);
            world.setBlockState(pos,newState);
            return false;
        }

        ChunkPos chunkPos = world.getChunk(pos).getPos();
        if (world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
            BlockState blockstate = world.getBlockState(pos);
            if (!world.isClient && !blockstate.isAir() && blockstate.calcBlockBreakingDelta(player, world, pos) > 0 && filter.test(blockstate)) {
                ItemStack save = player.getMainHandStack();
                player.setStackInHand(Hand.MAIN_HAND, stack);
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new WorldEventS2CPacket(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(blockstate), false));
                ((ServerPlayerEntity) player).interactionManager.tryBreakBlock(pos);
                world.setBlockState(pos,newState);
                player.setStackInHand(Hand.MAIN_HAND, save);
                return true;
            }
        }
        return false;
    }

    public static boolean replaceBlock(World world, BlockPos pos, BlockState newState){
        ChunkPos chunkPos = world.getChunk(pos).getPos();
        if (world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
            if (!world.isClient) {
                world.setBlockState(pos,newState);
                return true;
            }
        }
        return false;
    }

    public static boolean tryBreakBlockAsPlayer(ServerPlayerEntity player,BlockPos pos, ItemStack tool, boolean drop){
        var world = player.getWorld();
        BlockState blockState = world.getBlockState(pos);
        if (!tool.getItem().canMine(blockState, world, pos, player)) {
            return false;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            Block block = blockState.getBlock();
            if (block instanceof OperatorBlock && !player.isCreativeLevelTwoOp()) {
                world.updateListeners(pos, blockState, blockState, 3);
                return false;
            } else if (player.isBlockBreakingRestricted(world, pos, player.interactionManager.getGameMode())) {
                return false;
            }
        }

        boolean broke = tryBreakBlock((ServerWorld) world,pos,tool,drop);

        if(broke){
            // TODO: player statistics
        }

        return broke;
    }

    public static boolean tryBreakBlock(ServerWorld world, BlockPos pos, ItemStack tool, boolean drop) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block instanceof OperatorBlock) {
            world.updateListeners(pos, blockState, blockState, 3);
            return false;
        } else {
            block.spawnBreakParticles(world, null, pos, blockState);
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(null, blockState));
            boolean removed = world.removeBlock(pos, false);
            if (removed) {
                block.onBroken(world, pos, blockState);
                BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(pos) : null;
                if(drop)
                    Block.dropStacks(blockState, world, pos, blockEntity, null, tool);
            }

            //tool.postMine(world, blockState, pos, this.player);
            //if (removed && canHarvest(blockState,tool)) {
            //    block.afterBreak(world, this.player, pos, blockState, blockEntity, tool.copy());
            //}

            return true;
        }
    }

    public static boolean canHarvest(BlockState state, ItemStack tool){
        return !state.isToolRequired() || tool.isSuitableFor(state);
    }

    public static BlockHitResult raycastBlock(World world, Vec3d from, Vec3d to ) {
        return world.raycast(new RaycastContext(from,to, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,
                // i hate having to put this pointless entity here just to have it work
                // it never even gets used
                // whats the point
                // i hate raycasting
                new MarkerEntity(EntityType.MARKER,world)));

        //return (BlockHitResult)BlockView.raycast(context.getStart(), context.getEnd(), context, (innerContext, pos) -> {
        //    BlockState blockState = view.getBlockState(pos);
        //    Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
        //    return innerContext.getStatePredicate().test(blockState) ? new BlockHitResult(pos.toCenterPos(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), pos, false) : null;
        //}, (innerContext) -> {
        //    Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
        //    return BlockHitResult.createMissed(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()));
        //});
    }

    public static List<BlockHitResult> raycastBlocksInPath(World world, Vec3d from, Vec3d to) {
        var context = new BlockStateRaycastContext(from,to,b->false);
        List<BlockHitResult> res = new ArrayList<>();
        BlockView.raycast(context.getStart(), context.getEnd(), context, (innerContext, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
            res.add(new BlockHitResult(pos.toCenterPos(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), pos.mutableCopy(), false));
            return innerContext.getStatePredicate().test(blockState) ? new BlockHitResult(pos.toCenterPos(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), pos, false) : null;
        }, (innerContext) -> {
            Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
            return BlockHitResult.createMissed(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()));
        });

        return res;
    }

    public static boolean withinCube(Vec3i distance, int pedestalRange) {
        return Math.max(Math.max(
                Math.abs(distance.getX()),
                Math.abs(distance.getY())),
                Math.abs(distance.getZ())) <= pedestalRange;
    }

    public static List<BlockPos> getMovedBlockPositions(World world, BlockPos fromBlockPos, Direction direction){
        ShiftHandler shiftHandler = new ShiftHandler(world, fromBlockPos, direction);
        if (!shiftHandler.calculatePush())
            return List.of();
        return shiftHandler.getMovedBlocks();
    }

    public static boolean push(World world, BlockPos fromBlockPos, Direction direction) {
        BlockPos toBlockPos = fromBlockPos.offset(direction);

        ShiftHandler shiftHandler = new ShiftHandler(world, fromBlockPos, direction);
        // check if theres any blocks being pushed
        if (!shiftHandler.calculatePush()) {
            return false;
        } else {
            Map<BlockPos, BlockState> movedBlockMap = Maps.<BlockPos, BlockState>newHashMap();
            List<BlockPos> movedBlockPositions = shiftHandler.getMovedBlocks();
            List<BlockState> movedBlockStates = Lists.<BlockState>newArrayList();

            // fetch moved block states
            for (int i = 0; i < movedBlockPositions.size(); i++) {
                BlockPos movedBlockPosition = (BlockPos)movedBlockPositions.get(i);
                BlockState movedBlockState = world.getBlockState(movedBlockPosition);
                movedBlockStates.add(movedBlockState);
                movedBlockMap.put(movedBlockPosition, movedBlockState);
            }

            List<BlockPos> brokenBlockPositions = shiftHandler.getBrokenBlocks();
            BlockState[] influencedBlockStates = new BlockState[movedBlockPositions.size() + brokenBlockPositions.size()];
            int influencedBlockIndex = 0;

            // break blocks
            for (int k = brokenBlockPositions.size() - 1; k >= 0; k--) {
                BlockPos brokenBlockPosition = (BlockPos)brokenBlockPositions.get(k);
                BlockState brokenBlockState = world.getBlockState(brokenBlockPosition);
                BlockEntity blockEntity = brokenBlockState.hasBlockEntity() ? world.getBlockEntity(brokenBlockPosition) : null;
                Block.dropStacks(brokenBlockState, world, brokenBlockPosition, blockEntity);
                world.setBlockState(brokenBlockPosition, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                world.emitGameEvent(GameEvent.BLOCK_DESTROY, brokenBlockPosition, GameEvent.Emitter.of(brokenBlockState));
                if (!brokenBlockState.isIn(BlockTags.FIRE)) {
                    world.addBlockBreakParticles(brokenBlockPosition, brokenBlockState);
                }
                influencedBlockStates[influencedBlockIndex++] = brokenBlockState;
            }

            BlockState airState = Blocks.AIR.getDefaultState();

            // move blocks
            for (int k = movedBlockPositions.size() - 1; k >= 0; k--) {
                BlockPos movedFromBlockPos = (BlockPos)movedBlockPositions.get(k);
                BlockState movedBlockState = world.getBlockState(movedFromBlockPos);
                var movedToBlockPos = movedFromBlockPos.offset(direction);
                // prevent the next step from removing the block entity we're creating
                movedBlockMap.remove(movedToBlockPos);
                BlockState replacementState = ModBlocks.SHIFT.getDefaultState().with(Properties.FACING, direction);
                world.setBlockState(movedToBlockPos, replacementState, Block.NO_REDRAW | Block.MOVED);
                world.addBlockEntity(new ShiftBlockEntity(movedToBlockPos, replacementState, (BlockState)movedBlockStates.get(k), direction));
                if(!world.isClient) ShiftBlockS2CPacket.send((ServerWorld) world,movedToBlockPos,direction,replacementState,(BlockState)movedBlockStates.get(k));
                influencedBlockStates[influencedBlockIndex++] = movedBlockState;
            }

            // replace moved positions with air
            for (BlockPos movedBlockPosition : movedBlockMap.keySet()) {
                world.setBlockState(movedBlockPosition, airState, Block.NOTIFY_ALL | Block.FORCE_STATE | Block.MOVED);
            }

            for (Map.Entry<BlockPos, BlockState> entry : movedBlockMap.entrySet()) {
                BlockPos movedBlockPos = (BlockPos)entry.getKey();
                BlockState movedBlockState = (BlockState)entry.getValue();
                movedBlockState.prepare(world, movedBlockPos, 2);
                airState.updateNeighbors(world, movedBlockPos, Block.NOTIFY_LISTENERS);
                airState.prepare(world, movedBlockPos, 2);
            }

            influencedBlockIndex = 0;

            // update neighbors of broken blocks
            for (int l = brokenBlockPositions.size() - 1; l >= 0; l--) {
                BlockState influencedBlockState = influencedBlockStates[influencedBlockIndex++];
                BlockPos brokenBlockPos = (BlockPos)brokenBlockPositions.get(l);
                influencedBlockState.prepare(world, brokenBlockPos, 2);
                world.updateNeighborsAlways(brokenBlockPos, influencedBlockState.getBlock());
            }

            // update neighbors of moved blocks
            for (int l = movedBlockPositions.size() - 1; l >= 0; l--) {
                world.updateNeighborsAlways((BlockPos)movedBlockPositions.get(l), influencedBlockStates[influencedBlockIndex++].getBlock());
            }

            return true;
        }
    }
}
