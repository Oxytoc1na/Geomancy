package org.oxytocina.geomancy.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.oxytocina.geomancy.spells.SpellGrid;

public class SpellStoringItem extends Item {

    public final int width;
    public final int height;

    public SpellStoringItem(Settings settings, int width, int height) {
        super(settings);
        this.width=width;
        this.height=height;
    }

    public static SpellGrid getOrCreateGrid(ItemStack stack){
        var existing = readGrid(stack);
        if(existing!=null) return existing;
        return createDefaultGrid(stack);
    }

    public int getWidth(){return width;}
    public int getHeight(){return height;}

    protected static SpellGrid createDefaultGrid(ItemStack stack){
        if(!(stack.getItem() instanceof SpellStoringItem storage)) return null;

        SpellGrid grid = new SpellGrid(storage.getWidth(),storage.getHeight());
        writeGrid(stack,grid);
        return grid;
    }

    public static SpellGrid readGrid(ItemStack stack){
        if(stack==null||!stack.hasNbt()) return null;
        var Nbt = stack.getNbt();
        if(!Nbt.contains("spell", NbtElement.COMPOUND_TYPE)) return null;
        var subNbt = Nbt.getCompound("spell");

        return new SpellGrid(subNbt);
    }

    public static void writeGrid(ItemStack stack,SpellGrid grid){
        if(stack==null) return;
        NbtCompound spellCompound = new NbtCompound();
        grid.writeNbt(spellCompound);
        stack.setSubNbt("spell",spellCompound);
    }

    public void cast(ItemStack caster,ItemStack spellstorage, LivingEntity user){
        SpellGrid grid = readGrid(spellstorage);
        if(grid==null) return;
        grid.run(caster,spellstorage,user);
    }
}
