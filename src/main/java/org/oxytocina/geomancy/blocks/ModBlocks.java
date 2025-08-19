package org.oxytocina.geomancy.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.jetbrains.annotations.NotNull;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlock;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlock;
import org.oxytocina.geomancy.blocks.fluids.GoldFluidBlock;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.blocks.fluids.MoltenGoldCauldronBlock;
import org.oxytocina.geomancy.items.ExtraItemSettings;
import org.oxytocina.geomancy.items.LeadBlockItem;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.OctanguliteBlockItem;
import org.oxytocina.geomancy.registries.ModBlockSetTypes;
import org.oxytocina.geomancy.registries.ModWoodTypes;
import org.oxytocina.geomancy.sound.ModBlockSoundGroups;
import org.oxytocina.geomancy.world.tree.SoulOakSaplingGenerator;

import java.util.*;
import java.util.function.Function;

public class ModBlocks {


    public static final Hashtable<Block,Identifier> BlockIdentifiers = new Hashtable<>();


    // ores
    public static final ExperienceDroppingBlock MITHRIL_ORE = register("mithril_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
        AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
        ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());
    public static final ExperienceDroppingBlock DEEPSLATE_MITHRIL_ORE = register("deepslate_mithril_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
        AbstractBlock.Settings.copy(MITHRIL_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
        ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    public static final OctanguliteExperienceDroppingBlock OCTANGULITE_ORE = register("octangulite_ore", (AbstractBlock.Settings s) -> new OctanguliteExperienceDroppingBlock(s, UniformIntProvider.create(9,13),1),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(ModBlockSoundGroups.STONE_WHISPERS),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notSimpleCube().notRegularDrop().cutout(),new FabricItemSettings().rarity(Rarity.RARE));
    public static final OctanguliteExperienceDroppingBlock DEEPSLATE_OCTANGULITE_ORE = register("deepslate_octangulite_ore", (AbstractBlock.Settings s) -> new OctanguliteExperienceDroppingBlock(s, UniformIntProvider.create(9,13),1),
            AbstractBlock.Settings.copy(OCTANGULITE_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(ModBlockSoundGroups.DEEPSLATE_WHISPERS),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).notSimpleCube().notRegularDrop().cutout(),new FabricItemSettings().rarity(Rarity.RARE));

    public static final ExperienceDroppingBlock MOLYBDENUM_ORE = register("molybdenum_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));
    public static final ExperienceDroppingBlock DEEPSLATE_MOLYBDENUM_ORE = register("deepslate_molybdenum_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.copy(MOLYBDENUM_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));

    public static final ExperienceDroppingBlock TITANIUM_ORE = register("titanium_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));
    public static final ExperienceDroppingBlock DEEPSLATE_TITANIUM_ORE = register("deepslate_titanium_ore", (AbstractBlock.Settings s) -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.copy(TITANIUM_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));

    public static final LeadOre LEAD_ORE = register("lead_ore", (AbstractBlock.Settings s) -> new LeadOre(s, UniformIntProvider.create(9,13),1),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));
    public static final LeadOre DEEPSLATE_LEAD_ORE = register("deepslate_lead_ore", (AbstractBlock.Settings s) -> new LeadOre(s, UniformIntProvider.create(9,13),1),
            AbstractBlock.Settings.copy(LEAD_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1).notRegularDrop(),new FabricItemSettings().rarity(Rarity.COMMON));

    public static final ExperienceDroppingBlock TOURMALINE_ORE = register("tourmaline_ore", s -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notRegularDrop().noModels().cutout(),new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final ExperienceDroppingBlock DEEPSLATE_TOURMALINE_ORE = register("deepslate_tourmaline_ore", s -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.copy(TOURMALINE_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notRegularDrop().noModels().cutout(),new FabricItemSettings().rarity(Rarity.UNCOMMON));

    public static final ExperienceDroppingBlock AXINITE_ORE = register("axinite_ore", s -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final ExperienceDroppingBlock DEEPSLATE_AXINITE_ORE = register("deepslate_axinite_ore", s -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.copy(AXINITE_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON));

    public static final ExperienceDroppingBlock ORTHOCLASE_ORE = register("orthoclase_ore", s -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final ExperienceDroppingBlock DEEPSLATE_ORTHOCLASE_ORE = register("deepslate_orthoclase_ore", s -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.copy(ORTHOCLASE_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON));

    public static final ExperienceDroppingBlock PERIDOT_ORE = register("peridot_ore", s -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final ExperienceDroppingBlock DEEPSLATE_PERIDOT_ORE = register("deepslate_peridot_ore", s -> new ExperienceDroppingBlock(s, UniformIntProvider.create(9,13)),
            AbstractBlock.Settings.copy(PERIDOT_ORE).mapColor(MapColor.DEEPSLATE_GRAY).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE),
            ExtraBlockSettings.create().mineableByPickaxe().miningLevel(2).notRegularDrop(),new FabricItemSettings().rarity(Rarity.UNCOMMON));


    // raw ore blocks
    public static final Block RAW_MITHRIL_BLOCK = register("raw_mithril_block", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());
    public static final OctanguliteBlock RAW_OCTANGULITE_BLOCK = register("raw_octangulite_block", (settings -> new OctanguliteBlock(settings,5)), AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F).sounds(ModBlockSoundGroups.STONE_WHISPERS),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).noModels().cutout(),new FabricItemSettings().rarity(Rarity.RARE).fireproof());
    public static final MaddeningBlock RAW_MOLYBDENUM_BLOCK = register("raw_molybdenum_block", s->new MaddeningBlock(s,-5), AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final Block RAW_TITANIUM_BLOCK = register("raw_titanium_block", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final LeadBlock RAW_LEAD_BLOCK = register("raw_lead_block", (settings -> new LeadBlock(settings,5)), AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());

    // ore blocks
    public static final Block MITHRIL_BLOCK = register("mithril_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());
    public static final OctanguliteBlock OCTANGULITE_BLOCK = register("octangulite_block", (settings -> new OctanguliteBlock(settings,5)),AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(ModBlockSoundGroups.METAL_WHISPERS),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(3).tintedModels(),new FabricItemSettings().rarity(Rarity.RARE).fireproof());
    public static final MaddeningBlock MOLYBDENUM_BLOCK = register("molybdenum_block", s->new MaddeningBlock(s,-5),AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final Block TITANIUM_BLOCK = register("titanium_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final LeadBlock LEAD_BLOCK = register("lead_block", (settings -> new LeadBlock(settings,5)),AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final Block PERIDOT_BLOCK = register("peridot_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.PALE_GREEN).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final Block ORTHOCLASE_BLOCK = register("orthoclase_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_BROWN).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final Block TOURMALINE_BLOCK = register("tourmaline_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_RED).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());
    public static final Block AXINITE_BLOCK = register("axinite_block", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_BROWN).instrument(Instrument.BELL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL),ExtraBlockSettings.create().mineableByPickaxe().miningLevel(1),new FabricItemSettings().rarity(Rarity.COMMON).fireproof());

    // mithril anvil
    //public static final Block MITHRIL_ANVIL = register("mithril_anvil",AnvilBlock::new,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0F, 1200.0F).sounds(BlockSoundGroup.ANVIL).pistonBehavior(PistonBehavior.BLOCK),ExtraBlockSettings.create().mineableByPickaxe().notSimpleCube(),new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof());

    // wood
    public static final OctangulitePillarBlock SOUL_OAK_LOG = register("soul_oak_log",(s)->new OctangulitePillarBlock(s,0.3f),AbstractBlock.Settings.create().mapColor((state) -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.OAK_TAN : MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable(),ExtraBlockSettings.create().noModels().mineableByAxe(), new Item.Settings());
    public static final OctangulitePillarBlock STRIPPED_SOUL_OAK_LOG = register("stripped_soul_oak_log",(s)->new OctangulitePillarBlock(s,0.3f),AbstractBlock.Settings.create().mapColor((state) -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.OAK_TAN : MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable(),ExtraBlockSettings.create().tintedModels().mineableByAxe(), new Item.Settings());
    public static final OctangulitePillarBlock SOUL_OAK_WOOD = register("soul_oak_wood", s->new OctangulitePillarBlock(s,0.3f),AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable(),ExtraBlockSettings.create().tintedModels().mineableByAxe());
    public static final OctangulitePillarBlock STRIPPED_SOUL_OAK_WOOD = register("stripped_soul_oak_wood", s->new OctangulitePillarBlock(s,0.3f),AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable(),ExtraBlockSettings.create().tintedModels().mineableByAxe());
    public static final OctanguliteBlock SOUL_OAK_PLANKS = register("soul_oak_planks",(s)->new OctanguliteBlock(s,0.3f),AbstractBlock.Settings.copy(Blocks.OAK_PLANKS),ExtraBlockSettings.create().tintedModels().mineableByAxe(), new Item.Settings());
    public static final Block SOUL_OAK_LEAVES = register("soul_oak_leaves",Blocks.createLeavesBlock(BlockSoundGroup.GRASS),ExtraBlockSettings.create().tintedModels().notRegularDrop().mineableByHoe(),new FabricItemSettings());
    //public static final Block SOUL_OAK_SIGN = register("soul_oak_sign",s->new SignBlock(s, ModWoodTypes.SOUL_OAK),AbstractBlock.Settings.create().mapColor(SOUL_OAK_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable(),ExtraBlockSettings.create().itemHasOwnName().tintedModels().mineableByAxe());
    //public static final Block SOUL_OAK_WALL_SIGN = register("soul_oak_wall_sign",s->new WallSignBlock(s, ModWoodTypes.SOUL_OAK),AbstractBlock.Settings.create().mapColor(SOUL_OAK_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable(),ExtraBlockSettings.create().itemHasOwnName().tintedModels().mineableByAxe().dontGroupItem());
    //public static final Block SOUL_OAK_HANGING_SIGN = register("soul_oak_hanging_sign",s->new HangingSignBlock(s, ModWoodTypes.SOUL_OAK),AbstractBlock.Settings.create().mapColor(SOUL_OAK_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable(),ExtraBlockSettings.create().itemHasOwnName().tintedModels().mineableByAxe());
    //public static final Block SOUL_OAK_WALL_HANGING_SIGN = register("soul_oak_wall_hanging_sign",s->new WallHangingSignBlock(s, ModWoodTypes.SOUL_OAK),AbstractBlock.Settings.create().mapColor(SOUL_OAK_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(1.0F).burnable(),ExtraBlockSettings.create().itemHasOwnName().tintedModels().mineableByAxe().dontGroupItem());
    public static final Block SOUL_OAK_PRESSURE_PLATE = register("soul_oak_pressure_plate",s->new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,s,ModBlockSetTypes.SOUL_OAK),AbstractBlock.Settings.create().mapColor(SOUL_OAK_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).noCollision().strength(0.5f).burnable(),ExtraBlockSettings.create().pressurePlate(SOUL_OAK_PLANKS).tintedModels().mineableByAxe());
    public static final SaplingBlock SOUL_OAK_SAPLING = register("soul_oak_sapling", s->new SaplingBlock(new SoulOakSaplingGenerator(),s),AbstractBlock.Settings.create().mapColor(MapColor.PINK).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS).pistonBehavior(PistonBehavior.DESTROY),ExtraBlockSettings.create().noModels().mineableByAxe().cutout());
    public static final Block POTTED_SOUL_OAK_SAPLING = register("potted_soul_oak_sapling",Blocks.createFlowerPotBlock(SOUL_OAK_SAPLING),ExtraBlockSettings.create().notSimpleCube().cutout().dontGroupItem(),new FabricItemSettings());
    public static final Block SOUL_OAK_BUTTON = register("soul_oak_button",Blocks.createWoodenButtonBlock(ModBlockSetTypes.SOUL_OAK),ExtraBlockSettings.create().button(SOUL_OAK_PLANKS).tintedModels(),new FabricItemSettings());
    public static final Block SOUL_OAK_FENCE_GATE = register("soul_oak_fence_gate",s->new FenceGateBlock(s, ModWoodTypes.SOUL_OAK),AbstractBlock.Settings.create().mapColor(SOUL_OAK_PLANKS.getDefaultMapColor()).solid().instrument(Instrument.BASS).strength(2.0F, 3.0F).burnable(),ExtraBlockSettings.create().fenceGate(SOUL_OAK_PLANKS).tintedModels(),new FabricItemSettings());
    public static final OctanguliteStairsBlock SOUL_OAK_STAIRS = register("soul_oak_stairs",(s)->new OctanguliteStairsBlock(SOUL_OAK_PLANKS.getDefaultState(),s,0.3f),AbstractBlock.Settings.copy(SOUL_OAK_PLANKS),ExtraBlockSettings.create().stairs(SOUL_OAK_PLANKS).tintedModels(), new FabricItemSettings());
    public static final OctanguliteSlabBlock SOUL_OAK_SLAB = register("soul_oak_slab",(s)->new OctanguliteSlabBlock(s,0.3f),AbstractBlock.Settings.copy(SOUL_OAK_PLANKS),ExtraBlockSettings.create().slab(SOUL_OAK_PLANKS).tintedModels(), new FabricItemSettings());
    public static final OctanguliteFenceBlock SOUL_OAK_FENCE = register("soul_oak_fence",(s)->new OctanguliteFenceBlock(s,0.3f),AbstractBlock.Settings.copy(SOUL_OAK_PLANKS),ExtraBlockSettings.create().fence(SOUL_OAK_PLANKS).tintedModels(), new FabricItemSettings());
    public static final OctanguliteDoorBlock SOUL_OAK_DOOR = register("soul_oak_door",(s)->new OctanguliteDoorBlock(s, ModBlockSetTypes.SOUL_OAK,0.3f),AbstractBlock.Settings.copy(SOUL_OAK_PLANKS).nonOpaque(),ExtraBlockSettings.create().tintedModels().cutout(), new FabricItemSettings());
    public static final OctanguliteTrapdoorBlock SOUL_OAK_TRAPDOOR = register("soul_oak_trapdoor",(s)->new OctanguliteTrapdoorBlock(s,ModBlockSetTypes.SOUL_OAK,0.3f),AbstractBlock.Settings.copy(SOUL_OAK_PLANKS).nonOpaque(),ExtraBlockSettings.create().tintedModels().cutout(), new FabricItemSettings());

    // decorative
    public static final Block GILDED_DEEPSLATE = register("gilded_deepslate", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final Block DECORATED_GILDED_DEEPSLATE = register("decorated_gilded_deepslate", Block::new,AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE),ExtraBlockSettings.create().mineableByPickaxe().hasTextureVariants(4),new FabricItemSettings());

    public static final Block CUT_TITANIUM = register("cut_titanium", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).instrument(Instrument.BELL).requiresTool(),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final Block MOSSY_CUT_TITANIUM = register("mossy_cut_titanium", Block::new, AbstractBlock.Settings.copy(CUT_TITANIUM).mapColor(MapColor.PALE_GREEN),ExtraBlockSettings.copyFrom(CUT_TITANIUM),new FabricItemSettings());
    public static final Block TITANIUM_BRICKS = register("titanium_bricks", Block::new, AbstractBlock.Settings.copy(CUT_TITANIUM),ExtraBlockSettings.copyFrom(CUT_TITANIUM),new FabricItemSettings());
    public static final StairsBlock TITANIUM_BRICK_STAIRS = register("titanium_brick_stairs", (settings -> new StairsBlock(TITANIUM_BRICKS.getDefaultState(), settings)), AbstractBlock.Settings.copy(TITANIUM_BRICKS),ExtraBlockSettings.copyFrom(TITANIUM_BRICKS).stairs(TITANIUM_BRICKS),new FabricItemSettings());
    public static final SlabBlock TITANIUM_BRICK_SLABS = register("titanium_brick_slab", (SlabBlock::new), AbstractBlock.Settings.copy(TITANIUM_BRICKS),ExtraBlockSettings.copyFrom(TITANIUM_BRICKS).slab(TITANIUM_BRICKS),new FabricItemSettings());
    public static final WallBlock TITANIUM_BRICK_WALL = register("titanium_brick_wall", (WallBlock::new), AbstractBlock.Settings.copy(TITANIUM_BRICKS).solid(),ExtraBlockSettings.copyFrom(TITANIUM_BRICKS).wall(TITANIUM_BRICKS),new FabricItemSettings());

    public static final LeadBlock CUT_LEAD = register("cut_lead", (settings -> new LeadBlock(settings,1)), AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).instrument(Instrument.BELL).requiresTool(),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final LeadBlock LEAD_BRICKS = register("lead_bricks", (settings -> new LeadBlock(settings,1)), AbstractBlock.Settings.copy(CUT_LEAD),ExtraBlockSettings.copyFrom(CUT_LEAD),new FabricItemSettings());
    public static final LeadStairsBlock LEAD_BRICK_STAIRS = register("lead_brick_stairs", (settings -> new LeadStairsBlock(LEAD_BRICKS.getDefaultState(), settings,1)), AbstractBlock.Settings.copy(LEAD_BRICKS),ExtraBlockSettings.copyFrom(LEAD_BRICKS).stairs(LEAD_BRICKS),new FabricItemSettings());
    public static final LeadSlabBlock LEAD_BRICK_SLABS = register("lead_brick_slab", (settings -> new LeadSlabBlock(settings,1)), AbstractBlock.Settings.copy(LEAD_BRICKS),ExtraBlockSettings.copyFrom(LEAD_BRICKS).slab(LEAD_BRICKS),new FabricItemSettings());
    public static final LeadWallBlock LEAD_BRICK_WALL = register("lead_brick_wall", (settings -> new LeadWallBlock(settings,1)), AbstractBlock.Settings.copy(LEAD_BRICKS).solid(),ExtraBlockSettings.copyFrom(LEAD_BRICKS).wall(LEAD_BRICKS),new FabricItemSettings());

    public static final MaddeningBlock CUT_MOLYBDENUM = register("cut_molybdenum", s->new MaddeningBlock(s,-1), AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).instrument(Instrument.BELL).requiresTool(),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final MaddeningBlock MOLYBDENUM_BRICKS = register("molybdenum_bricks", s->new MaddeningBlock(s,-1), AbstractBlock.Settings.copy(CUT_MOLYBDENUM),ExtraBlockSettings.copyFrom(CUT_MOLYBDENUM),new FabricItemSettings());
    public static final MaddeningStairsBlock MOLYBDENUM_BRICK_STAIRS = register("molybdenum_brick_stairs", (s -> new MaddeningStairsBlock(MOLYBDENUM_BRICKS.getDefaultState(), s,-1)), AbstractBlock.Settings.copy(MOLYBDENUM_BRICKS),ExtraBlockSettings.copyFrom(MOLYBDENUM_BRICKS).stairs(MOLYBDENUM_BRICKS),new FabricItemSettings());
    public static final MaddeningSlabBlock MOLYBDENUM_BRICK_SLABS = register("molybdenum_brick_slab", s->new MaddeningSlabBlock(s,-1), AbstractBlock.Settings.copy(MOLYBDENUM_BRICKS),ExtraBlockSettings.copyFrom(MOLYBDENUM_BRICKS).slab(MOLYBDENUM_BRICKS),new FabricItemSettings());
    public static final MaddeningWallBlock MOLYBDENUM_BRICK_WALL = register("molybdenum_brick_wall", s->new MaddeningWallBlock(s,-1), AbstractBlock.Settings.copy(MOLYBDENUM_BRICKS).solid(),ExtraBlockSettings.copyFrom(MOLYBDENUM_BRICKS).wall(MOLYBDENUM_BRICKS),new FabricItemSettings());

    public static final Block CUT_MITHRIL = register("cut_mithril", Block::new, AbstractBlock.Settings.create().mapColor(MapColor.WHITE).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).instrument(Instrument.BELL).requiresTool(),ExtraBlockSettings.create().mineableByPickaxe(),new FabricItemSettings());
    public static final Block MITHRIL_BRICKS = register("mithril_bricks", Block::new, AbstractBlock.Settings.copy(CUT_MITHRIL),ExtraBlockSettings.copyFrom(CUT_MITHRIL),new FabricItemSettings());
    public static final StairsBlock MITHRIL_BRICK_STAIRS = register("mithril_brick_stairs", (settings -> new StairsBlock(MITHRIL_BRICKS.getDefaultState(), settings)), AbstractBlock.Settings.copy(MITHRIL_BRICKS),ExtraBlockSettings.copyFrom(MITHRIL_BRICKS).stairs(MITHRIL_BRICKS),new FabricItemSettings());
    public static final SlabBlock MITHRIL_BRICK_SLABS = register("mithril_brick_slab", (SlabBlock::new), AbstractBlock.Settings.copy(MITHRIL_BRICKS),ExtraBlockSettings.copyFrom(MITHRIL_BRICKS).slab(MITHRIL_BRICKS),new FabricItemSettings());
    public static final WallBlock MITHRIL_BRICK_WALL = register("mithril_brick_wall", (WallBlock::new), AbstractBlock.Settings.copy(MITHRIL_BRICKS).solid(),ExtraBlockSettings.copyFrom(MITHRIL_BRICKS).wall(MITHRIL_BRICKS),new FabricItemSettings());

    public static final OctanguliteBlock CUT_OCTANGULITE = register("cut_octangulite", settings -> new OctanguliteBlock(settings,1), AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).instrument(Instrument.BELL).requiresTool(),ExtraBlockSettings.create().mineableByPickaxe().tintedModels(),new FabricItemSettings());
    public static final OctanguliteBlock OCTANGULITE_BRICKS = register("octangulite_bricks", settings -> new OctanguliteBlock(settings,1), AbstractBlock.Settings.copy(CUT_OCTANGULITE),ExtraBlockSettings.copyFrom(CUT_OCTANGULITE),new FabricItemSettings());
    public static final OctanguliteStairsBlock OCTANGULITE_BRICK_STAIRS = register("octangulite_brick_stairs", (settings -> new OctanguliteStairsBlock(OCTANGULITE_BRICKS.getDefaultState(), settings,1)), AbstractBlock.Settings.copy(OCTANGULITE_BRICKS),ExtraBlockSettings.copyFrom(OCTANGULITE_BRICKS).stairs(OCTANGULITE_BRICKS),new FabricItemSettings());
    public static final OctanguliteSlabBlock OCTANGULITE_BRICK_SLABS = register("octangulite_brick_slab", s->new OctanguliteSlabBlock(s,1), AbstractBlock.Settings.copy(OCTANGULITE_BRICKS),ExtraBlockSettings.copyFrom(OCTANGULITE_BRICKS).slab(OCTANGULITE_BRICKS),new FabricItemSettings());
    public static final OctanguliteWallBlock OCTANGULITE_BRICK_WALL = register("octangulite_brick_wall", s->new OctanguliteWallBlock(s,1), AbstractBlock.Settings.copy(OCTANGULITE_BRICKS).solid(),ExtraBlockSettings.copyFrom(OCTANGULITE_BRICKS).wall(OCTANGULITE_BRICKS),new FabricItemSettings());


    // block entities
    public static final SmitheryBlock SMITHERY = register("smithery_block", SmitheryBlock::new, AbstractBlock.Settings.create().strength(3.0F, 6.0F).nonOpaque(), new ExtraBlockSettings().notSimpleCube().mineableByPickaxe());
    public static final SpellmakerBlock SPELLMAKER = register("spellmaker_block", SpellmakerBlock::new, AbstractBlock.Settings.create().strength(3.0F, 6.0F).nonOpaque(), new ExtraBlockSettings().notSimpleCube().mineableByPickaxe());

    // fluids
    private static AbstractBlock.Settings fluid(MapColor mapColor) {
        return AbstractBlock.Settings.create().mapColor(mapColor).replaceable().noCollision().pistonBehavior(PistonBehavior.DESTROY).liquid();
    }

    public static final Block MOLTEN_GOLD = register("molten_gold", settings -> new GoldFluidBlock(ModFluids.MOLTEN_GOLD,null,settings),fluid(MapColor.GOLD).luminance(value -> 15).replaceable(),ExtraBlockSettings.create().fluid());
    public static final Block MOLTEN_GOLD_CAULDRON = register("molten_gold_cauldron", MoltenGoldCauldronBlock::new,AbstractBlock.Settings.copy(Blocks.CAULDRON).luminance(state -> 15),ExtraBlockSettings.create().dontGroupItem().notSimpleCube());

    public static void register(){
        ItemGroupEvents.modifyEntriesEvent(ModItems.MAIN_ITEM_GROUP_KEY).register((itemGroup) -> {

            for(Block b : ExtraBlockSettings.BlocksInGroup){
                itemGroup.add(b.asItem());
            }
        });

    }

    private static <T extends Block> T register(String name, Function<AbstractBlock.Settings, T> blockFactory, AbstractBlock.Settings settings) {
        return register(name, blockFactory, settings,new ExtraBlockSettings(),new Item.Settings());
    }
    private static <T extends Block> T register(String name, Function<AbstractBlock.Settings, T> blockFactory, AbstractBlock.Settings settings,ExtraBlockSettings extraSettings) {
        return register(name, blockFactory, settings,extraSettings,new Item.Settings());
    }
    private static <T extends Block> T register(String name, Function<AbstractBlock.Settings, T> blockFactory, AbstractBlock.Settings settings,ExtraBlockSettings extraSettings, Item.Settings itemSettings) {
        // Create the block instance
        T block = blockFactory.apply(settings);
        return register(name,block,extraSettings,itemSettings);
    }
    private static <T extends Block> T register(String name, T block,ExtraBlockSettings extraSettings,Item.Settings itemSettings){
        if(block==null){
            Geomancy.logError("tried to register null block: "+name);
            return null;
        }

        if(BlockIdentifiers.containsKey(block)){
            Geomancy.logError("tried to register already registered block: "+name);
            return block;
        }

        // Create a registry key for the block
        RegistryKey<Block> blockKey = keyOfBlock(name);

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (extraSettings.shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            RegistryKey<Item> itemKey = keyOfItem(name);

            var extraItemSettings = ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom);
            if(!extraSettings.shouldAddItemToGroup) extraItemSettings.dontGroupItem();
            if(block instanceof WallBlock || block instanceof FenceBlock)
                extraItemSettings.modelType(ExtraItemSettings.ModelType.BlockPlusInventory);
            else if(block instanceof TrapdoorBlock)
                extraItemSettings.modelType(ExtraItemSettings.ModelType.BlockPlusBottom);

            if(block instanceof ILeadPoisoningBlock leadBlock){
                LeadBlockItem blockItem = new LeadBlockItem(block, itemSettings,leadBlock.getInventoryPoisoningSpeed());
                registerBlockItem(itemKey.getValue().getPath(),blockItem,extraItemSettings);
            }
            else if(block instanceof IMaddeningBlock octanguliteBlock){
                OctanguliteBlockItem blockItem = new OctanguliteBlockItem(block, itemSettings,octanguliteBlock.getInventoryMaddeningSpeed());
                registerBlockItem(itemKey.getValue().getPath(),blockItem,extraItemSettings);
            }
            else if(extraSettings.shouldItemHaveOwnName){
                AliasedBlockItem blockItem = new AliasedBlockItem(block, itemSettings);
                registerBlockItem(itemKey.getValue().getPath(),blockItem,extraItemSettings);
            }
            else{
                BlockItem blockItem = new BlockItem(block, itemSettings);
                registerBlockItem(itemKey.getValue().getPath(),blockItem,extraItemSettings);

            }


        }

        Registry.register(Registries.BLOCK, blockKey, block);

        BlockIdentifiers.put(block,blockKey.getValue());

        extraSettings.setBlock(block).apply();

        return block;
    }
    private static void registerBlockItem(String id, BlockItem item, @NotNull ExtraItemSettings settings){
        ModItems.register(id,item,settings);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Geomancy.MOD_ID, name));
    }
    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Geomancy.MOD_ID, name));
    }
}