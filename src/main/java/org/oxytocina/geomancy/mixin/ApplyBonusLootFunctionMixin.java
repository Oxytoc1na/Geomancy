package org.oxytocina.geomancy.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.util.Toolbox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ApplyBonusLootFunction.class)
public class ApplyBonusLootFunctionMixin {

    @Shadow
    @Final
    Enchantment enchantment;

    @Shadow
    @Final
    ApplyBonusLootFunction.Formula formula;

    // extra fortune from jewelry
    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void geomancy$process(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir){
        if(this.enchantment!=Enchantments.FORTUNE) return;

        ItemStack itemStack = context.get(LootContextParameters.TOOL);
        if (itemStack != null) {
            int i = EnchantmentHelper.getLevel(this.enchantment, itemStack);
            if(context.get(LootContextParameters.THIS_ENTITY) instanceof LivingEntity le)
                i += Toolbox.roundWithChance(IJewelryItem.getFortuneBonus(le));
            int j = this.formula.getValue(context.getRandom(), stack.getCount(), i);
            stack.setCount(j);
        }

        cir.setReturnValue(stack);
        cir.cancel();
    }
}
