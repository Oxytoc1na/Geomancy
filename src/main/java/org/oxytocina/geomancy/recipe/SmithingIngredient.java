package org.oxytocina.geomancy.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;

public class SmithingIngredient extends CountIngredient{

    public int mishapWeight = 1;

    public SmithingIngredient(Ingredient ingredient, int count, int slot) {
        this(ingredient,count,slot,1);
    }

    public SmithingIngredient(Ingredient ingredient,int count, int slot,int mishapWeight){
        super(ingredient, count, slot);
        this.mishapWeight = mishapWeight;
    }

    public SmithingIngredient(CountIngredient base,int mishapWeight){
        this(base.ingredient,base.count,base.slot,mishapWeight);
    }

    public static SmithingIngredient fromJson(@Nullable JsonElement json) {
        return fromJson(json, true);
    }

    public static SmithingIngredient fromJson(@Nullable JsonElement json, boolean allowAir) {
        if (json != null && !json.isJsonNull()) {
            CountIngredient base = CountIngredient.fromJson(json,allowAir);
            int mishapWeight = json.getAsJsonObject().get("mishapWeight").getAsInt();
            return new SmithingIngredient(base,mishapWeight);
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static SmithingIngredient fromPacket(PacketByteBuf buf) {
        CountIngredient ing = CountIngredient.fromPacket(buf);
        int mishapWeight = buf.readInt();
        return new SmithingIngredient(ing,mishapWeight);
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeInt(mishapWeight);
    }

    @Override
    public JsonElement toJson() {
        JsonObject res = super.toJson().getAsJsonObject();
        res.addProperty("mishapWeight",mishapWeight);
        return res;
    }

    public static SmithingIngredient ofItems(ItemConvertible... items) {
        return ofItems(1,items);
    }

    public static SmithingIngredient ofItems(int count,ItemConvertible... items) {
        return ofItems(count,1,items);
    }

    public static SmithingIngredient ofItems(int count,int mishapWeight, ItemConvertible... items) {
        return ofItems(count,1,-1,items);
    }

    public static SmithingIngredient ofItems(int count,int mishapWeight,int slot, ItemConvertible... items) {

        CountIngredient ingredient1 = CountIngredient.ofItems(count,slot,items);

        return new SmithingIngredient(ingredient1,mishapWeight);
    }
}
