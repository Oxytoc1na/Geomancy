package org.oxytocina.geomancy.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.fluids.GoldFluidBlock;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.items.ModItems;

import java.util.*;
import java.util.function.Function;

public class ModBlocks {


    public static final Hashtable<Block,Identifier> BlockIdentifiers = new Hashtable<Block,Identifier>();


    // ores
    public static final ExperienceDroppingBlock MITHRIL_ORE = (ExperienceDroppingBlock) register("mithril_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
        AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
        ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());
    public static final ExperienceDroppingBlock DEEPSLATE_MITHRIL_ORE = (ExperienceDroppingBlock) register("deepslate_mithril_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
        AbstractBlock.Settings.copy(MITHRIL_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
        ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    public static final ExperienceDroppingBlock OCTANGULITE_ORE = (ExperienceDroppingBlock) register("octangulite_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notSimpleCube(),new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final ExperienceDroppingBlock DEEPSLATE_OCTANGULITE_ORE = (ExperienceDroppingBlock) register("deepslate_octangulite_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.copy(MITHRIL_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notSimpleCube(),new FabricItemSettings().rarity(Rarity.UNCOMMON));


    // raw ore blocks
    public static final Block RAW_MITHRIL_BLOCK = register("raw_mithril_block", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    // ore blocks
    public static final Block MITHRIL_BLOCK = register("mithril_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    // mithril anvil
    public static final Block MITHRIL_ANVIL = register("mithril_anvil",AnvilBlock::new,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL).pistonBehavior(PistonBehavior.BLOCK),ExtraBlockSettings.create().mineableByPickaxe().notSimpleCube(),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    // decorative
    public static final Block GILDED_DEEPSLATE = register("gilded_deepslate", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final Block DECORATED_GILDED_DEEPSLATE = register("decorated_gilded_deepslate", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE),ExtraBlockSettings.create().mineableByPickaxe().hasTextureVariants(4),new FabricItemSettings());
    public static final Block OCTANGULITE_SCRAP = register("octangulite_scrap", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notSimpleCube(),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    // block entities
    public static final SmitheryBlock SMITHERY = (SmitheryBlock) register("smithery_block", SmitheryBlock::new, AbstractBlock.Settings.create().strength(3.0F, 6.0F).nonOpaque(), new ExtraBlockSettings().notSimpleCube().mineableByPickaxe());

    // fluids
    private static AbstractBlock.Settings fluid(MapColor mapColor) {
        return AbstractBlock.Settings.create().mapColor(mapColor).replaceable().noCollision().pistonBehavior(PistonBehavior.DESTROY).liquid();
    }

    public static final Block MOLTEN_GOLD = register("molten_gold", settings -> new GoldFluidBlock(ModFluids.MOLTEN_GOLD,null,settings),fluid(MapColor.GOLD).luminance(value -> 15).replaceable());


    public static void register(){
        ItemGroupEvents.modifyEntriesEvent(ModItems.CUSTOM_ITEM_GROUP_KEY).register((itemGroup) -> {

            for(Block b : ExtraBlockSettings.BlocksInGroup){
                itemGroup.add(b.asItem());
            }

        });
    }

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings) {
        return register(name, blockFactory, settings,new ExtraBlockSettings(),new Item.Settings());
    }
    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings,ExtraBlockSettings extraSettings) {
        return register(name, blockFactory, settings,extraSettings,new Item.Settings());
    }
    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings,ExtraBlockSettings extraSettings, Item.Settings itemSettings) {
        // Create a registry key for the block
        RegistryKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        Block block = blockFactory.apply(settings);

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (extraSettings.shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, itemSettings);
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        var res = Registry.register(Registries.BLOCK, blockKey, block);

        BlockIdentifiers.put(res,blockKey.getValue());

        extraSettings.setBlock(res).apply();

        return res;
    }
    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Geomancy.MOD_ID, name));
    }
    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Geomancy.MOD_ID, name));
    }
}