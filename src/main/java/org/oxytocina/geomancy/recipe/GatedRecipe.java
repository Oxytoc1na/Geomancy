package org.oxytocina.geomancy.recipe;

import net.fabricmc.api.*;
import net.fabricmc.loader.api.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.recipe.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.oxytocina.geomancy.util.AdvancementHelper;

public interface GatedRecipe<C extends Inventory> extends Recipe<C> {

    boolean isSecret();
    boolean isShapeless();
    Identifier getRequiredAdvancementIdentifier();
    Identifier getRecipeTypeUnlockIdentifier();

    String getRecipeTypeShortID();

    default boolean canPlayerCraft(PlayerEntity playerEntity) {
        return AdvancementHelper.hasAdvancementServer(playerEntity, getRecipeTypeUnlockIdentifier())
                && AdvancementHelper.hasAdvancementServer(playerEntity, getRequiredAdvancementIdentifier());
    }

    @Environment(EnvType.CLIENT)
    private void registerInToastManagerClient(RecipeType<?> recipeType, GatedRecipe<C> gatedRecipe) {
        //UnlockToastManager.registerGatedRecipe(recipeType, gatedRecipe);
    }

    default @Nullable Text getSecretHintText() {
        if (isSecret()) {
            String secretHintLangKey = getId().toTranslationKey("recipe", "hint").replace("/", ".");
            return Language.getInstance().hasTranslation(secretHintLangKey) ? Text.translatable(secretHintLangKey) : null;
        }
        return null;
    }

}