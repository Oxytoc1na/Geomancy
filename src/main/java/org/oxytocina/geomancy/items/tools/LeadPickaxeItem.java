package org.oxytocina.geomancy.items.tools;

import net.minecraft.item.AxeItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import org.oxytocina.geomancy.items.ILeadPoisoningItem;

public class LeadPickaxeItem extends PickaxeItem implements ILeadPoisoningItem {
    private final float poisoningSpeed;

    public LeadPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings, float poisoningSpeed) {
        super(material,attackDamage,attackSpeed,settings);
        this.poisoningSpeed = poisoningSpeed;
    }

    @Override
    public float getInInventoryPoisoningSpeed() {
        return poisoningSpeed;
    }
}
