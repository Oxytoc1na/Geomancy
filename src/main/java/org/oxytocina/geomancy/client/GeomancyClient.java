package org.oxytocina.geomancy.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
//import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;

import org.oxytocina.geomancy.blocks.blockEntities.ModBlockEntities;
import org.oxytocina.geomancy.client.blocks.blockEntities.SmitheryBlockEntityRenderer;
import org.oxytocina.geomancy.client.rendering.ModBlockTransparency;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.client.rendering.ModModelPredicateProvider;
import org.oxytocina.geomancy.client.screen.ModScreenHandlers;
import org.oxytocina.geomancy.client.screen.SmitheryScreen;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.event.KeyInputHandler;
import org.oxytocina.geomancy.networking.ModMessages;

public class GeomancyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ModScreenHandlers.register();
        ModColorizationHandler.register();
        ModBlockTransparency.register();
        ModModelPredicateProvider.register();
        KeyInputHandler.register();
        ModMessages.registerS2CPackets();
        ModFluids.registerClient();

        HandledScreens.register(ModScreenHandlers.SMITHERY_SCREEN_HANDLER, SmitheryScreen::new);

        BlockEntityRendererFactories.register(ModBlockEntities.SMITHERY_BLOCK_ENTITY, SmitheryBlockEntityRenderer::new);
    }
}
