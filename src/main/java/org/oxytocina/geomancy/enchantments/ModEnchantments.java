package org.oxytocina.geomancy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.Objects;

public class ModEnchantments {

    /// increases skill of a hammer for smithery and soulforge recipes
    /// targets: hammers
    public static final SkillfulEnchantment SKILLFUL        = register("skillful",new SkillfulEnchantment());
    /// increases progress per swing of a hammer in smithery and soulforge recipes
    /// targets: hammers
    public static final MightyEnchantment MIGHTY            = register("mighty",new MightyEnchantment());
    /// increases effectiveness of jewelry
    /// targets: jewelry
    public static final BrillianceEnchantment BRILLIANCE    = register("brilliance",new BrillianceEnchantment());
    /// decreases soul consumed from this item
    /// targets: soul storage items, spell storage items
    public static final SoulSaverEnchantment SOUL_SAVER     = register("soul_saver",new SoulSaverEnchantment());
    /// increases soul storage capacity of an item
    /// targets: soul storage
    public static final MesmerizingEnchantment MESMERIZING  = register("mesmerizing",new MesmerizingEnchantment());
    /// decreases effect of distance on casting cost
    /// targets: caster items
    public static final FocusedEnchantment FOCUSED          = register("focused",new FocusedEnchantment());

    public static void register(){

    }

    private static <T extends Enchantment> T register(String name, T enchantment) {
        return Registry.register(Registries.ENCHANTMENT, Geomancy.locate(name), enchantment);
    }

    public static int getLevel(ItemStack stack, String vanillaEnchantment){
        return getLevel(stack,new Identifier(Identifier.DEFAULT_NAMESPACE,vanillaEnchantment));
    }

    public static int getLevel(ItemStack stack, Enchantment enchantment){
        return getLevel(stack,Registries.ENCHANTMENT.getId(enchantment));
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
