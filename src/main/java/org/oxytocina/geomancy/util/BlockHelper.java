package org.oxytocina.geomancy.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.function.Predicate;

public class BlockHelper {
    public static boolean breakBlockWithDrops(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Predicate<BlockState> filter) {
        ChunkPos chunkPos = world.getChunk(pos).getPos();
        if (world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
            BlockState blockstate = world.getBlockState(pos);
            if (!world.isClient && !blockstate.isAir() && blockstate.calcBlockBreakingDelta(player, world, pos) > 0 && filter.test(blockstate)) {
                ItemStack save = player.getMainHandStack();
                player.setStackInHand(Hand.MAIN_HAND, stack);
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new WorldEventS2CPacket(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(blockstate), false));
                ((ServerPlayerEntity) player).interactionManager.tryBreakBlock(pos);
                player.setStackInHand(Hand.MAIN_HAND, save);
                return true;
            }
        }
        return false;
    }
}
