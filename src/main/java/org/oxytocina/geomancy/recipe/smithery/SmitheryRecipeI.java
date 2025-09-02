package org.oxytocina.geomancy.recipe.smithery;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public interface SmitheryRecipeI {
    List<ItemStack> getSmithingResult(Inventory inv, boolean removeItems, boolean preview, ItemStack hammer, LivingEntity hammerer, World world);

    int getDifficulty(Inventory inv, ItemStack hammer, LivingEntity holder);

    int getProgressRequired(Inventory inv);

    ItemStack getPreviewOutput(Inventory inv);

    boolean hasBaseStack();

    List<SmithingIngredient> getSmithingIngredients(Inventory inv);
}
