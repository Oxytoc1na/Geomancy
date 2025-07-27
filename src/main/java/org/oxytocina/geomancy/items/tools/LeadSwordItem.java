package org.oxytocina.geomancy.items.tools;

import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import org.oxytocina.geomancy.items.ILeadPoisoningItem;

public class LeadSwordItem extends SwordItem implements ILeadPoisoningItem {
    private final float poisoningSpeed;

    public LeadSwordItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings, float poisoningSpeed) {
        super(material,attackDamage,attackSpeed,settings);
        this.poisoningSpeed = poisoningSpeed;
    }

    @Override
    public float getInInventoryPoisoningSpeed() {
        return poisoningSpeed;
    }
}
