package org.oxytocina.geomancy.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import org.oxytocina.geomancy.client.screen.slots.SlotWithOnClickAction;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.oxytocina.geomancy.util.LeadUtil;
import org.oxytocina.geomancy.util.MadnessUtil;
import org.oxytocina.geomancy.util.SoulUtil;
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
        SoulUtil.queueRecalculateSoul(player);
        LeadUtil.queueRecalculatePoisoningSpeed(player);
        MadnessUtil.queueRecalculateMadnessSpeed(player);
        //}
    }

    @Inject(method="useOnBlock",cancellable = true,at=@At(value="HEAD"))
    public void geomancy$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        if(context.getPlayer() != null && context.getPlayer().hasStatusEffect(ModStatusEffects.CREATIVE_SHOCK))
        {
            Item item = this.getItem();
            if(
                    item instanceof BlockItem
                            || item instanceof FlintAndSteelItem
                            || item instanceof FireChargeItem
                            || item instanceof ShovelItem
                            || item instanceof BucketItem
            )
            {
                cir.setReturnValue(ActionResult.FAIL);
            }
        }
    }

}
