package org.oxytocina.geomancy.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.util.LeadUtil;
import org.oxytocina.geomancy.util.ManaUtil;

public class PlayerTickHandler implements ServerTickEvents.StartTick {
    @Override
    public void onStartTick(MinecraftServer server) {
        ManaUtil.tick(server);
        LeadUtil.tick(server);
    }
}
