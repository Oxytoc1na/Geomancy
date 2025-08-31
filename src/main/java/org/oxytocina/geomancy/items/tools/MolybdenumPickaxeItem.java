package org.oxytocina.geomancy.items.tools;

import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import org.oxytocina.geomancy.items.IMaddeningItem;

public class MolybdenumPickaxeItem extends PickaxeItem implements IMaddeningItem {
    private final float maddeningSpeed;

    public MolybdenumPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings, float maddeningSpeed) {
        super(material,attackDamage,attackSpeed,settings);
        this.maddeningSpeed=maddeningSpeed;
    }

    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
    }

    @Override
    public float getInHandMaddeningSpeed() {
        return maddeningSpeed*3;
    }
}
