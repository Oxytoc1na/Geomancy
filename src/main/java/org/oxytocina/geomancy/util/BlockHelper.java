package org.oxytocina.geomancy.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.function.Predicate;

public class BlockHelper {
    public static boolean breakBlockWithDrops(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Predicate<BlockState> filter) {
        if(world.isClient) return false;
        ChunkPos chunkPos = world.getChunk(pos).getPos();
        if (!world.isChunkLoaded(chunkPos.x, chunkPos.z)) return false;
        BlockState blockstate = world.getBlockState(pos);
        if(blockstate.isAir()) return false;

        if(player==null){
            return tryBreakBlock((ServerWorld) world,pos,stack);
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
            // TODO : casting blocks
            tryBreakBlock((ServerWorld) world,pos,stack);
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

    public static boolean tryBreakBlock(ServerWorld world, BlockPos pos, ItemStack tool) {
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
}
