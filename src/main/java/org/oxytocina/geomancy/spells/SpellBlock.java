package org.oxytocina.geomancy.spells;

import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class SpellBlock {
    public HashMap<String, SpellSignal> variables;
    public HashMap<String, SpellSignal> inputs;
    public HashMap<String, Parameter> parameters;
    public HashMap<String, SpellSignal> outputs;
    public boolean singleOutput;
    public SpellSignal output;
    public Identifier identifier;
    public Identifier hexTexture;
    public Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter;
    public Category category;

    public BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function;

    public SpellBlockResult run(SpellComponent component,SpellBlockArgs arguments){
        return function.apply(component,arguments);
    }


    public static SpellBlock create(String identifier,
                                    SpellSignal[] inputs,
                                    SpellSignal[] outputs,
                                    Parameter[] parameters,
                                    BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function,
                                    Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter)
    {
        return new SpellBlock(Geomancy.locate(identifier), Arrays.stream(inputs).toList(), Arrays.stream(outputs).toList(), Arrays.stream(parameters).toList(),function,sideConfigGetter);
    }

    public static SpellBlock create(String identifier,
                                    SpellSignal[] inputs,
                                    SpellSignal output,
                                    Parameter[] parameters,
                                    BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function,
                                    Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter)
    {
        return create(identifier,inputs,new SpellSignal[]{output},parameters,function,sideConfigGetter);
    }

    public static SpellBlock create(String identifier,
                                    SpellSignal[] inputs,
                                    SpellSignal output,
                                    BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function,
                                    Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter)
    {
        return create(identifier,inputs,new SpellSignal[]{output},new Parameter[]{},function,sideConfigGetter);
    }

    public static SpellBlock create(String identifier,
                                    SpellSignal[] inputs,
                                    Parameter[] parameters,
                                    BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function,
                                    Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter)
    {
        return create(identifier,inputs,new SpellSignal[]{},parameters,function,sideConfigGetter);
    }

    public static SpellBlock create(String identifier,
                                    SpellSignal[] inputs,
                                    BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function,
                                    Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter)
    {
        return create(identifier,inputs,new SpellSignal[]{},new Parameter[]{},function,sideConfigGetter);
    }

    public SpellBlock(Identifier identifier,
                      List<SpellSignal> inputs,
                      List<SpellSignal> outputs,
                      List<Parameter> parameters,
                      BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function,
                      Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter){
        this.identifier=identifier;
        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.variables = new HashMap<>();
        this.parameters = new HashMap<>();
        this.hexTexture = Geomancy.locate("textures/gui/spells/"+identifier.getPath()+".png");

        this.function=function;
        this.sideConfigGetter = sideConfigGetter;

        for(var v : inputs) {this.inputs.put(v.name,v);this.variables.put(v.name,v);}
        for(var v : outputs) {this.outputs.put(v.name,v);this.variables.put(v.name,v);}
        for(var v : parameters) {this.parameters.put(v.name,v);}

        this.singleOutput=outputs.size()==1;
        if(singleOutput) output=outputs.get(0);
    }

    public SpellBlock category(Category category){
        this.category=category;
        return this;
    }

    public SpellBlock hexTex(String texture){
        this.hexTexture=Geomancy.locate(texture);
        return this;
    }

    public SpellComponent.SideConfig[] getDefaultSideConfigs(SpellComponent component){
        return sideConfigGetter.apply(component);
    }

    public static SpellComponent.SideConfig[] sidesUniform(SpellComponent parent, SpellComponent.SideConfig.Mode mode,String varName){
        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
        for (int i = 0; i < 6; i++) {
            res[i] = SpellComponent.SideConfig.createSingle(parent,mode,SpellComponent.directions[i]);
            res[i].varName = varName;
        }
        return res;
    }

    public static SpellComponent.SideConfig[] sidesBlocked(SpellComponent parent){
        return sidesUniform(parent,SpellComponent.SideConfig.Mode.Blocked,"");
    }

    public static SpellComponent.SideConfig[] sidesInput(SpellComponent parent,String varName){
        return sidesUniform(parent,SpellComponent.SideConfig.Mode.Input,varName);
    }

    public static SpellComponent.SideConfig[] sidesOutput(SpellComponent parent,String varName){
        return sidesUniform(parent,SpellComponent.SideConfig.Mode.Output,varName);
    }

    public Parameter getParameter(String param) {
        if(parameters.containsKey(param)) return parameters.get(param);
        return null;
    }

    public Identifier getHexTexture(){
        return hexTexture;
    }

    public static class Parameter{

        private Parameter(Type type,String name, float defaultValue, float min, float max, String text){
            this.name = name;
            this.type=type;
            this.defaultValue = defaultValue;
            this.minimum=min;
            this.maximum=max;
            this.defaultText=text;
        }

        public static Parameter createNumber(String name, float defaultValue,float min, float max){
            return new Parameter(Type.ConstantNumber,name,defaultValue,min,max,"");
        }

        public static Parameter createText(String name, String defaultText){
            return new Parameter(Type.ConstantText,name,0,0,0,"defaultText");
        }

        public static Parameter createBoolean(String name, boolean defaultValue){
            return new Parameter(Type.ConstantBoolean,name,defaultValue?1:0,0,1,"");
        }

        public SpellSignal getDefaultSignal(){
            switch (type)
            {
                case ConstantBoolean: return SpellSignal.createBoolean(defaultValue).named(name);
                case ConstantNumber: return SpellSignal.createNumber(defaultValue).named(name);
                case ConstantText: return SpellSignal.createText(defaultText).named(name);
            }
            return SpellSignal.createNone().named(name);
        }

        public Type type;
        public String name;

        // for numbers and booleans
        public float defaultValue;
        public float minimum;
        public float maximum;

        // for text
        public String defaultText;

        public enum Type{
            ConstantNumber,
            ConstantText,
            ConstantBoolean,
        }
    }

    public enum Category{
        FlowControl,
        Provider,
        Arithmetic,
        Effector
    }
}
