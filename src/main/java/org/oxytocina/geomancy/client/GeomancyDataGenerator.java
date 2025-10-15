package org.oxytocina.geomancy.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import org.oxytocina.geomancy.client.datagen.*;
import org.oxytocina.geomancy.world.ModConfiguredFeatures;
import org.oxytocina.geomancy.world.ModPlacedFeatures;
import org.oxytocina.geomancy.world.biome.ModBiomes;
import org.oxytocina.geomancy.world.dimension.ModDimensions;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class GeomancyDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        init(fabricDataGenerator);
    }

    public static boolean initialized = false;
    public static synchronized void init(FabricDataGenerator fabricDataGenerator){
        if(initialized) return;
        try{
            FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

            pack.addProvider(ModAdvancementProvider::new);
            ModBlockTagProvider.precalcHybrids();
            pack.addProvider(ModBlockTagProvider::new);
            pack.addProvider(ModItemTagProvider::new);
            pack.addProvider(ModBiomeTagProvider::new);
            pack.addProvider(ModFluidTagProvider::new);
            pack.addProvider(ModDamageTypeTagProvider::new);
            pack.addProvider(ModBlockLootTableProvider::new);
            pack.addProvider(ModModelProvider::new);
            pack.addProvider(ModEnglishLangProvider::new);
            pack.addProvider(ModRecipeProvider::new);
            pack.addProvider(ModChestLootTableProvider::new);
            pack.addProvider(ModSoundProvider::new);
            pack.addProvider(ModWorldGenerator::new);
        }
        catch (Throwable t) {
            RuntimeException exception = new RuntimeException(String.format("Geomancy Initialization failed!",
                    t.fillInStackTrace(), Arrays.toString(t.getStackTrace())));

            Log.debug(LogCategory.ENTRYPOINT, "Geomancy");
            throw t;
        }

        initialized = true;
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::boostrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::boostrap);
        registryBuilder.addRegistry(RegistryKeys.BIOME, ModBiomes::boostrap);
        registryBuilder.addRegistry(RegistryKeys.DIMENSION_TYPE, ModDimensions::bootstrapType);
    }
}
