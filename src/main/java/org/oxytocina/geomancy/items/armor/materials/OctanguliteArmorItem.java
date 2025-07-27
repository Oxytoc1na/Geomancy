package org.oxytocina.geomancy.items.armor.materials;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.registries.ModModelLayers;
import org.oxytocina.geomancy.client.rendering.armor.OctanguliteArmorModel;
import org.oxytocina.geomancy.items.IMaddeningItem;

public class OctanguliteArmorItem extends ArmorItem implements IMaddeningItem {
    @Environment(EnvType.CLIENT)
    private BipedEntityModel<LivingEntity> model;

    private final float maddeningSpeed;

    public OctanguliteArmorItem(ArmorMaterial material, Type type, Settings settings, float maddeningSpeed) {
        super(material, type, settings);
        this.maddeningSpeed = maddeningSpeed;
    }


    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
    }

    @Override
    public float getWornMaddeningSpeed() {
        return maddeningSpeed*2;
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
        return RenderLayer.getEntitySolid(ModModelLayers.OCTANGULITE_ARMOR_MAIN_ID);
    }

    @NotNull
    public Identifier getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        return Geomancy.locate("textures/armor/octangulite_armor_main.png");
    }

    @Environment(EnvType.CLIENT)
    protected BipedEntityModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
        var models = MinecraftClient.getInstance().getEntityModelLoader();
        var root = models.getModelPart(ModModelLayers.MAIN_OCTANGULITE_LAYER);
        return new OctanguliteArmorModel(root, slot);
    }
}
