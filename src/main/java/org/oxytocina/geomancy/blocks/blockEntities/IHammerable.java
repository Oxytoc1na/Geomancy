package org.oxytocina.geomancy.blocks.blockEntities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IHammerable {
    ItemStack getLastHammerStack();
    PlayerEntity getLastHammerer();
    void onHitWithHammer(@Nullable PlayerEntity player, ItemStack hammer, float skill);

    boolean isHammerable();
}
