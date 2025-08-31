package org.oxytocina.geomancy.recipe.ritualforge;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.recipe.NbtIngredient;

import java.util.List;

public interface IRitualForgeRecipe {
    List<ItemStack> getResult(Inventory inv, boolean removeItems, boolean preview, LivingEntity owner);

    int getProgressRequired(Inventory inv);

    ItemStack getPreviewOutput(Inventory inv);

    boolean hasBaseStack();

    List<NbtIngredient> getNbtIngredients(Inventory inv);

    Identifier getIdentifier();
}
