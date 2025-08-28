package org.oxytocina.geomancy.mixin.client;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.oxytocina.geomancy.client.util.CamShakeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    //@Inject(method="Lnet/minecraft/client/render/GameRenderer;renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at=@At(value="HEAD"))
    //public void geomancy$render(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci){
    //
    //}

    @Inject(method="Lnet/minecraft/client/render/GameRenderer;renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V",locals = LocalCapture.CAPTURE_FAILHARD,at=@At(value="INVOKE",target="Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    public void geomancy$camShake(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci, boolean bl, Camera camera, MatrixStack matrixStack, double d){
        CamShakeUtil.tick(tickDelta,matrixStack);
    }
}
