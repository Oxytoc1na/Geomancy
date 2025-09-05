package org.oxytocina.geomancy.compat.emi.recipes;


import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.compat.emi.GatedModEMIRecipe;
import org.oxytocina.geomancy.compat.emi.ModEMIRecipeCategories;
import org.oxytocina.geomancy.recipe.smithery.GeodeRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeodeEMIRecipe extends GatedModEMIRecipe<GeodeRecipe> {

    public static final Identifier TEXTURE =
            Geomancy.locate("textures/gui/smithery_block_gui_rei.png");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 80;

    public GeodeEMIRecipe(GeodeRecipe recipe) {
        super(ModEMIRecipeCategories.SMITHING, recipe, WIDTH, HEIGHT);
        this.inputs = getIngredients(recipe);
    }

    @Override
    public boolean isUnlocked() {
        return super.isUnlocked();
    }

    private static List<EmiIngredient> getIngredients(GeodeRecipe recipe) {
        if(recipe == null) return Collections.emptyList();
        return List.of(EmiIngredient.of(recipe.base.ingredient));
    }

    @Override
    public void addUnlockedWidgets(WidgetHolder widgets) {

        Identifier backgroundTexture = TEXTURE;
        //// crafting input
        //widgets.addTexture(backgroundTexture, 0, 0, 54, 54, 29, 18);
        //// crafting output
        //widgets.addTexture(backgroundTexture, 90, 14, 26, 26, 122, 32);

        final int x = 15;
        final int y = 15;

        if (recipe.isShapeless()) {
            widgets.addTexture(EmiTexture.SHAPELESS, 94, 0);
            for (int i = 0; i < inputs.size(); i++) {
                widgets.addSlot(inputs.get(i), x+(i%3) * 18, y+(i/3) * 18).drawBack(true);
            }
        }
        else{
            for (int i = 0; i < inputs.size(); i++) {
                widgets.addSlot(inputs.get(i), x+(i%3) * 18, y+(i/3) * 18).drawBack(true);
            }
        }

        widgets.addSlot(outputs.get(0), WIDTH-18-x, y+14).large(true).drawBack(true).recipeContext(this);
        widgets.addTexture(TEXTURE,x+18*3+15,y+14-4,35,32,87,30);
    }
}