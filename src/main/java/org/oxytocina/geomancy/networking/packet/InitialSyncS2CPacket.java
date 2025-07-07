package org.oxytocina.geomancy.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.util.IEntityDataSaver;
import org.oxytocina.geomancy.util.ManaUtil;

public class InitialSyncS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        PlayerData data = PlayerData.fromBuf(buf);
        client.execute(()->{
            PlayerData.setClientData(client.player.getUuid(), data);
        });
    }
}
