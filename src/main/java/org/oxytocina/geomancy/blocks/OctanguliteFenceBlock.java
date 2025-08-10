package org.oxytocina.geomancy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.Toolbox;

public class OctanguliteFenceBlock extends FenceBlock implements IMaddeningBlock {
    public final float maddeningSpeed;

    public OctanguliteFenceBlock(Settings settings, float maddeningSpeed) {
        super(settings);
        this.maddeningSpeed = maddeningSpeed;
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
            Toolbox.playSound(ModSoundEvents.WHISPERS,world,pos, SoundCategory.AMBIENT,0.5f+random.nextFloat()*0.5f,0.8f+random.nextFloat()*0.4f);
    }
}
