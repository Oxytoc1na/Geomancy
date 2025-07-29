package org.oxytocina.geomancy.registries;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public class ModWoodTypes {

    public static final WoodType SOUL_OAK = register(new WoodType("soul_oak", ModBlockSetTypes.SOUL_OAK, BlockSoundGroup.WOOD, BlockSoundGroup.HANGING_SIGN, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN));

    private static WoodType register(WoodType type) {
        return type;
    }
}
