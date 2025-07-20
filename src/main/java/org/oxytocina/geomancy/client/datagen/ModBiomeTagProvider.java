package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.biome.v1.TheEndBiomes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSources;
import net.minecraft.world.biome.BuiltinBiomes;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.ModItems;

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
                        BiomeKeys.SWAMP,
                        BiomeKeys.MANGROVE_SWAMP,
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

        getOrCreateTagBuilder(VPB_LOWER)
                .forceAddTag(BiomeTags.IS_END)
                .setReplace(true);

        getOrCreateTagBuilder(VPB_LOW)
                .forceAddTag(BiomeTags.IS_MOUNTAIN)
                .add(BiomeKeys.DESERT)
                .setReplace(true);

        // default

        getOrCreateTagBuilder(VPB_HIGH)
                .forceAddTag(BiomeTags.IS_OCEAN)
                .setReplace(true);

        getOrCreateTagBuilder(VPB_HIGHEST)
                .forceAddTag(BiomeTags.IS_NETHER)
                .setReplace(true);

        getOrCreateTagBuilder(VPB_INSANE)
                .add(BiomeKeys.SOUL_SAND_VALLEY)
                .setReplace(true);

    }
}
