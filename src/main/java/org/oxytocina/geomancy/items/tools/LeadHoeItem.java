package org.oxytocina.geomancy.items.tools;

import net.minecraft.item.HoeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import org.oxytocina.geomancy.items.ILeadPoisoningItem;

public class LeadHoeItem extends HoeItem implements ILeadPoisoningItem {
    private final float poisoningSpeed;

    public LeadHoeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings, float poisoningSpeed) {
        super(material,attackDamage,attackSpeed,settings);
        this.poisoningSpeed = poisoningSpeed;
    }

    @Override
    public float getInInventoryPoisoningSpeed() {
        return poisoningSpeed;
    }
}
