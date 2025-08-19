package org.oxytocina.geomancy.registries;

import net.minecraft.block.dispenser.DispenserBehavior;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.recipe.*;
import net.minecraft.recipe.*;
import net.minecraft.registry.*;
import org.oxytocina.geomancy.recipe.smithery.*;

public class ModRecipeTypes {

    public static final String GOLD_CONVERTING_ID = "gold_converting";
    public static FluidConvertingRecipeSerializer<GoldConvertingRecipe> GOLD_CONVERTING_SERIALIZER;
    public static RecipeType<GoldConvertingRecipe> GOLD_CONVERTING;

    public static final String SMITHING_ID = "smithing";
    public static SmitheryRecipeSerializer<SmitheryRecipe> SMITHING_SERIALIZER;
    public static RecipeType<SmitheryRecipe> SMITHING;

    public static final String JEWELRY_ID = "jewelry";
    public static JewelryRecipeSerializer<JewelryRecipe> JEWELRY_SERIALIZER;
    public static RecipeType<JewelryRecipe> JEWELRY;

    public static final String GEODE_ID = "geode";
    public static GeodeRecipeSerializer<GeodeRecipe> GEODE_SERIALIZER;
    public static RecipeType<GeodeRecipe> GEODE;

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

        SMITHING_SERIALIZER = registerSerializer(SMITHING_ID, new SmitheryRecipeSerializer<>(SmitheryRecipe::new));
        SMITHING = registerRecipeType(SMITHING_ID);

        JEWELRY_SERIALIZER = registerSerializer(JEWELRY_ID, new JewelryRecipeSerializer<>(JewelryRecipe::new));
        JEWELRY = registerRecipeType(JEWELRY_ID);

        GEODE_SERIALIZER = registerSerializer(GEODE_ID, new GeodeRecipeSerializer<>(GeodeRecipe::new));
        GEODE = registerRecipeType(GEODE_ID);

        DispenserBehavior.registerDefaults();
    }

}