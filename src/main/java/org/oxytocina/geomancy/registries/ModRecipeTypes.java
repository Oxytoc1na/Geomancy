package org.oxytocina.geomancy.registries;

import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.recipe.*;
import net.minecraft.recipe.*;
import net.minecraft.registry.*;

public class ModRecipeTypes {

    public static final String GOLD_CONVERTING_ID = "gold_converting";
    public static FluidConvertingRecipeSerializer<GoldConvertingRecipe> GOLD_CONVERTING_SERIALIZER;
    public static RecipeType<GoldConvertingRecipe> GOLD_CONVERTING;



    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerSerializer(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, Geomancy.locate(id), serializer);
    }

    static <T extends Recipe<?>> RecipeType<T> registerRecipeType(String id) {
        return Registry.register(Registries.RECIPE_TYPE, Geomancy.locate(id), new RecipeType<T>() {
            @Override
            public String toString() {
                return Geomancy.MOD_ID + id;
            }
        });
    }

    public static void registerSerializer() {

        GOLD_CONVERTING_SERIALIZER = registerSerializer(GOLD_CONVERTING_ID, new FluidConvertingRecipeSerializer<>(GoldConvertingRecipe::new));
        GOLD_CONVERTING = registerRecipeType(GOLD_CONVERTING_ID);
    }

}