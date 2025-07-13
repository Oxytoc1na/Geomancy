package org.oxytocina.geomancy.mixin.client;

import com.llamalad7.mixinextras.injector.*;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.*;
import net.minecraft.client.*;
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

    /*
    @Unique
    private static final Identifier INCURABLE_EFFECT_BACKGROUNDS = SpectrumCommon.locate("textures/gui/incurable_effect_backgrounds.png");
    @Unique
    private static final Identifier NIGHT_EFFECT_BACKGROUNDS = SpectrumCommon.locate("textures/gui/night_alchemy_effect_backgrounds.png");
    @Unique
    private static final Identifier DIVINITY_EFFECT_BACKGROUNDS = SpectrumCommon.locate("textures/gui/divinity_effect_backgrounds.png");
     */
    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow public abstract void render(DrawContext context, float tickDelta);

    @Inject(method = "renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getArmor()I"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void spectrum$renderHealthBar(DrawContext context, CallbackInfo ci, PlayerEntity cameraPlayer, int lastHealth, boolean blinking, long timeStart, int health, HungerManager hungerManager, int foodLevel, int x, int foodX, int y, float maxHealth, int absorption, int heartRows, int rowHeight, int armorY) {
        ModHudRenderer.onHudRender(context, cameraPlayer);
    }

    @ModifyArg(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0))
    private Identifier modifyAmbientEffectBackgrounds(Identifier texture, @Local StatusEffectInstance effect) {
        return getTexture(texture, effect);
    }

    @ModifyArg(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1))
    private Identifier modifyEffectBackgrounds(Identifier texture, @Local StatusEffectInstance effect) {
        return getTexture(texture, effect);
    }

    private static Identifier getTexture(Identifier texture, StatusEffectInstance effect) {
        var type = effect.getEffectType();

        return texture;
    }
}
