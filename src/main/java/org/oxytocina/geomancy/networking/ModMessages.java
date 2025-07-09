package org.oxytocina.geomancy.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.entity.StateSaverAndLoader;
import org.oxytocina.geomancy.networking.packet.*;

public class ModMessages {

    // server to client
    public static final Identifier MANA_SYNC = Geomancy.locate("mana_sync");
    public static final Identifier LEAD_POISONING_SYNC = Geomancy.locate("lead_poisoning_sync");
    public static final Identifier MADNESS_SYNC = Geomancy.locate("madness_sync");
    public static final Identifier ITEM_MANA_SYNC = Geomancy.locate("item_mana_sync");
    public static final Identifier INITIAL_SYNC = Geomancy.locate("initial_sync");

    // client to server
    public static final Identifier CLIENT_JOINED = Geomancy.locate("client_joined");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(CLIENT_JOINED, ClientJoinedC2SPacket::receive);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.execute(() -> {
                try{
                    PlayerData playerState = StateSaverAndLoader.getPlayerState(handler.getPlayer());
                    PacketByteBuf data = PacketByteBufs.create();
                    playerState.writeBuf(data);
                    ServerPlayNetworking.send(handler.getPlayer(), INITIAL_SYNC, data);
                }
                catch (Exception ignored){

                }
            });
        });
    }

    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(MANA_SYNC, ManaSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LEAD_POISONING_SYNC, LeadPoisoningSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(MADNESS_SYNC, MadnessSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ITEM_MANA_SYNC, ItemManaSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(INITIAL_SYNC, InitialSyncS2CPacket::receive);
    }

    public static void sendToAllClients(MinecraftServer server, Identifier id, PacketByteBuf buf){
        for(var player : server.getPlayerManager().getPlayerList()){
            ServerPlayNetworking.send(player,ModMessages.ITEM_MANA_SYNC,buf);
        }
    }

}
