package org.oxytocina.geomancy.items.tools.materials;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import org.oxytocina.geomancy.items.ModItems;

public class TitaniumMaterial implements ToolMaterial {

    private static final int miningLevel                = 3;
    private static final int durability                 = 4000;
    private static final float miningSpeedMultiplier    = 8f;
    private static final float attackDamage             = 3f;
    private static final int enchantability             = 10;
    private static final Ingredient repairIngredient    = Ingredient.ofItems(ModItems.TITANIUM_INGOT);

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
