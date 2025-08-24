package org.oxytocina.geomancy.world.biome;

import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.world.biome.surface.ModMaterialRules;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

public class ModTerrablenderAPI implements TerraBlenderApi {
    @Override
    public void onTerraBlenderInitialized() {
        Geomancy.initializeForeign("TerraBlender");

        Regions.register(new ModOverworldRegion(Geomancy.locate("overworld"), 4));

        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, Geomancy.MOD_ID, ModMaterialRules.makeRules());
    }
}
