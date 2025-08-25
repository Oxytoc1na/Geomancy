package org.oxytocina.geomancy.spells;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpellBlockResult {
    public HashMap<String,SpellSignal> vars;
    public int iterations = 1;
    public String iterationVarName = "i";
    public List<SpellBlockResult> subResults = null;

    /// increases by 1 every time signals are pushed after block execution
    public int depth = 0;

    public SpellBlockResult(){
        vars = new HashMap<>();
    }

    public SpellBlockResult(HashMap<String,SpellSignal> vars){
        this.vars=vars;
    }

    public SpellBlockResult(SpellBlockArgs args){
        this.vars = new HashMap<>();
        vars.putAll(args.vars);
    }

    public SpellBlockResult add(SpellSignal signal){
        vars.put(signal.name,signal); return this;
    }

    public SpellBlockResult add(String name, float value){
        add(SpellSignal.createNumber(value).named(name)); return this;
    }

    public SpellBlockResult add(String name, double value){
        add(name,(float)value); return this;
    }

    public SpellBlockResult add(String name, boolean value){
        add(SpellSignal.createBoolean(value).named(name)); return this;
    }

    public SpellBlockResult add(String name, String value){
        add(SpellSignal.createText(value).named(name)); return this;
    }

    public SpellBlockResult add(String name, UUID value){
        add(SpellSignal.createUUID(value).named(name)); return this;
    }

    public SpellBlockResult add(String name, Vec3d value){
        add(SpellSignal.createVector(value).named(name)); return this;
    }

    public SpellBlockResult add(String name, Vec3i value){
        add(SpellSignal.createVector(value).named(name)); return this;
    }

    public SpellBlockResult add(String name, BlockPos value){
        add(SpellSignal.createVector(value.toCenterPos()).named(name)); return this;
    }

    public SpellBlockResult add(String name, List<SpellSignal> value) { add(SpellSignal.createList(value).named(name)); return this; }

    public SpellBlockResult addSubResult(SpellBlockResult res) { if(subResults==null) subResults = new ArrayList<>(); res.depth=depth; subResults.add(res); return this; }

    public static SpellBlockResult empty(){
        return new SpellBlockResult();
    }

    public SpellBlockResult clone(){
        SpellBlockResult res = new SpellBlockResult();
        res.vars.putAll(vars);
        res.iterationVarName=iterationVarName;
        res.depth=depth;
        if(subResults!=null){
            res.subResults = new ArrayList<>();
            for(var r : subResults)
                res.subResults.add(r.clone());
        }
        return res;
    }

    public void refreshSignalDepths() {
        for(var sig : vars.values())
            sig.setDepth(depth);
    }

    public void writeNbt(NbtCompound temp) {
        temp.putInt("iterations",iterations);
        temp.putInt("depth",depth);
        temp.putString("iterationVarName",iterationVarName);

        NbtList subResultsNbt = new NbtList();
        for(var s : subResults){
            var temp2 = new NbtCompound();
            s.writeNbt(temp2);
            subResultsNbt.add(temp2);
        }
        temp.put("subResults",subResultsNbt);

        var varsNbt = new NbtCompound();
        for(var s : vars.keySet()){
            varsNbt.put(s,vars.get(s).toNBT());
        }
        temp.put("vars",varsNbt);
    }
}
