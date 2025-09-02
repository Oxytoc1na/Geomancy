package org.oxytocina.geomancy.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.helpers.NbtHelper;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;

public class NbtIngredient extends CountIngredient {
    public NbtCompound nbt;

    public NbtIngredient(Ingredient ingredient, int count, int slot, NbtCompound nbt) {
        super(ingredient, count, slot);
        this.nbt=nbt;
    }

    public NbtIngredient(Ingredient ingredient, int count, int slot) {
        this(ingredient,count,slot,new NbtCompound());
    }

    public NbtIngredient(CountIngredient base,NbtCompound nbt){
        this(base.ingredient,base.count,base.slot,nbt);
    }

    public static NbtIngredient fromJson(@Nullable JsonElement json) {
        return fromJson(json, true);
    }

    public static NbtIngredient fromJson(@Nullable JsonElement json, boolean allowAir) {
        if (json != null && !json.isJsonNull()) {
            CountIngredient base = CountIngredient.fromJson(json,allowAir);
            var nbt = (NbtCompound) NbtHelper.fromJson(json.getAsJsonObject().get("nbt"));
            return new NbtIngredient(base,nbt);
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static NbtIngredient fromPacket(PacketByteBuf buf) {
        CountIngredient ing = CountIngredient.fromPacket(buf);
        var nbt = buf.readNbt();
        return new NbtIngredient(ing,nbt);
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeNbt(nbt);
    }

    @Override
    public JsonElement toJson() {
        JsonObject res = super.toJson().getAsJsonObject();
        res.add("nbt", NbtHelper.toJson(nbt));
        return res;
    }

    public static NbtIngredient ofItems(ItemConvertible... items) {
        return ofItems(1,items);
    }

    public static NbtIngredient ofItems(int count,ItemConvertible... items) {
        return ofItems(count,new NbtCompound(),items);
    }

    public static NbtIngredient ofItems(int count,NbtCompound nbt, ItemConvertible... items) {
        return ofItems(count,nbt,-1,items);
    }

    public static NbtIngredient ofItems(int count,NbtCompound nbt,int slot, ItemConvertible... items) {
        CountIngredient ingredient1 = CountIngredient.ofItems(count,slot,items);
        return new NbtIngredient(ingredient1,nbt);
    }

    public ItemStack getStack() {
        ItemStack res = ItemStack.EMPTY;
        if(ingredient.getMatchingStacks().length>0) res=ingredient.getMatchingStacks()[0];
        res.setNbt(nbt);
        return res;
    }
}
