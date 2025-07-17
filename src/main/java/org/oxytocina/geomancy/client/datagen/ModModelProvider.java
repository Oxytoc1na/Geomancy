package org.oxytocina.geomancy.client.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.block.Block;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;

import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.items.ExtraItemSettings;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    private static BlockStateModelGenerator blockGenerator;
    private static ItemModelGenerator itemModelGenerator;

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
        ModModelProvider.itemModelGenerator = itemModelGenerator;

        for(Item i : ExtraItemSettings.GeneratedModel){
            itemModelGenerator.register(i, Models.GENERATED);
        }

        for(Item i : ExtraItemSettings.HandheldModel){
            itemModelGenerator.register(i, Models.HANDHELD);
        }

        for(JewelryItem i : ExtraItemSettings.JewelryModel){
            registerJewelryItemModels(i);
        }

        for(var ent : ModItems.spawnEggs.entrySet()){
            registerSpawnEgg(ent.getValue());
        }

        registerSpellComponentItemModels();
    }

    private void registerJewelryItemModels(JewelryItem item){

        String itemPath = Registries.ITEM.getId(item).getPath();

        // base layer ID
        Identifier baseLayerTexture = Geomancy.locate("item/jewelry/"+itemPath);

        // generate base model
        Model baseModel = new Model(Optional.of(new Identifier("item/generated")),Optional.empty(),TextureKey.LAYER0);
        baseModel.upload(ModelIds.getItemModelId(item), TextureMap.layer0(baseLayerTexture), itemModelGenerator.writer, (id, textures) -> this.createBaseJewelryJson(item,id, textures));

        // generate gemmed models
        for (int i = 0; i < item.gemSlotCount; i++) {
            TextureKey[] keys = new TextureKey[i+2];
            TextureMap map = new TextureMap();
            keys[0] = TextureKey.of("layer0");
            map.put(keys[0],baseLayerTexture);
            for (int j = 1; j < keys.length; j++) {
                keys[j] = TextureKey.of("layer"+j);
                map.put(keys[j],Geomancy.locate("item/jewelry/"+itemPath+"_gem_"+j));
            }
            Model gemmedModel = new Model(Optional.of(new Identifier("item/generated")),Optional.empty(),keys);

            gemmedModel.upload(jewelryGemmedVariantModelID(item,i),map, itemModelGenerator.writer);
        }

    }

    public final JsonObject createBaseJewelryJson(JewelryItem item, Identifier id, Map<TextureKey, Identifier> textures) {
        JsonObject jsonObject = Models.GENERATED.createJson(id, textures);
        JsonArray overrides = new JsonArray();

        for (int i = 0; i < item.gemSlotCount; i++) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("geomancy:has_gem" , (i+0.5f)/(float)item.gemSlotCount);
            override.add("predicate", predicate);
            override.addProperty("model", jewelryGemmedVariantModelID(item,i).toString());
            overrides.add(override);
        }

        jsonObject.add("overrides", overrides);
        return jsonObject;
    }

    private void registerSpellComponentItemModels(){

        String itemPath = Registries.ITEM.getId(ModItems.SPELLCOMPONENT).getPath();

        // TODO: generate base model
        Model baseModel = new Model(Optional.of(new Identifier("item/generated")),Optional.empty(),TextureKey.LAYER0);
        baseModel.upload(ModelIds.getItemModelId(ModItems.SPELLCOMPONENT), TextureMap.layer0(Geomancy.locate("item/spells/bg_arithmetic")), itemModelGenerator.writer,(id, textures) -> {
            JsonObject jsonObject = Models.GENERATED.createJson(id, textures);
                JsonArray overrides = new JsonArray();

                for(var spellID : SpellBlocks.functionOrder.keySet())
                {
                    var comp = SpellBlocks.functions.get(spellID);
                    JsonObject override = new JsonObject();
                    JsonObject predicate = new JsonObject();
                    predicate.addProperty("geomancy:spell" , (SpellBlocks.functionOrder.get(spellID)-0.5f)/SpellBlocks.functionOrder.size());
                    predicate.addProperty("geomancy:has_spell" , 1);
                    override.add("predicate", predicate);
                    override.addProperty("model", Geomancy.locate("item/spells/"+comp.identifier.getPath()).toString());
                    overrides.add(override);
                }

                jsonObject.add("overrides", overrides);
                return jsonObject;
        });

        for(var compID : SpellBlocks.functions.keySet()){
            var comp = SpellBlocks.functions.get(compID);
            // base layer ID
            Identifier bgLayerTexture = Geomancy.locate("item/spells/bg_"+comp.category.toString().toLowerCase());
            Identifier fgLayerTexture = Geomancy.locate("item/spells/"+comp.identifier.getPath());

            // generate component model
            Model compModel = new Model(Optional.of(new Identifier("item/generated")),Optional.empty(),TextureKey.LAYER0,TextureKey.LAYER1);
            compModel.upload(Geomancy.locate("item/spells/"+comp.identifier.getPath()), TextureMap.layered(bgLayerTexture,fgLayerTexture), itemModelGenerator.writer);
        }
    }

    private static Identifier jewelryGemmedVariantModelID(JewelryItem item, int index){
        return Geomancy.locate("item/jewelry/"+Registries.ITEM.getId(item).getPath()+"_gem_"+(index+1));
    }

    private void registerSpawnEgg(SpawnEggItem item){
        // generate base model
        Model baseModel = new Model(Optional.of(new Identifier("item/template_spawn_egg")),Optional.empty());
        baseModel.upload(ModelIds.getItemModelId(item), new TextureMap(), itemModelGenerator.writer, (id, textures) -> {
            var res = new JsonObject();
            res.addProperty("parent","minecraft:item/template_spawn_egg");
            return res;
        });
    }


    @Override
    public String getName() {
        return "Geomancy Model Provider";
    }

}