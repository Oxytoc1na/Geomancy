package org.oxytocina.geomancy.client.registries;

import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.util.*;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.rendering.armor.CastingArmorModel;
import org.oxytocina.geomancy.client.rendering.armor.MithrilArmorModel;
import org.oxytocina.geomancy.client.rendering.armor.OctanguliteArmorModel;

@Environment(EnvType.CLIENT)
public class ModModelLayers {

	/**
	 * Armor
	 */
	public static final EntityModelLayer MAIN_OCTANGULITE_LAYER = new EntityModelLayer(Geomancy.locate("octangulite_armor"), "main");
	public static final Identifier OCTANGULITE_ARMOR_MAIN_ID = Geomancy.locate("textures/armor/octangulite_armor_main.png");

	public static final EntityModelLayer MAIN_MITHRIL_LAYER = new EntityModelLayer(Geomancy.locate("mithril_armor"), "main");
	public static final Identifier MITHRIL_ARMOR_MAIN_ID = Geomancy.locate("textures/armor/mithril_armor_main.png");

	public static final EntityModelLayer MAIN_CASTING_LAYER = new EntityModelLayer(Geomancy.locate("casting_armor"), "main");
	public static final Identifier CASTING_ARMOR_MAIN_ID = Geomancy.locate("textures/armor/casting_armor_main.png");

	public static void register() {
		EntityModelLayerRegistry.registerModelLayer(MAIN_OCTANGULITE_LAYER, () -> TexturedModelData.of(OctanguliteArmorModel.getModelData(), 64, 64));
		EntityModelLayerRegistry.registerModelLayer(MAIN_MITHRIL_LAYER, () -> TexturedModelData.of(MithrilArmorModel.getModelData(), 128, 64));
		EntityModelLayerRegistry.registerModelLayer(MAIN_CASTING_LAYER, () -> TexturedModelData.of(CastingArmorModel.getModelData(), 128, 64));
	}
	
}