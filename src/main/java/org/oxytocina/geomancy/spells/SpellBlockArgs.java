package org.oxytocina.geomancy.spells;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpellBlockArgs {
    public HashMap<String,SpellSignal> vars;

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
        return getText(name,null);
    }

    public Identifier getIdentifier(String name){
        return Identifier.tryParse(getText(name));
    }

    public String getText(String name,SpellContext ctx){
        return get(name).getTextValue(ctx);
    }

    public UUID getUUID(String name){
        return get(name).getUUIDValue();
    }

    public Vec3d getVector(String name){
        return get(name).getVectorValue();
    }

    public List<SpellSignal> getList(String name) { return get(name).getListValue(); }

    public BlockPos getBlockPos(String name) {
        return Toolbox.posToBlockPos(getVector(name));
    }

    public boolean has(String varName) {
        return vars.containsKey(varName);
    }

    public void writeNbt(NbtCompound temp) {
        var varsNbt = new NbtCompound();
        for(var s : vars.keySet()){
            varsNbt.put(s,vars.get(s).toNBT());
        }
        temp.put("vars",varsNbt);
    }
}
