package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.registries.ModBlockTags;
import org.oxytocina.geomancy.registries.ModItemTags;

import static org.oxytocina.geomancy.registries.ModBlockTags.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider<Block> {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK, registriesFuture);
    }

    protected static Map<TagKey<Block>,List<Block>> HYBRID_BLOCKS = new HashMap<TagKey<Block>,List<Block>>();
    protected static Map<TagKey<Block>,List<TagKey<Block>>> HYBRID_TAGS = new HashMap<TagKey<Block>,List<TagKey<Block>>>();

    public static Map<TagKey<Item>,List<Block>> HYBRID_ITEM_BLOCKS = new HashMap<>();
    public static Map<TagKey<Item>,List<TagKey<Item>>> HYBRID_ITEM_TAGS = new HashMap<>();

    public static void precalcHybrids() {
        // walls
        for(Block b : ExtraBlockSettings.WallBlocks.keySet()) addHybrid(BlockTags.WALLS, ItemTags.WALLS,b);

        // fences
        for(Block b : ExtraBlockSettings.FenceBlocks.keySet())addHybrid(BlockTags.FENCES, ItemTags.FENCES,b);
        addHybrid(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES,ModBlocks.SOUL_OAK_FENCE);

        // fence gates
        for(Block b : ExtraBlockSettings.FenceBlocks.keySet())addHybrid(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES,b);

        // stairs
        for(Block b : ExtraBlockSettings.StairsBlocks.keySet())addHybrid(BlockTags.STAIRS, ItemTags.STAIRS,b);

        // slabs
        for(Block b : ExtraBlockSettings.SlabBlocks.keySet())addHybrid(BlockTags.SLABS, ItemTags.SLABS,b);

        // soul fire bases
        addHybrid(BlockTags.SOUL_FIRE_BASE_BLOCKS,ItemTags.SOUL_FIRE_BASE_BLOCKS,ModBlocks.NULL_CRYSTAL);

        // wood
        addHybrid(BlockTags.PLANKS,ItemTags.PLANKS,ModBlocks.SOUL_OAK_PLANKS);
        addHybrid(BlockTags.LOGS_THAT_BURN,ItemTags.LOGS_THAT_BURN,
                ModBlocks.SOUL_OAK_LOG,
                ModBlocks.STRIPPED_SOUL_OAK_LOG,
                ModBlocks.SOUL_OAK_WOOD,
                ModBlocks.STRIPPED_SOUL_OAK_WOOD);
        addHybrid(BlockTags.WOODEN_BUTTONS,ItemTags.WOODEN_BUTTONS,
                ModBlocks.SOUL_OAK_BUTTON);
        addHybrid(BlockTags.WOODEN_DOORS,ItemTags.WOODEN_DOORS,
                ModBlocks.SOUL_OAK_DOOR);
        addHybrid(BlockTags.WOODEN_FENCES,ItemTags.WOODEN_FENCES,
                ModBlocks.SOUL_OAK_FENCE);
        addHybrid(BlockTags.WOODEN_SLABS,ItemTags.WOODEN_SLABS,
                ModBlocks.SOUL_OAK_SLAB);
        addHybrid(BlockTags.WOODEN_STAIRS,ItemTags.WOODEN_STAIRS,
                ModBlocks.SOUL_OAK_STAIRS);
        addHybrid(BlockTags.WOODEN_PRESSURE_PLATES,ItemTags.WOODEN_PRESSURE_PLATES,
                ModBlocks.SOUL_OAK_PRESSURE_PLATE);
        addHybrid(BlockTags.WOODEN_TRAPDOORS,ItemTags.WOODEN_TRAPDOORS,
                ModBlocks.SOUL_OAK_TRAPDOOR);
        addHybridTag(BlockTags.LOGS_THAT_BURN,ItemTags.LOGS_THAT_BURN,SOUL_OAK_LOGS, ModItemTags.SOUL_OAK_LOGS);
        addHybrid(BlockTags.LEAVES,ItemTags.LEAVES,ModBlocks.SOUL_OAK_LEAVES);
        addHybrid(BlockTags.SAPLINGS,ItemTags.SAPLINGS,ModBlocks.SOUL_OAK_SAPLING);

        // ores
        addHybrid(LEAD_ORES,ModItemTags.LEAD_ORES,ModBlocks.LEAD_ORE,ModBlocks.DEEPSLATE_LEAD_ORE);
        addHybrid(MOLYBDENUM_ORES,ModItemTags.MOLYBDENUM_ORES,ModBlocks.MOLYBDENUM_ORE,ModBlocks.DEEPSLATE_MOLYBDENUM_ORE);
        addHybrid(TITANIUM_ORES,ModItemTags.TITANIUM_ORES,ModBlocks.TITANIUM_ORE,ModBlocks.DEEPSLATE_TITANIUM_ORE);
        addHybrid(MITHRIL_ORES,ModItemTags.MITHRIL_ORES,ModBlocks.MITHRIL_ORE,ModBlocks.DEEPSLATE_MITHRIL_ORE);
        addHybrid(OCTANGULITE_ORES,ModItemTags.OCTANGULITE_ORES,ModBlocks.OCTANGULITE_ORE,ModBlocks.DEEPSLATE_OCTANGULITE_ORE);
        addHybrid(PERIDOT_ORES,ModItemTags.PERIDOT_ORES,ModBlocks.PERIDOT_ORE,ModBlocks.DEEPSLATE_PERIDOT_ORE);
        addHybrid(AXINITE_ORES,ModItemTags.AXINITE_ORES,ModBlocks.AXINITE_ORE,ModBlocks.DEEPSLATE_AXINITE_ORE);
        addHybrid(TOURMALINE_ORES,ModItemTags.TOURMALINE_ORES,ModBlocks.TOURMALINE_ORE,ModBlocks.DEEPSLATE_TOURMALINE_ORE);
        addHybrid(ORTHOCLASE_ORES,ModItemTags.ORTHOCLASE_ORES,ModBlocks.ORTHOCLASE_ORE,ModBlocks.DEEPSLATE_ORTHOCLASE_ORE);

        addHybridTags(C_ORES,ModItemTags.C_ORES,
                List.of(LEAD_ORES,MOLYBDENUM_ORES,TITANIUM_ORES,MITHRIL_ORES,OCTANGULITE_ORES,PERIDOT_ORES,AXINITE_ORES,TOURMALINE_ORES,ORTHOCLASE_ORES),
                List.of(ModItemTags.LEAD_ORES,ModItemTags.MOLYBDENUM_ORES,ModItemTags.TITANIUM_ORES,ModItemTags.MITHRIL_ORES,ModItemTags.OCTANGULITE_ORES
                    ,ModItemTags.PERIDOT_ORES,ModItemTags.AXINITE_ORES,ModItemTags.TOURMALINE_ORES,ModItemTags.ORTHOCLASE_ORES));
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        // required tools
        var builder = getOrCreateTagBuilder(PICKAXE_MINEABLES).setReplace(false);
        for(Block b : ExtraBlockSettings.ToolableBlock_Pickaxe) builder.add(b);

        builder = getOrCreateTagBuilder(AXE_MINEABLES).setReplace(false);
        for(Block b : ExtraBlockSettings.ToolableBlock_Axe) builder.add(b);

        builder = getOrCreateTagBuilder(SHOVEL_MINEABLES).setReplace(false);
        for(Block b : ExtraBlockSettings.ToolableBlock_Shovel) builder.add(b);

        builder = getOrCreateTagBuilder(HOE_MINEABLES).setReplace(false);
        for(Block b : ExtraBlockSettings.ToolableBlock_Hoe) builder.add(b);

        // climbable
        getOrCreateTagBuilder(BlockTags.CLIMBABLE).setReplace(false).add(
                ModBlocks.IRIDESCENT_VINES
        );

        // mining levels
        var levelbuilder_stone = getOrCreateTagBuilder(MININGLEVEL_STONE).setReplace(false);
        var levelbuilder_iron = getOrCreateTagBuilder(MININGLEVEL_IRON).setReplace(false);
        var levelbuilder_diamond = getOrCreateTagBuilder(MININGLEVEL_DIAMOND).setReplace(false);

        for(Block b : ExtraBlockSettings.BlockMiningLevels.keySet()){
            switch (ExtraBlockSettings.BlockMiningLevels.get(b)){
                case 1: levelbuilder_stone.add(b); break;
                case 2: levelbuilder_iron.add(b); break;
                case 3: levelbuilder_diamond.add(b); break;
                default:
            }
        }

        // octangulite
        var builder_octangulite = getOrCreateTagBuilder(OCTANGULITE).setReplace(false)
                .add(
                        ModBlocks.OCTANGULITE_BLOCK,
                        ModBlocks.RAW_OCTANGULITE_BLOCK,
                        ModBlocks.OCTANGULITE_ORE,
                        ModBlocks.DEEPSLATE_OCTANGULITE_ORE,
                        ModBlocks.CUT_OCTANGULITE,
                        ModBlocks.OCTANGULITE_BRICKS,
                        ModBlocks.OCTANGULITE_BRICK_STAIRS,
                        ModBlocks.OCTANGULITE_BRICK_SLABS,
                        ModBlocks.OCTANGULITE_BRICK_WALL
                );

        // null blocks
        {
            getOrCreateTagBuilder(NULL_BLOCKS).setReplace(false).add(
                            ModBlocks.NULL_ROCK,
                            ModBlocks.NULL_RUBBLE);

            getOrCreateTagBuilder(NULL_RUBBLE_REPLACEABLE).setReplace(false)
                    .add(ModBlocks.NULL_ROCK);

            getOrCreateTagBuilder(NULL_CRYSTAL_REPLACEABLE).setReplace(false)
                    .add(ModBlocks.NULL_ROCK);

            getOrCreateTagBuilder(NULL_HOLDS_SPIKES).setReplace(false)
                    .add(ModBlocks.NULL_ROCK,ModBlocks.NULL_RUBBLE);
        }


        getOrCreateTagBuilder(SOUL_OAK_LOGS).setReplace(false)
                .add(ModBlocks.SOUL_OAK_LOG)
                .add(ModBlocks.SOUL_OAK_WOOD)
                .add(ModBlocks.STRIPPED_SOUL_OAK_LOG)
                .add(ModBlocks.STRIPPED_SOUL_OAK_WOOD)
        ;




        // ambient souls
        addSoulTag(BlockTags.WOOL,SoulLevel.Few);
        addSoulTag(BlockTags.WOOL_CARPETS,SoulLevel.Few);
        addSoulTag(BlockTags.WOODEN_SLABS,SoulLevel.Few);
        addSoulTag(BlockTags.WOODEN_STAIRS,SoulLevel.Few);
        addSoulTag(BlockTags.PLANKS,SoulLevel.Few);
        addSoulTag(BlockTags.LOGS,SoulLevel.Few);
        addSoulTag(BlockTags.NYLIUM,SoulLevel.Few);
        addSoulTag(BlockTags.DIRT,SoulLevel.Few);

        addSoulTag(BlockTags.LEAVES,SoulLevel.Normal);
        addSoulTag(BlockTags.FLOWERS,SoulLevel.Normal);
        addSoulTag(BlockTags.FLOWER_POTS,SoulLevel.Normal);
        addSoulTag(BlockTags.SAPLINGS,SoulLevel.Normal);
        addSoulTag(BlockTags.CROPS,SoulLevel.Normal);
        addSoulTag(BlockTags.CORALS,SoulLevel.Normal);
        addSoulTag(BlockTags.CORAL_BLOCKS,SoulLevel.Normal);
        addSoulTag(BlockTags.CAVE_VINES,SoulLevel.Normal);
        addSoulBlocks(SoulLevel.Normal,
                Blocks.VINE,Blocks.KELP,Blocks.KELP_PLANT,
                Blocks.TALL_GRASS,Blocks.TALL_SEAGRASS,Blocks.GRASS,
                Blocks.SEAGRASS,Blocks.SEA_PICKLE);

        addSoulTag(BlockTags.BEEHIVES,SoulLevel.Many);
        addSoulTag(OCTANGULITE,SoulLevel.Many);
        addSoulBlocks(SoulLevel.Many,
                Blocks.INFESTED_COBBLESTONE,
                Blocks.INFESTED_CHISELED_STONE_BRICKS,
                Blocks.INFESTED_DEEPSLATE,
                Blocks.INFESTED_STONE,
                Blocks.INFESTED_STONE_BRICKS,
                Blocks.INFESTED_MOSSY_STONE_BRICKS,
                Blocks.INFESTED_CRACKED_STONE_BRICKS
                );

        addSoulTag(NULL_BLOCKS,SoulLevel.RemoveMany);

        actualizeHybrids();
    }
    void addSoulBlock(Block block) { addSoulBlocks(SoulLevel.Normal,block); }
    void addSoulBlocks(SoulLevel level,Block... blocks){
        var builder_main = getOrCreateTagBuilder(ADDS_SOULS).setReplace(false);
        var builder_sub = getOrCreateTagBuilder(switch(level){
            case Few -> ADDS_SOULS_FEW;
            case Normal -> ADDS_SOULS_NORMAL;
            case Many -> ADDS_SOULS_MANY;
            case RemoveMany -> REMOVES_SOULS_MANY;
        }).setReplace(false);

        for(var block : blocks){
            builder_main.add(block);
            builder_sub.add(block);
        }
    }
    void addSoulTag(TagKey<Block> tag, SoulLevel level){
        var builder_main = getOrCreateTagBuilder(ADDS_SOULS).setReplace(false);
        var builder_sub = getOrCreateTagBuilder(switch(level){
            case Few -> ADDS_SOULS_FEW;
            case Normal -> ADDS_SOULS_NORMAL;
            case Many -> ADDS_SOULS_MANY;
            case RemoveMany -> REMOVES_SOULS_MANY;
        }).setReplace(false);

        builder_main.forceAddTag(tag);
        builder_sub.forceAddTag(tag);
    }
    enum SoulLevel{
        Few,
        Normal,
        Many,
        RemoveMany
    }

    /// adds blocks to a block tag and to an item tag
    public static void addHybrid(TagKey<Block> blockKey, TagKey<Item> itemKey, Block... blocks){
        if(!HYBRID_BLOCKS.containsKey(blockKey)) HYBRID_BLOCKS.put(blockKey,new ArrayList<>());
        HYBRID_BLOCKS.get(blockKey).addAll(List.of(blocks));
        addItems(itemKey,blocks);
    }

    public static void addHybridTags(TagKey<Block> blockKey, TagKey<Item> itemKey, List<TagKey<Block>> blockTagsToAdd, List<TagKey<Item>> itemTagsToAdd) {
        if(!HYBRID_TAGS.containsKey(blockKey)) HYBRID_TAGS.put(blockKey,new ArrayList<>());
        HYBRID_TAGS.get(blockKey).addAll(blockTagsToAdd);

        if(!HYBRID_ITEM_TAGS.containsKey(itemKey)) HYBRID_ITEM_TAGS.put(itemKey,new ArrayList<>());
        HYBRID_ITEM_TAGS.get(itemKey).addAll(itemTagsToAdd);
    }


    public static void addHybridTag(TagKey<Block> blockKey, TagKey<Item> itemKey, TagKey<Block> blockTagToAdd, TagKey<Item> itemTagToAdd){
        addHybridTags(blockKey,itemKey,List.of(blockTagToAdd),List.of(itemTagToAdd));
    }

    public static void addItems(TagKey<Item> itemKey, Block... blocks){
        if(!HYBRID_ITEM_BLOCKS.containsKey(itemKey)) HYBRID_ITEM_BLOCKS.put(itemKey,new ArrayList<>());
        HYBRID_ITEM_BLOCKS.get(itemKey).addAll(List.of(blocks));
    }

    public void actualizeHybrids(){
        for(var entry : HYBRID_BLOCKS.entrySet()){
            var builder = getOrCreateTagBuilder(entry.getKey());
            builder.add(entry.getValue().toArray(new Block[0]));
        }

        for(var entry : HYBRID_TAGS.entrySet()){
            var builder = getOrCreateTagBuilder(entry.getKey());
            for(var tag : entry.getValue())
                builder.forceAddTag(tag);
        }
    }
}