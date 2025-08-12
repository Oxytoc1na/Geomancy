package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.oxytocina.geomancy.world.biome.ModBiomes;

import java.util.concurrent.CompletableFuture;

import static org.oxytocina.geomancy.registries.ModBiomeTags.*;

public class ModBiomeTagProvider extends FabricTagProvider<Biome> {
    public ModBiomeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BIOME, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(HAS_DWARVEN_REMNANTS)
                .forceAddTag(BiomeTags.IS_MOUNTAIN)
                .setReplace(true);

        getOrCreateTagBuilder(HAS_OCTANGULA)
                .add(
                        BiomeKeys.BAMBOO_JUNGLE,
                        BiomeKeys.BIRCH_FOREST,
                        BiomeKeys.CHERRY_GROVE,
                        BiomeKeys.DARK_FOREST,
                        BiomeKeys.FLOWER_FOREST,
                        BiomeKeys.FOREST,
                        BiomeKeys.GROVE,
                        BiomeKeys.JUNGLE,
                        BiomeKeys.MEADOW,
                        BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
                        BiomeKeys.OLD_GROWTH_PINE_TAIGA,
                        BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,
                        BiomeKeys.PLAINS,
                        BiomeKeys.SPARSE_JUNGLE,
                        BiomeKeys.SUNFLOWER_PLAINS
                        )
                .setReplace(true);

        getOrCreateTagBuilder(HAS_DIGSITE)
                .add(
                        ModBiomes.SOUL_SWAMP,
                        BiomeKeys.SWAMP,
                        BiomeKeys.MANGROVE_SWAMP
                )
                .setReplace(true);

        getOrCreateTagBuilder(HAS_PERIDOT_ORE)
                .forceAddTag(BiomeTags.IS_FOREST)
                .setReplace(true);

        getOrCreateTagBuilder(HAS_AXINITE_ORE)
                .forceAddTag(BiomeTags.IS_FOREST)
                .setReplace(true);

        getOrCreateTagBuilder(HAS_ORTHOCLASE_ORE)
                .add(
                        ModBiomes.SOUL_SWAMP,
                        BiomeKeys.SWAMP,
                        BiomeKeys.MANGROVE_SWAMP
                )
                .setReplace(true);

        getOrCreateTagBuilder(HAS_TOURMALINE_ORE)
                .forceAddTag(BiomeTags.IS_BADLANDS)
                .add(
                        BiomeKeys.PLAINS,
                        BiomeKeys.SUNFLOWER_PLAINS,
                        BiomeKeys.SNOWY_PLAINS
                )
                .setReplace(true);

        getOrCreateTagBuilder(VPB_NONE)
                .add(ModBiomes.NULL)
                .setReplace(true);

        getOrCreateTagBuilder(VPB_LOWEST)
                .forceAddTag(BiomeTags.IS_END)
                .setReplace(true);

        getOrCreateTagBuilder(VPB_LOWER)
                .add(BiomeKeys.DESERT)
                .setReplace(true);

        getOrCreateTagBuilder(VPB_LOW)
                .forceAddTag(BiomeTags.IS_MOUNTAIN)
                .forceAddTag(BiomeTags.IS_BADLANDS)
                .setReplace(true);

        // default

        getOrCreateTagBuilder(VPB_HIGH)
                .forceAddTag(BiomeTags.IS_OCEAN)
                .setReplace(true);

        getOrCreateTagBuilder(VPB_HIGHER)
                .forceAddTag(BiomeTags.IS_FOREST)
                .setReplace(true);

        getOrCreateTagBuilder(VPB_HIGHEST)
                .forceAddTag(BiomeTags.IS_NETHER)
                .add(BiomeKeys.SWAMP)
                .add(BiomeKeys.MANGROVE_SWAMP)
                .setReplace(true);

        getOrCreateTagBuilder(VPB_INSANE)
                .add(BiomeKeys.SOUL_SAND_VALLEY)
                .add(ModBiomes.SOUL_SWAMP)
                .setReplace(true);

    }
}
