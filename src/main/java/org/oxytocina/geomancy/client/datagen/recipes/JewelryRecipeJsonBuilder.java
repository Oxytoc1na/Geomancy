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
import org.oxytocina.geomancy.recipe.SmithingIngredient;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.function.Consumer;

public class JewelryRecipeJsonBuilder {
    private final RecipeCategory category;
    private final SmithingIngredient base;
    private final int progressRequiredBase;
    private final int difficulty;
    private final float gemProgressCostMultiplier;
    private final float gemDifficultyMultiplier;
    private final Advancement.Builder advancement = Advancement.Builder.createUntelemetered();
    private final RecipeSerializer<?> serializer;

    public JewelryRecipeJsonBuilder(RecipeSerializer<?> serializer, RecipeCategory category, SmithingIngredient base, int progressRequiredBase,float gemProgressCostMultiplier, int difficulty, float gemDifficultyMultiplier) {
        this.category = category;
        this.serializer = serializer;
        this.base = base;
        this.progressRequiredBase=progressRequiredBase;
        this.difficulty=difficulty;
        this.gemProgressCostMultiplier=gemProgressCostMultiplier;
        this.gemDifficultyMultiplier=gemDifficultyMultiplier;
    }

    public static JewelryRecipeJsonBuilder create(SmithingIngredient base, int progressRequiredBase,float gemProgressCostMultiplier, int difficulty, float gemDifficultyMultiplier, RecipeCategory category) {
        return new JewelryRecipeJsonBuilder(ModRecipeTypes.JEWELRY_SERIALIZER, category, base, progressRequiredBase,
                gemProgressCostMultiplier,difficulty, gemDifficultyMultiplier);
    }

    public JewelryRecipeJsonBuilder criterion(String name, CriterionConditions conditions) {
        this.advancement.criterion(name, conditions);
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.advancement.parent(CraftingRecipeJsonBuilder.ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(net.minecraft.advancement.AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        exporter.accept(new Provider(
                recipeId, this.serializer, this.base, this.progressRequiredBase, this.gemProgressCostMultiplier,
                this.difficulty,this.gemDifficultyMultiplier, this.advancement,
                recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    private void validate(Identifier recipeId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    public static record Provider(Identifier id, RecipeSerializer<?> type, SmithingIngredient base, int progressRequiredBase,float gemProgressCostMultiplier, int difficulty, float gemDifficultyMultiplier,
                                  Advancement.Builder advancement, Identifier advancementId) implements RecipeJsonProvider {
        public void serialize(JsonObject json) {
            //if (!this.group.isEmpty()) {
            //    json.addProperty("group", this.group);
            //}

            JsonObject ingredientElement = base.toJson().getAsJsonObject();
            //ingredientElement.addProperty("count", base.count); // always 1

            json.add("base", ingredientElement);
            json.addProperty("progressRequiredBase",this.progressRequiredBase);
            json.addProperty("gemProgressCostMultiplier",this.gemProgressCostMultiplier);
            json.addProperty("difficulty",this.difficulty);
            json.addProperty("gemDifficultyMultiplier",this.gemDifficultyMultiplier);
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
