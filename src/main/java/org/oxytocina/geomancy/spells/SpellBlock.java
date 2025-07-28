package org.oxytocina.geomancy.spells;

import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class SpellBlock {
    public HashMap<String, SpellSignal> variables;
    public HashMap<String, SpellSignal> inputs;
    public HashMap<String, Parameter> parameters;
    public HashMap<String, SpellSignal> outputs;
    public boolean singleOutput;
    public SpellSignal output;
    public Identifier identifier;
    public Identifier hexFrontTexture;
    public Identifier hexBackTexture;
    public Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter;
    public Category category;
    public int defaultLootWeight;
    public int recipeRequiredProgress;
    public int recipeDifficulty;

    public BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function;
    public Consumer<SpellComponent> initFunction;
    public Consumer<SpellComponent> postFunction;

    public SpellBlockResult run(SpellComponent component,SpellBlockArgs arguments){
        return function.apply(component,arguments);
    }

    public void initRun(SpellComponent component){
        if(initFunction!=null) initFunction.accept(component);
    }

    public void postRun(SpellComponent component){
        if(postFunction!=null) postFunction.accept(component);
    }

    protected SpellBlock(Identifier identifier,
                  List<SpellSignal> inputs,
                  List<SpellSignal> outputs,
                  List<Parameter> parameters,
                  BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function,
                  Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter,
                         Consumer<SpellComponent> initFunction,
                         Consumer<SpellComponent> postFunction,
                  Category category,
                  int defaultLootWeight,
                  int recipeRequiredProgress,
                  int recipeDifficulty){
        this.identifier=identifier;
        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.variables = new HashMap<>();
        this.parameters = new HashMap<>();
        this.category=category;
        this.hexFrontTexture = Geomancy.locate("textures/gui/spells/"+identifier.getPath()+".png");
        this.hexBackTexture = Geomancy.locate("textures/gui/spellmaker_hex_bg_"+category.toString().toLowerCase()+".png");
        this.defaultLootWeight=defaultLootWeight;
        this.recipeRequiredProgress=recipeRequiredProgress;
        this.recipeDifficulty=recipeDifficulty;

        this.function=function;
        this.sideConfigGetter = sideConfigGetter;
        this.initFunction = initFunction;
        this.postFunction=postFunction;

        for(var v : inputs) {this.inputs.put(v.name,v);this.variables.put(v.name,v);}
        for(var v : outputs) {this.outputs.put(v.name,v);this.variables.put(v.name,v);}
        for(var v : parameters) {this.parameters.put(v.name,v);}

        this.singleOutput=outputs.size()==1;
        if(singleOutput) output=outputs.get(0);
    }

    public SpellBlock hexTex(String texture){
        this.hexFrontTexture =Geomancy.locate(texture);
        return this;
    }

    public SpellComponent.SideConfig[] getDefaultSideConfigs(SpellComponent component){
        return sideConfigGetter.apply(component);
    }

    public Parameter getParameter(String param) {
        if(parameters.containsKey(param)) return parameters.get(param);
        return null;
    }

    public Identifier getHexFrontTexture(){
        return hexFrontTexture;
    }

    public Identifier getHexBackTexture(){
        return hexBackTexture;
    }

    public static class Builder{

        final Identifier identifier;
        Category category;
        SpellSignal[] inputs;
        SpellSignal[] outputs;
        Parameter[] parameters;
        int defaultLootWeight = 100;
        public int recipeRequiredProgress = 100;
        public int recipeDifficulty = 20;

        public Function<SpellComponent,SpellComponent.SideConfig[]> sideConfigGetter;
        public BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> function;
        public Consumer<SpellComponent> initFunction;
        public Consumer<SpellComponent> postFunction;

        private Builder(Identifier identifier){
            this.identifier=identifier;
        }

        public static Builder create(Identifier identifier){
            return new Builder(identifier);
        }

        public static Builder create(String identifier){
            return create(Geomancy.locate(identifier));
        }

        public SpellBlock build(){

            if(inputs==null) inputs = new SpellSignal[0];
            if(outputs==null) outputs = new SpellSignal[0];
            if(parameters==null) parameters = new Parameter[0];

            return new SpellBlock(
                    identifier,
                    Arrays.stream(inputs).toList(),
                    Arrays.stream(outputs).toList(),
                    Arrays.stream(parameters).toList(),
                    function,
                    sideConfigGetter,
                    initFunction,
                    postFunction,
                    category,
                    defaultLootWeight,
                    recipeRequiredProgress,
                    recipeDifficulty
            );
        }

        public Builder category(Category category){
            this.category=category;
            return this;
        }

        public Builder defaultLootWeight(int defaultLootWeight){
            this.defaultLootWeight=defaultLootWeight;
            return this;
        }

        public Builder recipeRequiredProgress(int recipeRequiredProgress){
            this.recipeRequiredProgress=recipeRequiredProgress;
            return this;
        }

        public Builder recipeDifficulty(int recipeDifficulty){
            this.recipeDifficulty=recipeDifficulty;
            return this;
        }

        public Builder parameters(Parameter... parameters){
            this.parameters=parameters;
            return this;
        }

        public Builder inputs(SpellSignal... inputs){
            this.inputs=inputs;
            return this;
        }

        public Builder outputs(SpellSignal... outputs){
            this.outputs=outputs;
            return this;
        }

        public Builder init(Consumer<SpellComponent> initFunction){
            this.initFunction=initFunction;
            return this;
        }

        public Builder func(BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> func){
            this.function=func;
            return this;
        }

        public Builder post(Consumer<SpellComponent> postFunction){
            this.postFunction=postFunction;
            return this;
        }

        public Builder sideConfigGetter(Function<SpellComponent,SpellComponent.SideConfig[]> func){
            this.sideConfigGetter=func;
            return this;
        }
    }

    public static class SideUtil{

        public static SpellComponent.SideConfig[] sidesUniform(SpellComponent parent, SpellComponent.SideConfig.Mode[] modes,String varName){
            SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
            for (int i = 0; i < 6; i++) {
                res[i] = SpellComponent.SideConfig.create(parent,modes,SpellComponent.directions[i]);
                res[i].varName = varName;
            }
            return res;
        }

        public static SpellComponent.SideConfig[] sidesFreeform(SpellComponent parent){
            return sidesUniform(parent,new SpellComponent.SideConfig.Mode[]{
                    SpellComponent.SideConfig.Mode.Blocked,
                    SpellComponent.SideConfig.Mode.Input,
                    SpellComponent.SideConfig.Mode.Output
            },"");
        }

        public static SpellComponent.SideConfig[] sidesBlocked(SpellComponent parent){
            return sidesUniform(parent,new SpellComponent.SideConfig.Mode[]{
                    SpellComponent.SideConfig.Mode.Blocked},"");
        }

        public static SpellComponent.SideConfig[] sidesInput(SpellComponent parent,String varName){
            return sidesUniform(parent,new SpellComponent.SideConfig.Mode[]{
                    SpellComponent.SideConfig.Mode.Input,SpellComponent.SideConfig.Mode.Blocked},varName);
        }

        public static SpellComponent.SideConfig[] sidesOutput(SpellComponent parent,String varName){
            return sidesUniform(parent,new SpellComponent.SideConfig.Mode[]{
                    SpellComponent.SideConfig.Mode.Output,SpellComponent.SideConfig.Mode.Blocked},varName);
        }

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
            return new Parameter(Type.ConstantText,name,0,0,0,defaultText);
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
        Effector,
        Reference
    }
}
