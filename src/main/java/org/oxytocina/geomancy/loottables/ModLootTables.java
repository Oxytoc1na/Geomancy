package org.oxytocina.geomancy.loottables;

import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.helpers.NbtHelper;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.jewelry.GemSlot;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.spells.SpellSignal;

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

    public static final LootTable OCTANGULA_HALL_CHEST;
    public static final LootTable OCTANGULA_SPELLMAKER_CHEST;

    public static final LootTable DIGSITE_HALLWAY_CHEST;

    static {
        // DWARVEN_REMNANTS_CHEST
        {
            DWARVEN_REMNANTS_CHEST = register("chests/dwarven_remnants", LootTable.builder()
                // ancient hall treasure map
                .pool(LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1.0F, 1.0F))
                        .with(ItemEntry.builder(
                                Items.MAP)
                                .apply(ExplorationMapLootFunction.builder()
                                        .withDestination(TagKey.of(RegistryKeys.STRUCTURE,Geomancy.locate("on_ancient_hall_map")))
                                        .withDecoration(MapIcon.Type.BANNER_YELLOW)
                                        .withSkipExistingChunks(false)
                                        .withZoom((byte)1)
                                )
                                .apply(SetNameLootFunction.builder(Text.translatable("item.geomancy.explorers_map.ancient_hall")))
                        )

                )
                    // lore
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(-1,1))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_1)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_2)
                                    .weight(7)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_3)
                                    .weight(4)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_4)
                                    .weight(2)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
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
                                Items.IRON_AXE)))
                    .randomSequenceId(Geomancy.locate("chests/dwarven_remnants"))
            );
        }

        //GEODE_STONE
        {
            GEODE_STONE = register("geodes/stone", LootTable.builder()
                    .pool(LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(1))
                            .with(ItemEntry.builder(Items.COBBLESTONE).weight(100)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(Items.COAL).weight(100)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(Items.IRON_ORE).weight(50)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(Items.GOLD_ORE).weight(50)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(Items.LAPIS_LAZULI).weight(50)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(ModItems.TOURMALINE).weight(50)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(ModItems.AXINITE).weight(50)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(ModItems.ORTHOCLASE).weight(50)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(ModItems.PERIDOT).weight(50)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(Items.REDSTONE).weight(50)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
                            .with(ItemEntry.builder(Items.EMERALD).weight(40)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(Items.DIAMOND).weight(20)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(10)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(ModItems.MUSIC_DISC_DIGGY).weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(Items.NETHER_STAR).weight(1)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 1.0F))))
                    )
            );
        }

        // ANCIENT_HALL_SMITHERY_CHEST
        {
            ANCIENT_HALL_SMITHERY_CHEST = register("chests/ancient_hall_smithery", LootTable.builder()
                    // lore
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(0,1))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_1)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_2)
                                    .weight(9)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_3)
                                    .weight(8)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_4)
                                    .weight(7)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
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
                    .pool(jewelryBuilder().rolls(UniformLootNumberProvider.create(1f,3f)))
            );
        }

        // ANCIENT_HALL_STORAGE_CHEST
        {
            ANCIENT_HALL_STORAGE_CHEST = register("chests/ancient_hall_storage", LootTable.builder()
                    // lore (war)
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1,1))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_WAR_1)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_WAR_2)
                                    .weight(9)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_WAR_3)
                                    .weight(8)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
                    // lore
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(0,1))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_1)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_2)
                                    .weight(9)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_3)
                                    .weight(8)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_4)
                                    .weight(7)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
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
                    // lore (war)
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(-1,1))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_WAR_1)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_WAR_2)
                                    .weight(9)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_WAR_3)
                                    .weight(8)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
                    // lore
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(0,1))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_1)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_2)
                                    .weight(9)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_3)
                                    .weight(8)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_BOOK_GOLDSMITH_4)
                                    .weight(7)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
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

        // OCTANGULA_HALL_CHEST
        {
            OCTANGULA_HALL_CHEST = register("chests/octangula_hall", LootTable.builder()
                    // lore
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(0,1))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_1)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_2)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_3)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_4)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_5)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
                    // octangulite
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(0.0F, 2.0F))
                            .with(ItemEntry.builder(
                                            ModItems.RAW_OCTANGULITE)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(0.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.OCTANGULITE_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.OCTANGULITE_NUGGET)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F)))))
                    // blocks
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
                            .with(ItemEntry.builder(
                                            ModBlocks.CUT_TITANIUM)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(0.0F, 8.0F))))
                            .with(ItemEntry.builder(
                                            Blocks.OXIDIZED_CUT_COPPER)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(0.0F, 8.0F))))
                    )
                    // treasure
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
                            .with(ItemEntry.builder(
                                            Items.EMERALD)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            Items.GOLD_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            Items.IRON_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.LEAD_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                    )
                    .pool(spellComponentsBuilder()
                            .rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
                    )
                    .randomSequenceId(Geomancy.locate("chests/dwarven_remnants"))
            );
        }

        // OCTANGULA_SPELLMAKER_CHEST
        {
            OCTANGULA_SPELLMAKER_CHEST = register("chests/octangula_spellmaker", LootTable.builder()
                    // lore
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1,2))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_1)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_2)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_3)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_4)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_5)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
                    // digsite treasure map
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1.0F, 1.0F))
                            .with(ItemEntry.builder(
                                            Items.MAP)
                                    .apply(ExplorationMapLootFunction.builder()
                                            .withDestination(TagKey.of(RegistryKeys.STRUCTURE,Geomancy.locate("on_digsite_map")))
                                            .withDecoration(MapIcon.Type.TARGET_POINT)
                                            .withSkipExistingChunks(false)
                                            .withZoom((byte)1)
                                    )
                                    .apply(SetNameLootFunction.builder(Text.translatable("item.geomancy.explorers_map.digsite")))
                            )

                    )
                    // components
                    .pool(spellComponentsBuilder()
                            .rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
                    )
                    // premade spells
                    .pool(premadeSpellsBuilder()
                            .rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
                    )
                    .randomSequenceId(Geomancy.locate("chests/dwarven_remnants"))
            );
        }

        // DIGSITE_HALLWAY_CHEST
        {
            DIGSITE_HALLWAY_CHEST = register("chests/digsite_hallway", LootTable.builder()
                    // lore
                    .pool(LootPool.builder()
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXODIA_1)
                                    .weight(10)
                                    .conditionally(RandomChanceLootCondition.builder(0.1f))
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(-3,1))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_1)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_2)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_3)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_4)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                            .with(ItemEntry.builder(
                                            ModItems.LORE_LOG_EXPEDITION_5)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                    )
                    // octangulite
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(0.0F, 2.0F))
                            .with(ItemEntry.builder(
                                            ModItems.RAW_OCTANGULITE)
                                    .weight(20)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(1.0F, 4.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.OCTANGULITE_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.OCTANGULITE_NUGGET)
                                    .weight(10)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 16.0F)))))
                    // blocks
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                            .with(ItemEntry.builder(
                                            ModBlocks.CUT_TITANIUM)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(3.0F, 8.0F))))
                            .with(ItemEntry.builder(
                                            ModBlocks.CUT_LEAD)
                                    .apply(SetCountLootFunction.builder(
                                            UniformLootNumberProvider.create(3.0F, 8.0F))))
                    )
                    // generic treasure
                    .pool(LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(-1.0F, 2.0F))
                            .with(ItemEntry.builder(
                                            Items.EMERALD)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            Items.DIAMOND)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F))))
                            .with(ItemEntry.builder(
                                            Items.GOLD_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.TITANIUM_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.LEAD_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.MOLYBDENUM_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.MITHRIL_INGOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.ORTHOCLASE)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.PERIDOT)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.TOURMALINE)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                            .with(ItemEntry.builder(
                                            ModItems.AXINITE)
                                    .weight(5)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                    )
                    // spell component
                    .pool(spellComponentsBuilder()
                            .rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
                    )
                    // component pouch
                    .pool(LootPool.builder()
                            .with(ItemEntry.builder(
                                            ModItems.COMPONENT_POUCH)
                                    .weight(10)
                                    .conditionally(RandomChanceLootCondition.builder(0.05f))
                                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
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
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if(!source.isBuiltin()) return;

            // register stone geodes
            if (Blocks.STONE.getLootTableId().equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry.builder(ModItems.STONE_GEODE)
                        .conditionally(RandomChanceLootCondition.builder(1/300f))
                );
                tableBuilder.pool(poolBuilder);
                return;
            }

            // exodia 1 can be found in digsite chests

            // exodia 2
            if (LootTables.BASTION_TREASURE_CHEST.equals(id)) {
                tableBuilder.pool(LootPool.builder().with(ItemEntry.builder(ModItems.LORE_LOG_EXODIA_2)));
                return;
            }

            // exodia 3
            if (LootTables.ANCIENT_CITY_CHEST.equals(id)) {
                tableBuilder.pool(LootPool.builder().with(ItemEntry.builder(ModItems.LORE_LOG_EXODIA_3).conditionally(RandomChanceLootCondition.builder(1/10f))));
                return;
            }

            // exodia 4
            if (LootTables.END_CITY_TREASURE_CHEST.equals(id)) {
                tableBuilder.pool(LootPool.builder().with(ItemEntry.builder(ModItems.LORE_LOG_EXODIA_4).conditionally(RandomChanceLootCondition.builder(1/4f))));
                return;
            }
        });
    }

    private static LootPool.Builder spellComponentsBuilder(){
        var res = LootPool.builder();

        for(var spell : SpellBlocks.functions.values()){
            NbtCompound nbt = new NbtCompound();
            nbt.put("component",SpellComponentStoringItem.getNbtFor(spell));
            res.with(ItemEntry.builder(ModItems.SPELLCOMPONENT)
                    .weight(spell.defaultLootWeight)
                    .apply(SetNbtLootFunction.builder(nbt)));
        }

        return res;
    }

    private static LootPool.Builder premadeSpellsBuilder(){
        var res = LootPool.builder();

        // broken fireball
        // broken blink (up)

        final HashMap<SpellGrid,Integer> spells = new HashMap<>();

        // hello world
        {
            spells.put(SpellGrid.builder("hello world")
                            .dim(ModItems.SPELLSTORAGE_SMALL)
                            .add(SpellComponent.builder(SpellBlocks.CONST_TEXT)
                                    .pos(0,1)
                                    .conf(SpellComponent.confBuilder("e").mode(SpellComponent.SideConfig.Mode.Output))
                            )
                            .add(SpellComponent.builder(SpellBlocks.PRINT)
                                    .pos(1,1)
                                    .conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Input))
                            )
                    .build(),1);
        }

        // into block teleport
        {
            spells.put(SpellGrid.builder("teleport")
                    .dim(ModItems.SPELLSTORAGE_SMALL)
                    .add(SpellComponent.builder(SpellBlocks.ENTITY_CASTER)
                            .pos(0,1)
                            .conf(SpellComponent.confBuilder("ne").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .add(SpellComponent.builder(SpellBlocks.TELEPORT)
                            .pos(1,0)
                            .conf(SpellComponent.confBuilder("sw","entity").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("se","position").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .add(SpellComponent.builder(SpellBlocks.RAYCAST_POS)
                            .pos(1,1)
                            .conf(SpellComponent.confBuilder("nw").mode(SpellComponent.SideConfig.Mode.Output))
                            .conf(SpellComponent.confBuilder("sw","length").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("se","dir").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("e","from").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .add(SpellComponent.builder(SpellBlocks.CONST_NUM)
                            .pos(1,2)
                            .conf(SpellComponent.confBuilder("ne").mode(SpellComponent.SideConfig.Mode.Output))
                            .param("val", SpellSignal.createNumber(100))
                    )
                    .add(SpellComponent.builder(SpellBlocks.DIR_CASTER)
                            .pos(2,2)
                            .conf(SpellComponent.confBuilder("nw").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .add(SpellComponent.builder(SpellBlocks.POS_CASTER)
                            .pos(2,1)
                            .conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .build(), 1);
        }

        // self lightning
        {
            spells.put(SpellGrid.builder("lightning")
                    .dim(ModItems.SPELLSTORAGE_SMALL)
                    .add(SpellComponent.builder(SpellBlocks.LIGHTNING)
                            .pos(1,1)
                            .conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .add(SpellComponent.builder(SpellBlocks.POS_CASTER)
                            .pos(0,1)
                            .conf(SpellComponent.confBuilder("e").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .build(),1);
        }

        for(var spell : spells.keySet()){
            int weight = spells.get(spell);
            NbtCompound nbt = new NbtCompound();
            try {
                spell.writeNbt(nbt);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            res.with(ItemEntry.builder(ModItems.SPELLSTORAGE_SMALL)
                    .weight(weight)
                    .apply(SetNbtLootFunction.builder(nbt)));
        }

        return res;
    }

    private static LootPool.Builder jewelryBuilder(){
        var res = LootPool.builder();

        HashMap<JewelryItem,Integer> weighted = new HashMap<>();

        for(var jewelryItem : weighted.keySet()){
            int weight = weighted.get(jewelryItem);
            var entryBuilder = ItemEntry.builder(jewelryItem)
                    .weight(weight);
            entryBuilder.apply(JewelryLootFunction.builder().conditionally(RandomChanceLootCondition.builder(0.8f)));
            res.with(entryBuilder);
        }

        return res;
    }
}
