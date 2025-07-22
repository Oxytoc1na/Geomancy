package org.oxytocina.geomancy.compat;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.recipe.smithery.SmitheryRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

public class GeomancyREIClientPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new SmithingCategory());

        registry.addWorkstations(SmithingCategory.SMITHING, EntryStacks.of(ModBlocks.SMITHERY));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(SmitheryRecipe.class,ModRecipeTypes.SMITHING, SmitheryDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(75, 30, 20, 30), SmithingScreen.class,
                SmithingCategory.SMITHING);
    }
}
