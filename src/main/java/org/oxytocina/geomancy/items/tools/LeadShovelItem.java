package org.oxytocina.geomancy.items.tools;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import org.oxytocina.geomancy.items.ILeadPoisoningItem;

public class LeadShovelItem extends ShovelItem implements ILeadPoisoningItem {
    private final float poisoningSpeed;

    public LeadShovelItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings, float poisoningSpeed) {
        super(material,attackDamage,attackSpeed,settings);
        this.poisoningSpeed = poisoningSpeed;
    }

    @Override
    public float getInInventoryPoisoningSpeed() {
        return poisoningSpeed;
    }
}
