package org.oxytocina.geomancy.items.armor.materials;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public interface IListenerArmor {
    void onGotHit(ItemStack stack, LivingEntity wearer,DamageSource source,float amount);
    void onJump(ItemStack stack, LivingEntity wearer);
    void onHit(ItemStack stack, LivingEntity wearer, LivingEntity target);
}
