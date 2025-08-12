package org.oxytocina.geomancy.world.biome;

import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.ModEntityTypes;
import org.oxytocina.geomancy.world.ModPlacedFeatures;

public class ModBiomes {

    public static final RegistryKey<Biome> SOUL_SWAMP = register("soul_swamp");
    public static final RegistryKey<Biome> NULL = register("null");

    public static RegistryKey<Biome> register(String name){
        return RegistryKey.of(RegistryKeys.BIOME, Geomancy.locate(name));
    }
    public static void register(){}

    public static void boostrap(Registerable<Biome> context) {
        context.register(SOUL_SWAMP, soulSwamp(context));
        context.register(NULL, nullBiome(context));
    }

    public static void globalOverworldGeneration(GenerationSettings.LookupBackedBuilder builder) {
        DefaultBiomeFeatures.addLandCarvers(builder);
        DefaultBiomeFeatures.addAmethystGeodes(builder);
        DefaultBiomeFeatures.addDungeons(builder);
        DefaultBiomeFeatures.addMineables(builder);
        DefaultBiomeFeatures.addSprings(builder);
        DefaultBiomeFeatures.addFrozenTopLayer(builder);
    }

    public static Biome soulSwamp(Registerable<Biome> context) {

        // spawns
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(spawnBuilder);
        DefaultBiomeFeatures.addBatsAndMonsters(spawnBuilder);
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntityTypes.STELLGE_ENGINEER, 10, 1, 4));

        // biome
        GenerationSettings.LookupBackedBuilder biomeBuilder =
                new GenerationSettings.LookupBackedBuilder(context.getRegistryLookup(RegistryKeys.PLACED_FEATURE),
                        context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER));
        globalOverworldGeneration(biomeBuilder);
        DefaultBiomeFeatures.addDefaultOres(biomeBuilder);
        DefaultBiomeFeatures.addMossyRocks(biomeBuilder);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.SOUL_OAK_TREE_PLACED_KEY);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_SWAMP);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_NORMAL);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_DEAD_BUSH);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_WATERLILY);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_SWAMP);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_SWAMP);
        DefaultBiomeFeatures.addDefaultMushrooms(biomeBuilder);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUGAR_CANE_SWAMP);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_PUMPKIN);
        biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, PlacedFeatures.of("seagrass_swamp"));

        return new Biome.Builder()
                .precipitation(true).downfall(0.9f).temperature(0.8f)
                .generationSettings(biomeBuilder.build()).spawnSettings(spawnBuilder.build())
                .effects((new BiomeEffects.Builder())
                        .waterColor(0x373F6D)
                        .waterFogColor(0x373F6D)
                        .skyColor(0x373F6D)
                        .grassColor(0x373F6D)
                        .foliageColor(0x373F6D)
                        .fogColor(0x373F6D)
                        .moodSound(BiomeMoodSound.CAVE)
                        .music(MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_SWAMP)).build())
                .build();
    }
    public static Biome nullBiome(Registerable<Biome> context) {

        // spawns
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(ModEntityTypes.STELLGE_ENGINEER, 10, 1, 4));

        // biome
        GenerationSettings.LookupBackedBuilder biomeBuilder =
                new GenerationSettings.LookupBackedBuilder(context.getRegistryLookup(RegistryKeys.PLACED_FEATURE),
                        context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER));

        return new Biome.Builder()
                .precipitation(false).downfall(0f).temperature(0f)
                .generationSettings(biomeBuilder.build()).spawnSettings(spawnBuilder.build())
                .effects((new BiomeEffects.Builder())
                        .waterColor(0x373F6D)
                        .waterFogColor(0x373F6D)
                        .skyColor(0x000000)
                        .grassColor(0x373F6D)
                        .foliageColor(0x373F6D)
                        .fogColor(0x000000)
                        .moodSound(BiomeMoodSound.CAVE)
                        .music(MusicType.createIngameMusic(SoundEvents.MUSIC_END)).build())
                .build();
    }
}
