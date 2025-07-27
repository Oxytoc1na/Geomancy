package org.oxytocina.geomancy.items.armor.materials;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.oxytocina.geomancy.items.ModItems;

public class MolybdenumMaterial implements ArmorMaterial {

    private static final String name                    = "molybdenum";
    private static final int toughness                  = 1;
    private static final int knockbackResistance        = 0;
    private static final int durabilityMultiplier       = 17;
    private static final int enchantability             = 20;
    private static final Ingredient repairIngredient    = Ingredient.ofItems(ModItems.MOLYBDENUM_INGOT);
    private static final SoundEvent equipSound          = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;

    @Override
    public int getDurability(ArmorItem.Type type) {
        return switch(type){
            case BOOTS -> 13;
            case LEGGINGS -> 15;
            case CHESTPLATE -> 16;
            case HELMET -> 11;
        }*durabilityMultiplier;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return switch(type){
            case BOOTS -> 2;
            case LEGGINGS -> 5;
            case CHESTPLATE -> 6;
            case HELMET -> 2;
        };
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
