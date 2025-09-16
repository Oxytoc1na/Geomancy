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

    public static TagKey<Item> register(String name){
        return register(Geomancy.locate(name));
    }
    public static TagKey<Item> register(Identifier id){
        return TagKey.of(RegistryKeys.ITEM,id);
    }
}
