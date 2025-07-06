package org.oxytocina.geomancy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.items.HammerItem;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;

public class BrillianceEnchantment extends Enchantment {

    protected BrillianceEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEARABLE, new EquipmentSlot[] {});
    }

    @Override
    public int getMinPower(int level) {
        return 1+level*10;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof JewelryItem;
    }
}
