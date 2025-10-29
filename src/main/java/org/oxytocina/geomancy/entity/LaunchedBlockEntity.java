package org.oxytocina.geomancy.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.oxytocina.geomancy.client.entity.LaunchedBlockEntityRenderer;

public class LaunchedBlockEntity extends FallingBlockEntity {
    public LaunchedBlockEntity(EntityType<? extends LaunchedBlockEntity> entityType, World world) {
        super(entityType, world);
    }

    private LaunchedBlockEntity(World world, double x, double y, double z, BlockState block) {
        this(ModEntityTypes.LAUNCHED_BLOCK, world);
        this.block = block;
        this.intersectionChecked = true;
        this.setPosition(x, y, z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setFallingBlockPos(this.getBlockPos());
    }
    public static LaunchedBlockEntity spawnFromBlock(World world, BlockPos pos, BlockState state) {
        LaunchedBlockEntity launchedBlockEntity = new LaunchedBlockEntity(
                world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, state.contains(Properties.WATERLOGGED) ? state.with(Properties.WATERLOGGED, false) : state
        );
        world.setBlockState(pos, state.getFluidState().getBlockState(), Block.NOTIFY_ALL);
        world.spawnEntity(launchedBlockEntity);
        return launchedBlockEntity;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }
}
