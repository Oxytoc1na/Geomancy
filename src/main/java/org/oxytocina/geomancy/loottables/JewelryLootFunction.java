package org.oxytocina.geomancy.loottables;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;
import org.oxytocina.geomancy.items.jewelry.GemSlot;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;

public class JewelryLootFunction extends ConditionalLootFunction {

    JewelryLootFunction(LootCondition[] conditions) {
        super(conditions);
    }

    public LootFunctionType getType() {
        return ModLootFunctionTypes.JEWELRY;
    }

    public ItemStack process(ItemStack stack, LootContext context) {
        var nbt = stack.getOrCreateNbt();

        NbtList gemList = new NbtList();
        int gemSlots = ((IJewelryItem)stack.getItem()).getGemSlotCount();
        for (int i = 0; i < gemSlots; i++) {
            if(context.getRandom().nextFloat() > 0.6f) continue;
            HashMap<Item,Integer> weights = new HashMap<>();
            for(var item : GemSlot.settingsMap.keySet()){
                weights.put(item,GemSlot.getSettings(item).lootWeight);
            }
            var chosen = Toolbox.selectWeightedRandomIndex(weights,null);
            GemSlot slot = new GemSlot(chosen,0.2f+1.8f*context.getRandom().nextFloat());
            gemList.add(GemSlot.toNbt(slot));
        }

        nbt.put("gems",gemList);

        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return builder(JewelryLootFunction::new);
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<JewelryLootFunction> {
        public void toJson(JsonObject jsonObject, JewelryLootFunction func, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, func, jsonSerializationContext);
        }

        public JewelryLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new JewelryLootFunction(lootConditions);
        }
    }
}
