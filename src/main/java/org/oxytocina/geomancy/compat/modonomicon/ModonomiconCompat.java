package org.oxytocina.geomancy.compat.modonomicon;

import com.klikli_dev.modonomicon.book.page.BookSmithingRecipePage;
import com.klikli_dev.modonomicon.client.render.page.*;
import com.klikli_dev.modonomicon.data.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.compat.GeomancyIntegrationPacks;
import org.oxytocina.geomancy.compat.modonomicon.pages.BookGatedRecipePage;
import org.oxytocina.geomancy.compat.modonomicon.pages.BookNbtSpotlightPage;
import org.oxytocina.geomancy.compat.modonomicon.pages.BookStellgeTextPage;
import org.oxytocina.geomancy.recipe.GatedRecipe;
import org.oxytocina.geomancy.recipe.smithery.SmitheryRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

public class ModonomiconCompat extends GeomancyIntegrationPacks.ModIntegrationPack {

    // Page Types
    public static final Identifier SMITHERY_CRAFTING = Geomancy.locate("smithery_crafting");
    public static final Identifier NBT_SPOTLIGHT_PAGE = Geomancy.locate("nbt_spotlight");
    public static final Identifier STELLGE_TEXT = Geomancy.locate("text_stellge");

    @Override
    public void register() {
        registerPages();
        registerUnlockConditions();
    }
	
	private void registerPages() {
        registerGatedRecipePage(SMITHERY_CRAFTING, ModRecipeTypes.SMITHING, true);

        LoaderRegistry.registerPageLoader(NBT_SPOTLIGHT_PAGE, BookNbtSpotlightPage::fromJson, BookNbtSpotlightPage::fromNetwork);
        LoaderRegistry.registerPageLoader(STELLGE_TEXT, BookStellgeTextPage::fromJson, BookStellgeTextPage::fromNetwork);
    }
    
    private void registerGatedRecipePage(Identifier id, RecipeType<? extends GatedRecipe<?>> recipeType, boolean supportsTwoRecipesOnOnePage) {
        LoaderRegistry.registerPageLoader(id,
                json -> BookGatedRecipePage.fromJson(id, recipeType, json, supportsTwoRecipesOnOnePage),
                buffer -> BookGatedRecipePage.fromNetwork(id, recipeType, buffer));
    }
	
	private void registerUnlockConditions() {

    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void registerClient() {
        PageRendererRegistry.registerPageRenderer(SMITHERY_CRAFTING, p -> new BookSmithingRecipePageRenderer((BookSmithingRecipePage) p));
        PageRendererRegistry.registerPageRenderer(NBT_SPOTLIGHT_PAGE, p -> new BookSpotlightPageRenderer((BookNbtSpotlightPage) p));
        PageRendererRegistry.registerPageRenderer(STELLGE_TEXT, p -> new BookTextPageRenderer((BookStellgeTextPage) p));
    }

}