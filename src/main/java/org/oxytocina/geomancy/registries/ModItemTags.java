package org.oxytocina.geomancy.registries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

public class ModItemTags {

    public static final TagKey<Item> MUSIC_DISCS = register(new Identifier("music_discs"));

    public static final TagKey<Item> SMELLY_ITEMS = register( "smelly_items");
    public static final TagKey<Item> JEWELRY_GEMS = register("jewelry_gems");
    public static final TagKey<Item> STELLGE_CURIOUS = register("stellge_curious");
    public static final TagKey<Item> OCTANGULITE = register("octangulite");
    public static final TagKey<Item> SPELL_STORING = register("spell_storing");
    public static final TagKey<Item> CASTING_ITEM = register("casting_item");

    public static TagKey<Item> register(String name){
        return register(Geomancy.locate(name));
    }
    public static TagKey<Item> register(Identifier id){
        return TagKey.of(RegistryKeys.ITEM,id);
    }
}
