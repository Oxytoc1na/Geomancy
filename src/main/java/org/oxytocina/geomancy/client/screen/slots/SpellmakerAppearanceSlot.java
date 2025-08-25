package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ClickType;
import org.oxytocina.geomancy.client.screen.SpellmakerScreenHandler;

public class SpellmakerAppearanceSlot extends PreviewSlot implements SlotWithOnClickAction {
    public SpellmakerAppearanceSlot(Inventory inventory, int index, int x, int y, SpellmakerScreenHandler handler) {
        super(inventory, index, x, y);
        this.handler=handler;
    }

    private final SpellmakerScreenHandler handler;
    public boolean enabled = true;

    @Override
    public boolean onClicked(ItemStack heldStack, ClickType type, PlayerEntity player) {
        handler.onAppearanceSlotClicked(this,heldStack);
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean b) {
        enabled=b;
    }
}
