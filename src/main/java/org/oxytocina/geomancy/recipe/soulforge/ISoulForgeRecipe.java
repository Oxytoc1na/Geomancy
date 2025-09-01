package org.oxytocina.geomancy.recipe.soulforge;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.recipe.NbtIngredient;

import java.util.List;

public interface ISoulForgeRecipe {
    List<ItemStack> getResult(Inventory inv, boolean removeItems, boolean preview, LivingEntity owner);

    int getProgressRequired(Inventory inv);

    ItemStack getPreviewOutput(Inventory inv);

    boolean hasBaseStack();

    List<NbtIngredient> getNbtIngredients(Inventory inv);

    Identifier getIdentifier();
}
