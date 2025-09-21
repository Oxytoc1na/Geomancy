package org.oxytocina.geomancy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public class ModEnchantment extends Enchantment {
    public final Function<ItemStack,Boolean> target;

    protected ModEnchantment(Rarity weight, Function<ItemStack,Boolean> target) {
        super(weight, EnchantmentTarget.TRIDENT, null); // i HATE enums
        this.target=target;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return target.apply(stack);
    }
}
