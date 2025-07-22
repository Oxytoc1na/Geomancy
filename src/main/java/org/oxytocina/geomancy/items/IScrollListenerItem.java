package org.oxytocina.geomancy.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IScrollListenerItem {
    boolean onScrolled(ItemStack stack, float delta, PlayerEntity player);
    boolean shouldBlockScrolling(ItemStack stack, PlayerEntity player);
}
