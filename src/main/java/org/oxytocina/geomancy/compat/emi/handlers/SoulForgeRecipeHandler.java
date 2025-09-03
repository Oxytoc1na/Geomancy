package org.oxytocina.geomancy.compat.emi.handlers;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.screen.slot.Slot;
import org.oxytocina.geomancy.blocks.blockEntities.SoulForgeBlockEntity;
import org.oxytocina.geomancy.client.screen.SoulForgeScreenHandler;
import org.oxytocina.geomancy.compat.emi.ModEMIRecipeCategories;

import java.util.ArrayList;
import java.util.List;

public class SoulForgeRecipeHandler implements StandardRecipeHandler<SoulForgeScreenHandler> {
    @Override
    public List<Slot> getInputSources(SoulForgeScreenHandler handler) {
        List<Slot> slots = new ArrayList<>();

        // crafting slots
        slots.addAll(handler.slots.subList(0, SoulForgeBlockEntity.SLOT_COUNT));

        // player inventory & hotbar
        slots.addAll(handler.slots.subList(SoulForgeBlockEntity.SLOT_COUNT, SoulForgeBlockEntity.SLOT_COUNT+9*4));

        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(SoulForgeScreenHandler handler) {
        return handler.slots.subList(0, SoulForgeBlockEntity.SLOT_COUNT);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        EmiRecipeCategory category = recipe.getCategory();
        return (category == ModEMIRecipeCategories.SOUL_FORGE || category == VanillaEmiRecipeCategories.CRAFTING) && recipe.supportsRecipeTree();
    }
}