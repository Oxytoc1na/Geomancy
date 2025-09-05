package org.oxytocina.geomancy.compat.emi;

import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.stack.*;
import dev.emi.emi.api.widget.TextWidget.*;
import dev.emi.emi.api.widget.*;
import net.minecraft.client.*;
import net.minecraft.registry.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.oxytocina.geomancy.compat.GeomancyIntegrationPacks;
import org.oxytocina.geomancy.util.AdvancementHelper;

import java.util.*;

public abstract class ModEMIRecipe implements EmiRecipe {
    public static final Text HIDDEN_LINE_1 = Text.translatable("geomancy.rei.locked_recipe");
    public static final Text HIDDEN_LINE_2 = Text.translatable("geomancy.rei.locked_recipe.2");
    public final EmiRecipeCategory category;
    public final Identifier recipeTypeUnlockIdentifier, recipeIdentifier;
    public final int width, height;
    public List<EmiIngredient> inputs = List.of();
    public List<EmiStack> outputs = List.of();

    public ModEMIRecipe(EmiRecipeCategory category, Identifier recipeTypeUnlockIdentifier, Identifier recipeIdentifier, int width, int height) {
        this.category = category;
        this.recipeTypeUnlockIdentifier = recipeTypeUnlockIdentifier;
        this.recipeIdentifier = recipeIdentifier;
        this.width = width;
        this.height = height;
    }

    public DynamicRegistryManager getRegistryManager() {
        return MinecraftClient.getInstance().world.getRegistryManager();
    }

    public boolean isUnlocked() {
        return GeomancyIntegrationPacks.isCreative() || recipeTypeUnlockIdentifier == null || hasAdvancement(recipeTypeUnlockIdentifier);
    }

    public boolean hasAdvancement(Identifier advancement) {
        MinecraftClient client = MinecraftClient.getInstance();
        return AdvancementHelper.hasAdvancementClient(client.player, advancement);
    }

    public abstract void addUnlockedWidgets(WidgetHolder widgets);

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public @Nullable Identifier getId() {
        return recipeIdentifier;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        if (isUnlocked()) {
            return width;
        } else {
            MinecraftClient client = MinecraftClient.getInstance();
            return Math.max(client.textRenderer.getWidth(HIDDEN_LINE_1), client.textRenderer.getWidth(HIDDEN_LINE_2)) + 8;
        }
    }

    @Override
    public int getDisplayHeight() {
        if (isUnlocked()) {
            return height;
        } else {
            return 32;
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        if (!isUnlocked()) {
            widgets.addText(HIDDEN_LINE_1, getDisplayWidth() / 2, getDisplayHeight() / 2 - 8, 0x3f3f3f, false).horizontalAlign(Alignment.CENTER);
            widgets.addText(HIDDEN_LINE_2, getDisplayWidth() / 2, getDisplayHeight() / 2 + 2, 0x3f3f3f, false).horizontalAlign(Alignment.CENTER);
        } else {
            addUnlockedWidgets(widgets);
        }
    }

    @Override
    public boolean supportsRecipeTree() {
        return EmiRecipe.super.supportsRecipeTree() && isUnlocked();
    }

}