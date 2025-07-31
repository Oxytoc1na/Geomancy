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

    public static final TagKey<Block> OCTANGULITE = register( "octangulite");
    public static final TagKey<Block> ADDS_SOULS = register( "adds_souls");
    public static final TagKey<Block> ADDS_SOULS_FEW = register( "adds_souls_few");
    public static final TagKey<Block> ADDS_SOULS_NORMAL = register( "adds_souls_normal");
    public static final TagKey<Block> ADDS_SOULS_MANY = register( "adds_souls_many");

    public static final TagKey<Block> SOUL_OAK_LOGS = register( "soul_oak_logs");

    public static TagKey<Block> register(String name){
        return register(Geomancy.locate(name));
    }
    public static TagKey<Block> register(Identifier id){
        return TagKey.of(RegistryKeys.BLOCK,id);
    }
}
