package org.oxytocina.geomancy.items.tools;

import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.spells.SpellSignal;

public interface IVariableStoringItem {
    SpellSignal getSignal(ItemStack stack,String name);
    boolean setSignal(ItemStack stack,SpellSignal signal);
    default String getAccessorPrefix(ItemStack stack){
        var nbt = stack.getNbt();
        if(nbt==null||!nbt.contains("display")) return "default";
        var subnbt = nbt.getCompound("display");
        if(subnbt.contains("Name")){
            return stack.getName().getString();
        }

        return "default";
    }
}
