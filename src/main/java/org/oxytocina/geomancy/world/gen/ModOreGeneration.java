package org.oxytocina.geomancy.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.registries.ModBiomeTags;
import org.oxytocina.geomancy.world.ModPlacedFeatures;
import net.minecraft.world.gen.GenerationStep;

public class ModOreGeneration {

    // ores
    public static final RegistryKey<PlacedFeature> MITHRIL_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"mithril_ore"));
    public static final RegistryKey<PlacedFeature> MOLYBDENUM_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"molybdenum_ore"));
    public static final RegistryKey<PlacedFeature> TITANIUM_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"titanium_ore"));
    public static final RegistryKey<PlacedFeature> LEAD_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"lead_ore"));
    public static final RegistryKey<PlacedFeature> OCTANGULITE_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"octangulite_ore"));

    public static final RegistryKey<PlacedFeature> PERIDOT_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"peridot_ore"));
    public static final RegistryKey<PlacedFeature> AXINITE_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"axinite_ore"));
    public static final RegistryKey<PlacedFeature> ORTHOCLASE_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"orthoclase_ore"));
    public static final RegistryKey<PlacedFeature> TOURMALINE_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"tourmaline_ore"));



    public static void generateOres() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, MITHRIL_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, MOLYBDENUM_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, TITANIUM_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, LEAD_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, OCTANGULITE_ORE_PLACED_KEY);

        BiomeModifications.addFeature(BiomeSelectors.tag(ModBiomeTags.HAS_PERIDOT_ORE), GenerationStep.Feature.UNDERGROUND_ORES, PERIDOT_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(ModBiomeTags.HAS_AXINITE_ORE), GenerationStep.Feature.UNDERGROUND_ORES, AXINITE_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(ModBiomeTags.HAS_ORTHOCLASE_ORE), GenerationStep.Feature.UNDERGROUND_ORES, ORTHOCLASE_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.tag(ModBiomeTags.HAS_TOURMALINE_ORE), GenerationStep.Feature.UNDERGROUND_ORES, TOURMALINE_ORE_PLACED_KEY);

    }
}
