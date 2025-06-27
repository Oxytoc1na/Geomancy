package org.oxytocina.geomancy.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class JewelryRecipeSerializer<R extends JewelryRecipe> implements GatedRecipeSerializer<R> {

    public final JewelryRecipeSerializer.RecipeFactory<R> recipeFactory;

    public JewelryRecipeSerializer(JewelryRecipeSerializer.RecipeFactory<R> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    public interface RecipeFactory<R> {
        R create(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, SmithingIngredient base, int progressRequiredBase, float gemProgressCostMultiplier, int difficulty, float gemDifficultyMultiplier);
    }

    @Override
    public R read(Identifier identifier, JsonObject jsonObject) {
        String group = readGroup(jsonObject);
        boolean secret = readSecret(jsonObject);
        Identifier requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);

        SmithingIngredient base = SmithingIngredient.fromJson(jsonObject.get("base"));

        int progressRequiredBase = jsonObject.get("progressRequiredBase").getAsInt();
        int difficulty = jsonObject.get("difficulty").getAsInt();

        float gemProgressCostMultiplier = jsonObject.get("gemProgressCostMultiplier").getAsFloat();
        float gemDifficultyMultiplier = jsonObject.get("gemDifficultyMultiplier").getAsFloat();

        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, base, progressRequiredBase, gemProgressCostMultiplier, difficulty, gemDifficultyMultiplier);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, R recipe) {
        packetByteBuf.writeString(recipe.group);
        packetByteBuf.writeBoolean(recipe.secret);
        writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);

        recipe.base.write(packetByteBuf);
        packetByteBuf.writeInt(recipe.progressRequiredBase);
        packetByteBuf.writeInt(recipe.difficulty);
        packetByteBuf.writeFloat(recipe.gemProgressCostMultiplier);
        packetByteBuf.writeFloat(recipe.gemDifficultyMultiplier);
    }

    @Override
    public R read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String group = packetByteBuf.readString();
        boolean secret = packetByteBuf.readBoolean();
        Identifier requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);

        SmithingIngredient base = SmithingIngredient.fromPacket(packetByteBuf);
        int progressRequiredBase = packetByteBuf.readInt();
        int difficulty = packetByteBuf.readInt();
        float gemProgressCostMultiplier = packetByteBuf.readFloat();
        float gemDifficultyMultiplier = packetByteBuf.readFloat();
        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, base, progressRequiredBase,gemProgressCostMultiplier,difficulty,gemDifficultyMultiplier);
    }

}
