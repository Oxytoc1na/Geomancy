package org.oxytocina.geomancy.mixin;

import net.minecraft.client.MinecraftClient;
import org.oxytocina.geomancy.client.event.KeyInputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method="Lnet/minecraft/client/MinecraftClient;handleInputEvents()V", at = @At(value="HEAD"))
    private void catchHotkeyPress(CallbackInfo ci){
        boolean bl = KeyInputHandler.KEY_ACTIVATE_SPELLS.isPressed();
        if(!bl) return;
        var inst = MinecraftClient.getInstance();
        var player = inst.player;
        if(player==null) return;
        for (int i = 0; i < 9; i++) {
            if (inst.options.hotbarKeys[i].wasPressed()) {
                if (!player.isSpectator()) {
                    KeyInputHandler.castPressed(i);
                }
            }
        }
    }

}
