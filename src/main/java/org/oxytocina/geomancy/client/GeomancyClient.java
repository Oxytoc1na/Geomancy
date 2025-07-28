package org.oxytocina.geomancy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
//import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;

import org.oxytocina.geomancy.blocks.blockEntities.ModBlockEntities;
import org.oxytocina.geomancy.client.blocks.blockEntities.SmitheryBlockEntityRenderer;
import org.oxytocina.geomancy.client.blocks.blockEntities.SpellmakerBlockEntityRenderer;
import org.oxytocina.geomancy.client.blocks.blockEntities.SpellstorerBlockEntityRenderer;
import org.oxytocina.geomancy.client.entity.ModEntityRenderers;
import org.oxytocina.geomancy.client.event.ClientPlayerTickHandler;
import org.oxytocina.geomancy.client.registries.ModModelLayers;
import org.oxytocina.geomancy.client.rendering.ModBlockTransparency;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.client.rendering.ModModelPredicateProvider;
import org.oxytocina.geomancy.client.rendering.armor.ModArmorRenderers;
import org.oxytocina.geomancy.client.screen.*;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.compat.GeomancyIntegrationPacks;
import org.oxytocina.geomancy.event.KeyInputHandler;
import org.oxytocina.geomancy.networking.ModMessages;

public class GeomancyClient implements ClientModInitializer {

    public static long tick = 0;

    @Override
    public void onInitializeClient() {

        ModScreenHandlers.register();
        ModScreens.register();
        ModColorizationHandler.register();
        ModBlockTransparency.register();
        ModModelPredicateProvider.register();
        KeyInputHandler.register();
        ModMessages.registerS2CPackets();
        ModFluids.registerClient();
        ModEntityRenderers.register();
        GeomancyIntegrationPacks.registerClient();
        ModArmorRenderers.register();
        ModModelLayers.register();

        ClientTickEvents.START_CLIENT_TICK.register(new ClientPlayerTickHandler());

        BlockEntityRendererFactories.register(ModBlockEntities.SMITHERY_BLOCK_ENTITY, SmitheryBlockEntityRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.SPELLMAKER_BLOCK_ENTITY, SpellmakerBlockEntityRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.SPELLSTORER_BLOCK_ENTITY, SpellstorerBlockEntityRenderer::new);


    }
}
