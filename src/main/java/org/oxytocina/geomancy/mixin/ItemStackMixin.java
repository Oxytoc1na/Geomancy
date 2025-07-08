package org.oxytocina.geomancy.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import org.oxytocina.geomancy.client.screen.SlotWithOnClickAction;
import org.oxytocina.geomancy.items.ManaStoringItem;
import org.oxytocina.geomancy.util.ManaUtil;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    // Injecting into onStackClicked instead of onClicked because onStackClicked is called first
    @Inject(at = @At("HEAD"), method = "onStackClicked", cancellable = true)
    public void geomancy$onStackClicked(Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (slot instanceof SlotWithOnClickAction slotWithOnClickAction) {
            if (slotWithOnClickAction.onClicked((ItemStack) (Object) this, clickType, player)) {
                cir.setReturnValue(true);
            }
        }
    }

    // Injecting into onStackClicked instead of onClicked because onStackClicked is called first
    @Inject(at = @At("TAIL"), method = "onStackClicked")
    public void geomancy$onStackClicked_Tail(Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {

        if(!(player instanceof ServerPlayerEntity serverPlayer)) return;

        //if(this.getItem() instanceof ManaStoringItem){
        ManaUtil.queueRecalculateMana(player);
        //}
    }

}
