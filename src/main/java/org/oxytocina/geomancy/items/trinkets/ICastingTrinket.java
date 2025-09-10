package org.oxytocina.geomancy.items.trinkets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.spells.SpellBlockArgs;

public interface ICastingTrinket {
    void trigger(ItemStack stack, LivingEntity wearer, SpellBlockArgs args);
    void tryCastOfHotkey(ItemStack stack, ServerPlayerEntity player, int selected);
}
