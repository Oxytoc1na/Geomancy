package org.oxytocina.geomancy.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import org.oxytocina.geomancy.client.datagen.*;

public class GeomancyDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModAdvancementProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModBiomeTagProvider::new);
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModBlockLootTableProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModEnglishLangProvider::new);
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModChestLootTableProvider::new);
    }
}
