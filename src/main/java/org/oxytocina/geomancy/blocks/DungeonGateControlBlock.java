package org.oxytocina.geomancy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.util.Toolbox;

public class DungeonGateControlBlock extends Block {
    public static final BooleanProperty POWERED;

    public DungeonGateControlBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
    }


    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean oldPowered = state.get(POWERED);
            boolean newPowered = world.isReceivingRedstonePower(pos);
            boolean changed = oldPowered!=newPowered;
            if(changed)
            {
                state = state.with(POWERED,newPowered);
                world.setBlockState(pos, state, 2);

                // propagate vanish
                for(var dir : DIRECTIONS){
                    BlockPos newP = pos.add(dir.getVector());
                    BlockState newState = world.getBlockState(newP);
                    if(newState.getBlock() instanceof DungeonGateBlock dgb){
                        dgb.propagateDesireVanished(newState,world,newP,pos,newPowered);
                        world.scheduleBlockTick(newP, dgb, 2);
                    }
                }
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    static {
        POWERED = Properties.POWERED;
    }
}
