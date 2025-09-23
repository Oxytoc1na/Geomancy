package org.oxytocina.geomancy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.MadnessUtil;
import org.oxytocina.geomancy.util.Toolbox;

public class OctanguliteExperienceDroppingBlock extends ExperienceDroppingBlock implements IMaddeningBlock {
    public final float maddeningSpeed;

    public OctanguliteExperienceDroppingBlock(Settings settings, IntProvider experience, float maddeningSpeed) {
        super(settings, experience);
        this.maddeningSpeed=maddeningSpeed;
    }


    @Override
    public float getAmbientMaddeningSpeed() {
        return maddeningSpeed;
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
