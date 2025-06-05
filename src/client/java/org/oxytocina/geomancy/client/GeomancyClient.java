package org.oxytocina.geomancy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
//import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;

import org.oxytocina.geomancy.fluids.ModFluids;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

public class GeomancyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        RegisterFluid(ModFluids.STILL_GOLD,ModFluids.FLOWING_GOLD);

    }

    private void RegisterFluid(Fluid still, Fluid flowing)
    {
        FluidRenderHandlerRegistry.INSTANCE.register(still, flowing, new SimpleFluidRenderHandler(
            Registries.FLUID.getId(still).withPrefixedPath("block/"),
            Registries.FLUID.getId(flowing).withPrefixedPath("block/"),
            0xFFFFFF
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), still, flowing);
    }
}
