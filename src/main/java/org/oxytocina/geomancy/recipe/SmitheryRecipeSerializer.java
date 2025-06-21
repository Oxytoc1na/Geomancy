package org.oxytocina.geomancy.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class SmitheryRecipeSerializer<R extends SmitheryRecipe> implements GatedRecipeSerializer<R> {

    public final SmitheryRecipeSerializer.RecipeFactory<R> recipeFactory;

    public SmitheryRecipeSerializer(SmitheryRecipeSerializer.RecipeFactory<R> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    public interface RecipeFactory<R> {
        R create(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, DefaultedList<Ingredient> inputIngredients, ItemStack outputItemStack);
    }

    @Override
    public R read(Identifier identifier, JsonObject jsonObject) {
        String group = readGroup(jsonObject);
        boolean secret = readSecret(jsonObject);
        Identifier requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);

        JsonArray jsonElement = JsonHelper.getArray(jsonObject, "ingredients");
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        for(JsonElement elem : jsonElement){
            Ingredient ingredient = Ingredient.fromJson(jsonElement);
            ingredients.add(ingredient);
        }
        ItemStack outputItemStack = RecipeUtils.itemStackWithNbtFromJson(JsonHelper.getObject(jsonObject, "result"));

        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredients, outputItemStack);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, R recipe) {
        packetByteBuf.writeString(recipe.group);
        packetByteBuf.writeBoolean(recipe.secret);
        writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);

        packetByteBuf.writeInt(recipe.inputs.size());
        for(var input : recipe.inputs){
            input.write(packetByteBuf);
        }

        packetByteBuf.writeItemStack(recipe.output);
    }

    @Override
    public R read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String group = packetByteBuf.readString();
        boolean secret = packetByteBuf.readBoolean();
        Identifier requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);

        int ingredientCount = packetByteBuf.readInt();
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        for (int i = 0; i < ingredientCount; i++) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            ingredients.add(ingredient);
        }

        ItemStack outputItemStack = packetByteBuf.readItemStack();
        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredients, outputItemStack);
    }

}
