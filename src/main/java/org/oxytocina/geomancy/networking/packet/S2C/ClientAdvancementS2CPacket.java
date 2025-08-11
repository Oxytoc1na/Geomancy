package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.progression.advancement.ClientAdvancements;
import org.oxytocina.geomancy.util.ManaUtil;

public class ClientAdvancementS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            Identifier id = buf.readIdentifier();
            ClientAdvancements.add(id);
        }
    }

    public static void send(ServerPlayerEntity player, Identifier... ids){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(ids.length);
        for(var id : ids)
            buf.writeIdentifier(id);
        ServerPlayNetworking.send(player,ModMessages.CLIENT_ADVANCEMENT,buf);
    }
}
