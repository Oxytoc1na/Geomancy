package org.oxytocina.geomancy.loottables;

import com.google.common.collect.Sets;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.Set;

public class ModLootTables {
    private static final Set<Identifier> LOOT_TABLES = Sets.newHashSet();

    public static final Identifier DWARVEN_REMNANTS_CHEST = register("chests/dwarven_remnants");

    public static void register(){

    }

    private static Identifier register(String id) {
        return registerLootTable(new Identifier(Geomancy.MOD_ID,id));
    }

    private static Identifier registerLootTable(Identifier id) {
        if (LOOT_TABLES.add(id)) {
            return id;
        } else {
            throw new IllegalArgumentException(id + " is already a registered geomancy loot table");
        }
    }
}
