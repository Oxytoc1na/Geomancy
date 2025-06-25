package org.oxytocina.geomancy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.Objects;

public class ModEnchantments {

    public static final SkillfulEnchantment SKILLFUL = (SkillfulEnchantment) register("skillful",new SkillfulEnchantment());

    public static void initialize(){

    }

    private static Enchantment register(String name, Enchantment enchantment) {
        return (Enchantment) Registry.register(Registries.ENCHANTMENT, Geomancy.locate(name), enchantment);
    }

    public static int getLevel(ItemStack stack, String vanillaEnchantment){
        return getLevel(stack,new Identifier(Identifier.DEFAULT_NAMESPACE,vanillaEnchantment));
    }

    public static int getLevel(ItemStack stack, Identifier enchantment){
        int res = 0;
        var l = stack.getEnchantments();
        for (int i = 0; i < l.size(); i++) {
            NbtCompound c = l.getCompound(i);
            if(Objects.equals(c.getString("id"), enchantment.toString())){
                return c.getInt("lvl");
            }
        }
        return res;
    }
}
