package org.oxytocina.geomancy.client.rendering;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;

public class ModModelPredicateProvider {

    public static void register(){

        // jewelry
        for(JewelryItem item : JewelryItem.List) {
            ModelPredicateProviderRegistry.register(item, Geomancy.locate("has_gem"), (itemStack, clientWorld, livingEntity, seed) -> item.getHasGemPredicate(itemStack));
        }

        // spell components
        ModelPredicateProviderRegistry.register(ModItems.SPELLCOMPONENT,
                Geomancy.locate("spell"),
                (itemStack, clientWorld, livingEntity, seed) ->
                        ModItems.SPELLCOMPONENT.getSpellPredicate(itemStack));
        ModelPredicateProviderRegistry.register(ModItems.SPELLCOMPONENT,
                Geomancy.locate("has_spell"),
                (itemStack, clientWorld, livingEntity, seed) ->
                        SpellComponentStoringItem.readComponent(itemStack)!=null?1:0);


    }
}
