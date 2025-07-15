package org.oxytocina.geomancy.spells;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class SpellSignal {

    public Type type;
    public String name;
    public float numberValue;
    public String textValue;
    public UUID uuidValue;

    public SpellSignal(Type type, String name, float numberValue, String textValue, UUID uuidValue){
        this.type=type;
        this.name=name;
        this.numberValue=numberValue;
        this.textValue=textValue;
        this.uuidValue=uuidValue;
    }

    public static SpellSignal createBoolean(boolean defaultValue) {
        return new SpellSignal(Type.Boolean,"bool",defaultValue?1:0,"",null);
    }

    public static SpellSignal createBoolean(float defaultValue) {
        return createBoolean(defaultValue>0);
    }


    public static SpellSignal createNumber(float defaultValue) {
        return new SpellSignal(Type.Number,"num",defaultValue,"",null);
    }

    public static SpellSignal createAny() {
        return new SpellSignal(Type.Any,"any",0,"",null);
    }
    public static SpellSignal createNone() {
        return new SpellSignal(Type.None,"none",0,"",null);
    }

    public static SpellSignal createText(String defaultValue) {
        return new SpellSignal(Type.Text,"text",0,defaultValue,null);
    }

    public static SpellSignal createUUID(UUID defaultValue) {
        return new SpellSignal(Type.UUID,"uuid",0,"",defaultValue);
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
        }
        return 0;
    }

    public boolean getBooleanValue(){
        return getNumberValue()>0;
    }

    public String getTextValue(){
        switch(type){
            case Text: return textValue;
            case UUID: return uuidValue.toString();
            case Number: return Float.toString(getNumberValue());
            case Boolean: return getBooleanValue()?"true":"false";
        }
        return "N/A";
    }

    public UUID getUUIDValue(){
        return uuidValue;
    }

    @Override
    public String toString() {
        return type.toString()+":"+getTextValue();
    }

    public enum Type{
        None,
        Any,
        Boolean,
        Number,
        Text,
        UUID,
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
        }

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

        switch(type){
            case Number: numberValue = nbt.getFloat("val"); break;
            case Boolean: numberValue = nbt.getBoolean("val")?1:0; break;
            case Text: textValue = nbt.getString("val"); break;
            case UUID: uuidValue = nbt.getUuid("val"); break;
        }

        return new SpellSignal(type,name,numberValue,textValue,uuidValue);
    }

    @Override
    public SpellSignal clone()
    {
        return new SpellSignal(type,name,numberValue,textValue,uuidValue);
    }

    public static boolean typesCompatible(Type from, Type onto){

        switch(onto){
            case Any : return true;
            case None: return false;
            case Number,Boolean: return from == SpellSignal.Type.Number||from == SpellSignal.Type.Boolean;
        }

        return from==onto;
    }
}
