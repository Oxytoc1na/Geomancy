package org.oxytocina.geomancy.world.tree;

import net.minecraft.world.gen.foliage.FoliagePlacerType;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.mixin.FoliagePlacerTypeInvoker;
import org.oxytocina.geomancy.world.tree.custom.SoulOakFoliagePlacer;

public class ModFoliagePlacerTypes {
    public static final FoliagePlacerType<?> SOUL_OAK_FOLIAGE_PLACER = FoliagePlacerTypeInvoker.callRegister("soul_oak_foliage_placer", SoulOakFoliagePlacer.CODEC);

    public static void register() {
        Geomancy.logInfo("Registering Foliage Placer for " + Geomancy.MOD_ID);
    }
}
