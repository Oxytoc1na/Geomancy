package org.oxytocina.geomancy.sound;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public class ModBlockSoundGroups {
    public static final BlockSoundGroup STONE_WHISPERS = new BlockSoundGroup(
            1.0F,
            1.0F,
            ModSoundEvents.BLOCK_STONE_WHISPERS_BREAK,
            SoundEvents.BLOCK_STONE_STEP,
            ModSoundEvents.BLOCK_STONE_WHISPERS_PLACE,
            SoundEvents.BLOCK_STONE_HIT,
            SoundEvents.BLOCK_STONE_FALL
    );
    public static final BlockSoundGroup DEEPSLATE_WHISPERS = new BlockSoundGroup(
            1.0F,
            1.0F,
            ModSoundEvents.BLOCK_DEEPSLATE_WHISPERS_BREAK,
            SoundEvents.BLOCK_DEEPSLATE_STEP,
            ModSoundEvents.BLOCK_DEEPSLATE_WHISPERS_PLACE,
            SoundEvents.BLOCK_DEEPSLATE_HIT,
            SoundEvents.BLOCK_DEEPSLATE_FALL
    );
    public static final BlockSoundGroup METAL_WHISPERS = new BlockSoundGroup(
            1.0F,
            1.0F,
            ModSoundEvents.BLOCK_METAL_WHISPERS_BREAK,
            SoundEvents.BLOCK_METAL_STEP,
            ModSoundEvents.BLOCK_METAL_WHISPERS_PLACE,
            SoundEvents.BLOCK_METAL_HIT,
            SoundEvents.BLOCK_METAL_FALL
    );
}
