package org.oxytocina.geomancy.client.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class SpellComponentSelectionSlot extends Slot {
    private final SpellmakerScreenHandler handler;
    public SpellComponentSelectionSlot(Inventory inventory, int index, int x, int y,SpellmakerScreenHandler handler) {
        super(inventory, index, x, y);
        this.handler=handler;
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return false;
    }
}
