package org.oxytocina.geomancy.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.oxytocina.geomancy.enchantments.ModEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Inject(method="getPossibleEntries",at=@At(value= "RETURN"), cancellable = true)
    private static void getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir){
        var list = cir.getReturnValue();
        boolean isBook = stack.isOf(Items.BOOK);
        for (Enchantment enchantment : Registries.ENCHANTMENT) {
            if (enchantment instanceof ModEnchantment modEnchantment && (!modEnchantment.isTreasure()||treasureAllowed) && modEnchantment.isAvailableForRandomSelection() && (modEnchantment.isAcceptableItem(stack)||isBook)) {
                for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; i--) {
                    if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
                        list.add(new EnchantmentLevelEntry(enchantment, i));
                        break;
                    }
                }
            }
        }

        cir.setReturnValue(list);
    }
}
