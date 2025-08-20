package org.oxytocina.geomancy.compat;

import net.minecraft.client.MinecraftClient;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.compat.*;

import net.fabricmc.api.*;
import net.fabricmc.loader.api.*;
import org.oxytocina.geomancy.compat.modonomicon.ModonomiconCompat;

import java.util.*;
import java.util.function.*;

public class GeomancyIntegrationPacks {
	
	protected static final Map<String, ModIntegrationPack> INTEGRATION_PACKS = new HashMap<>();
	
	
	public abstract static class ModIntegrationPack {
		public abstract void register();
		
		public abstract void registerClient();
	}
	
	protected static void registerIntegrationPack(String modId, Supplier<ModIntegrationPack> container) {
		if (/*!Geomancy.CONFIG.IntegrationPacksToSkipLoading.contains(modId) &&*/ FabricLoader.getInstance().isModLoaded(modId)) {
			INTEGRATION_PACKS.put(modId, container.get());
		}
	}
	
	public static final String CONNECTOR_ID = "connectormod";
	public static final String AE2_ID = "ae2";
	public static final String GOBBER_ID = "gobber2";
	public static final String ALLOY_FORGERY_ID = "alloy_forgery";
	public static final String TRAVELERS_BACKPACK_ID = "travelersbackpack";
	public static final String BOTANIA_ID = "botania";
	public static final String MODONOMICON_ID = "modonomicon";
	public static final String CREATE_ID = "create";
	public static final String FARMERSDELIGHT_ID = "farmersdelight";
	public static final String NEEPMEAT_ID = "neepmeat";
	public static final String MALUM_ID = "malum";
	public static final String EXCLUSIONS_LIB_ID = "exclusions_lib";

	@SuppressWarnings("Convert2MethodRef")
	public static void register() {
		registerIntegrationPack(MODONOMICON_ID, () -> new ModonomiconCompat());
		
		if (!FabricLoader.getInstance().isModLoaded(CONNECTOR_ID)) {
			// Connector on forge causes a lot of issues since most
			// code bases of forge mods differ quite a lot from their fabric counterparts
			//registerIntegrationPack(AE2_ID, () -> new AE2Compat());
		}
		
		for (ModIntegrationPack container : INTEGRATION_PACKS.values()) {
			container.register();
		}
	}
	
	@Environment(EnvType.CLIENT)
	public static void registerClient() {
		for (ModIntegrationPack container : INTEGRATION_PACKS.values()) {
			container.registerClient();
		}
	}
	
	public static boolean isIntegrationPackActive(String modId) {
		return INTEGRATION_PACKS.containsKey(modId);
	}

	public static boolean isCreative(){
		if(MinecraftClient.getInstance()!=null && MinecraftClient.getInstance().player!=null) return MinecraftClient.getInstance().player.isCreative();
		return false;
	}

}