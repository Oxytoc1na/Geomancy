package org.oxytocina.geomancy.compat.modonomicon.client.pages;

import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.recipe.GatedRecipe;
import org.oxytocina.geomancy.recipe.smithery.SmitheryRecipe;

import java.util.Arrays;

public abstract class BookGatedRecipeStellgePageRenderer<R extends GatedRecipe<?>, T extends BookRecipePage<R>> extends BookRecipePageRenderer<R, T> {

    public BookGatedRecipeStellgePageRenderer(T page) {
        super(page);
    }

    public void renderTitle(DrawContext drawContext, int recipeY, boolean second) {
        BookTextHolder title = second ? page.getTitle2() : page.getTitle1();
        if (!title.getString().isEmpty()) {
            int titleY = second ? recipeY - (page.getTitle2().isEmpty() ? 10 : 0) - 10 : -5;
            super.renderTitle(drawContext, title, false, BookContentScreen.PAGE_WIDTH / 2, titleY);
        }
    }

}