package org.oxytocina.geomancy.spells;

import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.UUID;

public class SpellBlockArgs {
    public HashMap<String,SpellSignal> vars;
    public int iterations = 1;

    public int depth = 0;

    public SpellBlockArgs(){
        vars = new HashMap<>();
    }

    public static SpellBlockArgs empty(){
        return new SpellBlockArgs();
    }

    public SpellBlockArgs(HashMap<String,SpellSignal> vars){
        this.vars=vars;
    }

    public SpellBlockResult toRes(){return new SpellBlockResult(this);}

    public SpellSignal get(String name){
        if(!vars.containsKey(name)) return null;
        return vars.get(name);
    }

    public float getNumber(String name){
        return get(name).getNumberValue();
    }

    public int getInt(String name){
        return Math.round(getNumber(name));
    }

    public boolean getBoolean(String name){
        return get(name).getBooleanValue();
    }

    public String getText(String name){
        return get(name).getTextValue();
    }

    public UUID getUUID(String name){
        return get(name).getUUIDValue();
    }

    public Vec3d getVector(String name){
        return get(name).getVectorValue();
    }
}
