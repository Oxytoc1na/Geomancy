package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

import net.minecraft.data.client.Models;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONDENSED_DIRT);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_MITHRIL_ORE);
    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        itemModelGenerator.register(ModItems.SUSPICIOUS_SUBSTANCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.GUIDITE_SWORD, Models.HANDHELD);
    }

    @Override
    public String getName() {
        return "Geomancy Model Provider";
    }
}