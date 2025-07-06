package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
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

        addOreDrop(ModBlocks.MITHRIL_ORE,ModItems.RAW_MITHRIL);
        addOreDrop(ModBlocks.DEEPSLATE_MITHRIL_ORE,ModItems.RAW_MITHRIL);

        addOreDrop(ModBlocks.OCTANGULITE_ORE,ModItems.RAW_OCTANGULITE);
        addOreDrop(ModBlocks.DEEPSLATE_OCTANGULITE_ORE,ModItems.RAW_OCTANGULITE);

        addOreDrop(ModBlocks.MOLYBDENIUM_ORE,ModItems.RAW_MOLYBDENIUM);
        addOreDrop(ModBlocks.DEEPSLATE_MOLYBDENIUM_ORE,ModItems.RAW_MOLYBDENIUM);

        for(Block b : ExtraBlockSettings.RegularDropBlocks){
            addDrop(b);
        }
    }

    private void addOreDrop(Block block, ItemConvertible result){
        addOreDrop(block,result,block);
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
}
