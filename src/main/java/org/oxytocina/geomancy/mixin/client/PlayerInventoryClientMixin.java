package org.oxytocina.geomancy.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.items.IScrollListenerItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryClientMixin {
    @Shadow public abstract ItemStack getStack(int slot);

    @Shadow @Final public PlayerEntity player;

    @Shadow public int selectedSlot;

    @Inject(method="scrollInHotbar", at = @At("HEAD"), cancellable = true)
    public void geomancy$scrollInHotbar(double scrollAmount, CallbackInfo ci){
        // prevent scrolling while scrolling is consumed by scrolling listeners
        if(scrollAmount!=0){
            if(MinecraftClient.getInstance().player!=null){
                boolean cancel = false;
                var items = MinecraftClient.getInstance().player.getHandItems();
                for(var stack: items){
                    if(!(stack.getItem() instanceof IScrollListenerItem listener)) continue;
                    if(listener.shouldBlockScrolling(stack,MinecraftClient.getInstance().player))
                    {
                        cancel=true;
                    }
                }

                if(cancel)
                    ci.cancel();
            }
        }

    }
}
