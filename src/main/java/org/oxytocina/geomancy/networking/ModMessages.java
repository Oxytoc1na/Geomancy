package org.oxytocina.geomancy.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.networking.packet.ManaSyncC2SPacket;

public class ModMessages {

    public static final Identifier MANA_SYNC_ID = Geomancy.locate("mana_sync");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(MANA_SYNC_ID, ManaSyncC2SPacket::receive);
    }

    public static void registerS2CPackets(){

    }


}
