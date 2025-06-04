package org.oxytocina.geomancy;

import net.fabricmc.api.ModInitializer;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.fluids.ModFluids;
import org.oxytocina.geomancy.features.ModFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Geomancy implements ModInitializer {

    public static final String MOD_ID = "geomancy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Loading Geomancy");

        ModItems.initialize();
        ModBlocks.initialize();
        ModFluids.initialize();
        ModFeatures.initialize();

    }
}
