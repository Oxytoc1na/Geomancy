package org.oxytocina.geomancy.compat.modonomicon.client.pages;

import com.mojang.blaze3d.systems.*;
import net.minecraft.client.gui.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.compat.modonomicon.ModonomiconHelper;
import org.oxytocina.geomancy.compat.modonomicon.pages.BookGatedRecipePage;
import org.oxytocina.geomancy.recipe.smithery.SmitheryRecipe;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;

import java.util.*;

public class BookSmitheryCraftingPageRenderer extends BookGatedRecipePageRenderer<SmitheryRecipe, BookGatedRecipePage<SmitheryRecipe>> {
	
	private static final Identifier BACKGROUND_TEXTURE = Geomancy.locate("textures/gui/modonomicon/pedestal_crafting1.png");

	public BookSmitheryCraftingPageRenderer(BookGatedRecipePage<SmitheryRecipe> page) {
		super(page);
	}
	
	@Override
	protected int getRecipeHeight() {
		return 110;
	}

    @Override
    protected void drawRecipe(DrawContext drawContext, SmitheryRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
        World world = parentScreen.getMinecraft().world;
        if (world == null) return;

        RenderSystem.enableBlend();
        drawContext.drawTexture(BACKGROUND_TEXTURE, recipeX - 2, recipeY - 2, 0, 0, 106, 97, 128, 256);

        renderTitle(drawContext, recipeY, second);

        // the output
        parentScreen.renderItemStack(drawContext, recipeX + 78, recipeY + 22, mouseX, mouseY, recipe.getOutput(world.getRegistryManager()));

        // the ingredients
        List<SmithingIngredient> ingredients = recipe.getSmithingIngredients();
        int wrap = 3;
        for (int i = 0; i < ingredients.size(); i++) {
            ModonomiconHelper.renderSmithingIngredient(drawContext, parentScreen, recipeX + (i % wrap) * 19 + 3, recipeY + (i / wrap) * 19 + 3, mouseX, mouseY, ingredients.get(i));
        }
    }

}