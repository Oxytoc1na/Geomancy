package org.oxytocina.geomancy.progression.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.networking.packet.S2C.ClientAdvancementS2CPacket;

import java.util.ArrayList;

public class ClientAdvancements {
    public static ArrayList<Identifier> clientAdvancements = new ArrayList<>();

    @Environment(EnvType.CLIENT)
    public static void clear(){
        clientAdvancements.clear();
    }

    @Environment(EnvType.CLIENT)
    public static void add(Identifier id){
        if(has(id)) return;
        clientAdvancements.add(id);
    }

    /// i cant for the life of me figure out how to mixin to methods that are in an enum, so this isnt a thing for now...
    @Environment(EnvType.CLIENT)
    public static void remove(Identifier id){
        if(!has(id)) return;
        clientAdvancements.remove(id);
    }

    @Environment(EnvType.CLIENT)
    public static boolean has(Identifier id){
        return id==null||clientAdvancements.contains(id);
    }

    /// adds(!) advancements that are unlocked to the client advancements
    public static void sync(ServerPlayerEntity spe){
        ArrayList<Identifier> ids = new ArrayList<>();
        var tracker = spe.getAdvancementTracker();
        for(var adv : tracker.progress.keySet()){
            if(!tracker.getProgress(adv).isDone()) continue;
            ids.add(adv.getId());
        }
        ClientAdvancementS2CPacket.sendSync(spe,ids.toArray(new Identifier[0]));
    }
}
