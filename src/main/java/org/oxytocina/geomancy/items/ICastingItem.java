package org.oxytocina.geomancy.items;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ICastingItem {
    DefaultedList<ItemStack> readInventoryFromNbt(ItemStack stack);
    void saveInventoryToNbt(ItemStack stack);

    DefaultedList<ItemStack> getItems(ItemStack stack);

}
