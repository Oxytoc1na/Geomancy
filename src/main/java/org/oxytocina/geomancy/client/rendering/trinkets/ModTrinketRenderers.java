package org.oxytocina.geomancy.client.rendering.trinkets;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import org.oxytocina.geomancy.items.ModItems;

public class ModTrinketRenderers {

    public static void register() {
        TrinketRendererRegistry.registerRenderer(ModItems.MANIA_MASK,new SimpleMaskTrinketRenderer());
        TrinketRendererRegistry.registerRenderer(ModItems.SORROW_MASK,new SimpleMaskTrinketRenderer());
        TrinketRendererRegistry.registerRenderer(ModItems.PARANOIA_MASK,new SimpleMaskTrinketRenderer());
        TrinketRendererRegistry.registerRenderer(ModItems.MELANCHOLY_MASK,new SimpleMaskTrinketRenderer());
        TrinketRendererRegistry.registerRenderer(ModItems.ADAPTIVE_MASK,new AdaptiveMaskTrinketRenderer());
    }
}
