package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import org.oxytocina.geomancy.util.AdvancementHelper;
import org.oxytocina.geomancy.util.Toolbox;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;

public class SmitheryPreviewSlot extends PreviewSlot implements SlotWithOnClickAction{
    public SmitheryPreviewSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean onClicked(ItemStack heldStack, ClickType type, PlayerEntity player) {
        if (this.inventory instanceof SmitheryBlockEntity smithery) {
            if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                if (smithery.currentRecipe != null) {
                    AdvancementHelper.grantAdvancementCriterion(serverPlayerEntity, "main/simple_tried_to_take_smithery_result", "simple_tried_to_take_smithery_result");
                }
            }
        }
        return false;
    }
}
