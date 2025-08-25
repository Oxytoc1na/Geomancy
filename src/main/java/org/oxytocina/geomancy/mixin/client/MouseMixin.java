package org.oxytocina.geomancy.mixin.client;

import net.minecraft.client.Mouse;

import org.oxytocina.geomancy.client.event.ScrollTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    // This will affect *every* use of the mouse wheel and alter the tracker accordingly.
    // Has no impact from a blackbox perspective though since the tooltip position will be reset when selecting an item.
    @Inject(method = "onMouseScroll(JDD)V", at = @At("HEAD"))
    private void trackWheel (long window, double horizontal, double vertical, CallbackInfo info) {
        scrollY(vertical);
    }

    @Unique
    private void scrollY(double vertical) {
        ScrollTracker.scroll(vertical);
    }
}