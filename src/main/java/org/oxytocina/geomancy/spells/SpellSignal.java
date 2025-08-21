package org.oxytocina.geomancy.spells;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.oxytocina.geomancy.helpers.NbtHelper;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SpellSignal {

    public Type type;
    public String name;
    public float numberValue;
    public String textValue;
    public UUID uuidValue;
    public Vec3d vectorValue;
    public List<SpellSignal> listValue;

    // prevent endless loops
    public int depth = 0;

    protected SpellSignal(Type type, String name, float numberValue, String textValue, UUID uuidValue,Vec3d vector,List<SpellSignal> list){
        this.type=type;
        this.name=name;
        this.numberValue=numberValue;
        this.textValue=textValue;
        this.uuidValue=uuidValue;
        this.vectorValue =vector;
        this.listValue=list;
    }

    public static SpellSignal createBoolean(boolean defaultValue) {
        return new SpellSignal(Type.Boolean,"bool",defaultValue?1:0,"",null,null,null);
    }

    public static SpellSignal createBoolean(float defaultValue) {
        return createBoolean(defaultValue>0);
    }
    public static SpellSignal createBoolean() {
        return createBoolean(false);
    }


    public static SpellSignal createNumber(float defaultValue) {
        return new SpellSignal(Type.Number,"num",defaultValue,"",null,null,null);
    }
    public static SpellSignal createNumber(double defaultValue) {
        return createNumber((float)defaultValue);
    }
    public static SpellSignal createNumber() {
        return createNumber(0f);
    }

    public static SpellSignal createAny() {
        return new SpellSignal(Type.Any,"any",0,"",null,null,null);
    }
    public static SpellSignal createNone() {
        return new SpellSignal(Type.None,"none",0,"",null,null,null);
    }

    public static SpellSignal createText() { return createText("");}
    public static SpellSignal createText(String defaultValue) {
        return new SpellSignal(Type.Text,"text",0,defaultValue,null,null,null);
    }

    public static SpellSignal createUUID() { return createUUID(null);}
    public static SpellSignal createUUID(UUID defaultValue) {
        return new SpellSignal(Type.UUID,"uuid",0,"",defaultValue,null,null);
    }

    public static SpellSignal createVector() { return createVector(null);}
    public static SpellSignal createVector(Vec3d defaultValue) {
        return new SpellSignal(Type.Vector,"vec",0,"",null,defaultValue,null);
    }

    public static SpellSignal createList() { return createList(null);}
    public static SpellSignal createList(List<SpellSignal> defaultValue) {
        return new SpellSignal(Type.List,"list",0,"",null,null,defaultValue);
    }

    public SpellSignal named(String name){
        this.name=name;
        return this;
    }

    public float getNumberValue() {
        switch(type){
            case Number, Boolean -> {
                return numberValue;
            }
            case Vector -> {
                return (float)vectorValue.length();
            }
            case List -> {return getListValueOrEmpty().size();}
        }
        return 0;
    }

    public int getIntValue() {
        return Math.round(getNumberValue());
    }

    public Vec3d getVectorValue(){
        return vectorValue;
    }

    public boolean getBooleanValue(){
        return getNumberValue()>0;
    }

    public String getTextValue(){
        return getTextValue(null);
    }

    public String getTextValue(SpellContext ctx){
        switch(type){
            case Text: return textValue;
            case UUID: {
                // attempt to fetch entity
                Entity ent = null;
                if(ctx!=null && ctx.getWorld() instanceof ServerWorld sw){
                    ent = sw.getEntity(uuidValue);
                }
                if(ent!=null){
                    var entName = ent.getName().getString();
                    return entName;
                }
                return uuidValue.toString();
            }
            case Number: return Float.toString(getNumberValue());
            case Boolean: return getBooleanValue()?"true":"false";
            case Vector: return getVectorValue().toString();
            case List: {
                String res = "[";
                var lv = getListValue();
                for (int i = 0; i < lv.size(); i++) {
                    res+=lv.get(i).getTextValue(ctx);
                    if(i<lv.size()-1) res+=",";
                }
                res+="]";
                return res;
            }
        }
        return "N/A";
    }

    public UUID getUUIDValue(){
        return uuidValue;
    }

    public List<SpellSignal> getListValue() {return listValue;}
    public List<SpellSignal> getListValueOrEmpty() {return listValue!=null?listValue:new ArrayList<>();}

    public Entity getEntity(World world)
    {
        if(type!=Type.UUID) return null;
        if(!(world instanceof ServerWorld serverWorld)) return null;
        return serverWorld.getEntity(getUUIDValue());
    }

    @Override
    public String toString() {
        return type.toString()+":"+getTextValue();
    }

    public String toString(SpellContext ctx) {
        return type.toString()+":"+getTextValue(ctx);
    }

    public Text toText(){
        return getTypeText().append(Text.literal(" "+name).formatted(Formatting.GRAY));
    }

    public MutableText getTypeText(){
        return Text.translatable("geomancy.spellmaker.types."+type.toString().toLowerCase()).formatted(Formatting.DARK_AQUA);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth){
        this.depth=depth;
    }

    public NbtCompound toNBT() {
        NbtCompound res = new NbtCompound();
        writeNbt(res);
        return res;
    }


    public enum Type{
        None,
        Any,
        Boolean,
        Number,
        Text,
        UUID,
        Vector,
        List,
    }

    public void writeNbt(NbtCompound nbt)
    {
        nbt.putString("type",type.toString());
        nbt.putString("name",name);
        switch(type){
            case Number: nbt.putFloat("val",getNumberValue()); break;
            case Boolean: nbt.putBoolean("val",getBooleanValue()); break;
            case Text: nbt.putString("val",getTextValue()); break;
            case UUID: nbt.putUuid("val",getUUIDValue()); break;
            case Vector: nbt.put("val", NbtHelper.vectorToNbt(getVectorValue())); break;
            case List: nbt.put("val", listToNbt(getListValue())); break;
        }

    }
    private static NbtList listToNbt(List<SpellSignal> list){
        NbtList res = new NbtList();
        for(var s : list){
            NbtCompound nbt = new NbtCompound();
            s.writeNbt(nbt);
            res.add(nbt);
        }
        return res;
    }
    private static List<SpellSignal> listFromNbt(NbtList nbtl){
        List<SpellSignal> res = new ArrayList<>();
        for(int i = 0; i < nbtl.size(); i++){
            NbtCompound nbt = nbtl.getCompound(i);
            var sig = fromNBT(nbt);
            res.add(sig);
        }
        return res;
    }

    public static SpellSignal fromNBT(NbtCompound nbt){

        String typeString = nbt.getString("type");
        Type type;
        try {type = Type.valueOf(typeString);}
        catch (Exception ignored){type = Type.None;}

        String name = nbt.getString("name");

        float numberValue = 0;
        String textValue = "";
        UUID uuidValue = null;
        Vec3d vectorValue = null;
        List<SpellSignal> listValue = null;

        switch(type){
            case Number: numberValue = nbt.getFloat("val"); break;
            case Boolean: numberValue = nbt.getBoolean("val")?1:0; break;
            case Text: textValue = nbt.getString("val"); break;
            case UUID: uuidValue = nbt.getUuid("val"); break;
            case Vector: vectorValue = NbtHelper.vectorFromNbt(nbt); break;
            case List: listValue = listFromNbt(nbt.getList("val", NbtElement.COMPOUND_TYPE));
        }

        return new SpellSignal(type,name,numberValue,textValue,uuidValue,vectorValue,listValue);
    }

    @Override
    public SpellSignal clone()
    {
        List<SpellSignal> newList = null;
        if(listValue!=null){
            newList = new ArrayList<>();
            for(var s : listValue) newList.add(s.clone());
        }
        return new SpellSignal(type,name,numberValue,textValue,uuidValue, vectorValue,newList);
    }

    public static boolean typesCompatible(Type from, Type onto){

        switch(onto){
            case Any : return true;
            case None: return false;
            case Number,Boolean: return from == SpellSignal.Type.Number||from == SpellSignal.Type.Boolean||from==Type.Vector;
        }

        return from==onto;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SpellSignal that = (SpellSignal) o;
        return
                Float.compare(numberValue, that.numberValue) == 0
                        && depth == that.depth
                        && type == that.type
                        && Objects.equals(name, that.name)
                        && Objects.equals(textValue, that.textValue)
                        && Objects.equals(uuidValue, that.uuidValue)
                        && Objects.equals(vectorValue, that.vectorValue)
                        && listValuesAreEqual(this,that);
    }

    private static boolean listValuesAreEqual(SpellSignal a, SpellSignal b){
        var lva = a.getListValue();
        var lvb = b.getListValue();
        if(lva == null && lvb != null) return false;
        if(lva==null) return true;
        if(lva.size()!=lvb.size()) return false;

        for (int i = 0; i < lva.size(); i++) {
            if(!lva.get(i).equals(lvb.get(i))) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, numberValue, textValue, uuidValue, vectorValue, listValue, depth);
    }
}
