package org.oxytocina.geomancy.loottables;

import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.JsonSerializer;
import org.oxytocina.geomancy.Geomancy;

public class ModLootFunctionTypes {
    public static final LootFunctionType JEWELRY = register("jewelry", new JewelryLootFunction.Serializer());

    private static LootFunctionType register(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, Geomancy.locate(id), new LootFunctionType(jsonSerializer));
    }

    public static void register(){

    }
}
