package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.registries.ModBlockTags;

import static org.oxytocina.geomancy.registries.ModBlockTags.*;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider<Block> {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK, registriesFuture);
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

        // walls
        builder = getOrCreateTagBuilder(WALLS).setReplace(false);
        for(Block b : ExtraBlockSettings.WallBlocks.keySet()){
            builder.add(b);
        }

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
        addSoulBlock(Blocks.VINE,SoulLevel.Normal);
        addSoulBlock(Blocks.KELP,SoulLevel.Normal);
        addSoulBlock(Blocks.KELP_PLANT,SoulLevel.Normal);
        addSoulBlock(Blocks.TALL_GRASS,SoulLevel.Normal);
        addSoulBlock(Blocks.TALL_SEAGRASS,SoulLevel.Normal);
        addSoulBlock(Blocks.GRASS,SoulLevel.Normal);
        addSoulBlock(Blocks.SEAGRASS,SoulLevel.Normal);
        addSoulBlock(Blocks.SEA_PICKLE,SoulLevel.Normal);

        addSoulTag(BlockTags.BEEHIVES,SoulLevel.Many);
        addSoulTag(OCTANGULITE,SoulLevel.Many);


    }
    void addSoulBlock(Block block) { addSoulBlock(block,SoulLevel.Normal); }
    void addSoulBlock(Block block,SoulLevel level){
        var builder_main = getOrCreateTagBuilder(ADDS_SOULS).setReplace(false);
        var builder_sub = getOrCreateTagBuilder(switch(level){
            case Few -> ADDS_SOULS_FEW;
            case Normal -> ADDS_SOULS_NORMAL;
            case Many -> ADDS_SOULS_MANY;
        }).setReplace(false);

        builder_main.add(block);
        builder_sub.add(block);
    }
    void addSoulTag(TagKey<Block> tag, SoulLevel level){
        var builder_main = getOrCreateTagBuilder(ADDS_SOULS).setReplace(false);
        var builder_sub = getOrCreateTagBuilder(switch(level){
            case Few -> ADDS_SOULS_FEW;
            case Normal -> ADDS_SOULS_NORMAL;
            case Many -> ADDS_SOULS_MANY;
        }).setReplace(false);

        builder_main.forceAddTag(tag);
        builder_sub.forceAddTag(tag);
    }
    enum SoulLevel{
        Few,
        Normal,
        Many
    }

}