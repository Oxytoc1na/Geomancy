package org.oxytocina.geomancy.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.recipe.soulforge.SoulForgeRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoulForgeDisplay extends BasicDisplay {

    public SoulForgeDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public SoulForgeDisplay(SoulForgeRecipe recipe) {
        super(getInputList(recipe), List.of(EntryIngredient.of(EntryStacks.of(recipe.getOutput(null)))));
    }

    private static List<EntryIngredient> getInputList(SoulForgeRecipe recipe) {
        if(recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>();

        var ingredients = recipe.getNbtIngredients(null);

        list.add(EntryIngredients.of(ModItems.SOUL_PREVIEW.getRequirementStack(recipe.getCost())));

        if(recipe.isShapeless()){
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
        return SoulForgeCategory.SOUL_FORGE;
    }
}