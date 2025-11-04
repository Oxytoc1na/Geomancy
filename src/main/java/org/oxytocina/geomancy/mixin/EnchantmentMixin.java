package org.oxytocina.geomancy.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method="isAcceptableItem",at=@At(value="HEAD"),cancellable = true)
    public void geomancy$isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment ench = (Enchantment)(Object)this;
        if(stack.isIn(ModItemTags.CASTING_ITEM))
        {
            if(ench== Enchantments.FORTUNE)
                cir.setReturnValue(true);
        }
    }
}
