package org.oxytocina.geomancy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.blockEntities.ModBlockEntities;
import org.oxytocina.geomancy.enchantments.ModEnchantments;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.fluids.ModFluids;
import org.oxytocina.geomancy.features.ModFeatures;
import org.oxytocina.geomancy.loottables.ModLootTables;
import org.oxytocina.geomancy.progression.advancement.ModCriteria;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Geomancy implements ModInitializer {

    public static final String MOD_ID = "geomancy";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Map<Identifier, TagKey<Item>> CACHED_ITEM_TAG_MAP = new HashMap<>();
    //public static SpectrumConfig CONFIG;

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
        ModRecipeTypes.registerSerializer();
        ModSoundEvents.initialize();
        ModLootTables.initialize();
        ModCriteria.initialize();
        ModBlockEntities.initialize();
        ModEnchantments.initialize();

        //ModDamageSources.initialize( ?????? , DynamicRegistryManager.EMPTY);

        logInfo(Registries.RECIPE_SERIALIZER.get(locate(ModRecipeTypes.GOLD_CONVERTING_ID)).toString());

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
