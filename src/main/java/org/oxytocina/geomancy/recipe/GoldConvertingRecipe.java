package org.oxytocina.geomancy.recipe;

import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.*;

public class GoldConvertingRecipe extends FluidConvertingRecipe {

    public static final Identifier UNLOCK_IDENTIFIER = null;

    public GoldConvertingRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, @NotNull Ingredient inputIngredient, ItemStack outputItemStack) {
        super(id, group, secret, requiredAdvancementIdentifier, inputIngredient, outputItemStack);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModFluids.MOLTEN_GOLD_BUCKET);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.GOLD_CONVERTING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.GOLD_CONVERTING;
    }

    @Override
    public Identifier getRecipeTypeUnlockIdentifier() {
        return UNLOCK_IDENTIFIER;
    }

    @Override
    public String getRecipeTypeShortID() {
        return ModRecipeTypes.GOLD_CONVERTING_ID;
    }

}