package org.oxytocina.geomancy.mixin.client;

import com.llamalad7.mixinextras.sugar.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.hud.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import org.oxytocina.geomancy.client.hud.ModHudRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow public abstract void render(DrawContext context, float tickDelta);

    @Inject(method = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isDemo()Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void geomancy$render(DrawContext context, float tickDelta, CallbackInfo ci) {
        ModHudRenderer.onHudRender(context, getCameraPlayer());
    }

    @ModifyArg(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0))
    private Identifier modifyAmbientEffectBackgrounds(Identifier texture, @Local StatusEffectInstance effect) {
        return getTexture(texture, effect);
    }

    @ModifyArg(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1))
    private Identifier modifyEffectBackgrounds(Identifier texture, @Local StatusEffectInstance effect) {
        return getTexture(texture, effect);
    }

    @Unique
    private static Identifier getTexture(Identifier texture, StatusEffectInstance effect) {
        var type = effect.getEffectType();

        return texture;
    }
}
