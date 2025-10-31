package org.oxytocina.geomancy.registries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

public class ModBlockTags {

    public static final TagKey<Block> PICKAXE_MINEABLES = register(Identifier.of(Identifier.DEFAULT_NAMESPACE, "mineable/pickaxe"));
    public static final TagKey<Block> AXE_MINEABLES = register(Identifier.of(Identifier.DEFAULT_NAMESPACE, "mineable/axe"));
    public static final TagKey<Block> SHOVEL_MINEABLES = register(Identifier.of(Identifier.DEFAULT_NAMESPACE, "mineable/shovel"));
    public static final TagKey<Block> HOE_MINEABLES = register(Identifier.of(Identifier.DEFAULT_NAMESPACE, "mineable/hoe"));

    public static final TagKey<Block> MININGLEVEL_STONE = register(Identifier.of(Identifier.DEFAULT_NAMESPACE, "needs_stone_tool"));
    public static final TagKey<Block> MININGLEVEL_IRON = register(Identifier.of(Identifier.DEFAULT_NAMESPACE, "needs_iron_tool"));
    public static final TagKey<Block> MININGLEVEL_DIAMOND = register(Identifier.of(Identifier.DEFAULT_NAMESPACE, "needs_diamond_tool"));

    public static final TagKey<Block> LEAD_ORES = register( "lead_ores");
    public static final TagKey<Block> MITHRIL_ORES = register( "mithril_ores");
    public static final TagKey<Block> MOLYBDENUM_ORES = register( "molybdenum_ores");
    public static final TagKey<Block> TITANIUM_ORES = register( "titanium_ores");
    public static final TagKey<Block> OCTANGULITE_ORES = register( "octangulite_ores");
    public static final TagKey<Block> PERIDOT_ORES = register( "peridot_ores");
    public static final TagKey<Block> TOURMALINE_ORES = register( "tourmaline_ores");
    public static final TagKey<Block> AXINITE_ORES = register( "axinite_ores");
    public static final TagKey<Block> ORTHOCLASE_ORES = register( "orthoclase_ores");

    public static final TagKey<Block> OCTANGULITE = register( "octangulite");
    public static final TagKey<Block> NULL_BLOCKS = register( "null_blocks");
    public static final TagKey<Block> ADDS_SOULS = register( "adds_souls");
    public static final TagKey<Block> ADDS_SOULS_FEW = register( "adds_souls_few");
    public static final TagKey<Block> ADDS_SOULS_NORMAL = register( "adds_souls_normal");
    public static final TagKey<Block> ADDS_SOULS_MANY = register( "adds_souls_many");
    public static final TagKey<Block> REMOVES_SOULS_MANY = register( "removes_souls_many");

    public static final TagKey<Block> SOUL_OAK_LOGS = register( "soul_oak_logs");
    public static final TagKey<Block> HAMMER_MINEABLES = register( "mineable/hammer");

    public static final TagKey<Block> NULL_RUBBLE_REPLACEABLE = register( "null/rubble_replaceable");
    public static final TagKey<Block> NULL_CRYSTAL_REPLACEABLE = register( "null/crystal_replaceable");
    public static final TagKey<Block> NULL_HOLDS_SPIKES = register( "null/holds_spikes");

    public static final TagKey<Block> RAW_ORE_BLOCKS = register( "raw_ore");
    public static final TagKey<Block> METAL_BLOCKS = register( "metal");

    public static final TagKey<Block> LAUNCHED_HOT = register( "launched/hot");
    public static final TagKey<Block> LAUNCHED_COLD = register( "launched/cold");
    public static final TagKey<Block> LAUNCHED_HARMLESS = register( "launched/harmless");
    public static final TagKey<Block> LAUNCHED_WEAK = register( "launched/weak");
    public static final TagKey<Block> LAUNCHED_STRONG = register( "launched/strong");
    public static final TagKey<Block> LAUNCHED_HEAVY = register( "launched/heavy");
    public static final TagKey<Block> LAUNCHED_SUPERHEAVY = register( "launched/superheavy");


    // common tags
    public static final TagKey<Block> C_ORES = registerCommon("ores");
    public static final TagKey<Block> C_GLASS_BLOCKS = registerCommon("glass_blocks");
    public static final TagKey<Block> C_GLASS_PANES = registerCommon("glass_panes");
    public static final TagKey<Block> C_OBSIDIANS = registerCommon("obsidians");


    public static TagKey<Block> register(String name){
        return register(Geomancy.locate(name));
    }
    public static TagKey<Block> registerCommon(String name){
        return register(new Identifier("c",name));
    }
    public static TagKey<Block> register(Identifier id){
        return TagKey.of(RegistryKeys.BLOCK,id);
    }
}
