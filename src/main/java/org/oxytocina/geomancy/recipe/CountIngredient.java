package org.oxytocina.geomancy.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;

public class CountIngredient {
    public Ingredient ingredient;
    public int count;
    public int slot;

    public CountIngredient(Ingredient ingredient, int count, int slot){
        this.ingredient = ingredient;
        this.count=count;
        this.slot = slot;
    }

    public static CountIngredient fromJson(@Nullable JsonElement json) {
        return fromJson(json, true);
    }

    public static CountIngredient fromJson(@Nullable JsonElement json, boolean allowAir) {
        if (json != null && !json.isJsonNull()) {
            Ingredient ingredient1 = Ingredient.fromJson(json,allowAir);
            int count = json.getAsJsonObject().get("count").getAsInt();
            int slot = -1;
            if(json.getAsJsonObject().has("slot")) slot = json.getAsJsonObject().get("slot").getAsInt();
            return new CountIngredient(ingredient1,count,slot);
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static CountIngredient fromPacket(PacketByteBuf buf) {
        Ingredient ingredient1 = Ingredient.fromPacket(buf);
        int count = buf.readInt();
        int slot = buf.readInt();
        return new CountIngredient(ingredient1, count, slot);
    }

    public void write(PacketByteBuf buf) {
        ingredient.write(buf);
        buf.writeInt(count);
        buf.writeInt(slot);
    }

    public boolean test(ItemStack stack){
        return ingredient.test(stack);
    }

    public JsonElement toJson() {
        JsonElement ingredientElement = ingredient.toJson();

        var res = ingredientElement.getAsJsonObject();
        res.addProperty("count",count);
        if(hasSlot())res.addProperty("slot",slot);

        return res;

    }

    public boolean hasSlot() {return slot!=-1;}

    public static CountIngredient ofItems(ItemConvertible... items) {
        return ofItems(1,items);
    }

    public static CountIngredient ofItems(int count, ItemConvertible... items) {
        return ofItems(count,-1,items);
    }

    public static CountIngredient ofItems(int count,int slot, ItemConvertible... items) {

        Ingredient ingredient1 = Ingredient.ofItems(items);

        return new CountIngredient(ingredient1,count,slot);
    }
}
