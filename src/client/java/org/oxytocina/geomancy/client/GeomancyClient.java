package org.oxytocina.geomancy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import org.oxytocina.geomancy.fluids.ModFluids;

public class GeomancyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_GOLD, ModFluids.FLOWING_GOLD, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/lava_still"),
                new Identifier("minecraft:block/lava_flow"),
                0xFFFF00
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_GOLD, ModFluids.FLOWING_GOLD);

    }
}
