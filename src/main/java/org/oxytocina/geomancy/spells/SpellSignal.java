package org.oxytocina.geomancy.spells;

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

    public static SpellSignal createNumber(float defaultValue) {
        return new SpellSignal(Type.Number,"num",defaultValue,"",null);
    }

    public static SpellSignal createAny() {
        return new SpellSignal(Type.Any,"any",0,"",null);
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
}
