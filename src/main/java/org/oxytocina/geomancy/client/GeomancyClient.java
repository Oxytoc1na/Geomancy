package org.oxytocina.geomancy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
//import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;

import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.ModBlockEntities;
import org.oxytocina.geomancy.client.blocks.blockEntities.PedestalBlockEntityRenderer;
import org.oxytocina.geomancy.client.blocks.blockEntities.SmitheryBlockEntityRenderer;
import org.oxytocina.geomancy.client.blocks.blockEntities.SpellmakerBlockEntityRenderer;
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
import org.oxytocina.geomancy.client.event.KeyInputHandler;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.particles.ModParticleFactories;
import org.oxytocina.geomancy.world.dimension.ModDimensions;

public class GeomancyClient implements ClientModInitializer {

    public static long tick = 0;

    public static boolean initialized = false;

    public synchronized static void initialize(){
        if(initialized) return;
        Geomancy.logInfo("Initializing Geomancy Client");

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
        ModDimensions.registerClient();
        ModParticleFactories.register();

        ClientTickEvents.START_CLIENT_TICK.register(new ClientPlayerTickHandler());
        ClientPlayConnectionEvents.JOIN.register(new ClientPlayConnectionJoin());
        ClientPlayConnectionEvents.DISCONNECT.register(new ClientPlayConnectionLeave());

        BlockEntityRendererFactories.register(ModBlockEntities.SMITHERY_BLOCK_ENTITY, SmitheryBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SPELLMAKER_BLOCK_ENTITY, SpellmakerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, PedestalBlockEntityRenderer::new);


        Geomancy.logInfo("Finished Initializing Geomancy Client");
        initialized=true;
    }
    @Override
    public void onInitializeClient() {
        initialize();
    }
}
