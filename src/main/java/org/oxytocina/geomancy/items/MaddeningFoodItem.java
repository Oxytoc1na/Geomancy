package org.oxytocina.geomancy.items;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class MaddeningFoodItem extends FoodItem implements IMaddeningItem{
    public MaddeningFoodItem(Settings settings, @NotNull Consumer<LivingEntity> onEaten) {
        super(settings, onEaten);
    }

    @Override
    public float getInInventoryMaddeningSpeed() {
        return 0;
    }
}
