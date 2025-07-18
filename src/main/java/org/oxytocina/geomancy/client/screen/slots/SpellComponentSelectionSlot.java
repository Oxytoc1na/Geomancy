package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import org.oxytocina.geomancy.client.screen.SpellmakerScreenHandler;

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
