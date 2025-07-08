package org.oxytocina.geomancy.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.oxytocina.geomancy.util.IEntityDataSaver;
import org.oxytocina.geomancy.util.ManaUtil;

public class ManaSyncS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        ManaUtil.setManaCap(client.player,buf.readFloat());
        ManaUtil.setMana(client.player,buf.readFloat());
    }

    //public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
    //    EntityType.COW.spawn(player.getServerWorld(),player.getBlockPos(),null);
    //}
}
