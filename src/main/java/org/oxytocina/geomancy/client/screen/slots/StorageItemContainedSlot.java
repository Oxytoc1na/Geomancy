package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import org.oxytocina.geomancy.client.screen.StorageItemScreenHandler;

public class StorageItemContainedSlot extends TagFilterSlot {


    public boolean enabled = true;

    private final StorageItemScreenHandler handler;
    public StorageItemContainedSlot(Inventory inventory, int index, int x, int y, StorageItemScreenHandler handler, TagKey<Item> tag) {
        super(inventory, index, x, y, tag);
        this.handler=handler;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.inventory.setStack(this.getIndex(), stack);
        handler.markDirty();
    }


}
