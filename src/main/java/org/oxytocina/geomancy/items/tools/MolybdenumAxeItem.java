package org.oxytocina.geomancy.items.tools;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import org.oxytocina.geomancy.items.IMaddeningItem;
import org.oxytocina.geomancy.items.IManaStoringItem;

public class MolybdenumAxeItem extends AxeItem implements IMaddeningItem {
    private final float maddeningSpeed;

    public MolybdenumAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings, float maddeningSpeed) {
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
