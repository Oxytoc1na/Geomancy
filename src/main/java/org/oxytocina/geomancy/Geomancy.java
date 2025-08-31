package org.oxytocina.geomancy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.block.CauldronBlock;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.blockEntities.ModBlockEntities;
import org.oxytocina.geomancy.client.screen.ModScreenHandlers;
import org.oxytocina.geomancy.commands.ModCommands;
import org.oxytocina.geomancy.compat.GeomancyIntegrationPacks;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.oxytocina.geomancy.enchantments.ModEnchantments;
import org.oxytocina.geomancy.entity.ModEntityAttributes;
import org.oxytocina.geomancy.entity.ModEntityTypes;
import org.oxytocina.geomancy.event.ServerStopHandler;
import org.oxytocina.geomancy.event.ServerTickHandler;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.items.tools.SoulBoreItem;
import org.oxytocina.geomancy.loottables.ModLootFunctionTypes;
import org.oxytocina.geomancy.loottables.ModLootTables;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.particles.ModParticleFactories;
import org.oxytocina.geomancy.particles.ModParticleTypes;
import org.oxytocina.geomancy.progression.advancement.ModCriteria;
import org.oxytocina.geomancy.registries.ModCauldronBehaviors;
import org.oxytocina.geomancy.registries.ModDispenserBehaviors;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.registries.ModWoodTypes;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.util.GeomancyConfig;
import org.oxytocina.geomancy.world.dimension.ModDimensions;
import org.oxytocina.geomancy.world.gen.ModWorldGeneration;
import org.oxytocina.geomancy.world.tree.ModFoliagePlacerTypes;
import org.oxytocina.geomancy.world.tree.ModTrunkPlacerTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Geomancy implements ModInitializer {

    public static final String MOD_ID = "geomancy";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Map<Identifier, TagKey<Item>> CACHED_ITEM_TAG_MAP = new HashMap<>();
    //public static SpectrumConfig CONFIG;

    public static boolean startedInitialization = false;
    public static boolean initializing = false;
    public static boolean finishedInitialization = false;

    public static GeomancyConfig CONFIG = null;

    // called from APIs if they so mischievously tried to access geomancys static variables before it got the chance to load itself
    public static void initializeForeign(String from){
        if(finishedInitialization) return;

        if(initializing)
        {
            Geomancy.logWarning("waiting for finished initialization from foreign entrypoint "+from+"!");
            int waited = 0;
            while(initializing){
                try{
                    Thread.sleep(1);
                    waited++;
                    if(waited%10000 == 0)
                        Geomancy.logWarning("waited for finished initialization from foreign entrypoint "+from+" for "+waited+"ms!");

                }
                catch (Exception ignored)
                {
                    Geomancy.logError("error while waiting from foreign entrypoint "+from+"!");
                    return;
                }
            }

            return;
        }


        Geomancy.logWarning("initializing from foreign entrypoint "+from+"!");
        initialize();
    }

    @Override
    public void onInitialize() {
        initialize();
    }

    public static synchronized void initialize(){
        if(startedInitialization) return;
        startedInitialization =true;
        initializing=true;

        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Loading Geomancy");
        long startTime = System.nanoTime();

        try {

            CONFIG = GeomancyConfig.create();

            ModItems.register();
            IJewelryItem.populateItemGroup();
            SpellComponentStoringItem.populateItemGroup();

            ModBlocks.register();
            ModWoodTypes.registerStrippables();
            ModFluids.register();
            ModBlockEntities.register();
            ModScreenHandlers.register();
            SoulBoreItem.register();
            SpellBlocks.register(); // prevent first execution of spells from taking much longer than normal

            ModRecipeTypes.registerSerializer();
            ModSoundEvents.register();
            ModLootFunctionTypes.register();
            ModLootTables.register();
            ModCriteria.register();
            ModEnchantments.register();
            ModParticleTypes.register();
            ModMessages.registerC2SPackets();
            ModCommands.register();

            ModEntityTypes.register();
            ModEntityAttributes.register();

            GeomancyIntegrationPacks.register();
            ModStatusEffects.register();

            ModWorldGeneration.generateModWorldGen();
            ModTrunkPlacerTypes.register();
            ModFoliagePlacerTypes.register();
            ModDimensions.register();

            ModDispenserBehaviors.register();
            ModCauldronBehaviors.register();

            ServerTickEvents.START_SERVER_TICK.register(new ServerTickHandler());
            ServerLifecycleEvents.SERVER_STOPPED.register(new ServerStopHandler());

        } catch (Throwable t) {
            RuntimeException exception = new RuntimeException(String.format("Geomancy Initialization failed!",
                    t.fillInStackTrace(), Arrays.toString(t.getStackTrace())));
            
            Log.debug(LogCategory.ENTRYPOINT, "Geomancy");
            throw t;
        }

        long msTaken = TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-startTime);
        LOGGER.info("Finished Loading Geomancy. ms:"+msTaken);

        finishedInitialization=true;
        initializing = false;
    }

    public static void logInfo(String message,World world) {
        LOGGER.info("[Geomancy "+(world!=null&&world.isClient?"Client":"Server")+"] " + message);
    }

    public static void logWarning(String message,World world) {
        LOGGER.warn("[Geomancy "+(world!=null&&world.isClient?"Client":"Server")+"] " + message);
    }

    public static void logError(String message,World world) {
        LOGGER.error("[Geomancy "+(world!=null&&world.isClient?"Client":"Server")+"] " + message);
    }

    public static void logInfo(String message) {
        LOGGER.info("[Geomancy] " + message);
    }

    public static void logWarning(String message) {
        LOGGER.warn("[Geomancy] " + message);
    }

    public static void logError(String message) {
        LOGGER.error("[Geomancy] " + message);
    }

    public static Identifier locate(String name) {
        return new Identifier(MOD_ID, name);
    }

    // Will be null when playing on a dedicated server!
    @Nullable
    public static MinecraftServer minecraftServer;

    static {
        //Set up config
        //logInfo("Loading config file...");
        //AutoConfig.register(SpectrumConfig.class, JanksonConfigSerializer::new);
        //CONFIG = AutoConfig.getConfigHolder(SpectrumConfig.class).getConfig();
        //logInfo("Finished loading config file.");
    }

    /**
     * When initializing a block entity, world can still be null
     * Therefore we use the RecipeManager reference from MinecraftServer
     * This in turn does not work on clients connected to dedicated servers, though
     * since SpectrumCommon.minecraftServer is null
     */
    public static Optional<RecipeManager> getRecipeManager(@Nullable World world) {
        return world == null ? minecraftServer == null ? Optional.empty() : Optional.of(minecraftServer.getRecipeManager()) : Optional.of(world.getRecipeManager());
    }

    public static boolean Client() {
        return FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER;
    }

    public static boolean Server() {return !Client();}
}
