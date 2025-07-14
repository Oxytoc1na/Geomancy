package org.oxytocina.geomancy.items;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ICastingItem {
    Inventory getInventory();
    void saveInventory();


}
