package org.oxytocina.geomancy.client.datagen.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.recipe.CountIngredient;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.function.Consumer;

public class SmitheryRecipeJsonBuilder {
    private final RecipeCategory category;
    private final DefaultedList<SmithingIngredient> inputs;
    private final Item output;
    private final int count;
    private final int progressRequired;
    private final int difficulty;
    private final boolean shapeless;
    private final Advancement.Builder advancement = Advancement.Builder.createUntelemetered();
    private final RecipeSerializer<?> serializer;

    public SmitheryRecipeJsonBuilder(RecipeSerializer<?> serializer, RecipeCategory category, DefaultedList<SmithingIngredient> inputs, Item output, int count, int progressRequired, int difficulty, boolean shapeless) {
        this.category = category;
        this.serializer = serializer;
        this.inputs = inputs;
        this.output = output;
        this.count = count;
        this.progressRequired=progressRequired;
        this.difficulty=difficulty;
        this.shapeless=shapeless;
    }

    public static SmitheryRecipeJsonBuilder create(DefaultedList<SmithingIngredient> inputs, Item output,int count, int progressRequired, int difficulty, boolean shapeless, RecipeCategory category) {
        return new SmitheryRecipeJsonBuilder(ModRecipeTypes.SMITHING_SERIALIZER, category, inputs, output,
                count,progressRequired, difficulty, shapeless);
    }

    public SmitheryRecipeJsonBuilder criterion(String name, CriterionConditions conditions) {
        this.advancement.criterion(name, conditions);
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.advancement.parent(CraftingRecipeJsonBuilder.ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(net.minecraft.advancement.AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        exporter.accept(new Provider(
                recipeId, this.serializer, this.inputs, this.output, this.count, this.progressRequired,
                this.difficulty,this.shapeless, this.advancement,
                recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    private void validate(Identifier recipeId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    public static record Provider(Identifier id, RecipeSerializer<?> type, DefaultedList<SmithingIngredient> inputs,
                                  Item output, int count,int progressRequired,int difficulty,boolean shapeless,
                                  Advancement.Builder advancement, Identifier advancementId) implements RecipeJsonProvider {
        public void serialize(JsonObject json) {
            //if (!this.group.isEmpty()) {
            //    json.addProperty("group", this.group);
            //}

            JsonArray ingredientsArray = new JsonArray();

            for(CountIngredient ingredient : this.inputs) {
                JsonObject ingredientElement = ingredient.toJson().getAsJsonObject();
                ingredientElement.addProperty("count", ingredient.count);
                ingredientsArray.add(ingredientElement);
            }

            json.add("ingredients", ingredientsArray);
            JsonObject resultObject = new JsonObject();
            resultObject.addProperty("item", Registries.ITEM.getId(this.output).toString());
            if (this.count > 1) {
                resultObject.addProperty("count", this.count);
            }
            json.addProperty("progressRequired",this.progressRequired);
            json.addProperty("difficulty",this.difficulty);
            json.addProperty("shapeless",this.shapeless);
            json.add("result", resultObject);
        }

        public Identifier getRecipeId() {
            return this.id;
        }

        public RecipeSerializer<?> getSerializer() {
            return this.type;
        }

        @Nullable
        public JsonObject toAdvancementJson() {
            return this.advancement.toJson();
        }

        @Nullable
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}
