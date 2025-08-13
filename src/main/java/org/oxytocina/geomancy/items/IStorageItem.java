package org.oxytocina.geomancy.items;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
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
    boolean autocollects();

    default void tryCollect(ItemStack storage, ItemEntity entity, PlayerEntity player, ItemStack stack){
        if(!stack.isIn(getStorableTag())) return;

        var inv = getInventory(storage);

        // try to stack onto existing stacks
        for (int i = 0; i < inv.size(); i++) {
            if(stack.isEmpty()) return;

            var baseStack = inv.getStack(i);
            if(!baseStack.isStackable()) continue;
            if(!ItemStack.canCombine(baseStack, stack)) continue;

            int combinedCount = baseStack.getCount() + stack.getCount();
            if (combinedCount <= stack.getMaxCount()) {
                stack.setCount(0);
                baseStack.setCount(combinedCount);
                saveInventoryToNbt(storage);
                return;
            } else if (baseStack.getCount() < stack.getMaxCount()) {
                stack.decrement(stack.getMaxCount() - baseStack.getCount());
                baseStack.setCount(stack.getMaxCount());
                saveInventoryToNbt(storage);
            }
        }

        // try to stack onto free slots
        for (int i = 0; i < inv.size(); i++) {
            var baseStack = inv.getStack(i);
            if(!baseStack.isEmpty()) continue;
            inv.setStack(i,stack.copyAndEmpty());
            saveInventoryToNbt(storage);
            return;
        }
    }
}
