package org.oxytocina.geomancy.world.biome.surface;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.world.biome.ModBiomes;

public class ModMaterialRules {
    private static final MaterialRules.MaterialRule DIRT = makeStateRule(Blocks.DIRT);
    private static final MaterialRules.MaterialRule GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final MaterialRules.MaterialRule OCTANGULITE_ORE = makeStateRule(ModBlocks.OCTANGULITE_ORE);
    private static final MaterialRules.MaterialRule DEEPLSATE_OCTANGULITE_ORE = makeStateRule(ModBlocks.DEEPSLATE_OCTANGULITE_ORE);

    public static MaterialRules.MaterialRule makeRules() {
        MaterialRules.MaterialCondition isAtOrAboveWaterLevel = MaterialRules.water(-1, 0);

        MaterialRules.MaterialRule grassSurface = MaterialRules.sequence(MaterialRules.condition(isAtOrAboveWaterLevel, GRASS_BLOCK), DIRT);

        return MaterialRules.sequence(
                MaterialRules.sequence(MaterialRules.condition(MaterialRules.biome(ModBiomes.SOUL_SWAMP),
                                MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING, OCTANGULITE_ORE))
                        ),

                // Default to a grass and dirt surface
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, grassSurface)
        );
    }

    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }
}
