package org.oxytocina.geomancy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
//import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;

import org.oxytocina.geomancy.blocks.blockEntities.ModBlockEntities;
import org.oxytocina.geomancy.client.blocks.blockEntities.SmitheryBlockEntityRenderer;
import org.oxytocina.geomancy.client.rendering.ModBlockTransparency;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.client.rendering.ModModelPredicateProvider;
import org.oxytocina.geomancy.client.screen.ModScreenHandlers;
import org.oxytocina.geomancy.client.screen.SmitheryScreen;
import org.oxytocina.geomancy.fluids.ModFluids;

public class GeomancyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        RegisterFluid(ModFluids.STILL_GOLD,ModFluids.FLOWING_GOLD);

        ModScreenHandlers.initialize();
        ModColorizationHandler.initialize();
        ModBlockTransparency.initialize();
        ModModelPredicateProvider.initialize();

        HandledScreens.register(ModScreenHandlers.SMITHERY_SCREEN_HANDLER, SmitheryScreen::new);

        BlockEntityRendererFactories.register(ModBlockEntities.SMITHERY_BLOCK_ENTITY, SmitheryBlockEntityRenderer::new);
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
