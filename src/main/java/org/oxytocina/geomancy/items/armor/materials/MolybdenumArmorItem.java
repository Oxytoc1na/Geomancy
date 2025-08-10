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

public class MolybdenumArmorItem extends ArmorItem implements IMaddeningItem {
    @Environment(EnvType.CLIENT)
    private BipedEntityModel<LivingEntity> model;

    private final float maddeningSpeed;

    public MolybdenumArmorItem(ArmorMaterial material, Type type, Settings settings, float maddeningSpeed) {
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
}
