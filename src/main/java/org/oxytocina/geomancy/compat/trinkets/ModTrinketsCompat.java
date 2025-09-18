package org.oxytocina.geomancy.compat.trinkets;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.util.TriState;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.artifacts.ArtifactItem;

public class ModTrinketsCompat {
    public static void register(){
        TrinketsApi.registerTrinketPredicate(Geomancy.locate("artifact_tooltip"),(stack,slot,entity)->{
            if(stack.getItem() instanceof ArtifactItem artifactItem)
                return slot.inventory().getSlotType().getName() == "ring" ? TriState.TRUE : TriState.FALSE;
            return TriState.DEFAULT;
        });
    }
}
