package org.oxytocina.geomancy.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;
import org.joml.Vector2i;

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

    public ItemStack castingItem;
    public ItemStack spellStorageItem;
    public LivingEntity casterEntity;
    public World world;

    public SpellComponent(SpellGrid parent, Vector2i position, SpellBlock function){
        this.function=function;
        this.parent=parent;
        this.position=position;
        sideConfigs = function.getDefaultSideConfigs();

        for(var p : function.parameters.keySet()){
            configuredParameters.put(p,new ConfiguredParameter(function.parameters.get(p)));
        }
    }

    public SpellComponent(SpellGrid parent,NbtCompound nbt){
        this.parent = parent;
        readNbt(nbt);
    }

    public void preRunSetup(ItemStack casterItem, ItemStack containerItem, LivingEntity casterEntity){
        this.castingItem=casterItem;
        this.spellStorageItem=containerItem;
        this.casterEntity=casterEntity;
        this.world=casterEntity.getWorld();
    }

    public void run(){
        onSpellInit();
    }

    // used for constant providers and data getters
    public void onSpellInit(){
        tryExecute();
    }

    public boolean tryAcceptSignalFrom(String dir, SpellSignal signal){
        String sideConfDir = mirrorDirection(dir);
        var conf = getSideConfig(sideConfDir);
        if(!conf.isInput()) return false;

        // check types
        if(!conf.canReceiveSignalOfType(function,signal.type)) return false;

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

    public void pushSignal(String dir, SpellSignal signal){
        if(!hasNeighbor(dir)) return; // no neighbor to push to
        getNeighbor(dir).tryAcceptSignalFrom(dir,signal);
    }

    public void tryExecute(){
        if(!canExecute()) return;

        HashMap<String,SpellSignal> args = new HashMap<>();
        args.putAll(this.receivedSignals);
        for(var param : configuredParameters.values())
        {
            var paramSig = param.getSignal();
            args.put(paramSig.name,paramSig);
        }

        var result = function.run(this,new SpellBlockArgs(args));
        for (int i = 0; i < result.iterations; i++) {
            pushSignals(result.vars);
        }
        this.receivedSignals.clear();
    }

    // check if all required signals have been received
    public boolean canExecute(){
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
        sideConfigs = new SideConfig[6];
        for (int i = 0; i < 6; i++) {
            if(!(sidesNbt.get(i) instanceof NbtCompound comp)) {
                sideConfigs[i] = function.getDefaultSideConfigs()[i];
                continue;
            }
            sideConfigs[i] = SideConfig.createFromNbt(this,comp);
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
            if(!params.containsKey(param.parameter.name)) continue;
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

    public static class SideConfig{
        public String dir;
        public String varName;
        public int selectedMode = 0;
        public ArrayList<Mode> modes;

        private SideConfig(SpellComponent parent, NbtCompound nbt){
            readNbt(nbt);
            modes = parent.function.getDefaultSideConfigs()[getDirIndex(dir)].modes;
        }

        public SideConfig(String dir, String varName, Mode[] modes){
            this.dir=dir;
            this.varName=varName;
            this.modes=new ArrayList<>();
            this.modes.addAll(Arrays.asList(modes));
        }

        public static SideConfig create(Mode[] modes,String dir){
            return new SideConfig(dir,"",modes);
        }

        public static SideConfig createBlocked(String dir){
            return new SideConfig(dir,"",new Mode[]{Mode.Blocked});
        }

        public static SideConfig createOutput(String dir){
            return new SideConfig(dir,"",new Mode[]{Mode.Output});
        }

        public static SideConfig createInput(String dir){
            return new SideConfig(dir,"",new Mode[]{Mode.Input});
        }

        public static SideConfig createSingle(Mode mode, String dir){
            return new SideConfig(dir,"",new Mode[]{mode});
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
            if(modes.contains(mode)) selectedMode = modes.indexOf(mode);
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
    }
}
