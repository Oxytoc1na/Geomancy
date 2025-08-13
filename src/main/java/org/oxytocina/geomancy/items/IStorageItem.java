package org.oxytocina.geomancy.items;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;

public interface IStorageItem {
    HashMap<ItemStack,DefaultedList<ItemStack>> inventories = new HashMap<>();
    HashMap<ItemStack,Inventory> actualInventories = new HashMap<>();

    DefaultedList<ItemStack> readInventoryFromNbt(ItemStack stack);
    void saveInventoryToNbt(ItemStack stack);

    DefaultedList<ItemStack> getItems(ItemStack stack);
    int getStorageSize(ItemStack stack);
    Inventory getInventory(ItemStack stack);
    void setInventory(ItemStack stack, NbtCompound nbt);
    void setStack(ItemStack output, int i, ItemStack stack);
    TagKey<Item> getStorableTag();
}
