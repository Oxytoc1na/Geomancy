package org.oxytocina.geomancy.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface SmitheryRecipeI {
    List<ItemStack> getSmithingResult(Inventory inv, boolean removeItems, boolean preview);

    int getDifficulty(Inventory inv);

    int getProgressRequired(Inventory inv);

    ItemStack getPreviewOutput(Inventory inv);

    boolean hasBaseStack();

    List<SmithingIngredient> getSmithingIngredients(Inventory inv);
}
