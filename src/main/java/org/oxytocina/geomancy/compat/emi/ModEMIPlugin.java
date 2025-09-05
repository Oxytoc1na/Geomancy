package org.oxytocina.geomancy.compat.emi;

import dev.emi.emi.api.*;
import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.stack.*;
import net.minecraft.inventory.*;
import net.minecraft.recipe.*;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.client.screen.ModScreenHandlers;
import org.oxytocina.geomancy.compat.emi.handlers.*;
import org.oxytocina.geomancy.compat.emi.recipes.*;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

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
        registry.addCategory(ModEMIRecipeCategories.SOUL_FORGE);
        registry.addWorkstation(ModEMIRecipeCategories.SOUL_FORGE, EmiStack.of(ModBlocks.SOUL_FORGE));
        registry.addCategory(ModEMIRecipeCategories.GEODE);
        registry.addWorkstation(ModEMIRecipeCategories.GEODE, EmiStack.of(ModBlocks.SMITHERY));
    }

    public void registerRecipes(EmiRegistry registry) {
        // TODO: Register our recipes ourselves
        // right now dev.emi.emi.VanillaPlugin handles them
        // which does not process the unlock check
        //addAll(registry, RecipeType.CRAFTING, ShapedGatedCraftingEMIRecipe::new);
        //addAll(registry, RecipeType.CRAFTING, ShapelessGatedCraftingEMIRecipe::new);

        addAll(registry, ModRecipeTypes.SMITHING, SmitheryEMIRecipe::new);
        addAll(registry, ModRecipeTypes.GEODE, GeodeEMIRecipe::new);
        addAll(registry, ModRecipeTypes.SOULFORGE_SIMPLE, SoulForgeEMIRecipe::new);

    }

    public void registerRecipeHandlers(EmiRegistry registry) {
        registry.addRecipeHandler(ModScreenHandlers.SMITHERY_SCREEN_HANDLER, new SmitheryRecipeHandler());
        registry.addRecipeHandler(ModScreenHandlers.SOULFORGE_SCREEN_HANDLER, new SoulForgeRecipeHandler());
    }

    public <C extends Inventory, T extends Recipe<C>> void addAll(EmiRegistry registry, RecipeType<T> type, Function<T, EmiRecipe> constructor) {
        for (T recipe : registry.getRecipeManager().listAllOfType(type)) {
            registry.addRecipe(constructor.apply(recipe));
        }
    }

}