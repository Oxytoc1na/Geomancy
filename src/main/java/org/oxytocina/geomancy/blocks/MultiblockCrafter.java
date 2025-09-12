package org.oxytocina.geomancy.blocks;


import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public interface MultiblockCrafter {

    static void spawnItemStackAsEntitySplitViaMaxCount(World world, Vec3d pos, ItemStack itemStack, int amount, Vec3d velocity, boolean neverDespawn, @Nullable Entity owner) {
        while (amount > 0) {
            int currentAmount = Math.min(amount, itemStack.getMaxCount());

            ItemStack resultStack = itemStack.copy();
            resultStack.setCount(currentAmount);
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), resultStack);
            itemEntity.setVelocity(velocity);
            itemEntity.setPickupDelay(20);
            if (neverDespawn) {
                itemEntity.setNeverDespawn();
            }
            if (owner != null) {
                itemEntity.setOwner(owner.getUuid());
            }
            world.spawnEntity(itemEntity);

            amount -= currentAmount;
        }
    }

    static void spawnOutputAsItemEntity(World world, BlockPos pos, ItemStack outputItemStack) {
        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, outputItemStack);
        itemEntity.addVelocity(0, 0.1, 0);
        world.spawnEntity(itemEntity);
    }

}