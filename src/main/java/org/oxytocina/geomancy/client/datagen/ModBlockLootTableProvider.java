package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.*;
import net.minecraft.loot.entry.*;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.ModItems;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
    public ModBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {

        addOreDrop(ModBlocks.MITHRIL_ORE,ModBlocks.DEEPSLATE_MITHRIL_ORE,ModItems.RAW_MITHRIL);
        addOreDrop(ModBlocks.OCTANGULITE_ORE,ModBlocks.DEEPSLATE_OCTANGULITE_ORE,ModItems.RAW_OCTANGULITE);
        addOreDrop(ModBlocks.MOLYBDENUM_ORE,ModBlocks.DEEPSLATE_MOLYBDENUM_ORE,ModItems.RAW_MOLYBDENUM);
        addOreDrop(ModBlocks.TITANIUM_ORE,ModBlocks.DEEPSLATE_TITANIUM_ORE,ModItems.RAW_TITANIUM);
        addOreDrop(ModBlocks.LEAD_ORE,ModBlocks.DEEPSLATE_LEAD_ORE,ModItems.RAW_LEAD);

        addOreDrop(ModBlocks.PERIDOT_ORE,ModBlocks.DEEPSLATE_PERIDOT_ORE,ModItems.PERIDOT);
        addOreDrop(ModBlocks.ORTHOCLASE_ORE,ModBlocks.DEEPSLATE_ORTHOCLASE_ORE,ModItems.ORTHOCLASE);
        addOreDrop(ModBlocks.AXINITE_ORE,ModBlocks.DEEPSLATE_AXINITE_ORE,ModItems.AXINITE);
        addOreDrop(ModBlocks.TOURMALINE_ORE,ModBlocks.DEEPSLATE_TOURMALINE_ORE,ModItems.TOURMALINE);

        addLeaves(ModBlocks.SOUL_OAK_LEAVES,ModBlocks.SOUL_OAK_SAPLING, Items.STICK);

        for(Block b : ExtraBlockSettings.RegularDropBlocks){
            addDrop(b);
        }
    }

    private void addOreDrop(Block block, ItemConvertible result){
        addOreDrop(block,result,block);
    }
    private void addOreDrop(Block ore, Block deepslateOre, ItemConvertible result){
        addOreDrop(ore,result,ore);
        addOreDrop(deepslateOre,result,deepslateOre);
    }
    private void addOreDrop(Block block, ItemConvertible fortuneResult, ItemConvertible silktouchResult){
        addDrop(block,LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1f))
                        .with(AlternativeEntry.builder()
                                .alternatively(
                                        ItemEntry.builder(silktouchResult)
                                                .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH,IntRange.atLeast(1)))))
                                )
                                .alternatively(
                                        ItemEntry.builder(fortuneResult)
                                                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
                                                .apply(ExplosionDecayLootFunction.builder())
                                )
                        )
                ).randomSequenceId(ModBlocks.BlockIdentifiers.get(block))
        );
    }

    private void addLeaves(Block block, ItemConvertible sapling, ItemConvertible stick){
        addDrop(block,LootTable.builder()
                // pool 1 : silk touch or sapling
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1f))
                        .with(AlternativeEntry.builder()
                                .alternatively(
                                        ItemEntry.builder(block)
                                                .conditionally(BlockLootTableGenerator.WITH_SILK_TOUCH_OR_SHEARS)
                                )
                                .alternatively(
                                        ItemEntry.builder(sapling)
                                                .conditionally(SurvivesExplosionLootCondition.builder())
                                                .conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE,0.05f, 0.0625f, 0.083333336f, 0.1f))
                                )
                        )
                )
                // pool 2: sticks
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1f))
                        .conditionally(InvertedLootCondition.builder(BlockLootTableGenerator.WITH_SILK_TOUCH_OR_SHEARS))
                        .with(ItemEntry.builder(stick)
                                .conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE,0.02f, 0.022222223f, 0.025f, 0.033333335f, 0.1f))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,2)))
                                .apply(ExplosionDecayLootFunction.builder())
                        )
                ).randomSequenceId(ModBlocks.BlockIdentifiers.get(block))
        );
    }
}
