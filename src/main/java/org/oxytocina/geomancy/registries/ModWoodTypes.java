package org.oxytocina.geomancy.registries;

import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import org.oxytocina.geomancy.blocks.ModBlocks;

public class ModWoodTypes {

    public static final WoodType SOUL_OAK = register(new WoodType("soul_oak", ModBlockSetTypes.SOUL_OAK, BlockSoundGroup.WOOD, BlockSoundGroup.HANGING_SIGN, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN));

    private static WoodType register(WoodType type) {
        return type;
    }

    public static void registerStrippables(){
        StrippableBlockRegistry.register(ModBlocks.SOUL_OAK_LOG,ModBlocks.STRIPPED_SOUL_OAK_LOG);
        StrippableBlockRegistry.register(ModBlocks.SOUL_OAK_WOOD,ModBlocks.STRIPPED_SOUL_OAK_WOOD);
    }
}
