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

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider<Block> {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK, registriesFuture);
    }

    public static void precalcHybrids() {
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

        addHybridTag(BlockTags.LOGS_THAT_BURN,ItemTags.LOGS_THAT_BURN,SOUL_OAK_LOGS, ModItemTags.SOUL_OAK_LOGS);
        addHybrid(BlockTags.LEAVES,ItemTags.LEAVES,ModBlocks.SOUL_OAK_LEAVES);
        addHybrid(BlockTags.SAPLINGS,ItemTags.SAPLINGS,ModBlocks.SOUL_OAK_SAPLING);

        // ores
        addHybrid(LEAD_ORES,ModItemTags.LEAD_ORES,ModBlocks.LEAD_ORE,ModBlocks.DEEPSLATE_LEAD_ORE);
        addHybrid(MOLYBDENUM_ORES,ModItemTags.MOLYBDENUM_ORES,ModBlocks.MOLYBDENUM_ORE,ModBlocks.DEEPSLATE_MOLYBDENUM_ORE);
        addHybrid(TITANIUM_ORES,ModItemTags.TITANIUM_ORES,ModBlocks.TITANIUM_ORE,ModBlocks.DEEPSLATE_TITANIUM_ORE);
        addHybrid(MITHRIL_ORES,ModItemTags.MITHRIL_ORES,ModBlocks.MITHRIL_ORE,ModBlocks.DEEPSLATE_MITHRIL_ORE);
        addHybrid(OCTANGULITE_ORES,ModItemTags.OCTANGULITE_ORES,ModBlocks.OCTANGULITE_ORE,ModBlocks.DEEPSLATE_OCTANGULITE_ORE);

        addHybridTags(C_ORES,ModItemTags.C_ORES,
                List.of(LEAD_ORES,MOLYBDENUM_ORES,TITANIUM_ORES,MITHRIL_ORES,OCTANGULITE_ORES),
                List.of(ModItemTags.LEAD_ORES,ModItemTags.MOLYBDENUM_ORES,ModItemTags.TITANIUM_ORES,ModItemTags.MITHRIL_ORES,ModItemTags.OCTANGULITE_ORES));


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
    public void addHybrid(TagKey<Block> blockKey, TagKey<Item> itemKey, Block... blocks){
        getOrCreateTagBuilder(blockKey).add(blocks);
        addItems(itemKey,blocks);
    }

    public void addHybridTags(TagKey<Block> blockKey, TagKey<Item> itemKey, List<TagKey<Block>> blockTagsToAdd, List<TagKey<Item>> itemTagsToAdd) {
        var blockBuilder = getOrCreateTagBuilder(blockKey);
        var itemBuilder = getItemBuilder(itemKey);

        for(var bt : blockTagsToAdd) blockBuilder.forceAddTag(bt);
        for(var it : itemTagsToAdd) itemBuilder.forceAddTag(it);
    }


    public void addHybridTag(TagKey<Block> blockKey, TagKey<Item> itemKey, TagKey<Block> blockTagToAdd, TagKey<Item> itemTagToAdd){
        getOrCreateTagBuilder(blockKey).forceAddTag(blockTagToAdd);
        getItemBuilder(itemKey).forceAddTag(itemTagToAdd);
    }

    public void addItems(TagKey<Item> itemKey, Block... blocks){
        var itemBuilder = getItemBuilder(itemKey);
        for(var block : blocks) itemBuilder.add(block.asItem());
    }

    public FabricTagProvider<Item>.FabricTagBuilder getItemBuilder(TagKey<Item> itemKey) {
        return ModItemTagProvider.INSTANCE.getBuilder(itemKey);
    }
}