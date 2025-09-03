package org.oxytocina.geomancy.compat.emi.handlers;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.screen.slot.Slot;
import org.oxytocina.geomancy.client.screen.SoulForgeScreenHandler;
import org.oxytocina.geomancy.compat.emi.ModEMIRecipeCategories;

import java.util.ArrayList;
import java.util.List;

public class SoulForgeRecipeHandler implements StandardRecipeHandler<SoulForgeScreenHandler> {
    @Override
    public List<Slot> getInputSources(SoulForgeScreenHandler handler) {
        List<Slot> slots = new ArrayList<>();

        // crafting slots
        slots.addAll(handler.slots.subList(0, 10));

        // player inventory & hotbar
        slots.addAll(handler.slots.subList(10, 10+9*4));

        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(SoulForgeScreenHandler handler) {
        return handler.slots.subList(0, 10);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        EmiRecipeCategory category = recipe.getCategory();
        return (category == ModEMIRecipeCategories.SOUL_FORGE || category == VanillaEmiRecipeCategories.CRAFTING) && recipe.supportsRecipeTree();
    }
}