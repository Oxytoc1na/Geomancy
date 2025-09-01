package org.oxytocina.geomancy.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.networking.packet.S2C.CamShakeS2CPacket;
import org.oxytocina.geomancy.util.SimplexNoise;

public class CamShakeUtil {

    @Environment(EnvType.CLIENT)
    public static float t = 0;
    @Environment(EnvType.CLIENT)
    public static float shakeIntensity = 0;
    @Environment(EnvType.CLIENT)
    public static float shakeDuration = 0;
    @Environment(EnvType.CLIENT)
    public static float shakeSpeed = 1;

    /// time in seconds
    @Environment(EnvType.CLIENT)
    public static double time = 0;

    @Environment(EnvType.CLIENT)
    public static void tick(float tickDelta, MatrixStack matrixStack) {
        float deltaTime = MinecraftClient.getInstance().getLastFrameDuration()/20f;
        t+=deltaTime;
        time+=deltaTime;
        float currentShakeIntensity = Math.min(1,getCurrentShake()/4) * Geomancy.CONFIG.shakeIntensity.value();
        if(currentShakeIntensity<=0) return;

        final float speed = 5f*shakeSpeed;
        final float lerpT = 1;//Math.min(t*10,1);
        float x = lerpT*currentShakeIntensity*getShakeOffset(time,time,speed);
        float y = lerpT*currentShakeIntensity*getShakeOffset(-time,time,speed);
        float xA = 5*lerpT*currentShakeIntensity*getShakeOffset(time,-time,speed);
        float zA = 5*lerpT*currentShakeIntensity*getShakeOffset(-time,-time,speed);

        matrixStack.translate(x,y, 0.0F);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(zA));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(xA));
    }

    @Environment(EnvType.CLIENT)
    public static void shake(float intensity){
        shake(intensity,intensity);
    }

    @Environment(EnvType.CLIENT)
    public static void shake(float intensity, float duration){
        shake(intensity,duration,1);
    }

    @Environment(EnvType.CLIENT)
    public static void shake(float intensity, float duration, float speed){
        if(intensity<getCurrentShake()) return; // dont let weaker shakes outdo the heftier ones
        shakeIntensity=intensity;
        shakeDuration=duration;
        shakeSpeed=speed;
        t=0;
    }

    @Environment(EnvType.CLIENT)
    private static float getCurrentShake(){
        return MathHelper.lerp(Math.max(0,1-t/shakeDuration),0,shakeIntensity);
    }

    @Environment(EnvType.CLIENT)
    private static float getShakeOffset(double x, double y, double scale){
        return 1-2*SimplexNoise.noiseNormalized(x,y,0,scale);
    }

    public static void cause(World world, Vec3d pos, float range, float intensity) {
        cause(world,pos,range,intensity,intensity);
    }

    public static void cause(World world, Vec3d pos, float range, float intensity, float duration) {
        cause(world,pos,range,intensity,duration,1);
    }

    public static void cause(World world, Vec3d pos, float range, float intensity, float duration, float speed){
        if(!(world instanceof ServerWorld sw)) return;
        CamShakeS2CPacket.send(sw,pos,range,intensity,duration,speed);
    }
}
