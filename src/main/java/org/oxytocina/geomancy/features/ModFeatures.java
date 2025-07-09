package org.oxytocina.geomancy.features;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.oxytocina.geomancy.Geomancy;

public class ModFeatures {

    // ores
    public static final RegistryKey<PlacedFeature> MITHRIL_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"mithril_ore"));
    public static final RegistryKey<PlacedFeature> MOLYBDENUM_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"molybdenum_ore"));
    public static final RegistryKey<PlacedFeature> TITANIUM_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"titanium_ore"));
    public static final RegistryKey<PlacedFeature> LEAD_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"lead_ore"));
    public static final RegistryKey<PlacedFeature> OCTANGULITE_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Geomancy.MOD_ID,"octangulite_ore"));


    public static void register(){

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, MITHRIL_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, MOLYBDENUM_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, TITANIUM_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, LEAD_ORE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, OCTANGULITE_ORE_PLACED_KEY);
    }
}
