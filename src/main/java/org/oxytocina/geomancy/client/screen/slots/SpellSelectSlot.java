package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.client.screen.SpellSelectScreenHandler;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.AdvancementHelper;

public class SpellSelectSlot extends PreviewSlot implements SlotWithOnClickAction{
    private final SpellSelectScreenHandler handler;
    public final SpellGrid grid;

    public SpellSelectSlot(Inventory inventory, int index, int x, int y, SpellSelectScreenHandler handler) {
        super(inventory, index, x, y);
        this.handler=handler;
        this.grid= SpellStoringItem.readGrid(getStack());
    }

    @Override
    public boolean onClicked(ItemStack heldStack, ClickType type, PlayerEntity player) {
        handler.onSlotClicked(this);
        return false;
    }

}
