package org.oxytocina.geomancy.items.armor.materials;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import org.oxytocina.geomancy.items.ILeadPoisoningItem;

public class LeadArmorItem extends ArmorItem implements ILeadPoisoningItem {
    private final float poisoningSpeed;

    public LeadArmorItem(ArmorMaterial material, Type type, Settings settings, float poisoningSpeed) {
        super(material, type, settings);
        this.poisoningSpeed=poisoningSpeed;
    }


    @Override
    public float getInInventoryPoisoningSpeed() {
        return poisoningSpeed;
    }

    @Override
    public float getWornPoisoningSpeed() {
        return poisoningSpeed*2;
    }
}
