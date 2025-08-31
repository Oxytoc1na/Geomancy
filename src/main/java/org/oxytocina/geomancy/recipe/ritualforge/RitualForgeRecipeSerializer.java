package org.oxytocina.geomancy.recipe.ritualforge;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.oxytocina.geomancy.recipe.GatedRecipeSerializer;
import org.oxytocina.geomancy.recipe.RecipeUtils;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;

public class RitualForgeRecipeSerializer<R extends RitualForgeRecipe> implements GatedRecipeSerializer<R> {

    public final RecipeFactory<R> recipeFactory;

    public RitualForgeRecipeSerializer(RecipeFactory<R> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    public interface RecipeFactory<R> {
        R create(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, SmithingIngredient base, ItemStack outputItemStack, float cost);
    }

    @Override
    public R read(Identifier identifier, JsonObject jsonObject) {
        String group = readGroup(jsonObject);
        boolean secret = readSecret(jsonObject);
        Identifier requiredAdvancementIdentifier = readRequiredAdvancementIdentifier(jsonObject);

        SmithingIngredient base = SmithingIngredient.fromJson(jsonObject.get("base"));
        ItemStack outputItemStack = RecipeUtils.itemStackWithNbtFromJson(JsonHelper.getObject(JsonHelper.getObject(jsonObject, "result"),"item"));
        float cost = jsonObject.get("cost").getAsFloat();

        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, base, outputItemStack, cost);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, R recipe) {
        packetByteBuf.writeString(recipe.group);
        packetByteBuf.writeBoolean(recipe.secret);
        writeNullableIdentifier(packetByteBuf, recipe.requiredAdvancementIdentifier);

        recipe.base.write(packetByteBuf);
        packetByteBuf.writeItemStack(recipe.output);
        packetByteBuf.writeFloat(recipe.cost);
    }

    @Override
    public R read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String group = packetByteBuf.readString();
        boolean secret = packetByteBuf.readBoolean();
        Identifier requiredAdvancementIdentifier = readNullableIdentifier(packetByteBuf);

        SmithingIngredient base = SmithingIngredient.fromPacket(packetByteBuf);
        ItemStack outputItemStack = packetByteBuf.readItemStack();
        float cost = packetByteBuf.readFloat();
        return this.recipeFactory.create(identifier, group, secret, requiredAdvancementIdentifier, base,outputItemStack, cost);
    }

}
