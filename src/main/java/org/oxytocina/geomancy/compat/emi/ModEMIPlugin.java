package org.oxytocina.geomancy.compat.emi;

import dev.emi.emi.api.*;
import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.stack.*;
import dev.emi.emi.config.*;
import dev.emi.emi.runtime.*;
import net.minecraft.block.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.fluid.*;
import net.minecraft.inventory.*;
import net.minecraft.recipe.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.client.screen.ModScreenHandlers;
import org.oxytocina.geomancy.compat.emi.handlers.SmitheryRecipeHandler;
import org.oxytocina.geomancy.compat.emi.recipes.SmitheryEMIRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.*;
import java.util.function.*;

public class ModEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registerCategories(registry);
        registerRecipes(registry);
        registerRecipeHandlers(registry);
    }

    public void registerCategories(EmiRegistry registry) {
        registry.addCategory(ModEMIRecipeCategories.SMITHING);
        registry.addWorkstation(ModEMIRecipeCategories.SMITHING, EmiStack.of(ModBlocks.SMITHERY));
    }

    public void registerRecipes(EmiRegistry registry) {
        // TODO: Register our recipes ourselves
        // right now dev.emi.emi.VanillaPlugin handles them
        // which does not process the unlock check
        //addAll(registry, RecipeType.CRAFTING, ShapedGatedCraftingEMIRecipe::new);
        //addAll(registry, RecipeType.CRAFTING, ShapelessGatedCraftingEMIRecipe::new);

        addAll(registry, ModRecipeTypes.SMITHING, SmitheryEMIRecipe::new);

    }

    public void registerRecipeHandlers(EmiRegistry registry) {
        registry.addRecipeHandler(ModScreenHandlers.SMITHERY_SCREEN_HANDLER, new SmitheryRecipeHandler());
    }

    public static Identifier syntheticId(String type, Block block) {
        Identifier blockId = Registries.BLOCK.getId(block);
        // Note that all recipe ids here start with "spectrum:/" which is legal, but impossible to represent with real files
        return new Identifier("spectrum:/" + type + "/" + blockId.getNamespace() + "/" + blockId.getPath());
    }

    public <C extends Inventory, T extends Recipe<C>> void addAll(EmiRegistry registry, RecipeType<T> type, Function<T, EmiRecipe> constructor) {
        for (T recipe : registry.getRecipeManager().listAllOfType(type)) {
            registry.addRecipe(constructor.apply(recipe));
        }
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception thrown when parsing EMI recipe (no ID available)");
            EmiReloadLog.error(e);
        }
    }

}