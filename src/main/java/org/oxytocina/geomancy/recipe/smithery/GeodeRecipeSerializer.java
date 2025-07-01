package org.oxytocina.geomancy.recipe.smithery;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.recipe.GatedRecipeSerializer;

public class GeodeRecipeSerializer<R extends GeodeRecipe> implements GatedRecipeSerializer<R> {

    public final GeodeRecipeSerializer.RecipeFactory<R> recipeFactory;

    public GeodeRecipeSerializer(GeodeRecipeSerializer.RecipeFactory<R> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    public interface RecipeFactory<R> {
        R create(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, SmithingIngredient base, int progressRequiredBase, int difficulty, float difficultyPerMighty);
    }

    @Override
    public R read(Identifier identifier, JsonObject jsonObject) {
        String group = readGroup(jsonObject);
        boolean secret = readSecret(jsonObject);
        Identifier requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);

        SmithingIngredient base = SmithingIngredient.fromJson(jsonObject.get("base"));

        int progressRequiredBase = jsonObject.get("progressRequiredBase").getAsInt();
        int difficulty = jsonObject.get("difficulty").getAsInt();

        float difficultyPerMighty = jsonObject.get("difficultyPerMighty").getAsFloat();

        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, base, progressRequiredBase, difficulty, difficultyPerMighty);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, R recipe) {
        packetByteBuf.writeString(recipe.group);
        packetByteBuf.writeBoolean(recipe.secret);
        writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);

        recipe.base.write(packetByteBuf);
        packetByteBuf.writeInt(recipe.progressRequiredBase);
        packetByteBuf.writeInt(recipe.difficulty);
        packetByteBuf.writeFloat(recipe.difficultyPerMighty);
    }

    @Override
    public R read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String group = packetByteBuf.readString();
        boolean secret = packetByteBuf.readBoolean();
        Identifier requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);

        SmithingIngredient base = SmithingIngredient.fromPacket(packetByteBuf);
        int progressRequiredBase = packetByteBuf.readInt();
        int difficulty = packetByteBuf.readInt();
        float difficultyPerMighty = packetByteBuf.readFloat();
        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, base, progressRequiredBase,difficulty,difficultyPerMighty);
    }

}
