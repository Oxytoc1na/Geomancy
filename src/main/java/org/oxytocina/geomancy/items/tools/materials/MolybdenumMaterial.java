package org.oxytocina.geomancy.items.tools.materials;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import org.oxytocina.geomancy.items.ModItems;

public class MolybdenumMaterial implements ToolMaterial {

    private static final int miningLevel                = 2;
    private static final int durability                 = 300;
    private static final float miningSpeedMultiplier    = 7f;
    private static final float attackDamage             = 2f;
    private static final int enchantability             = 20;
    private static final Ingredient repairIngredient    = Ingredient.ofItems(ModItems.MOLYBDENUM_INGOT);

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return miningSpeedMultiplier;
    }

    @Override
    public float getAttackDamage() {
        return attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return miningLevel;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient;
    }
}
