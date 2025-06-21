package org.oxytocina.geomancy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class Toolbox {

    public static Random random = new Random();

    public static Vec3d RandomItemDropVelocity(float speed){
        float angle = random.nextFloat()*2*(float)Math.PI;
        return new Vec3d(1,0.5,0).rotateY(angle).multiply(speed);
    }

    public static ItemEntity spawnItemStackAsEntity(World world, Vec3d pos, ItemStack itemStack) {
        return spawnItemStackAsEntity(world, pos, itemStack, new Vec3d(0,0,0));
    }

    public static ItemEntity spawnItemStackAsEntity(World world, Vec3d pos, ItemStack itemStack, Vec3d velocity) {
        return spawnItemStackAsEntity(world, pos, itemStack, velocity, true, null);
    }

    public static ItemEntity spawnItemStackAsEntity(World world, Vec3d pos, ItemStack itemStack, Vec3d velocity, boolean neverDespawn, @Nullable Entity owner) {

            ItemStack resultStack = itemStack.copy();
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
            return itemEntity;
    }
}
