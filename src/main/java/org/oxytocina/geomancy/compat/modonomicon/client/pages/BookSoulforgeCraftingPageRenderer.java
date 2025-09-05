package org.oxytocina.geomancy.compat.modonomicon.client.pages;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.compat.modonomicon.pages.BookGatedRecipePage;
import org.oxytocina.geomancy.recipe.smithery.SmitheryRecipe;
import org.oxytocina.geomancy.recipe.soulforge.SoulForgeRecipe;
import org.oxytocina.geomancy.util.DrawHelper;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.Arrays;

public class BookSoulforgeCraftingPageRenderer extends BookGatedRecipePageRenderer<SoulForgeRecipe, BookGatedRecipePage<SoulForgeRecipe>> {

	private static final Identifier BACKGROUND_TEXTURE = Geomancy.locate("textures/gui/modonomicon/pedestal_crafting1.png");

	public BookSoulforgeCraftingPageRenderer(BookGatedRecipePage<SoulForgeRecipe> page) {
		super(page);
	}
	
	@Override
	protected int getRecipeHeight() {
		return 78;
	}

    @Override
    protected void drawRecipe(DrawContext drawContext, SoulForgeRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
        // render title if applicable
        if (!second) {
            if (!this.page.getTitle1().isEmpty()) {
                this.renderTitle(drawContext, this.page.getTitle1(), false, BookContentScreen.PAGE_WIDTH / 2, -5);
            }
        } else {
            if (!this.page.getTitle2().isEmpty()) {
                this.renderTitle(drawContext, this.page.getTitle2(), false, BookContentScreen.PAGE_WIDTH / 2,
                        recipeY - (this.page.getTitle2().getString().isEmpty() ? 10 : 0) - 10);
            }
        }

        // draw crafting background texture
        RenderSystem.enableBlend();
        drawContext.drawTexture(this.page.getBook().getCraftingTexture(), recipeX - 2, recipeY - 2, 0, 0, 100, 62, 128, 256);

        // draw shapelessness
        boolean shaped = !recipe.isShapeless();
        if (!shaped) {
            int iconX = recipeX + 62;
            int iconY = recipeY + 2;
            drawContext.drawTexture(this.page.getBook().getCraftingTexture(), iconX, iconY, 0, 64, 11, 11, 128, 256);
            if (this.parentScreen.isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11)) {
                this.parentScreen.setTooltip(Text.translatable(ModonomiconConstants.I18n.Tooltips.RECIPE_CRAFTING_SHAPELESS));
            }
        }

        // draw output
        this.parentScreen.renderItemStack(drawContext, recipeX + 79, recipeY + 22, mouseX, mouseY, recipe.getOutput(this.parentScreen.getMinecraft().world.getRegistryManager()));

        // draw ingredients
        var ingredients = recipe.getNbtIngredients(null);
        if(!ingredients.isEmpty())
        {
            var baseIng = ingredients.get(0);
            int x = recipeX + 19 + 3;
            int y = recipeY + 19 + 3;
            this.parentScreen.renderItemStacks(drawContext, x, y, mouseX, mouseY, Arrays.asList(baseIng.ingredient.getMatchingStacks()), baseIng.count);

            for (int i = 1; i < ingredients.size(); i++) {
                var ingredient = ingredients.get(i);
                float angle = (float)Math.PI*2*((float)i/ (ingredients.size()-1));
                var offset = Toolbox.rotateVector(new Vector2f(24,0),angle);
                int drawPosX = x+(int)offset.x;
                int drawPosY = y+(int)offset.y;
                this.parentScreen.renderItemStacks(drawContext, drawPosX, drawPosY, mouseX, mouseY, Arrays.asList(ingredient.ingredient.getMatchingStacks()), ingredient.count);
            }
        }


        // draw recipe icon
        this.parentScreen.renderItemStack(drawContext, recipeX + 79, recipeY + 41, mouseX, mouseY, recipe.createIcon());
    }

}