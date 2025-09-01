package org.oxytocina.geomancy.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.oxytocina.geomancy.blocks.blockEntities.IPedestalListener;

public class ServerStopHandler implements ServerLifecycleEvents.ServerStopped {
    @Override
    public void onServerStopped(MinecraftServer minecraftServer) {
        IPedestalListener.clear();
    }
}
