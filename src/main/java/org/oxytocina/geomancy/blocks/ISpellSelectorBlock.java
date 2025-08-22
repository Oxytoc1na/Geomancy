package org.oxytocina.geomancy.blocks;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.items.tools.IVariableStoringItem;
import org.oxytocina.geomancy.spells.SpellGrid;

import java.util.ArrayList;
import java.util.Objects;

public interface ISpellSelectorBlock {
    default boolean setSelectedSpell(BlockEntity entity, String spellName){
        var selectable = getCastableSpellItems(entity);
        for (int i = 0; i < selectable.size(); i++) {
            var spell = SpellStoringItem.readGrid(selectable.get(i));
            if(spell!=null&& Objects.equals(spell.name, spellName)){
                setSelectedSpellIndex(entity,i);
                return true;
            }
        }

        return false;
    }

    default ArrayList<ItemStack> getCastableSpellItems(BlockEntity entity){
        if(!(entity instanceof ISpellSelectorBlock)) return null;

        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getStorageSize(entity); i++) {
            var spell = getStack(entity,i);
            if(!(spell.getItem() instanceof SpellStoringItem)) continue;
            var grid = SpellStoringItem.readGrid(spell);
            if(grid==null||grid.library) continue;
            res.add(spell);
        }
        return res;
    }

    ItemStack getStack(BlockEntity entity, int index);
    int getStorageSize(BlockEntity entity);

    default int getSelectedSpellIndex(BlockEntity entity){
        return 0;
    }

    default void setSelectedSpellIndex(BlockEntity entity,int index){
        return;
    }

    default int getInstalledSpellsCount(BlockEntity entity){
        return getCastableSpellItems(entity).size();
    }

    default SpellGrid getSpell(BlockEntity entity, String name){
        if(!(entity instanceof ISpellSelectorBlock caster)) return null;

        for (int i = 0; i < caster.getStorageSize(entity); i++) {
            var contender = caster.getStack(entity,i);
            if(!(contender.getItem() instanceof SpellStoringItem storer)) continue;
            var grid = SpellStoringItem.readGrid(contender);
            if(grid==null) continue;
            if(Objects.equals(grid.name, name)) return grid;
        }

        return null;
    }

    default ItemStack getSpellStorageStack(BlockEntity entity, String name){
        if(!(entity instanceof ISpellSelectorBlock caster)) return null;

        for (int i = 0; i < caster.getStorageSize(entity); i++) {
            var contender = caster.getStack(entity,i);
            if(!(contender.getItem() instanceof SpellStoringItem storer)) continue;
            var grid = SpellStoringItem.readGrid(contender);
            if(grid==null) continue;
            if(Objects.equals(grid.name, name)) return contender;
        }

        return null;
    }

    default ArrayList<ItemStack> getVariableStorageItems(BlockEntity entity){
        if(!(entity instanceof ISpellSelectorBlock)) return null;

        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getStorageSize(entity); i++) {
            var contender = getStack(entity,i);
            if(!(contender.getItem() instanceof IVariableStoringItem)) continue;
            res.add(contender);
        }
        return res;
    }

    default ItemStack getVariableStorageItem(BlockEntity entity, String name) {
        var items = getVariableStorageItems(entity);
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var itemItem = (IVariableStoringItem) item.getItem();
            if(itemItem.getAccessorPrefix(item).equals(name)) return item;
        }
        return null;
    }

    void markDirty(BlockEntity entity);
}
