package org.oxytocina.geomancy.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.helpers.NbtHelper;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;

import java.util.Objects;

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
        res.setCount(count);
        return res;
    }

    @Override
    public boolean test(ItemStack stack){
        return ingredient.test(stack) && nbtsMatch(stack.getNbt(),nbt);
    }

    public boolean nbtsMatch(NbtCompound testedNbt, NbtCompound conditionNbt){
        if(conditionNbt==null|| conditionNbt.isEmpty()) return true;
        if(testedNbt==null||testedNbt.isEmpty()) return false;

        for(var key : conditionNbt.getKeys())
        {
            if(!testedNbt.contains(key)) return false;
            if(!nbtElementsMatch(testedNbt.get(key),conditionNbt.get(key))) return false;
        }

        return true;
    }

    public boolean nbtElementsMatch(NbtElement testedElement, NbtElement conditionElement){
        if(testedElement.getType() != conditionElement.getType()) return false;
        switch(testedElement.getType()){
            case NbtElement.COMPOUND_TYPE : return nbtsMatch((NbtCompound) testedElement,(NbtCompound) conditionElement);
            case NbtElement.STRING_TYPE: return Objects.equals(testedElement.asString(), conditionElement.asString());
        }
        if(testedElement instanceof AbstractNbtNumber testedNumber && conditionElement instanceof AbstractNbtNumber conditionNumber){
            return testedNumber.doubleValue() == conditionNumber.doubleValue();
        }
        return true;
    }
}
