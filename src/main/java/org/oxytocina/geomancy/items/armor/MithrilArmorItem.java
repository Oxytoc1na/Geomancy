package org.oxytocina.geomancy.items.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.registries.ModModelLayers;
import org.oxytocina.geomancy.client.rendering.armor.MithrilArmorModel;
import org.oxytocina.geomancy.items.jewelry.JewelryArmorItem;
import org.oxytocina.geomancy.items.jewelry.JewelryItemSettings;

public class MithrilArmorItem extends JewelryArmorItem {
    @Environment(EnvType.CLIENT)
    private BipedEntityModel<LivingEntity> model;

    public MithrilArmorItem(ArmorMaterial material, Type type, Settings settings, JewelryItemSettings jewelryItemSettings) {
        super(material, type, settings, jewelryItemSettings);
    }

    @Environment(EnvType.CLIENT)
    public BipedEntityModel<LivingEntity> getArmorModel() {
        if (model == null) {
            model = provideArmorModelForSlot(getSlotType());
        }
        return model;
    }

    // this takes the "unused" stack, so addons can mixin into it
    public RenderLayer getRenderLayer(ItemStack stack) {
        return RenderLayer.getEntitySolid(ModModelLayers.MITHRIL_ARMOR_MAIN_ID);
    }

    @NotNull
    public Identifier getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        return Geomancy.locate("textures/armor/mithril_armor_main.png");
    }

    @Environment(EnvType.CLIENT)
    protected BipedEntityModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
        var models = MinecraftClient.getInstance().getEntityModelLoader();
        var root = models.getModelPart(ModModelLayers.MAIN_MITHRIL_LAYER);
        return new MithrilArmorModel(root, slot);
    }
}
