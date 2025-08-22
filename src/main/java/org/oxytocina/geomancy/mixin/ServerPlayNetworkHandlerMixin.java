package org.oxytocina.geomancy.mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.util.EntityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public abstract ServerPlayerEntity getPlayer();

    @Inject(method="Lnet/minecraft/server/network/ServerPlayNetworkHandler;onChatMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V",at=@At(value="HEAD"))
    public void geomancy$onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci)
    {
        EntityUtil.onMessageSent(this.getPlayer(),packet.chatMessage());
    }
}
