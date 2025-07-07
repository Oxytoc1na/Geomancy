package org.oxytocina.geomancy.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.networking.packet.ClientJoinedC2SPacket;
import org.oxytocina.geomancy.networking.packet.ManaSyncS2CPacket;

public class ModMessages {

    // server to client
    public static final Identifier MANA_SYNC = Geomancy.locate("mana_sync");

    // client to server
    public static final Identifier CLIENT_JOINED = Geomancy.locate("client_joined");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(CLIENT_JOINED, ClientJoinedC2SPacket::receive);

    }

    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(MANA_SYNC, ManaSyncS2CPacket::receive);

    }


}
