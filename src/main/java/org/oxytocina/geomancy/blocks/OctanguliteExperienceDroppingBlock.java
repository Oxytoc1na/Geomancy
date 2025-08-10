package org.oxytocina.geomancy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import org.oxytocina.geomancy.sound.ModSoundEvents;
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

    static final float whisperChance = 0.2f;
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(random.nextFloat()<whisperChance)
            Toolbox.playSound(ModSoundEvents.WHISPERS,world,pos, SoundCategory.AMBIENT,0.5f+random.nextFloat()*0.5f,0.8f+random.nextFloat()*0.4f);
    }
}
