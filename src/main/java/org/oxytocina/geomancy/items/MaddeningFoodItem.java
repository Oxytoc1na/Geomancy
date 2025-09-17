package org.oxytocina.geomancy.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class MaddeningFoodItem extends FoodItem implements IMaddeningItem, ICustomRarityItem{
    public MaddeningFoodItem(Settings settings, @NotNull Consumer<LivingEntity> onEaten) {
        super(settings, onEaten);
    }

    @Override
    public float getInInventoryMaddeningSpeed() {
        return 0;
    }

    @Override
    public Text getName(ItemStack stack) {
        return colorizeName(stack,super.getName(stack));
    }

    @Override
    public Rarity getRarity() {
        return Rarity.Octangulite;
    }
}
