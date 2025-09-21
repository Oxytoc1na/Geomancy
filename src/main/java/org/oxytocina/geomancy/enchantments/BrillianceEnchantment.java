package org.oxytocina.geomancy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;

public class BrillianceEnchantment extends ModEnchantment {

    protected BrillianceEnchantment() {
        super(Rarity.RARE, s->s.getItem() instanceof JewelryItem);
    }

    @Override
    public int getMinPower(int level) {
        return 1+level*10;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
