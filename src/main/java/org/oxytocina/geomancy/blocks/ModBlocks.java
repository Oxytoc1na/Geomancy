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
import org.oxytocina.geomancy.items.LeadBlockItem;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.OctanguliteBlockItem;
import org.oxytocina.geomancy.sound.ModBlockSoundGroups;

import java.util.*;
import java.util.function.Function;

public class ModBlocks {


    public static final Hashtable<Block,Identifier> BlockIdentifiers = new Hashtable<Block,Identifier>();


    // ores
    public static final ExperienceDroppingBlock MITHRIL_ORE = (ExperienceDroppingBlock) register("mithril_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
        AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
        ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());
    public static final ExperienceDroppingBlock DEEPSLATE_MITHRIL_ORE = (ExperienceDroppingBlock) register("deepslate_mithril_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
        AbstractBlock.Settings.copy(MITHRIL_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
        ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    public static final OctanguliteExperienceDroppingBlock OCTANGULITE_ORE = (OctanguliteExperienceDroppingBlock) register("octangulite_ore", (AbstractBlock.Settings s) -> new OctanguliteExperienceDroppingBlock(s, UniformIntProvider.create(9,13),1),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(ModBlockSoundGroups.STONE_WHISPERS),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notSimpleCube().notRegularDrop(),new FabricItemSettings().rarity(Rarity.RARE));
    public static final OctanguliteExperienceDroppingBlock DEEPSLATE_OCTANGULITE_ORE = (OctanguliteExperienceDroppingBlock) register("deepslate_octangulite_ore", (AbstractBlock.Settings s) -> new OctanguliteExperienceDroppingBlock(s, UniformIntProvider.create(9,13),1),
            AbstractBlock.Settings.copy(OCTANGULITE_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(ModBlockSoundGroups.DEEPSLATE_WHISPERS),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notSimpleCube().notRegularDrop(),new FabricItemSettings().rarity(Rarity.RARE));

    public static final ExperienceDroppingBlock MOLYBDENUM_ORE = (ExperienceDroppingBlock) register("molybdenum_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));
    public static final ExperienceDroppingBlock DEEPSLATE_MOLYBDENUM_ORE = (ExperienceDroppingBlock) register("deepslate_molybdenum_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.copy(MOLYBDENUM_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));

    public static final ExperienceDroppingBlock TITANIUM_ORE = (ExperienceDroppingBlock) register("titanium_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));
    public static final ExperienceDroppingBlock DEEPSLATE_TITANIUM_ORE = (ExperienceDroppingBlock) register("deepslate_titanium_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.copy(TITANIUM_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));

    public static final LeadOre LEAD_ORE = (LeadOre) register("lead_ore", (AbstractBlock.Settings s) -> new LeadOre(s, UniformIntProvider.create(9,13),1),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));
    public static final LeadOre DEEPSLATE_LEAD_ORE = (LeadOre) register("deepslate_lead_ore", (AbstractBlock.Settings s) -> new LeadOre(s, UniformIntProvider.create(9,13),1),
            AbstractBlock.Settings.copy(LEAD_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));


    // raw ore blocks
    public static final Block RAW_MITHRIL_BLOCK = register("raw_mithril_block", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());
    public static final OctanguliteBlock RAW_OCTANGULITE_BLOCK = (OctanguliteBlock) register("raw_octangulite_block", (settings -> new OctanguliteBlock(settings,5)), AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F).sounds(ModBlockSoundGroups.STONE_WHISPERS),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notSimpleCube(),new FabricItemSettings().rarity(Rarity.RARE).fireproof());
    public static final Block RAW_MOLYBDENUM_BLOCK = register("raw_molybdenum_block", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final Block RAW_TITANIUM_BLOCK = register("raw_titanium_block", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final LeadBlock RAW_LEAD_BLOCK = (LeadBlock) register("raw_lead_block", (settings -> new LeadBlock(settings,5)), AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());

    // ore blocks
    public static final Block MITHRIL_BLOCK = register("mithril_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());
    public static final OctanguliteBlock OCTANGULITE_BLOCK = (OctanguliteBlock)register("octangulite_block", (settings -> new OctanguliteBlock(settings,5)),AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(ModBlockSoundGroups.METAL_WHISPERS),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notSimpleCube(),new FabricItemSettings().rarity(Rarity.RARE).fireproof());
    public static final Block MOLYBDENUM_BLOCK = register("molybdenum_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final Block TITANIUM_BLOCK = register("titanium_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final LeadBlock LEAD_BLOCK = (LeadBlock) register("lead_block", (settings -> new LeadBlock(settings,5)),AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());

    // mithril anvil
    public static final Block MITHRIL_ANVIL = register("mithril_anvil",AnvilBlock::new,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL).pistonBehavior(PistonBehavior.BLOCK),ExtraBlockSettings.create().mineableByPickaxe().notSimpleCube(),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    // decorative
    public static final Block GILDED_DEEPSLATE = register("gilded_deepslate", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final Block DECORATED_GILDED_DEEPSLATE = register("decorated_gilded_deepslate", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE),ExtraBlockSettings.create().mineableByPickaxe().hasTextureVariants(4),new FabricItemSettings());
    public static final Block CUT_TITANIUM = register("cut_titanium", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).instrument(Instrument.BELL).requiresTool(),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final Block MOSSY_CUT_TITANIUM = register("mossy_cut_titanium", Block::new, AbstractBlock.Settings.copy(CUT_TITANIUM).mapColor(MapColor.PALE_GREEN),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());

    public static final LeadBlock CUT_LEAD = (LeadBlock)register("cut_lead", (settings -> new LeadBlock(settings,1)), AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).instrument(Instrument.BELL).requiresTool(),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final LeadBlock LEAD_BRICKS = (LeadBlock)register("lead_bricks", (settings -> new LeadBlock(settings,1)), AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).instrument(Instrument.BELL).requiresTool(),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final LeadStairsBlock LEAD_BRICK_STAIRS = (LeadStairsBlock)register("lead_brick_stairs", (settings -> new LeadStairsBlock(LEAD_BRICKS.getDefaultState(), settings,1)), AbstractBlock.Settings.copy(LEAD_BRICKS),ExtraBlockSettings.create().mineableByPickaxe().stairs(LEAD_BRICKS),new FabricItemSettings());
    public static final LeadSlabBlock LEAD_BRICK_SLABS = (LeadSlabBlock)register("lead_brick_slab", (settings -> new LeadSlabBlock(settings,1)), AbstractBlock.Settings.copy(LEAD_BRICKS),ExtraBlockSettings.create().mineableByPickaxe().slab(LEAD_BRICKS),new FabricItemSettings());

    // block entities
    public static final SmitheryBlock SMITHERY = (SmitheryBlock) register("smithery_block", SmitheryBlock::new, AbstractBlock.Settings.create().strength(3.0F, 6.0F).nonOpaque(), new ExtraBlockSettings().notSimpleCube().mineableByPickaxe());
    public static final SpellmakerBlock SPELLMAKER = (SpellmakerBlock) register("spellmaker_block", SpellmakerBlock::new, AbstractBlock.Settings.create().strength(3.0F, 6.0F).nonOpaque(), new ExtraBlockSettings().notSimpleCube().mineableByPickaxe());
    public static final SpellstorerBlock SPELLSTORER = (SpellstorerBlock) register("spellstorer_block", SpellstorerBlock::new, AbstractBlock.Settings.create().strength(3.0F, 6.0F).nonOpaque(), new ExtraBlockSettings().notSimpleCube().mineableByPickaxe());

    // fluids
    private static AbstractBlock.Settings fluid(MapColor mapColor) {
        return AbstractBlock.Settings.create().mapColor(mapColor).replaceable().noCollision().pistonBehavior(PistonBehavior.DESTROY).liquid();
    }

    public static final Block MOLTEN_GOLD = register("molten_gold", settings -> new GoldFluidBlock(ModFluids.MOLTEN_GOLD,null,settings),fluid(MapColor.GOLD).luminance(value -> 15).replaceable(),ExtraBlockSettings.create().fluid());


    public static void register(){
        ItemGroupEvents.modifyEntriesEvent(ModItems.MAIN_ITEM_GROUP_KEY).register((itemGroup) -> {

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

            if(block instanceof ILeadPoisoningBlock leadBlock){
                LeadBlockItem blockItem = new LeadBlockItem(block, itemSettings,leadBlock.getInventoryPoisoningSpeed());
                Registry.register(Registries.ITEM, itemKey, blockItem);
            }
            else if(block instanceof IOctanguliteBlock octanguliteBlock){
                OctanguliteBlockItem blockItem = new OctanguliteBlockItem(block, itemSettings,octanguliteBlock.getInventoryMaddeningSpeed());
                Registry.register(Registries.ITEM, itemKey, blockItem);
            }
            else{
                BlockItem blockItem = new BlockItem(block, itemSettings);
                Registry.register(Registries.ITEM, itemKey, blockItem);
            }


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