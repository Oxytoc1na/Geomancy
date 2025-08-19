package org.oxytocina.geomancy.client.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.*;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;

import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.ExtraItemSettings;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.spells.SpellBlocks;

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

        //blockStateModelGenerator.registerAnvil(ModBlocks.MITHRIL_ANVIL);

        blockStateModelGenerator.registerSimpleState(ModBlocks.SMITHERY);

        // cube all
        for(Block b : ExtraBlockSettings.SimpleCubeBlocks){
            Identifier modelId = ModelIds.getBlockSubModelId(b, "");
            var dat = ExtraBlockSettings.logged.get(b);
            if(dat.shouldGenerateModels)
                switch(dat.modelType)
                {
                    case Tinted:
                        modelId = ModModels.TINTED_CUBE_ALL.upload(b,TextureMap.all(b), blockGenerator.modelCollector);
                        break;
                    default:
                        modelId = TexturedModel.CUBE_ALL.upload(b, blockGenerator.modelCollector);
                        break;
                }

            blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(b,modelId));
        }

        // stairs
        for(StairsBlock b : ExtraBlockSettings.StairsBlocks.keySet()){

            Identifier baseBlockID = Registries.BLOCK.getId(ExtraBlockSettings.StairsBlocks.get(b));
            Identifier id = Registries.BLOCK.getId(b);
            var sup = BlockStateModelGenerator.createStairsBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath("_inner"),
                    id.withPrefixedPath("block/"),
                    id.withPrefixedPath("block/").withSuffixedPath("_outer"));
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model baseModel,innerModel,outerModel;
            switch(dat.modelType)
            {
                case Tinted:
                    baseModel = ModModels.TINTED_STAIRS;
                    innerModel = ModModels.TINTED_INNER_STAIRS;
                    outerModel = ModModels.TINTED_OUTER_STAIRS;
                    break;
                default:
                    baseModel = Models.STAIRS;
                    innerModel = Models.INNER_STAIRS;
                    outerModel = Models.OUTER_STAIRS;
                    break;
            }

            blockStateModelGenerator.createSubModel(b,"",innerModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.BOTTOM,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.TOP,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.SIDE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));

            blockStateModelGenerator.createSubModel(b,"",baseModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.BOTTOM,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.TOP,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.SIDE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));

            blockStateModelGenerator.createSubModel(b,"",outerModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.BOTTOM,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.TOP,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.SIDE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
        }

        // slab
        for(SlabBlock b : ExtraBlockSettings.SlabBlocks.keySet()){
            Identifier baseBlockID = Registries.BLOCK.getId(ExtraBlockSettings.SlabBlocks.get(b));
            Identifier id = Registries.BLOCK.getId(b);
            var sup = BlockStateModelGenerator.createSlabBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath(""),
                    id.withPrefixedPath("block/").withSuffixedPath("_top"),
                    baseBlockID.withPrefixedPath("block/"));
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model baseModel,topModel;
            switch(dat.modelType)
            {
                case Tinted:
                    baseModel = ModModels.TINTED_SLAB;
                    topModel = ModModels.TINTED_SLAB_TOP;
                    break;
                default:
                    baseModel = Models.SLAB;
                    topModel = Models.SLAB_TOP;
                    break;
            }

            blockStateModelGenerator.createSubModel(b,"",baseModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.BOTTOM,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.TOP,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.SIDE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));

            blockStateModelGenerator.createSubModel(b,"",topModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.BOTTOM,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.TOP,Geomancy.locate("block/"+baseBlockID.getPath()));
                res.put(TextureKey.SIDE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
        }

        // wall
        for(WallBlock b : ExtraBlockSettings.WallBlocks.keySet()){
            Identifier baseBlockID = Registries.BLOCK.getId(ExtraBlockSettings.WallBlocks.get(b));
            Identifier id = Registries.BLOCK.getId(b);
            var sup = BlockStateModelGenerator.createWallBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath("_post"),
                    id.withPrefixedPath("block/").withSuffixedPath("_side"),
                    id.withPrefixedPath("block/").withSuffixedPath("_side_tall"));
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model postModel,sideModel,sideWallModel,inventoryModel;
            switch(dat.modelType)
            {
                case Tinted:
                    postModel =         ModModels.TINTED_TEMPLATE_WALL_POST;
                    sideModel =         ModModels.TINTED_TEMPLATE_WALL_SIDE;
                    sideWallModel =     ModModels.TINTED_TEMPLATE_WALL_SIDE_TALL;
                    inventoryModel =    ModModels.TINTED_WALL_INVENTORY;
                    break;
                default:
                    postModel =         Models.TEMPLATE_WALL_POST;
                    sideModel =         Models.TEMPLATE_WALL_SIDE;
                    sideWallModel =     Models.TEMPLATE_WALL_SIDE_TALL;
                    inventoryModel =    Models.WALL_INVENTORY;
                    break;
            }

            blockStateModelGenerator.createSubModel(b,"",postModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.WALL,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",sideModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.WALL,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",sideWallModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.WALL,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",inventoryModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.WALL,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
        }

        // fence
        for(FenceBlock b : ExtraBlockSettings.FenceBlocks.keySet()){
            Identifier baseBlockID = Registries.BLOCK.getId(ExtraBlockSettings.FenceBlocks.get(b));
            Identifier id = Registries.BLOCK.getId(b);
            var sup = BlockStateModelGenerator.createFenceBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath("_post"),
                    id.withPrefixedPath("block/").withSuffixedPath("_side"));
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model postModel,sideModel,inventoryModel;
            switch(dat.modelType)
            {
                case Tinted:
                    postModel =         ModModels.TINTED_FENCE_POST;
                    sideModel =         ModModels.TINTED_FENCE_SIDE;
                    inventoryModel =    ModModels.TINTED_FENCE_INVENTORY;
                    break;
                default:
                    postModel =         Models.FENCE_POST;
                    sideModel =         Models.FENCE_SIDE;
                    inventoryModel =    Models.FENCE_INVENTORY;
                    break;
            }

            blockStateModelGenerator.createSubModel(b,"",postModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",sideModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",inventoryModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
        }

        // fence gate
        for(FenceGateBlock b : ExtraBlockSettings.FenceGateBlocks.keySet()){
            Identifier baseBlockID = Registries.BLOCK.getId(ExtraBlockSettings.FenceGateBlocks.get(b));
            Identifier id = Registries.BLOCK.getId(b);
            var sup = BlockStateModelGenerator.createFenceGateBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath("_open"),
                    id.withPrefixedPath("block/").withSuffixedPath(""),
                    id.withPrefixedPath("block/").withSuffixedPath("_wall_open"),
                    id.withPrefixedPath("block/").withSuffixedPath("_wall"),
                    true
                    );
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model closedModel,openModel,closedWallModel,openWallModel;
            switch(dat.modelType)
            {
                case Tinted:
                    closedModel =       ModModels.TINTED_TEMPLATE_FENCE_GATE;
                    openModel =         ModModels.TINTED_TEMPLATE_FENCE_GATE_OPEN;
                    closedWallModel =   ModModels.TINTED_TEMPLATE_FENCE_GATE_WALL;
                    openWallModel =     ModModels.TINTED_TEMPLATE_FENCE_GATE_WALL_OPEN;
                    break;
                default:
                    closedModel =       Models.TEMPLATE_FENCE_GATE;
                    openModel =         Models.TEMPLATE_FENCE_GATE_OPEN;
                    closedWallModel =   Models.TEMPLATE_FENCE_GATE_WALL;
                    openWallModel =     Models.TEMPLATE_FENCE_GATE_WALL_OPEN;
                    break;
            }

            blockStateModelGenerator.createSubModel(b,"",closedModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",openModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",closedWallModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",openWallModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
        }

        // pressure plate
        for(PressurePlateBlock b : ExtraBlockSettings.PressurePlateBlocks.keySet()){
            Identifier baseBlockID = Registries.BLOCK.getId(ExtraBlockSettings.PressurePlateBlocks.get(b));
            Identifier id = Registries.BLOCK.getId(b);
            var sup = BlockStateModelGenerator.createPressurePlateBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath(""),
                    id.withPrefixedPath("block/").withSuffixedPath("_down"));
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model upModel,downModel;
            switch(dat.modelType)
            {
                case Tinted:
                    upModel =       ModModels.TINTED_PRESSURE_PLATE_UP;
                    downModel =     ModModels.TINTED_PRESSURE_PLATE_DOWN;
                    break;
                default:
                    upModel =       Models.PRESSURE_PLATE_UP;
                    downModel =     Models.PRESSURE_PLATE_DOWN;
                    break;
            }

            blockStateModelGenerator.createSubModel(b,"",upModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",downModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
        }

        // button
        for(ButtonBlock b : ExtraBlockSettings.ButtonBlocks.keySet()){
            Identifier baseBlockID = Registries.BLOCK.getId(ExtraBlockSettings.ButtonBlocks.get(b));
            Identifier id = Registries.BLOCK.getId(b);
            var sup = BlockStateModelGenerator.createButtonBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath(""),
                    id.withPrefixedPath("block/").withSuffixedPath("_pressed"));
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model baseModel,pressedModel,inventoryModel;
            switch(dat.modelType)
            {
                case Tinted:
                    baseModel =       ModModels.TINTED_BUTTON;
                    pressedModel =    ModModels.TINTED_BUTTON_PRESSED;
                    inventoryModel =  ModModels.TINTED_BUTTON_INVENTORY;
                    break;
                default:
                    baseModel =       Models.BUTTON;
                    pressedModel =    Models.BUTTON_PRESSED;
                    inventoryModel =  Models.BUTTON_INVENTORY;
                    break;
            }

            blockStateModelGenerator.createSubModel(b,"",baseModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",pressedModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",inventoryModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
        }

        // pillar
        for(PillarBlock b : ExtraBlockSettings.PillarBlocks){
            Identifier baseBlockID = Registries.BLOCK.getId(b);
            Identifier id = Registries.BLOCK.getId(b);
            var sup = BlockStateModelGenerator.createAxisRotatedBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath(""),
                    id.withPrefixedPath("block/").withSuffixedPath("_horizontal")
                    );
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model baseModel,horizontal;
            switch(dat.modelType)
            {
                case Tinted:
                    baseModel =     ModModels.TINTED_CUBE_COLUMN;
                    horizontal =    ModModels.TINTED_CUBE_COLUMN_HORIZONTAL;
                    break;
                default:
                    baseModel =     Models.CUBE_COLUMN;
                    horizontal =    Models.CUBE_COLUMN_HORIZONTAL;
                    break;
            }

            blockStateModelGenerator.createSubModel(b,"",baseModel,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.END,Geomancy.locate("block/"+baseBlockID.getPath()+"_top"));
                res.put(TextureKey.SIDE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
            blockStateModelGenerator.createSubModel(b,"",horizontal,(identifier -> {
                TextureMap res = new TextureMap();
                res.put(TextureKey.END,Geomancy.locate("block/"+baseBlockID.getPath()+"_top"));
                res.put(TextureKey.SIDE,Geomancy.locate("block/"+baseBlockID.getPath()));
                return res;
            }));
        }

        // doors
        for(DoorBlock b : ExtraBlockSettings.DoorBlocks){
            Identifier id = Registries.BLOCK.getId(b);
            Identifier baseBlockID = id;
            var sup = BlockStateModelGenerator.createDoorBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath("_bottom_left"),
                    id.withPrefixedPath("block/").withSuffixedPath("_bottom_left_open"),
                    id.withPrefixedPath("block/").withSuffixedPath("_bottom_right"),
                    id.withPrefixedPath("block/").withSuffixedPath("_bottom_right_open"),
                    id.withPrefixedPath("block/").withSuffixedPath("_top_left"),
                    id.withPrefixedPath("block/").withSuffixedPath("_top_left_open"),
                    id.withPrefixedPath("block/").withSuffixedPath("_top_right"),
                    id.withPrefixedPath("block/").withSuffixedPath("_top_right_open")
            );
            // generate (useful) item model
            blockStateModelGenerator.modelCollector.accept(ModelIds.getItemModelId(b.asItem()), () -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("parent", "minecraft:item/generated");
                JsonObject sub = new JsonObject();
                sub.addProperty("layer0","geomancy:item/"+id.getPath());
                jsonObject.add("textures", sub);
                return jsonObject;
            });
            // this line for some reason generates an unwanted item model if we dont have one already???
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model bottomLeftClosed,bottomLeftOpen,bottomRightClosed,bottomRightOpen;
            Model topLeftClosed,topLeftOpen,topRightClosed,topRightOpen;
            switch(dat.modelType)
            {
                case Tinted:
                    bottomLeftClosed =     ModModels.TINTED_DOOR_BOTTOM_LEFT;
                    bottomLeftOpen =       ModModels.TINTED_DOOR_BOTTOM_LEFT_OPEN;
                    bottomRightClosed =    ModModels.TINTED_DOOR_BOTTOM_RIGHT;
                    bottomRightOpen =      ModModels.TINTED_DOOR_BOTTOM_RIGHT_OPEN;
                    topLeftClosed =        ModModels.TINTED_DOOR_TOP_LEFT;
                    topLeftOpen =          ModModels.TINTED_DOOR_TOP_LEFT_OPEN;
                    topRightClosed =       ModModels.TINTED_DOOR_TOP_RIGHT;
                    topRightOpen =         ModModels.TINTED_DOOR_TOP_RIGHT_OPEN;
                    break;
                default:
                    bottomLeftClosed =     Models.DOOR_BOTTOM_LEFT;
                    bottomLeftOpen =       Models.DOOR_BOTTOM_LEFT_OPEN;
                    bottomRightClosed =    Models.DOOR_BOTTOM_RIGHT;
                    bottomRightOpen =      Models.DOOR_BOTTOM_RIGHT_OPEN;
                    topLeftClosed =        Models.DOOR_TOP_LEFT;
                    topLeftOpen =          Models.DOOR_TOP_LEFT_OPEN;
                    topRightClosed =       Models.DOOR_TOP_RIGHT;
                    topRightOpen =         Models.DOOR_TOP_RIGHT_OPEN;
                    break;
            }
            for(var model : new Model[]{bottomLeftClosed,bottomLeftOpen,bottomRightClosed,bottomRightOpen,topLeftClosed,topLeftOpen,topRightClosed,topRightOpen})
            {
                blockStateModelGenerator.createSubModel(b,"",model,(identifier -> {
                    TextureMap res = new TextureMap();
                    res.put(TextureKey.TOP,Geomancy.locate("block/"+baseBlockID.getPath()+"_top"));
                    res.put(TextureKey.BOTTOM,Geomancy.locate("block/"+baseBlockID.getPath()+"_bottom"));
                    return res;
                }));
            }
        }

        // trapdoors
        for(TrapdoorBlock b : ExtraBlockSettings.TrapdoorBlocks){
            Identifier id = Registries.BLOCK.getId(b);
            Identifier baseBlockID = id;
            var sup = BlockStateModelGenerator.createTrapdoorBlockState(b,
                    id.withPrefixedPath("block/").withSuffixedPath("_top"),
                    id.withPrefixedPath("block/").withSuffixedPath("_bottom"),
                    id.withPrefixedPath("block/").withSuffixedPath("_open")
            );
            blockStateModelGenerator.blockStateCollector.accept(sup);

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;

            Model topModel, bottomModel, openModel;
            switch(dat.modelType)
            {
                case Tinted:
                    topModel =     ModModels.TINTED_TEMPLATE_TRAPDOOR_TOP;
                    bottomModel =  ModModels.TINTED_TEMPLATE_TRAPDOOR_BOTTOM;
                    openModel =    ModModels.TINTED_TEMPLATE_TRAPDOOR_OPEN;
                    break;
                default:
                    topModel =     Models.TEMPLATE_TRAPDOOR_TOP;
                    bottomModel =  Models.TEMPLATE_TRAPDOOR_BOTTOM;
                    openModel =    Models.TEMPLATE_TRAPDOOR_OPEN;
                    break;
            }
            for(var model : new Model[]{topModel, bottomModel, openModel})
            {
                blockStateModelGenerator.createSubModel(b,"",model,(identifier -> {
                    TextureMap res = new TextureMap();
                    res.put(TextureKey.TEXTURE,Geomancy.locate("block/"+baseBlockID.getPath()));
                    return res;
                }));
            }
        }


        // cube variants
        for(Block b : ExtraBlockSettings.VariantCubeBlocks.keySet()){

            var dat = ExtraBlockSettings.logged.get(b);
            if(!dat.shouldGenerateModels) continue;


            BlockStateVariant[] variants = new BlockStateVariant[ExtraBlockSettings.VariantCubeBlocks.get(b)];

            Model model;
            switch(dat.modelType)
            {
                case Tinted:
                    model =     ModModels.TINTED_CUBE_ALL;
                    break;
                default:
                    model =     Models.CUBE_ALL;
                    break;
            }

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

        // fluids
        // TODO
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

        for(IJewelryItem i : ExtraItemSettings.JewelryModel){
            registerJewelryItemModels(i);
        }

        for(Item i : ExtraItemSettings.ParentInventoryModel){
            Model wallModel = new Model(Optional.of(Geomancy.locate("block/"+Registries.ITEM.getId(i).getPath()+"_inventory")),Optional.empty());
            itemModelGenerator.register(i, wallModel);
        }

        for(Item i : ExtraItemSettings.ParentBottomModel){
            Model wallModel = new Model(Optional.of(Geomancy.locate("block/"+Registries.ITEM.getId(i).getPath()+"_bottom")),Optional.empty());
            itemModelGenerator.register(i, wallModel);
        }

        for(var ent : ModItems.spawnEggs.entrySet()){
            registerSpawnEgg(ent.getValue());
        }

        registerSpellComponentItemModels();
    }

    private void registerJewelryItemModels(IJewelryItem item){

        String itemPath = Registries.ITEM.getId((Item)item).getPath();

        // base layer ID
        Identifier baseLayerTexture = Geomancy.locate("item/jewelry/"+itemPath);

        // generate base model
        Model baseModel = new Model(Optional.of(new Identifier("item/generated")),Optional.empty(),TextureKey.LAYER0);
        baseModel.upload(ModelIds.getItemModelId((Item)item), TextureMap.layer0(baseLayerTexture), itemModelGenerator.writer, (id, textures) -> this.createBaseJewelryJson(item,id, textures));

        // generate gemmed models
        for (int i = 0; i < item.getGemSlotCount(); i++) {
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

    public final JsonObject createBaseJewelryJson(IJewelryItem item, Identifier id, Map<TextureKey, Identifier> textures) {
        JsonObject jsonObject = Models.GENERATED.createJson(id, textures);
        JsonArray overrides = new JsonArray();

        for (int i = 0; i < item.getGemSlotCount(); i++) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("geomancy:has_gem" , (i+0.5f)/(float)item.getGemSlotCount());
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

    private static Identifier jewelryGemmedVariantModelID(IJewelryItem item, int index){
        return Geomancy.locate("item/jewelry/"+Registries.ITEM.getId((Item)item).getPath()+"_gem_"+(index+1));
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