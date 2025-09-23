package org.oxytocina.geomancy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.MadnessUtil;
import org.oxytocina.geomancy.util.Toolbox;

public class OctanguliteStairsBlock extends MaddeningStairsBlock {

    public OctanguliteStairsBlock(BlockState baseBlockState, Settings settings, float maddeningSpeed) {
        super(baseBlockState, settings,maddeningSpeed);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(random.nextFloat()<OctanguliteBlock.whisperChance)
            MadnessUtil.whisperAt(world,pos);
    }
}
