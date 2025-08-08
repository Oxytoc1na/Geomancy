package org.oxytocina.geomancy.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FoodItem extends Item {
    public Consumer<LivingEntity> onEaten;

    public FoodItem(Settings settings, @NotNull Consumer<LivingEntity> onEaten) {
        super(settings);
        this.onEaten=onEaten;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        onEaten.accept(user);
        return super.finishUsing(stack, world, user);
    }
}
