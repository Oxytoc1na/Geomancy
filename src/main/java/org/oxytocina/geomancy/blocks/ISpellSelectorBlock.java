package org.oxytocina.geomancy.blocks;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.items.tools.IVariableStoringItem;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.spells.SpellGrid;

import java.util.ArrayList;
import java.util.Objects;

public interface ISpellSelectorBlock {

    default ItemStack getFirstCasterItem(BlockEntity entity){
        var items = getCasterItems(entity);
        if(items.isEmpty()) return null;
        return items.get(0);
    }
    default ArrayList<ItemStack> getCasterItems(BlockEntity entity){
        if(!(entity instanceof ISpellSelectorBlock)) return null;
        ArrayList<ItemStack> res = new ArrayList<>();
        for(int i = 0; i < getStorageSize(entity);i++){
            var contender = getStack(entity,i);
            if(!(contender.getItem() instanceof SoulCastingItem sci)) continue;
            res.add(contender);
        }
        return res;
    }

    default ArrayList<ItemStack> getCastableSpellItems(BlockEntity entity, ItemStack caster){
        if(!(entity instanceof ISpellSelectorBlock)) return null;

        if(!(caster.getItem() instanceof SoulCastingItem sci)) return null;

        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < sci.getStorageSize(caster); i++) {
            var spell = sci.getStack(caster,i);
            if(!(spell.getItem() instanceof SpellStoringItem)) continue;
            var grid = SpellStoringItem.readGrid(spell);
            if(grid==null||grid.library) continue;
            res.add(spell);
        }
        return res;
    }

    ItemStack getStack(BlockEntity entity, int index);
    int getStorageSize(BlockEntity entity);

    default int getSelectedSpellIndex(BlockEntity entity,ItemStack caster){
        if(!(caster.getItem() instanceof ISpellSelectorItem sps)) return 0;
        return sps.getSelectedSpellIndex(caster);
    }

    void markDirty(BlockEntity entity);
}
