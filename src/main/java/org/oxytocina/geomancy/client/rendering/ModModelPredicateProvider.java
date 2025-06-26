package org.oxytocina.geomancy.client.rendering;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;

public class ModModelPredicateProvider {

    public static void initialize(){

        // jewelry
        for(JewelryItem item : JewelryItem.List) {
            ModelPredicateProviderRegistry.register(item, Geomancy.locate("has_gem"), (itemStack, clientWorld, livingEntity, seed) -> item.getHasGemPredicate(itemStack));
        }


    }
}
