package org.oxytocina.geomancy.spells;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Vector2i;
import org.oxytocina.geomancy.Geomancy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class SpellComponent {
    public SideConfig[] sideConfigs;

    public HashMap<String,SpellSignal> receivedSignals = new HashMap<>();
    public HashMap<String,ConfiguredParameter> configuredParameters = new HashMap<>();
    public HashMap<String,SpellComponent> neighbors = new HashMap<>();

    public SpellBlock function;

    public SpellGrid parent;
    public Vector2i position;

    public SpellContext context;

    public int rotation = 0;

    public HashMap<String,String> castClearedData = new HashMap<>();

    public SpellComponent(SpellGrid parent, Vector2i position, SpellBlock function,SideConfig[] sideConfigs,HashMap<String,ConfiguredParameter> configuredParameters){
        this.parent=parent;
        this.position=position;
        this.function=function;
        this.sideConfigs=sideConfigs;
        this.configuredParameters=configuredParameters;
    }

    public World world(){
        return context.caster.getWorld();
    }

    public LivingEntity caster(){
        return context.caster;
    }

    public SpellComponent(SpellGrid parent, Vector2i position, SpellBlock function){
        this.function=function;
        this.parent=parent;
        this.position=position;
        sideConfigs = function.getDefaultSideConfigs(this);

        for(var p : function.parameters.keySet()){
            configuredParameters.put(p,new ConfiguredParameter(function.parameters.get(p)));
        }
    }

    public SpellComponent(SpellGrid parent,NbtCompound nbt){
        this.parent = parent;
        readNbt(nbt);
    }

    public void preRunSetup(SpellContext context){
        this.context = context;
        castClearedData.clear();
        function.initRun(this);
    }

    public void run(){
        tryExecute();
    }

    public void postRun(){
        function.postRun(this);
    }

    public boolean tryAcceptSignalFrom(String dir, SpellSignal signal){
        String sideConfDir = mirrorDirection(dir);
        var conf = getSideConfig(sideConfDir);
        if(!conf.isInput()) return false;

        // check types
        if(!conf.canReceiveSignalOfType(function,signal.type)) {
            SpellBlocks.tryLogDebugWrongSignal(this,signal.type,conf.getSignalType(this.function));
            return false;
        }

        receiveSignal(signal.clone().named(conf.varName));
        return true;
    }

    public void receiveSignal(SpellSignal signal){
        if(hasSignal(signal.name)) return;

        receivedSignals.put(signal.name,signal);
        tryExecute();
    }

    public void pushSignals(HashMap<String,SpellSignal> signals){
        if(signals.isEmpty()) return;
        for (SideConfig sideConfig : sideConfigs) {
            if (!sideConfig.isOutput()) continue;
            if (!signals.containsKey(sideConfig.varName)) continue;
            pushSignal(sideConfig.dir, signals.get(sideConfig.varName));
        }
    }

    public void pushSignals(SpellBlockResult res){
        if(res.depth>context.depthLimit) return;
        res.refreshSignalDepths();
        pushSignals(res.vars);
    }

    public void pushSignal(String dir, SpellSignal signal){
        if(!hasNeighbor(dir)) return; // no neighbor to push to
        getNeighbor(dir).tryAcceptSignalFrom(dir,signal);
    }

    public void tryExecute(){
        if(!canExecute()) return;

        HashMap<String,SpellSignal> args = new HashMap<>();
        args.putAll(this.receivedSignals);

        // prevent endless loops
        int highestSignalDepth = context.baseDepth;
        for(var sig : this.receivedSignals.values())
            if(sig.getDepth() > highestSignalDepth)
                highestSignalDepth = sig.getDepth();
        context.highestRecordedDepth = Math.max(context.highestRecordedDepth,highestSignalDepth);

        if(highestSignalDepth>context.depthLimit){
            //SpellBlocks.tryLogDebugDepthLimitReached(this);
            return;
        }

        for(var param : configuredParameters.values())
        {
            var paramSig = param.getSignal();
            args.put(paramSig.name,paramSig);
        }

        var blockArgs = new SpellBlockArgs(args);
        var result = function.run(this,blockArgs);
        result.depth = highestSignalDepth+1;

        for (int i = 0; i < result.iterations; i++) {
            var iterationResult = result.clone();
            iterationResult.depth+=i;

            if(iterationResult.depth > context.depthLimit){
                context.depthLimitReached = true;
                //SpellBlocks.tryLogDebugDepthLimitReached(this);
                break;
            }

            iterationResult.add(result.iterationVarName,i);
            iterationResult.refreshSignalDepths();
            pushSignals(iterationResult.vars);
        }
        this.receivedSignals.clear();
    }

    // check if all required signals have been received
    public boolean canExecute(){
        if(context==null) return false;
        for(var varName : function.inputs.keySet()){
            if(!receivedSignals.containsKey(varName)) return false;
            var varObj = function.inputs.get(varName);
            var received = receivedSignals.get(varName);
            if(!SpellSignal.typesCompatible(received.type,varObj.type)) return false; // mismatching types in a signal
        }
        return true;
    }

    public SideConfig getSideConfig(String dir){
        return sideConfigs[getDirIndex(dir)];
    }

    public void setNeighbor(String dir, SpellComponent comp){
        neighbors.put(dir,comp);
    }

    public SpellComponent getNeighbor(String dir){
        return hasNeighbor(dir)?neighbors.get(dir):null;
    }

    public boolean hasNeighbor(String dir){
        return neighbors.containsKey(dir) && neighbors.get(dir) != null;
    }

    public void writeNbt(NbtCompound nbt){
        nbt.putInt("rotation",rotation);
        nbt.putString("func",function.identifier.toString());
        if(position!=null){
            nbt.putInt("x",position.x);
            nbt.putInt("y",position.y);
        }
        NbtList sidesNbt = new NbtList();
        for (var s : sideConfigs)
        {
            NbtCompound cComp = new NbtCompound();
            s.writeNbt(cComp);
            sidesNbt.add(cComp);
        }
        nbt.put("sides",sidesNbt);
        NbtList paramsNbt = new NbtList();
        for (var s : configuredParameters.values())
        {
            NbtCompound cComp = new NbtCompound();
            s.writeNbt(cComp);
            paramsNbt.add(cComp);
        }
        nbt.put("params",paramsNbt);
    }

    public void readNbt(NbtCompound nbt){
        rotation = nbt.getInt("rotation");
        function = SpellBlocks.get(nbt.getString("func"));
        if(
                nbt.contains("x",NbtElement.INT_TYPE) &&
                nbt.contains("y",NbtElement.INT_TYPE)
        )
            position = new Vector2i(
                nbt.getInt("x"),
                nbt.getInt("y")
                );
        else position=null;

        NbtList sidesNbt = nbt.getList("sides", NbtElement.COMPOUND_TYPE);
        sideConfigs = function.getDefaultSideConfigs(this);
        for (int i = 0; i < sidesNbt.size(); i++) {
            if(!(sidesNbt.get(i) instanceof NbtCompound comp)) {
                continue;
            }
            var conf = SideConfig.createFromNbt(this,comp);
            sideConfigs[getDirIndex(conf.dir)] = conf;
        }

        // read configured parameters
        NbtList paramsNbt = nbt.getList("params", NbtElement.COMPOUND_TYPE);
        configuredParameters.clear();
        var params = function.parameters;
        for (int i = 0; i < paramsNbt.size(); i++) {
            if(!(paramsNbt.get(i) instanceof NbtCompound comp)) {
                continue;
            }
            var param = ConfiguredParameter.fromNbt(function,comp);
            // ignore unwanted parameters
            if(param.parameter==null||!params.containsKey(param.parameter.name)) continue;
            configuredParameters.put(param.parameter.name,param);
        }
        // fill in missing parameters
        for (var param : params.keySet())
            if(!configuredParameters.containsKey(param))
                configuredParameters.put(param,new ConfiguredParameter(params.get(param)));

    }

    public void setParam(String param,float val){
        var p = getParam(param);
        if(p==null) return;
        p.setValue(val);
    }

    public void setParam(String param,String val){
        var p = getParam(param);
        if(p==null) return;
        p.setValue(val);
    }

    public void setParam(String param,boolean val){
        setParam(param,val?1:0);
    }

    public ConfiguredParameter getParam(String key){
        if(configuredParameters.containsKey(key)) return configuredParameters.get(key);
        return null;
    }

    // direction helpers

    public static String mirrorDirection(String dir){
        return rotateDirection(dir,3);
    }

    public final static String[] directions = new String[]{"ne","e","se","sw","w","nw"};

    public static String rotateDirection(String dir, int clockwiseTicks){
        int newIndex = (getDirIndex(dir)+clockwiseTicks)%directions.length;
        return directions[newIndex];
    }

    public static int getDirIndex(String dir){
        for (int i = 0; i < directions.length; i++) {
            if(Objects.equals(dir, directions[i])) return i;
        }
        return 0;
    }

    public static String getDir(int index){return directions[((index%6)+6)%6];}

    public boolean hasSignal(String name){
        return receivedSignals.containsKey(name);
    }

    public Identifier getHexFrontTexture(){
        return function.getHexFrontTexture();
    }

    public Identifier getHexBackTexture(){
        return function.getHexBackTexture();
    }

    /// rotates side configs clockwise by specified amount
    public void rotate(int amount){
        rotation = (rotation-(amount%6)+6)%6;

        SideConfig[] newConfigs = new SideConfig[6];
        for(int i = 0; i < 6; i++)
        {
            newConfigs[i] = sideConfigs[(i-(amount%6)+6)%6];
            newConfigs[i].dir = getDir(i);
        }
        sideConfigs = newConfigs;
    }

    public boolean canAcceptParam(String paramName, String val){
        return getParam(paramName).canAccept(val);
    }

    public void setAndParseParam(String paramName, String val){
        getParam(paramName).setParsed(val);
    }

    public ItemStack getItemStack() {
        return function.getItemStack();
    }

    public static class SideConfig{
        public String dir;
        public String varName;
        public int selectedMode = 0;
        public ArrayList<Mode> modes;
        public SpellComponent parent;

        private SideConfig(SpellComponent parent, NbtCompound nbt){
            this.parent=parent;
            readNbt(nbt);
            modes = parent.function.getDefaultSideConfigs(parent)[
                    (getDirIndex(dir)+parent.rotation+6)%6
                    ].modes;
            sanityCheckVarName();
        }

        public SideConfig(SpellComponent parent,String dir, String varName, Mode[] modes){
            this.parent=parent;
            this.dir=dir;
            this.varName=varName;
            this.modes=new ArrayList<>();
            this.modes.addAll(Arrays.asList(modes));
        }

        public static SideConfig create(SpellComponent parent, Mode[] modes,String dir){
            return new SideConfig(parent,dir,"",modes);
        }

        public static SideConfig createBlocked(SpellComponent parent, String dir){
            return new SideConfig(parent,dir,"",new Mode[]{Mode.Blocked});
        }

        public static SideConfig createOutput(SpellComponent parent, String dir){
            return new SideConfig(parent,dir,"",new Mode[]{Mode.Output});
        }

        public static SideConfig createToggleableOutput(SpellComponent parent, String dir){
            return new SideConfig(parent,dir,"",new Mode[]{Mode.Output,Mode.Blocked});
        }

        public static SideConfig createInput(SpellComponent parent, String dir){
            return new SideConfig(parent,dir,"",new Mode[]{Mode.Input});
        }

        public static SideConfig createToggleableInput(SpellComponent parent, String dir){
            return new SideConfig(parent,dir,"",new Mode[]{Mode.Input,Mode.Blocked});
        }

        public static SideConfig createFreeform(SpellComponent parent, String dir){
            return new SideConfig(parent,dir,"",new Mode[]{Mode.Input,Mode.Output,Mode.Blocked});
        }

        public static SideConfig createSingle(SpellComponent parent, Mode mode, String dir){
            return new SideConfig(parent,dir,"",new Mode[]{mode});
        }

        public static SideConfig createFromNbt(SpellComponent parent, NbtCompound nbt) {
            return new SideConfig(parent,nbt);
        }

        public boolean isOutput(){
            return activeMode() == Mode.Output;
        }

        public boolean isInput(){
            return activeMode() == Mode.Input;
        }

        public void setMode(Mode mode){
            setMode(modes.indexOf(mode));
        }

        public void setMode(int index){
            if(selectedMode==index) return;
            selectedMode=index;
            // set variable to fitting one
            sanityCheckVarName();
        }

        public Mode activeMode(){
            return modes.get(selectedMode);
        }

        public SpellSignal.Type getSignalType(SpellBlock func){
            if(func==null||func.inputs==null||!func.inputs.containsKey(varName)) return SpellSignal.Type.None;
            return func.inputs.get(varName).type;
        }

        public boolean canReceiveSignalOfType(SpellBlock block, SpellSignal.Type type){
            if(!block.inputs.containsKey(varName)) return false;
            SpellSignal.Type ownType = block.inputs.get(varName).type;

            return SpellSignal.typesCompatible(type,ownType);
        }

        public void writeNbt(NbtCompound nbt){
            nbt.putString("dir",dir);
            nbt.putString("var",varName);
            nbt.putInt("mode",selectedMode);
        }

        public void readNbt(NbtCompound nbt){
            dir = nbt.getString("dir");
            varName = nbt.getString("var");
            selectedMode = nbt.getInt("mode");
        }

        public SideConfig named(String varName){
            this.varName=varName;
            return this;
        }

        public SideConfig disabled(){
            setMode(Mode.Blocked);
            return this;
        }

        public Identifier getTexture(){
            if(activeMode()==Mode.Blocked) return null;
            return Geomancy.locate("textures/gui/spells/"+(activeMode()==Mode.Input?"in":"out")+"_"+dir+".png");
        }

        public SpellSignal getSignal(SpellComponent component){
            return switch (activeMode()) {
                case Blocked -> SpellSignal.createNone();
                case Input -> component.function.inputs.get(varName);
                case Output -> component.function.outputs.get(varName);
            };
        }

        private void sanityCheckVarName() {
            switch(activeMode()){
                case Input:
                    if(parent.function.inputs.containsKey(varName)) return;
                    Geomancy.logError("variable name sanity check failed! in: "+varName+", parent: "+parent.function.identifier.toString()+", side: "+dir);
                    setVar(
                            parent.function.inputs.keySet().stream().findFirst().orElse(""));
                    return;
                case Output:
                    if(parent.function.outputs.containsKey(varName)) return;
                    Geomancy.logError("variable name sanity check failed! out: "+varName+", parent: "+parent.function.identifier.toString()+", side: "+dir);
                    setVar(parent.function.outputs.keySet().stream().findFirst().orElse(""));
                    return;
                case Blocked: varName="";return;
            }
        }

        public void setVar(String newVar){
            varName=newVar;
        }

        public void setShaderColor(){
            switch(activeMode())
            {
                case Input:
                    RenderSystem.setShaderColor(0,1,0,1); break;
                case Output:
                    RenderSystem.setShaderColor(1,0,0,1); break;
            }
        }

        public enum Mode {
            Blocked,
            Input,
            Output
        }
    }

    public static class ConfiguredParameter{
        public SpellSignal signal;
        public SpellBlock.Parameter parameter;

        public ConfiguredParameter(SpellBlock.Parameter base){
            this.parameter=base;
            setSignal(base.getDefaultSignal());
        }

        public SpellSignal getSignal(){
            return signal;
        }
        public void setSignal(SpellSignal signal){
            this.signal=signal;
        }

        public void setValue(float val){
            signal.numberValue=val;
        }

        public void setValue(String val){
            signal.textValue=val;
        }

        public void writeNbt(NbtCompound nbt){
            nbt.putString("param",parameter.name);
            NbtCompound sigComp = new NbtCompound();
            signal.writeNbt(sigComp);
            nbt.put("signal",sigComp);
        }

        private ConfiguredParameter(){}
        public static ConfiguredParameter fromNbt(SpellBlock parent, NbtCompound nbt){
            ConfiguredParameter res = new ConfiguredParameter();
            res.readNbt(parent,nbt);
            return res;
        }

        public void readNbt(SpellBlock parent, NbtCompound nbt){
            this.parameter = parent.getParameter(nbt.getString("param"));
            setSignal(SpellSignal.fromNBT(nbt.getCompound("signal")));
        }

        public boolean canAccept(String val){
            switch(parameter.type){
                case ConstantText: return true;
                case ConstantNumber:
                case ConstantBoolean:
                    try{
                        Float.parseFloat(val);
                        return true;
                    }
                    catch(Exception e){
                        return false;
                    }
            }
            return false;
        }

        public void setParsed(String val){
            switch(parameter.type){
                case ConstantText: setValue(val); break;
                case ConstantNumber:
                case ConstantBoolean:
                    try{
                        setValue(Float.parseFloat(val));
                    }
                    catch(Exception ignored){

                    }
                    break;
            }
        }
    }

    public SpellComponent clone(){
        return new SpellComponent(parent,position,function,sideConfigs,configuredParameters);
    }

    public MutableText getRuntimeName(){
        return Text.translatable("geomancy.spellcomponent."+this.function.identifier.getPath()).formatted(Formatting.DARK_AQUA)
                .append(Text.literal(" ["+context.grid.getRuntimeName(context).getString()+":"+position.x+","+position.y+"]").formatted(Formatting.DARK_GRAY));
    }
}
