package org.oxytocina.geomancy.util;

import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.blocks.blockEntities.AutocasterBlockEntity;

public class InventoryUtil {
    public static ItemStack tryInsert(Inventory inv, ItemStack stack){return tryInsert(inv,stack,-1);}
    public static ItemStack tryInsert(Inventory inv, ItemStack stack, int slotOverride){
        if(slotOverride>=0) return tryInsertSlot(inv,stack,slotOverride);

        // try to stack onto existing stacks
        for (int i = 0; i < inv.size(); i++) {
            if(stack.isEmpty()) return stack;

            var baseStack = inv.getStack(i);
            if(!baseStack.isStackable()) continue;
            if(!ItemStack.canCombine(baseStack, stack)) continue;

            int combinedCount = baseStack.getCount() + stack.getCount();
            if (combinedCount <= stack.getMaxCount()) {
                baseStack.setCount(combinedCount);
                stack.setCount(0);
                return stack;
            } else if (baseStack.getCount() < stack.getMaxCount()) {
                stack.decrement(stack.getMaxCount() - baseStack.getCount());
                baseStack.setCount(stack.getMaxCount());
            }
        }

        // try to stack onto free slots
        for (int i = 0; i < inv.size(); i++) {
            var baseStack = inv.getStack(i);
            if(!baseStack.isEmpty()) continue;
            inv.setStack(i,stack.copy());
            stack.setCount(0);
            return stack;
        }

        // leftovers
        return stack;
    }

    public static boolean canInsertFully(Inventory inv, ItemStack stack){return canInsertFully(inv,stack,-1);}
    public static boolean canInsertFully(Inventory inv, ItemStack stack, int slotOverride) {
        if(slotOverride>=0) return canInsertFullySlot(inv,stack,slotOverride);
        int left = stack.getCount();

        // try to stack onto free slots
        for (int i = 0; i < inv.size(); i++) {
            var baseStack = inv.getStack(i);
            if(!baseStack.isEmpty()) continue;
            return true;
        }

        // try to stack onto existing stacks
        for (int i = 0; i < inv.size(); i++) {
            if(left<=0) return true;

            var baseStack = inv.getStack(i);
            if(!baseStack.isStackable()) continue;
            if(!ItemStack.canCombine(baseStack, stack)) continue;

            int combinedCount = baseStack.getCount() + stack.getCount();
            if (combinedCount <= stack.getMaxCount()) {
                return true;
            } else if (baseStack.getCount() < stack.getMaxCount()) {
                left-=stack.getMaxCount() - baseStack.getCount();
            }
        }

        return left<=0;
    }

    public static ItemStack tryInsertSlot(Inventory inv, ItemStack stack, int slot){
        // try to stack onto existing stacks
        if(stack.isEmpty()) return stack;

        var baseStack = inv.getStack(slot);

        // try to stack onto free slots
        if(baseStack.isEmpty()){
            inv.setStack(slot,stack.copyAndEmpty());
            return stack;
        }

        if(!baseStack.isStackable()) return stack;
        if(!ItemStack.canCombine(baseStack, stack)) return stack;

        int combinedCount = baseStack.getCount() + stack.getCount();
        if (combinedCount <= stack.getMaxCount()) {
            baseStack.setCount(combinedCount);
            stack.setCount(0);
            return stack;
        } else if (baseStack.getCount() < stack.getMaxCount()) {
            stack.decrement(stack.getMaxCount() - baseStack.getCount());
            baseStack.setCount(stack.getMaxCount());
        }

        return stack;
    }

    public static boolean canInsertFullySlot(Inventory inv, ItemStack stack, int slot) {
        int left = stack.getCount();
        if(left<=0) return true;

        // try to stack onto free slots
        var baseStack = inv.getStack(slot);
        if(baseStack.isEmpty()) return true;

        // try to stack onto existing stacks
        if(!baseStack.isStackable()) return false;
        if(!ItemStack.canCombine(baseStack, stack)) return false;

        int combinedCount = baseStack.getCount() + stack.getCount();
        if (combinedCount <= stack.getMaxCount()) {
            return true;
        } else if (baseStack.getCount() < stack.getMaxCount()) {
            left-=stack.getMaxCount() - baseStack.getCount();
        }

        return left<=0;
    }

    public static float getSlotWithStack(Inventory inv, ItemStack stack) {
        if(inv instanceof PlayerInventory pi) return pi.getSlotWithStack(stack);
        if(inv instanceof AutocasterBlockEntity acbe) return acbe.getSlotWithStack(stack);

        for (int i = 0; i < inv.size(); i++) {
            if(ItemStack.areEqual(inv.getStack(i),stack)) return i;
        }
        return -1;
    }
}
