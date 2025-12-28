package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class StorageItemHotbarSlot extends Slot{
    public StorageItemHotbarSlot(Inventory inventory, int index, int x, int y,boolean enabled) {
        super(inventory, index, x, y);
        this.enabled=enabled;
    }

    public boolean enabled;

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return enabled;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return enabled;
    }

    @Override
    public boolean canTakePartial(PlayerEntity player) {
        if(!enabled) return false;
        return super.canTakePartial(player);
    }

    @Override
    public void onQuickTransfer(ItemStack newItem, ItemStack original) {

    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        if(!enabled) return;
        super.onTakeItem(player, stack);
    }

    @Override
    protected void onTake(int amount) {
        if(!enabled) return;
        super.onTake(amount);
    }
}
