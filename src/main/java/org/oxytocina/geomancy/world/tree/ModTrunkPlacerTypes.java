package org.oxytocina.geomancy.world.tree;

import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.mixin.TrunkPlacerTypeInvoker;
import org.oxytocina.geomancy.world.tree.custom.SoulOakTrunkPlacer;

public class ModTrunkPlacerTypes {
    public static final TrunkPlacerType<?> SOUL_OAK_TRUNK_PLACER = TrunkPlacerTypeInvoker.callRegister("soul_oak_trunk_placer", SoulOakTrunkPlacer.CODEC);

    public static void register() {
        Geomancy.logInfo("Registering Trunk Placers for " + Geomancy.MOD_ID);
    }
}
