package org.oxytocina.geomancy.client.rendering.armor;

import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.items.ModItems;

import java.util.*;

public class ModArmorRenderers {
	
	public static final List<Item> OCTANGULITE_ARMOR =
			List.of(
					ModItems.OCTANGULITE_HELMET,
					ModItems.OCTANGULITE_CHESTPLATE,
					ModItems.OCTANGULITE_LEGGINGS,
					ModItems.OCTANGULITE_BOOTS
			);
	public static final List<Item> MITHRIL_ARMOR =
			List.of(
					ModItems.MITHRIL_HELMET,
					ModItems.MITHRIL_CHESTPLATE,
					ModItems.MITHRIL_LEGGINGS,
					ModItems.MITHRIL_BOOTS
			);
	
	public static void register() {
		ArmorRenderer.register(OctanguliteArmorModel::renderPartStatic, OCTANGULITE_ARMOR.toArray(new Item[0]));
		ArmorRenderer.register(MithrilArmorModel::renderPartStatic, MITHRIL_ARMOR.toArray(new Item[0]));
	}

	public static void renderPart(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, Model model, Identifier texture,float r, float g, float b, float a) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), false, stack.hasGlint());
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, r,g,b,a);
	}
	
}