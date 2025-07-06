package org.oxytocina.geomancy.loottables;

import com.google.common.collect.Sets;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.items.ModItems;

import java.util.HashMap;
import java.util.Set;

public class ModLootTables {
    public static final HashMap<Identifier, LootTable> LOOT_TABLES = new HashMap<>();
    public static final HashMap<Identifier, LootTable.Builder> LOOT_TABLE_BUILDERS = new HashMap<>();

    public static final LootTable DWARVEN_REMNANTS_CHEST;
    public static final LootTable GEODE_STONE;

    public static final LootTable ANCIENT_HALL_SMITHERY_CHEST;
    public static final LootTable ANCIENT_HALL_STORAGE_CHEST;
    public static final LootTable ANCIENT_HALL_KITCHEN_CHEST;
    public static final LootTable ANCIENT_HALL_FOOD_CHEST;
    public static final LootTable ANCIENT_HALL_BARRACKS_CHEST;

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

        // ANCIENT_HALL_SMITHERY_CHEST
        {
            ANCIENT_HALL_SMITHERY_CHEST = register("chests/ancient_hall_smithery", LootTable.builder()
                    // mithril
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(3.0F, 6.0F))
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
                    // vanilla treasure
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(3.0F, 7.0F))
                            .with(ItemEntry.builder(
                                            Items.EMERALD)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))))
                            .with(ItemEntry.builder(
                                            Items.DIAMOND)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
                            .with(ItemEntry.builder(
                                            Items.GOLD_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
                            .with(ItemEntry.builder(
                                            Items.IRON_INGOT)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 10.0F))))
                            .with(ItemEntry.builder(
                                            Items.LAPIS_LAZULI)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F)))))
                    // buckets
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
                            .with(ItemEntry.builder(
                                    ModFluids.MOLTEN_GOLD_BUCKET))
                            .with(ItemEntry.builder(
                                    Items.BUCKET)))
                    // hammers
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                            .with(ItemEntry.builder(
                                    ModItems.IRON_HAMMER)
                                    .weight(10)
                                    .apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 25.0F)))
                                    .apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.0f,0.5f)))
                            )
                            .with(ItemEntry.builder(
                                            ModItems.MITHRIL_HAMMER)
                                    .weight(1)
                                    .apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)))
                                    .apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.0f,0.5f)))
                            ))
            );
        }

        // ANCIENT_HALL_STORAGE_CHEST
        {
            ANCIENT_HALL_STORAGE_CHEST = register("chests/ancient_hall_storage", LootTable.builder()
                    // treasure
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(3.0F, 7.0F))
                            .with(ItemEntry.builder(
                                            Items.EMERALD)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))))
                            .with(ItemEntry.builder(
                                            Items.DIAMOND)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
                            .with(ItemEntry.builder(
                                            Items.GOLD_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
                            .with(ItemEntry.builder(
                                            Items.IRON_INGOT)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F,10.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.MITHRIL_INGOT)
                                    .weight(2)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
                            .with(ItemEntry.builder(
                                            Items.LAPIS_LAZULI)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
                    )
                    // special treasure
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(0.0F, 3.0F))
                            .with(ItemEntry.builder(
                                        ModFluids.MOLTEN_GOLD_BUCKET)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            ModItems.EMPTY_ARTIFACT)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            ModItems.MUSIC_DISC_DIGGY)
                                    .weight(10))
                    )
            );
        }

        // ANCIENT_HALL_KITCHEN_CHEST
        {
            ANCIENT_HALL_KITCHEN_CHEST = register("chests/ancient_hall_kitchen", LootTable.builder()
                    // food
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(3.0F, 6.0F))
                            .with(ItemEntry.builder(
                                            Items.ROTTEN_FLESH)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.POTATO)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.POISONOUS_POTATO)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            Items.HONEY_BOTTLE)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            Items.POTION)
                                    .weight(5)
                                    .apply(
                                            SetPotionLootFunction.builder(Potions.POISON))
                            )
                    )
                    // tools
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
                            .with(ItemEntry.builder(
                                            Items.IRON_SWORD)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            Items.BUCKET)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            Items.FLINT_AND_STEEL)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            Items.WOODEN_SHOVEL)
                                    .weight(10))
                    )
                    // dishes
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(3.0F, 6.0F))
                            .with(ItemEntry.builder(
                                            Items.BOWL)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.GLASS_BOTTLE)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                    )
            );
        }

        // ANCIENT_HALL_FOOD_CHEST
        {
            ANCIENT_HALL_FOOD_CHEST = register("chests/ancient_hall_food", LootTable.builder()
                    // food
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(4.0F, 9.0F))
                            .with(ItemEntry.builder(
                                            Items.ROTTEN_FLESH)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.POTATO)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.POISONOUS_POTATO)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            Items.HONEY_BOTTLE)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            Items.POTION)
                                    .weight(5)
                                    .apply(
                                            SetPotionLootFunction.builder(Potions.POISON))
                            )
                            .with(ItemEntry.builder(
                                            Items.BREAD)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                    )
                    // tools
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
                            .with(ItemEntry.builder(
                                            Items.IRON_SWORD)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            Items.BUCKET)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            Items.FLINT_AND_STEEL)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            Items.WOODEN_SHOVEL)
                                    .weight(10))
                    )
                    // dishes
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(3.0F, 6.0F))
                            .with(ItemEntry.builder(
                                            Items.BOWL)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.GLASS_BOTTLE)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                    )
                    // seeds
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(3.0F, 6.0F))
                            .with(ItemEntry.builder(
                                            Items.WHEAT_SEEDS)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.MELON_SEEDS)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.BEETROOT_SEEDS)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.PUMPKIN_SEEDS)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 6.0F))))
                            .with(ItemEntry.builder(
                                            Items.TORCHFLOWER_SEEDS)
                                    .weight(1)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 2.0F))))
                    )
            );
        }

        // ANCIENT_HALL_BARRACKS_CHEST
        {
            ANCIENT_HALL_BARRACKS_CHEST = register("chests/ancient_hall_barracks", LootTable.builder()
                    // belongings
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(3.0F, 7.0F))
                            .with(ItemEntry.builder(
                                            Items.CLOCK)
                                    .weight(5))
                            .with(ItemEntry.builder(
                                            Items.COMPASS)
                                    .weight(5))
                            .with(ItemEntry.builder(
                                            Items.BOOK)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            Items.INK_SAC)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(
                                            Items.FEATHER)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                    )
                    // special treasure
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(0.0F, 3.0F))
                            .with(ItemEntry.builder(
                                            ModFluids.MOLTEN_GOLD_BUCKET)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            ModItems.EMPTY_ARTIFACT)
                                    .weight(10))
                            .with(ItemEntry.builder(
                                            ModItems.MUSIC_DISC_DIGGY)
                                    .weight(10))
                    )
            );
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
