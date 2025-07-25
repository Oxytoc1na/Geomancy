package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;

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
    }


}