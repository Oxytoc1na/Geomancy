package org.oxytocina.geomancy.recipe;

import com.google.common.collect.*;
import com.google.gson.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.exceptions.*;
import org.oxytocina.geomancy.helpers.NbtHelper;
//import de.dafuqs.matchbooks.recipe.*;
import net.minecraft.block.*;
import net.minecraft.command.argument.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.recipe.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;

import net.minecraft.util.collection.*;
import java.util.*;

public class RecipeUtils {

    public static ItemStack itemStackWithNbtFromJson(JsonObject json) {
        Item item = ShapedRecipe.getItem(json);
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int count = JsonHelper.getInt(json, "count", 1);

            if (count < 1) {
                throw new JsonSyntaxException("Invalid output count: " + count);
            } else {
                ItemStack stack = new ItemStack(item, count);

                Optional<NbtCompound> nbt = NbtHelper.getNbtCompound(json.get("nbt"));
                nbt.ifPresent(stack::setNbt);

                return stack;
            }
        }
    }

    public static JsonObject itemStackWithNbtToJson(ItemStack stack) {
        Item item = stack.getItem();
        JsonObject json = new JsonObject();

        json.addProperty("item",Registries.ITEM.getId(item).toString());
        json.addProperty("count",stack.getCount());
        if(stack.hasNbt()){
            json.add("nbt",NbtHelper.toJson(stack.getNbt()));
        }

        return json;
    }

}