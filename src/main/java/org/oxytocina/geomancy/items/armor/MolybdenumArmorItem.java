package org.oxytocina.geomancy.items.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
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
