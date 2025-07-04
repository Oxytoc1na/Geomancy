package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.items.ExtraItemSettings;
import org.oxytocina.geomancy.items.ModItems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    private static BlockStateModelGenerator blockGenerator;

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockGenerator = blockStateModelGenerator;

        blockStateModelGenerator.registerAnvil(ModBlocks.MITHRIL_ANVIL);

        blockStateModelGenerator.registerSimpleState(ModBlocks.SMITHERY);

        // cube all
        for(Block b : ExtraBlockSettings.SimpleCubeBlocks){
            blockStateModelGenerator.registerSimpleCubeAll(b);
        }

        // cube variants
        for(Block b : ExtraBlockSettings.VariantCubeBlocks.keySet()){

            BlockStateVariant[] variants = new BlockStateVariant[ExtraBlockSettings.VariantCubeBlocks.get(b)];

            Model model = new Model(Optional.of(Identifier.of(Identifier.DEFAULT_NAMESPACE,"block/cube_all")),Optional.empty(),TextureKey.ALL);

            String blockPath = Registries.BLOCK.getId(b).getPath();

            // Variants
            for (int i = 0; i < ExtraBlockSettings.VariantCubeBlocks.get(b); i++) {
                variants[i] = BlockStateVariant.create().put(
                        VariantSettings.MODEL,
                        new Identifier(
                                Geomancy.MOD_ID,
                                "block/"+blockPath+"/" + blockPath+(i+1)
                        )
                );

                //TexturedModel.CUBE_ALL.upload(b,blockStateModelGenerator.modelCollector);
                model.upload(
                        Identifier.of(Geomancy.MOD_ID,"block/"+blockPath+"/"+blockPath+(i+1)),
                        TextureMap.all(Identifier.of(Geomancy.MOD_ID,"block/"+blockPath+"/"+blockPath+(i+1))),
                        blockStateModelGenerator.modelCollector);
            }

            // Model for inventory
            model.upload(
                    Identifier.of(Geomancy.MOD_ID,"block/"+blockPath),
                    TextureMap.all(Identifier.of(Geomancy.MOD_ID,"block/"+blockPath+"/"+blockPath+"1")),
                    blockStateModelGenerator.modelCollector);

            VariantsBlockStateSupplier supplier = VariantsBlockStateSupplier.create(b,variants);
            blockStateModelGenerator.blockStateCollector.accept(supplier);
        }

    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        for(Item i : ExtraItemSettings.GeneratedModel){
            itemModelGenerator.register(i, Models.GENERATED);
        }

        for(Item i : ExtraItemSettings.HandheldModel){
            itemModelGenerator.register(i, Models.HANDHELD);
        }
    }

    @Override
    public String getName() {
        return "Geomancy Model Provider";
    }

}