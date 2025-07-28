package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.client.screen.SpellstorerItemScreenHandler;
import org.oxytocina.geomancy.client.screen.SpellstorerScreenHandler;
import org.oxytocina.geomancy.registries.ModItemTags;

public class SpellstorerItemSpellSlot extends TagFilterSlot {


    public boolean enabled = true;

    private final SpellstorerItemScreenHandler handler;
    public SpellstorerItemSpellSlot(Inventory inventory, int index, int x, int y, SpellstorerItemScreenHandler handler) {
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
