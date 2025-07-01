package org.oxytocina.geomancy.recipe;

//import de.dafuqs.spectrum.*;
//import de.dafuqs.spectrum.registries.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.*;

public class GoldConvertingRecipe extends FluidConvertingRecipe {

    //public static final Identifier UNLOCK_IDENTIFIER = SpectrumCommon.locate("midgame/create_midnight_aberration");
    private static final Set<Item> outputItems = new HashSet<>();

    public GoldConvertingRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, @NotNull Ingredient inputIngredient, ItemStack outputItemStack) {
        super(id, group, secret, requiredAdvancementIdentifier, inputIngredient, outputItemStack);
        outputItems.add(outputItemStack.getItem());
    }

    public static boolean isExistingOutputItem(@NotNull ItemStack itemStack) {
        return outputItems.contains(itemStack.getItem());
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
        Geomancy.logError("getRecipeTypeUnlockIdentifier returning null");
        return null; //UNLOCK_IDENTIFIER;
    }

    @Override
    public String getRecipeTypeShortID() {
        return ModRecipeTypes.GOLD_CONVERTING_ID;
    }

}