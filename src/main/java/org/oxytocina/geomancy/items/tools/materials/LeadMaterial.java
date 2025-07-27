package org.oxytocina.geomancy.items.tools.materials;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import org.oxytocina.geomancy.items.ModItems;

public class LeadMaterial implements ToolMaterial {

    private static final int miningLevel                = 1;
    private static final int durability                 = 150;
    private static final float miningSpeedMultiplier    = 5f;
    private static final float attackDamage             = 1.5f;
    private static final int enchantability             = 10;
    private static final Ingredient repairIngredient    = Ingredient.ofItems(ModItems.LEAD_INGOT);

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
