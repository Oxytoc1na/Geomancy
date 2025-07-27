package org.oxytocina.geomancy.items.armor.materials;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.oxytocina.geomancy.items.ModItems;

public class MithrilMaterial implements ArmorMaterial {

    private static final String name                    = "mithril";
    private static final int toughness                  = 2;
    private static final int knockbackResistance        = 0;
    private static final int durabilityMultiplier       = 33;
    private static final int enchantability             = 20;
    private static final Ingredient repairIngredient    = Ingredient.ofItems(ModItems.MITHRIL_INGOT);
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
            case BOOTS -> 3;
            case LEGGINGS -> 6;
            case CHESTPLATE -> 8;
            case HELMET -> 3;
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
