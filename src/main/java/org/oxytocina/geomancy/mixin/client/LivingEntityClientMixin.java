package org.oxytocina.geomancy.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.util.EntityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityClientMixin {
    @Shadow
    private int jumpingCooldown;

    @Inject(method="Lnet/minecraft/entity/LivingEntity;tickMovement()V", at = @At(value="INVOKE", target="Lnet/minecraft/entity/LivingEntity;isOnGround()Z",ordinal=2))
    private void geomancy$prejump(CallbackInfo ci){
        if(this.jumpingCooldown<=0){
            LivingEntity thisEntity = (LivingEntity) (Object) this;
            World world = thisEntity.getWorld();
            if (world.isClient) {
                if(thisEntity instanceof ClientPlayerEntity cpe){
                    // send jump packet
                    // i really wish i didnt have to do it like this but oh well
                    ClientPlayNetworking.send(ModMessages.PLAYER_JUMP, PacketByteBufs.create());
                }
            }
        }
    }
}
