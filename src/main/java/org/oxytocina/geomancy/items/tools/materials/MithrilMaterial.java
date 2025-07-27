package org.oxytocina.geomancy.items.tools.materials;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import org.oxytocina.geomancy.items.ModItems;

public class MithrilMaterial implements ToolMaterial {

    private static final int miningLevel                = 4;
    private static final int durability                 = 1600;
    private static final float miningSpeedMultiplier    = 9f;
    private static final float attackDamage             = 4f;
    private static final int enchantability             = 20;
    private static final Ingredient repairIngredient    = Ingredient.ofItems(ModItems.MITHRIL_INGOT);

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
