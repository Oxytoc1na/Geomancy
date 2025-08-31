package org.oxytocina.geomancy.items.tools;

import net.minecraft.item.HoeItem;
import net.minecraft.item.ToolMaterial;
import org.oxytocina.geomancy.items.IMaddeningItem;

public class MolybdenumHoeItem extends HoeItem implements IMaddeningItem {
    private final float maddeningSpeed;

    public MolybdenumHoeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings, float maddeningSpeed) {
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
