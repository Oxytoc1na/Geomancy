package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.client.screen.SpellmakerScreenHandler;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;

public class SpellmakerHotbarSlot extends Slot {
    public SpellmakerHotbarSlot(Inventory inventory, int index, int x, int y,SpellmakerScreenHandler handler) {
        super(inventory, index, x, y);
        this.handler=handler;
    }

    private final SpellmakerScreenHandler handler;

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {

        if(getStack().getItem() instanceof SpellComponentStoringItem scsi){
            if(!(playerEntity instanceof ServerPlayerEntity))
                handler.trySelectComponentOf(SpellComponentStoringItem.readComponent(getStack()).function);
            return false;
        }

        return true;
    }
}
