package org.oxytocina.geomancy.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import org.oxytocina.geomancy.recipe.smithery.SmitheryRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmitheryDisplay extends BasicDisplay {

    public SmitheryDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public SmitheryDisplay(SmitheryRecipe recipe) {
        super(getInputList(recipe), List.of(EntryIngredient.of(EntryStacks.of(recipe.getOutput(null)))));
    }

    private static List<EntryIngredient> getInputList(SmitheryRecipe recipe) {
        if(recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>();

        var ingredients = recipe.getSmithingIngredients();

        if(recipe.getShapeless()){
            // shapeless
            for(var ing : ingredients){
                list.add(EntryIngredients.ofIngredient(ing.ingredient));
            }
        }
        else{
            // shaped
            outer:
            for (int i = 0; i < 9; i++) {
                for(var ing : ingredients){
                    if(ing.slot!=i) continue;
                    list.add(EntryIngredients.ofIngredient(ing.ingredient));
                    continue outer;
                }
                list.add(EntryIngredients.of(ItemStack.EMPTY));
            }
        }

        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SmithingCategory.SMITHING;
    }
}