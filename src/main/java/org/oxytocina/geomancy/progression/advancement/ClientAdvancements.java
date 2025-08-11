package org.oxytocina.geomancy.progression.advancement;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.jmx.Server;
import org.oxytocina.geomancy.networking.packet.S2C.ClientAdvancementS2CPacket;

import java.util.ArrayList;

public class ClientAdvancements {
    public static ArrayList<Identifier> clientAdvancements = new ArrayList<>();

    public static void clear(){
        clientAdvancements.clear();
    }

    public static void add(Identifier id){
        if(has(id)) return;
        clientAdvancements.add(id);
    }

    public static boolean has(Identifier id){
        return id==null||clientAdvancements.contains(id);
    }

    public static void sync(ServerPlayerEntity spe){
        ArrayList<Identifier> ids = new ArrayList<>();
        var tracker = spe.getAdvancementTracker();
        for(var adv : tracker.progress.keySet()){
            if(!tracker.getProgress(adv).isDone()) continue;
            ids.add(adv.getId());
        }
        ClientAdvancementS2CPacket.send(spe,ids.toArray(new Identifier[0]));
    }
}
