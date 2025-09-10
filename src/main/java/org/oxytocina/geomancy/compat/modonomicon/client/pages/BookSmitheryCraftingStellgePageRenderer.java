package org.oxytocina.geomancy.compat.modonomicon.client.pages;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.compat.modonomicon.pages.BookGatedRecipePage;
import org.oxytocina.geomancy.compat.modonomicon.pages.BookGatedRecipeStellgePage;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.recipe.smithery.SmitheryRecipe;
import org.oxytocina.geomancy.util.SimplexNoise;
import org.oxytocina.geomancy.util.StellgeUtil;

import java.util.Arrays;

public class BookSmitheryCraftingStellgePageRenderer extends BookGatedRecipeStellgePageRenderer<SmitheryRecipe, BookGatedRecipeStellgePage<SmitheryRecipe>> {

	private static final Identifier BACKGROUND_TEXTURE = Geomancy.locate("textures/gui/modonomicon/pedestal_crafting1.png");

	public BookSmitheryCraftingStellgePageRenderer(BookGatedRecipeStellgePage<SmitheryRecipe> page) {
		super(page);
	}

	@Override
	protected int getRecipeHeight() {
		return 78;
	}

    @Override
    protected void drawRecipe(DrawContext drawContext, SmitheryRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
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
        float chanceToShow = (StellgeUtil.getKnowledge(MinecraftClient.getInstance().player)+page.knowledgeBonus)/page.requiredKnowledge /page.recipeFraction;
        if(chanceToShow >= 0.5f)
            this.parentScreen.renderItemStack(drawContext, recipeX + 79, recipeY + 22, mouseX, mouseY, recipe.getOutput(this.parentScreen.getMinecraft().world.getRegistryManager()));
        else
            this.parentScreen.renderItemStack(drawContext, recipeX + 79, recipeY + 22, mouseX, mouseY, previewStack());

        // draw ingredients
        var ingredients = recipe.getSmithingIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            int slot = shaped?ingredient.slot:i;
            int x = recipeX + (slot % 3) * 19 + 3;
            int y = recipeY + (slot / 3) * 19 + 3;
            chanceToShow = (StellgeUtil.getKnowledge(MinecraftClient.getInstance().player)+page.knowledgeBonus)/page.requiredKnowledge /page.recipeFraction;
            if(SimplexNoise.noiseNormalized(slot,slot/2f,0,3.73219f) < chanceToShow)
                this.parentScreen.renderItemStacks(drawContext, x, y, mouseX, mouseY, Arrays.asList(ingredient.ingredient.getMatchingStacks()), ingredient.count);
            else
                this.parentScreen.renderItemStack(drawContext, x, y, mouseX, mouseY, previewStack());
        }

        // draw recipe icon
        this.parentScreen.renderItemStack(drawContext, recipeX + 79, recipeY + 41, mouseX, mouseY, recipe.createIcon());
    }

    public static ItemStack previewStack(){
        return ModItems.GEODE_PREVIEW.getDefaultStack();
    }
}