package org.oxytocina.geomancy.spells;

import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.UUID;

public class SpellBlockResult {
    public HashMap<String,SpellSignal> vars;
    public int iterations = 1;
    public String iterationVarName = "i";

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

    public void add(SpellSignal signal){
        vars.put(signal.name,signal);
    }

    public void add(String name, float value){
        add(SpellSignal.createNumber(value).named(name));
    }

    public void add(String name, double value){
        add(name,(float)value);
    }

    public void add(String name, boolean value){
        add(SpellSignal.createBoolean(value).named(name));
    }

    public void add(String name, String value){
        add(SpellSignal.createText(value).named(name));
    }

    public void add(String name, UUID value){
        add(SpellSignal.createUUID(value).named(name));
    }

    public void add(String name, Vec3d value){
        add(SpellSignal.createVector(value).named(name));
    }

    public static SpellBlockResult empty(){
        return new SpellBlockResult();
    }

    public SpellBlockResult clone(){
        SpellBlockResult res = new SpellBlockResult();
        res.vars.putAll(vars);
        res.iterationVarName=iterationVarName;
        res.depth=depth;
        return res;
    }

    public void refreshSignalDepths() {
        for(var sig : vars.values())
            sig.setDepth(depth);
    }
}
