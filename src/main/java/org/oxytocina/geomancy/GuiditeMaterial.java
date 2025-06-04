package org.oxytocina.geomancy;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import org.oxytocina.geomancy.items.ModItems;

public class GuiditeMaterial implements ToolMaterial {
    @Override
    public int getDurability() {
        return 455;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 5;
    }

    @Override
    public float getAttackDamage() {
        return 1.5f;
    }

    @Override
    public int getMiningLevel() {
        return 3;
    }

    @Override
    public int getEnchantability() {
        return 22;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.SUSPICIOUS_SUBSTANCE, Items.POTATO);
    }
    // Your IDE should override the interface's methods for you, or at least shout at you to do so.
}
