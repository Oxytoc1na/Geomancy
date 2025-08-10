package org.oxytocina.geomancy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.Toolbox;

public class MaddeningWallBlock extends WallBlock implements IMaddeningBlock {
    public final float maddeningSpeed;

    public MaddeningWallBlock(Settings settings, float maddeningSpeed) {
        super(settings);
        this.maddeningSpeed = maddeningSpeed;
    }

    @Override
    public float getAmbientMaddeningSpeed() {
        return maddeningSpeed;
    }
}
