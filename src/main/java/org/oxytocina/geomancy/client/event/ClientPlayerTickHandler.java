package org.oxytocina.geomancy.client.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.hud.ModHudRenderer;
import org.oxytocina.geomancy.effects.ParanoiaStatusEffect;
import org.oxytocina.geomancy.event.ScrollTracker;
import org.oxytocina.geomancy.items.IScrollListenerItem;

public class ClientPlayerTickHandler implements ClientTickEvents.StartTick {
    @Override
    public void onStartTick(MinecraftClient client) {
        GeomancyClient.tick++;

        if(ScrollTracker.delta!=0){
            if(client.player!=null){
                client.player.getHandItems().forEach(stack -> {
                    if(!(stack.getItem() instanceof IScrollListenerItem listener)) return;
                    listener.onScrolled(stack,ScrollTracker.delta,client.player);
                });
            }
        }


        ScrollTracker.update();
        ModHudRenderer.tick();
        ParanoiaStatusEffect.tick();
    }
}
