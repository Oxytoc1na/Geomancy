package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.oxytocina.geomancy.client.screen.SpellstorerScreenHandler;
import org.oxytocina.geomancy.registries.ModItemTags;

public class SpellstorerSpellSlot extends TagFilterSlot {


    public boolean enabled = true;

    private final SpellstorerScreenHandler handler;
    public SpellstorerSpellSlot(Inventory inventory, int index, int x, int y, SpellstorerScreenHandler handler) {
        super(inventory, index, x, y, ModItemTags.SPELL_STORING);
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
