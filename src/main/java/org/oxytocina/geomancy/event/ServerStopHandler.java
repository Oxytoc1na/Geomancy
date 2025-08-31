package org.oxytocina.geomancy.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.oxytocina.geomancy.blocks.blockEntities.IPedestalListener;
import org.oxytocina.geomancy.util.EntityUtil;
import org.oxytocina.geomancy.util.LeadUtil;
import org.oxytocina.geomancy.util.MadnessUtil;
import org.oxytocina.geomancy.util.ManaUtil;

public class ServerStopHandler implements ServerLifecycleEvents.ServerStopped {
    @Override
    public void onServerStopped(MinecraftServer minecraftServer) {
        IPedestalListener.clear();
    }
}
