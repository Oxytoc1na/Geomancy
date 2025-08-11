package org.oxytocina.geomancy.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.progression.advancement.ClientAdvancements;
import org.oxytocina.geomancy.util.StellgeUtil;

public class ClientPlayConnectionLeave implements ClientPlayConnectionEvents.Disconnect {

    @Override
    public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        // reset world specific client data
        ClientAdvancements.clear();
        StellgeUtil.clientAdvancementKnowledge=0;
    }
}
