package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import net.minecraft.data.client.Models;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        for(Block b : ExtraBlockSettings.SimpleCubeBlocks){
            blockStateModelGenerator.registerSimpleCubeAll(b);
        }
    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        itemModelGenerator.register(ModItems.GUIDITE_SWORD, Models.HANDHELD);

        for(Item i : ModItems.ItemsWithGeneratedModel){
            itemModelGenerator.register(i, Models.GENERATED);
        }
    }

    @Override
    public String getName() {
        return "Geomancy Model Provider";
    }
}