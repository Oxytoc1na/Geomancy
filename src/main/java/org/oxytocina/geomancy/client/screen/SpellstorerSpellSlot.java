package org.oxytocina.geomancy.client.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SpellstorerSpellSlot extends Slot {


    public boolean enabled = true;

    private final SpellstorerScreenHandler handler;
    public SpellstorerSpellSlot(Inventory inventory, int index, int x, int y, SpellstorerScreenHandler handler) {
        super(inventory, index, x, y);
        this.handler=handler;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void markDirty() {
        super.markDirty();

    }

    @Override
    public void setStack(ItemStack stack) {
        this.inventory.setStack(this.getIndex(), stack);
        handler.markDirty();
    }

    //@Override
    //public void onTakeItem(PlayerEntity player, ItemStack stack) {
    //    super.onTakeItem(player, stack);
    //    handler.markDirty();
    //}

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return true;
    }


}
