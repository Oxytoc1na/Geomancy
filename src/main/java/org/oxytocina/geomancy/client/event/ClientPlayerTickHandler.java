package org.oxytocina.geomancy.client.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.event.ScrollTracker;

public class ClientPlayerTickHandler implements ClientTickEvents.StartTick {
    @Override
    public void onStartTick(MinecraftClient client) {
        GeomancyClient.tick++;
        ScrollTracker.update();
    }
}
