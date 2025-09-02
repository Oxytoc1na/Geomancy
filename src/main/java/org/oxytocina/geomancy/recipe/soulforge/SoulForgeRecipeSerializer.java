package org.oxytocina.geomancy.recipe.soulforge;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.oxytocina.geomancy.recipe.GatedRecipeSerializer;
import org.oxytocina.geomancy.recipe.NbtIngredient;
import org.oxytocina.geomancy.recipe.RecipeUtils;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;

import java.util.ArrayList;
import java.util.List;

public class SoulForgeRecipeSerializer<R extends SoulForgeRecipe> implements GatedRecipeSerializer<R> {

    public final RecipeFactory<R> recipeFactory;

    public SoulForgeRecipeSerializer(RecipeFactory<R> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    public interface RecipeFactory<R> {
        R create(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, List<NbtIngredient> inputs, ItemStack outputItemStack, float cost, float instability, float speed);
    }

    @Override
    public R read(Identifier identifier, JsonObject jsonObject) {
        String group = readGroup(jsonObject);
        boolean secret = readSecret(jsonObject);
        Identifier requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);

        var ingredientArr = jsonObject.getAsJsonArray("ingredients");
        List<NbtIngredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientArr.size(); i++) {
            var ingredientJson = ingredientArr.get(i);
            NbtIngredient ingredient = NbtIngredient.fromJson(ingredientJson);
            ingredients.add(ingredient);
        }

        ItemStack outputItemStack = RecipeUtils.itemStackWithNbtFromJson(JsonHelper.getObject(JsonHelper.getObject(jsonObject, "result"),"item"));
        float cost = jsonObject.get("cost").getAsFloat();
        float instability = jsonObject.get("instability").getAsFloat();
        float speed = jsonObject.get("speed").getAsFloat();

        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, ingredients, outputItemStack, cost,instability,speed);
    }

    @Override
    public void write(PacketByteBuf buf, R recipe) {
        buf.writeString(recipe.group);
        buf.writeBoolean(recipe.secret);
        writeNullableIdentifier(buf, recipe.requiredAdvancementIdentifier);

        buf.writeInt(recipe.inputs.size());
        for (int i = 0; i < recipe.inputs.size(); i++) {
            recipe.inputs.get(i).write(buf);
        }

        buf.writeItemStack(recipe.output);
        buf.writeFloat(recipe.cost);
        buf.writeFloat(recipe.instability);
        buf.writeFloat(recipe.speed);
    }

    @Override
    public R read(Identifier identifier, PacketByteBuf buf) {
        String group = buf.readString();
        boolean secret = buf.readBoolean();
        Identifier requiredAdvancementIdentifier = readNullableIdentifier(buf);

        int inputSize = buf.readInt();
        List<NbtIngredient> inputs = new ArrayList<>();
        for (int i = 0; i < inputSize; i++) {
            inputs.add(NbtIngredient.fromPacket(buf));
        }

        ItemStack outputItemStack = buf.readItemStack();
        float cost = buf.readFloat();
        float instability = buf.readFloat();
        float speed = buf.readFloat();
        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, inputs,outputItemStack, cost,instability,speed);
    }

}
