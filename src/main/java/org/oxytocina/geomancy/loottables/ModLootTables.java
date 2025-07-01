package org.oxytocina.geomancy.loottables;

import com.google.common.collect.Sets;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.ModItems;

import java.util.HashMap;
import java.util.Set;

public class ModLootTables {
    public static final HashMap<Identifier, LootTable> LOOT_TABLES = new HashMap<>();
    public static final HashMap<Identifier, LootTable.Builder> LOOT_TABLE_BUILDERS = new HashMap<>();

    public static final LootTable DWARVEN_REMNANTS_CHEST;
    public static final LootTable GEODE_STONE;

    static {
        // DWARVEN_REMNANTS_CHEST
        {
            DWARVEN_REMNANTS_CHEST = register("chests/dwarven_remnants", LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(-1.0F, 1.0F))
                        .with(ItemEntry.builder(
                                ModItems.MUSIC_DISC_DIGGY)))
                .pool(LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
                        .with(ItemEntry.builder(
                                        ModItems.RAW_MITHRIL)
                                .weight(20)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(1.0F, 4.0F))))
                        .with(ItemEntry.builder(
                                        ModItems.MITHRIL_INGOT)
                                .weight(5)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                        .with(ItemEntry.builder(
                                        ModItems.MITHRIL_NUGGET)
                                .weight(10)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 16.0F)))))
                .pool(LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
                        .with(ItemEntry.builder(
                                        ModBlocks.GILDED_DEEPSLATE)
                                .weight(20)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(3.0F, 8.0F))))
                        .with(ItemEntry.builder(
                                        ModBlocks.DECORATED_GILDED_DEEPSLATE)
                                .weight(10)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F)))))
                .pool(LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(3.0F, 7.0F))
                        .with(ItemEntry.builder(
                                        Items.EMERALD)
                                .weight(5)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))))
                        .with(ItemEntry.builder(
                                        Items.DIAMOND)
                                .weight(5)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                        .with(ItemEntry.builder(
                                        Items.GOLD_INGOT)
                                .weight(5)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F)))))
                .pool(LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
                        .with(ItemEntry.builder(
                                Items.IRON_HELMET))
                        .with(ItemEntry.builder(
                                Items.IRON_CHESTPLATE))
                        .with(ItemEntry.builder(
                                Items.IRON_LEGGINGS))
                        .with(ItemEntry.builder(
                                Items.IRON_BOOTS))
                        .with(ItemEntry.builder(
                                Items.IRON_SWORD))
                        .with(ItemEntry.builder(
                                Items.IRON_PICKAXE))
                        .with(ItemEntry.builder(
                                Items.IRON_AXE))));
        }

        //GEODE_STONE
        {
            GEODE_STONE = register("geodes/stone", LootTable.builder()
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1.0F, 1.0F))
                            .with(ItemEntry.builder(
                                            Items.EMERALD)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            Items.DIAMOND)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            Items.LAPIS_LAZULI)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))));
        }
    }

    private static LootTable register(String id,LootTable.Builder builder) {
        return register(new Identifier(Geomancy.MOD_ID,id),builder);
    }

    private static LootTable register(Identifier id,LootTable.Builder builder) {

        LOOT_TABLES.put(id,builder.build());
        LOOT_TABLE_BUILDERS.put(id,builder);
        return LOOT_TABLES.get(id);

    }

    public static void register() {

    }
}
