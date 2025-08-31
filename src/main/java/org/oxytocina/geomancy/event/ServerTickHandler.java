package org.oxytocina.geomancy.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.oxytocina.geomancy.util.EntityUtil;
import org.oxytocina.geomancy.util.LeadUtil;
import org.oxytocina.geomancy.util.MadnessUtil;
import org.oxytocina.geomancy.util.ManaUtil;

public class ServerTickHandler implements ServerTickEvents.StartTick {
    @Override
    public void onStartTick(MinecraftServer server) {
        ManaUtil.tick(server);
        LeadUtil.tick(server);
        MadnessUtil.tick(server);
        EntityUtil.tick(server);
    }
}
