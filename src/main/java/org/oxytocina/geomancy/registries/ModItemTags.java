package org.oxytocina.geomancy.registries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

public class ModItemTags {

    public static final TagKey<Item> JEWELRY_GEMS = register("jewelry_gems");
    public static final TagKey<Item> STELLGE_CURIOUS = register("stellge_curious");
    public static final TagKey<Item> OCTANGULITE = register("octangulite");
    public static final TagKey<Item> SPELL_STORING = register("spell_storing");
    public static final TagKey<Item> VARIABLE_STORING = register("variable_storing");
    public static final TagKey<Item> FITS_IN_CASTERS = register("fits_in_casters");
    public static final TagKey<Item> FITS_IN_SOUL_BORE = register("fits_in_soul_bore");
    public static final TagKey<Item> COMPONENT_STORING = register("component_storing");
    public static final TagKey<Item> CASTING_ITEM = register("casting_item");
    public static final TagKey<Item> SOUL_OAK_LOGS = register("soul_oak_logs");
    public static final TagKey<Item> GEODES = register("geodes");

    public static final TagKey<Item> LEAD_ORES = register( "lead_ores");
    public static final TagKey<Item> MITHRIL_ORES = register( "mithril_ores");
    public static final TagKey<Item> MOLYBDENUM_ORES = register( "molybdenum_ores");
    public static final TagKey<Item> TITANIUM_ORES = register( "titanium_ores");
    public static final TagKey<Item> OCTANGULITE_ORES = register( "octangulite_ores");

    // common tags
    public static final TagKey<Item> C_RAW_ORES = registerCommon("raw_ores");
    public static final TagKey<Item> C_ORES = registerCommon("ores");
    public static final TagKey<Item> C_INGOTS = registerCommon("ingots");
    public static final TagKey<Item> C_NUGGETS = registerCommon("nuggets");
    public static final TagKey<Item> C_FOODS = registerCommon("foods");
    public static final TagKey<Item> C_GEMS = registerCommon("gems");

    public static TagKey<Item> register(String name){
        return register(Geomancy.locate(name));
    }
    public static TagKey<Item> registerCommon(String name){
        return register(new Identifier("c",name));
    }
    public static TagKey<Item> register(Identifier id){
        return TagKey.of(RegistryKeys.ITEM,id);
    }
}
