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
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.recipe.CountIngredient;
import org.oxytocina.geomancy.recipe.NbtIngredient;
import org.oxytocina.geomancy.recipe.RecipeUtils;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.List;
import java.util.function.Consumer;

public class SoulForgeRecipeJsonBuilder {
    private final RecipeCategory category;
    private final List<NbtIngredient> inputs;
    private final ItemStack output;
    private final int count;
    private final float cost;
    private final float instability;
    private final Advancement.Builder advancement = Advancement.Builder.createUntelemetered();
    @Nullable private final Identifier requiredAdvancement;
    private final RecipeSerializer<?> serializer;

    public SoulForgeRecipeJsonBuilder(RecipeSerializer<?> serializer, RecipeCategory category, List<NbtIngredient> inputs,
                                      ItemStack output, int count, float cost, float instability, @Nullable Identifier requiredAdvancement) {
        this.category = category;
        this.serializer = serializer;
        this.inputs = inputs;
        this.output = output;
        this.count = count;
        this.cost=cost;
        this.instability=instability;
        this.requiredAdvancement=requiredAdvancement;
    }

    public static SoulForgeRecipeJsonBuilder create(List<NbtIngredient> inputs, Item output, int count, float cost, float instability, RecipeCategory category, Identifier requiredAdvancement) {
        return new SoulForgeRecipeJsonBuilder(ModRecipeTypes.SOULFORGE_SIMPLE_SERIALIZER, category, inputs, new ItemStack(output),
                count,cost, instability, requiredAdvancement);
    }

    public static SoulForgeRecipeJsonBuilder create(List<NbtIngredient> inputs, ItemStack output, int count, float cost, float instability, RecipeCategory category, Identifier requiredAdvancement) {
        return new SoulForgeRecipeJsonBuilder(ModRecipeTypes.SOULFORGE_SIMPLE_SERIALIZER, category, inputs, output,
                count,cost, instability, requiredAdvancement);
    }

    public SoulForgeRecipeJsonBuilder criterion(String name, CriterionConditions conditions) {
        this.advancement.criterion(name, conditions);
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.advancement.parent(CraftingRecipeJsonBuilder.ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(net.minecraft.advancement.AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        exporter.accept(new Provider(
                recipeId, this.serializer, this.inputs, this.output, this.count, this.cost,
                this.instability, this.advancement,
                recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/"),requiredAdvancement));
    }

    private void validate(Identifier recipeId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    public static record Provider(Identifier id, RecipeSerializer<?> type, List<NbtIngredient> inputs,
                                  ItemStack output, int count,float cost, float instability,
                                  Advancement.Builder advancement, Identifier advancementId, Identifier requiredAdvancement) implements RecipeJsonProvider {
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
            resultObject.add("item", RecipeUtils.itemStackWithNbtToJson(output));
            json.addProperty("cost",this.cost);
            json.addProperty("instability",this.instability);
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
