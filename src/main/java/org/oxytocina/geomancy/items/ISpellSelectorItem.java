package org.oxytocina.geomancy.items;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Pair;
import org.oxytocina.geomancy.items.tools.IVariableStoringItem;
import org.oxytocina.geomancy.items.tools.StorageItem;
import org.oxytocina.geomancy.spells.SpellGrid;

import java.util.*;
import java.util.function.Function;

public interface ISpellSelectorItem {
    default boolean setSelectedSpell(ItemStack stack, String spellName){
        int index = getSpellIndexOfSpell(stack,spellName);
        if(index==-1) return false;
        setSelectedSpellIndex(stack,index);
        return true;
    }

    default int getSpellIndexOfSpell(ItemStack stack, String spellName){
        var selectable = getCastableSpellItems(stack);
        for (int i = 0; i < selectable.size(); i++) {
            var spell = SpellStoringItem.readGrid(selectable.get(i));
            if(spell!=null&& Objects.equals(spell.name, spellName)){
                setSelectedSpellIndex(stack,i);
                return i;
            }
        }

        return -1;
    }

    default ArrayList<ItemStack> getSpellItems(ItemStack stack, Function<SpellGrid,Boolean> predicate){
        if(!(stack.getItem() instanceof ISpellSelectorItem)) return null;

        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getStorageSize(stack); i++) {
            var spell = getStack(stack,i);
            if(!(spell.getItem() instanceof SpellStoringItem)) continue;
            var grid = SpellStoringItem.readGrid(spell);
            if(grid==null||!predicate.apply(grid)) continue;
            res.add(spell);
        }
        return res;
    }

    default ArrayList<ItemStack> getCastableSpellItems(ItemStack stack){
        return getSpellItems(stack,g->!g.library);
    }

    ItemStack getStack(ItemStack storage, int index);
    int getStorageSize(ItemStack stack);

    default int getSelectedSpellIndex(ItemStack stack){
        if(!stack.getOrCreateNbt().contains("selected", NbtElement.INT_TYPE)) return 0;
        int res = stack.getNbt().getInt("selected");
        int installed = getInstalledSpellsCount(stack);
        if(installed<=0)return 0;
        res = ((res%installed)+installed)%installed;
        return res;
    }

    default void setSelectedSpellIndex(ItemStack stack,int index){
        int installed = getInstalledSpellsCount(stack);
        if(installed<=0)index=0;
        else index = ((index%installed)+installed)%installed;
        stack.getOrCreateNbt().putInt("selected",index);
    }

    default int getInstalledSpellsCount(ItemStack stack){
        return getCastableSpellItems(stack).size();
    }

    default SpellGrid getSpell(ItemStack casterItem, String name){
        if(!(casterItem.getItem() instanceof ISpellSelectorItem caster)) return null;

        for (int i = 0; i < caster.getStorageSize(casterItem); i++) {
            var contender = caster.getStack(casterItem,i);
            if(!(contender.getItem() instanceof SpellStoringItem storer)) continue;
            var grid = SpellStoringItem.readGrid(contender);
            if(grid==null) continue;
            if(Objects.equals(grid.name, name)) return grid;
        }

        return null;
    }

    default ItemStack getSpellStorageStack(ItemStack casterItem, String name){
        if(!(casterItem.getItem() instanceof ISpellSelectorItem caster)) return null;

        for (int i = 0; i < caster.getStorageSize(casterItem); i++) {
            var contender = caster.getStack(casterItem,i);
            if(!(contender.getItem() instanceof SpellStoringItem storer)) continue;
            var grid = SpellStoringItem.readGrid(contender);
            if(grid==null) continue;
            if(Objects.equals(grid.name, name)) return contender;
        }

        return null;
    }

    /// returns a map containing lists that contain variable storage items.
    /// variable storage items in the main inventory are located under key null.
    static Map<ItemStack,List<ItemStack>> getAllVariableStorageItems(Inventory inv){
        Map<ItemStack,List<ItemStack>> res = new LinkedHashMap<>();
        for (int i = 0; i < inv.size(); i++) {
            var mainInvStack = inv.getStack(i);
            if(mainInvStack.getItem() instanceof IVariableStoringItem)
            {
                // add stack to main
                if(!res.containsKey(null)) res.put(null,new ArrayList<>());
                res.get(null).add(mainInvStack);
                continue;
            }
            if(mainInvStack.getItem() instanceof ISpellSelectorItem sps){
                res.put(mainInvStack,sps.getVariableStorageItems(mainInvStack));
            }
        }
        return res;
    }

    /// left: the item that contains the variable storage item. null if main inventory.
    /// right: the variable storage item.
    static Pair<ItemStack,ItemStack> pickVariableStorageItem(Inventory inv, String name){
        ItemStack container = null;
        ItemStack res = null;
        var containers = getAllVariableStorageItems(inv);
        for (var contender : containers.keySet()) {
            var picked = pickVariableStorageItem(containers.get(contender),name);
            if(picked != null)
            {
                container = contender;
                res = picked;
                break;
            }
        }
        return new Pair<>(container,res);
    }

    static ItemStack pickVariableStorageItem(List<ItemStack> items, String name) {
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var itemItem = (IVariableStoringItem) item.getItem();
            if(itemItem.getAccessorPrefix(item).equals(name)) return item;
        }
        return null;
    }

    static void markDirtyStatic(ItemStack stack){
        var item = stack.getItem();
        if(item instanceof ISpellSelectorItem sps){sps.markDirty(stack); return;}
        if(item instanceof StorageItem storer) {storer.markDirty(stack); return;}
    }

    default List<ItemStack> getVariableStorageItems(ItemStack stack){
        if(!(stack.getItem() instanceof ISpellSelectorItem)) return null;

        List<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getStorageSize(stack); i++) {
            var contender = getStack(stack,i);
            if(!(contender.getItem() instanceof IVariableStoringItem)) continue;
            res.add(contender);
        }
        return res;
    }

    default ItemStack getVariableStorageItem(ItemStack stack, String name) {
        var items = getVariableStorageItems(stack);
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var itemItem = (IVariableStoringItem) item.getItem();
            if(itemItem.getAccessorPrefix(item).equals(name)) return item;
        }
        return null;
    }

    void markDirty(ItemStack casterItem);
    void onSpellChanged(ItemStack stack, ClientPlayerEntity player, int spellIndex);

    default boolean spellPresent(ItemStack stack, String spellName){
        return getSpellIndexOfSpell(stack,spellName) != -1;
    }

    default SpellGrid getSelectedSpell(ItemStack stack){
        var spells = getCastableSpellItems(stack);
        if(spells.isEmpty()) return null;
        return SpellStoringItem.readGrid(spells.get(getSelectedSpellIndex(stack)));
    }
}
