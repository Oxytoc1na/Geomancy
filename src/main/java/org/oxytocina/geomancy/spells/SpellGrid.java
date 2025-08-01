package org.oxytocina.geomancy.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Vector2i;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SpellGrid {
    public int width;
    public int height;
    public String name;
    public boolean library; // if true, hides spell from spell selection
    public HashMap<Vector2i,SpellComponent> components;
    public float soulCostMultiplier = 1;

    public SpellGrid(ItemStack stack, NbtCompound nbt){
        this.components=new HashMap<>();
        readNbt(nbt);
        // soul saver
        if(stack.getItem() instanceof SpellStoringItem storer)
            soulCostMultiplier=storer.getSoulCostMultiplier(stack);
    }

    public SpellGrid(int width, int height){
        this.width=width;
        this.height=height;
        this.name="";
        this.library=false;
        this.components=new HashMap<>();
    }

    public SpellBlockResult runReferenced(SpellContext parent,SpellComponent casterComp,SpellBlockArgs args){
        SpellContext context = parent.createReferenced(casterComp);
        context.internalVars = args;
        context.grid = this;

        context.refreshAvailableSoul();
        for (var comp : components.values())
            comp.preRunSetup(context);

        context.stage = SpellContext.Stage.Run;
        for (var comp : components.values())
            comp.run();

        context.refreshAvailableSoul();
        for (var comp : components.values())
            comp.postRun();

        return context.referenceResult;
    }

    public void run(ItemStack casterItem, ItemStack containerItem, LivingEntity casterEntity){

        float costMultiplier = soulCostMultiplier;
        if(casterEntity.hasStatusEffect(ModStatusEffects.REGRETFUL))
        {
            var amp = casterEntity.getStatusEffect(ModStatusEffects.REGRETFUL).getAmplifier();
            costMultiplier *= 1+(amp+1)*0.5f;
        }

        SpellContext context = new SpellContext(this,casterEntity,casterItem,containerItem,0,costMultiplier,0);
        context.refreshAvailableSoul();

        try{
            for (var comp : components.values())
                comp.preRunSetup(context);

            context.stage = SpellContext.Stage.Run;
            for (var comp : components.values())
                comp.run();

            for (var comp : components.values())
                comp.postRun();
        }
        catch(Exception ignored){
            Geomancy.logError("AAAAA!!!! Spells threw an exception! DEBUG ME!");
        }

        if(context.depthLimitReached && context.debugging){
            SpellBlocks.tryLogDebugDepthLimitReached(context);
        }

        if(context.soulConsumed > 0){
            SpellBlocks.playCastSound(context);
        }

    }

    public boolean tryRemoveComponent(Vector2i position){
        if(!components.containsKey(position)) return false;
        components.remove(position);
        return true;
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
        return
                position.x>=0 &&
                        position.y>=0&&
                        position.x<width&&
                        position.y<height&&
                positionIsInGrid(position.x,position.y,width,height);
    }

    public MutableText getName(){
        if(Objects.equals(name, "")) return Text.translatable("geomancy.spellstorage.unnamed");
        return Text.literal(name);
    }

    public MutableText getRuntimeName(SpellContext context){
        MutableText res = getName();
        if(context.isChild()) res = context.parentCall.grid.getRuntimeName(context.parentCall).append(" -> ").append(res);
        return res;
    }

    public void writeNbt(NbtCompound nbt){
        nbt.putInt("width",width);
        nbt.putInt("height",height);
        nbt.putString("name",name);
        nbt.putBoolean("lib",library);
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
        library=nbt.getBoolean("lib");
        NbtList compsNbt = nbt.getList("components", NbtElement.COMPOUND_TYPE);
        for (var c:compsNbt)
        {
            if(!(c instanceof NbtCompound nbtComp)) continue;

            SpellComponent comp = new SpellComponent(this,nbtComp);
            tryAddComponent(comp);
        }
    }

    // cuts off hexagonal edges
    public static boolean positionIsInGrid(int x, int y, int width, int height){
        int ySkew = y%2;
        float midYIndex = height/2f-0.5f;
        float diffToMidYIndex = Math.abs(y-midYIndex);

        if(x>width/2f){
            // to the right of the middle
            int xDiffToEdges = width-x;
            return xDiffToEdges>=(diffToMidYIndex+1+ySkew)/2;
        }
        else{
            // to the left of the middle
            int xDiffToEdges = x;
            return xDiffToEdges>=(diffToMidYIndex-ySkew)/2;
        }
    }
}
