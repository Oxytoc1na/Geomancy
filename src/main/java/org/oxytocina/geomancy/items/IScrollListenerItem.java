package org.oxytocina.geomancy.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IScrollListenerItem {
    @Environment(EnvType.CLIENT)
    boolean onScrolled(ItemStack stack, float delta, PlayerEntity player);
    @Environment(EnvType.CLIENT)
    boolean shouldBlockScrolling(ItemStack stack, PlayerEntity player);
}
