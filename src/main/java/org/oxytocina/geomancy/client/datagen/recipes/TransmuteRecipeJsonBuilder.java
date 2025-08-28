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
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.recipe.CountIngredient;
import org.oxytocina.geomancy.recipe.RecipeUtils;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.function.Consumer;

public class TransmuteRecipeJsonBuilder {
    private final RecipeCategory category;
    private final SmithingIngredient input;
    private final ItemStack output;
    private final int count;
    private final float cost;
    private final Advancement.Builder advancement = Advancement.Builder.createUntelemetered();
    @Nullable private final Identifier requiredAdvancement;
    private final RecipeSerializer<?> serializer;

    public TransmuteRecipeJsonBuilder(RecipeSerializer<?> serializer, RecipeCategory category, SmithingIngredient input, ItemStack output, int count, float cost, @Nullable Identifier requiredAdvancement) {
        this.category = category;
        this.serializer = serializer;
        this.input = input;
        this.output = output;
        this.count = count;
        this.cost = cost;
        this.requiredAdvancement=requiredAdvancement;
    }

    public static TransmuteRecipeJsonBuilder create(SmithingIngredient input, Item output, int count, float cost, RecipeCategory category, Identifier requiredAdvancement) {
        return new TransmuteRecipeJsonBuilder(ModRecipeTypes.SMITHING_SERIALIZER, category, input, new ItemStack(output),
                count,cost, requiredAdvancement);
    }

    public static TransmuteRecipeJsonBuilder create(SmithingIngredient input, ItemStack output, int count, float cost, RecipeCategory category, Identifier requiredAdvancement) {
        return new TransmuteRecipeJsonBuilder(ModRecipeTypes.SMITHING_SERIALIZER, category, input, output,
                count,cost, requiredAdvancement);
    }

    public TransmuteRecipeJsonBuilder criterion(String name, CriterionConditions conditions) {
        this.advancement.criterion(name, conditions);
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.advancement.parent(CraftingRecipeJsonBuilder.ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(net.minecraft.advancement.AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        exporter.accept(new Provider(
                recipeId, this.serializer, this.input, this.output, this.count, this.cost, this.advancement,
                recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/"),requiredAdvancement));
    }

    private void validate(Identifier recipeId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    public static record Provider(Identifier id, RecipeSerializer<?> type, SmithingIngredient input,
                                  ItemStack output, int count, float cost,
                                  Advancement.Builder advancement, Identifier advancementId, Identifier requiredAdvancement) implements RecipeJsonProvider {
        public void serialize(JsonObject json) {
            json.add("base", this.input.toJson().getAsJsonObject());
            JsonObject resultObject = new JsonObject();
            resultObject.add("item", RecipeUtils.itemStackWithNbtToJson(output));
            json.addProperty("cost",this.cost);
            json.add("result", resultObject);
            if(requiredAdvancement!=null)json.addProperty("required_advancement",requiredAdvancement.toString());
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
