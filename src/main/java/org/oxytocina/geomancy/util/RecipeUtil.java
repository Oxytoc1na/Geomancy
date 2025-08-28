package org.oxytocina.geomancy.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.oxytocina.geomancy.inventories.AutoCraftingInventory;
import org.oxytocina.geomancy.recipe.FluidConvertingRecipe;
import org.oxytocina.geomancy.recipe.GatedModRecipe;

import java.util.Collections;

public class RecipeUtil {
    private static final AutoCraftingInventory AUTO_INVENTORY = new AutoCraftingInventory(1, 1);

    public static <R extends GatedModRecipe<Inventory>> R getConversionRecipeFor(RecipeType<R> recipeType, @NotNull World world, ItemStack itemStack) {
        AUTO_INVENTORY.setInputInventory(Collections.singletonList(itemStack));
        return world.getRecipeManager().getFirstMatch(recipeType, AUTO_INVENTORY, world).orElse(null);
    }

    public static ItemStack craft(GatedModRecipe<Inventory> recipe, ItemStack itemStack, World world) {
        AUTO_INVENTORY.setInputInventory(Collections.singletonList(itemStack));
        return recipe.craft(AUTO_INVENTORY, world.getRegistryManager());
    }
}
