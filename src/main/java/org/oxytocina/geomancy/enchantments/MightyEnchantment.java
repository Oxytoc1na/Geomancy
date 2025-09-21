package org.oxytocina.geomancy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.items.tools.HammerItem;

public class MightyEnchantment extends ModEnchantment {

    protected MightyEnchantment() {
        super(Rarity.UNCOMMON, s->s.getItem() instanceof HammerItem);
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
