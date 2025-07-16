package org.oxytocina.geomancy.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;

public class SpellGrid {
    public int width;
    public int height;
    public String name;
    public HashMap<Vector2i,SpellComponent> components;

    public SpellGrid(NbtCompound nbt){
        this.components=new HashMap<>();
        readNbt(nbt);
    }

    public SpellGrid(int width, int height){
        this.width=width;
        this.height=height;
        this.name="";
        this.components=new HashMap<>();
    }

    public void run(ItemStack casterItem, ItemStack containerItem, LivingEntity casterEntity){
        for (var comp : components.values())
            comp.preRunSetup(casterItem,containerItem,casterEntity);
        for (var comp : components.values())
            comp.run();
    }

    public boolean tryAddComponent(SpellComponent component){
        if(components.containsKey(component.position)) return false; // occupied
        if(!inBounds(component.position)) return false; // out of bounds

        component.parent=this;
        components.put(component.position,component);
        recalculateNeighbors(component.position);

        return true;
    }

    public SpellComponent getComponent(Vector2i pos){
        if(!components.containsKey(pos)) return null;
        return components.get(pos);
    }

    public void recalculateNeighbors(Vector2i position){
        var componentAtThisPosition = getComponent(position);
        var neighborPosistions = getNeighboringPositions(position);
        for (int i = 0; i < 6; i++) {
            var comp = getComponent(neighborPosistions.get(i));
            String dir = SpellComponent.getDir(i);
            if(componentAtThisPosition!=null){
                componentAtThisPosition.setNeighbor(dir,comp);
            }
            if(comp==null) continue;
            comp.setNeighbor(SpellComponent.mirrorDirection(dir),componentAtThisPosition);

        }
    }

    public ArrayList<Vector2i> getNeighboringPositions(Vector2i pos){
        ArrayList<Vector2i> res = new ArrayList<>();

        int ySkew = pos.y%2;

        /*

          o   6   1
            5   o   2
          o   4   3
         */

        res.add(new Vector2i(pos.x+ySkew,pos.y-1));
        res.add(new Vector2i(pos.x+1,pos.y));

        res.add(new Vector2i(pos.x+ySkew,pos.y+1));
        res.add(new Vector2i(pos.x-1+ySkew,pos.y+1));

        res.add(new Vector2i(pos.x-1,pos.y));
        res.add(new Vector2i(pos.x-1+ySkew,pos.y-1));

        return res;
    }

    public boolean inBounds(Vector2i position){
        return position.x>=0 && position.y>=0&&position.x<width&&position.y<height;
    }

    public void writeNbt(NbtCompound nbt){
        nbt.putInt("width",width);
        nbt.putInt("height",height);
        nbt.putString("name",name);
        NbtList compsNbt = new NbtList();
        for (var c:components.values())
        {
            NbtCompound cComp = new NbtCompound();
            c.writeNbt(cComp);
            compsNbt.add(cComp);
        }
        nbt.put("components",compsNbt);
    }

    public void readNbt(NbtCompound nbt){
        width=nbt.getInt("width");
        height=nbt.getInt("height");
        name=nbt.getString("name");
        NbtList compsNbt = nbt.getList("components", NbtElement.COMPOUND_TYPE);
        for (var c:compsNbt)
        {
            if(!(c instanceof NbtCompound nbtComp)) continue;

            SpellComponent comp = new SpellComponent(this,nbtComp);
            tryAddComponent(comp);
        }
    }
}
