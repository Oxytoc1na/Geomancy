package org.oxytocina.geomancy.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface SmitheryRecipeI {
    public List<ItemStack> getSmithingResult(Inventory inv, boolean removeItems);

    public int getDifficulty();

    public int getProgressRequired();

    public ItemStack getPreviewOutput(Inventory inv);

    public boolean hasBaseStack();

    public List<SmithingIngredient> getSmithingIngredients(Inventory inv);
}
