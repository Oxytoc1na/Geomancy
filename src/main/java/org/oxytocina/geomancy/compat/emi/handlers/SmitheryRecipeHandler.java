package org.oxytocina.geomancy.compat.emi.handlers;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.screen.slot.Slot;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SoulForgeBlockEntity;
import org.oxytocina.geomancy.client.screen.SmitheryScreenHandler;
import org.oxytocina.geomancy.compat.emi.ModEMIRecipeCategories;

import java.util.ArrayList;
import java.util.List;

public class SmitheryRecipeHandler implements StandardRecipeHandler<SmitheryScreenHandler> {
    @Override
    public List<Slot> getInputSources(SmitheryScreenHandler handler) {
        List<Slot> slots = new ArrayList<>();

        // crafting slots
        slots.addAll(handler.slots.subList(0, SmitheryBlockEntity.SLOT_COUNT));

        // player inventory & hotbar
        slots.addAll(handler.slots.subList(SmitheryBlockEntity.SLOT_COUNT, SmitheryBlockEntity.SLOT_COUNT+9*4));

        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(SmitheryScreenHandler handler) {
        return handler.slots.subList(0, SmitheryBlockEntity.SLOT_COUNT);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        EmiRecipeCategory category = recipe.getCategory();
        return (category == ModEMIRecipeCategories.SMITHING || category == VanillaEmiRecipeCategories.CRAFTING) && recipe.supportsRecipeTree();
    }
}