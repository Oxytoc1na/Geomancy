package org.oxytocina.geomancy.compat.emi;

import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.stack.*;
import dev.emi.emi.api.widget.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import org.oxytocina.geomancy.compat.GeomancyIntegrationPacks;
import org.oxytocina.geomancy.recipe.GatedRecipe;

import java.util.*;

public abstract class GatedModEMIRecipe<T extends GatedRecipe<?>> extends ModEMIRecipe {

    public static final Text LOCKED = Text.translatable("geomancy.rei.locked_recipe");
    public static final Text LOCKED_2 = Text.translatable("geomancy.rei.locked_recipe.2");

    public final @Nullable Text secretHintText;

    public final T recipe;

    public GatedModEMIRecipe(EmiRecipeCategory category, T recipe, int width, int height) {
        super(category, recipe.getRecipeTypeUnlockIdentifier(), recipe.getId(), width, height);
        this.recipe = recipe;
        this.outputs = List.of(EmiStack.of(recipe.getOutput(getRegistryManager())));
        this.secretHintText = recipe.getSecretHintText();
    }

    @Override
    public boolean isUnlocked() {
        return GeomancyIntegrationPacks.isCreative() || (hasAdvancement(recipe.getRequiredAdvancementIdentifier()) && super.isUnlocked());
    }

    @Override
    public boolean hideCraftable() {
        return !GeomancyIntegrationPacks.isCreative() && (recipe.isSecret() || super.hideCraftable());
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        if (recipe.isSecret() && isUnlocked()) {
            if (secretHintText == null) {
                widgets.addText(LOCKED, getDisplayWidth() / 2, getDisplayHeight() / 2, 0x3f3f3f, false).horizontalAlign(TextWidget.Alignment.CENTER);
            } else {
                widgets.addText(LOCKED_2, getDisplayWidth() / 2, getDisplayHeight() / 2 - 8, 0x3f3f3f, false).horizontalAlign(TextWidget.Alignment.CENTER);
                widgets.addText(secretHintText, getDisplayWidth() / 2, getDisplayHeight() / 2 + 2, 0x3f3f3f, false).horizontalAlign(TextWidget.Alignment.CENTER);
            }
        } else {
            super.addWidgets(widgets);
        }
    }

}