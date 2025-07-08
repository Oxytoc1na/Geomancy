package org.oxytocina.geomancy.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.oxytocina.geomancy.entity.ManaStoringItemData;
import org.oxytocina.geomancy.util.ManaUtil;

public class ItemManaSyncS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        ManaStoringItemData.setFromBuffer(buf);
    }

    //public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
    //    EntityType.COW.spawn(player.getServerWorld(),player.getBlockPos(),null);
    //}
}
