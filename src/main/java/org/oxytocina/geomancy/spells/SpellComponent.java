package org.oxytocina.geomancy.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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

    public ItemStack castingItem;
    public ItemStack spellStorageItem;
    public SpellGrid parent;
    public Vector2i position;
    public LivingEntity casterEntity;

    public SpellComponent(SpellGrid parent, Vector2i position, SpellBlock function){
        this.function=function;
        this.parent=parent;
        this.position=position;
        sideConfigs = function.getDefaultSideConfigs();
    }

    public void run(ItemStack casterItem, ItemStack containerItem, LivingEntity casterEntity){
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
        if(conf.getSignalType(function) != signal.type) return false;

        receiveSignal(signal);
        return true;
    }

    public void receiveSignal(SpellSignal signal){
        if(hasSignal(signal.name)) return;

        receivedSignals.put(signal.name,signal);
        tryExecute();
    }

    public void pushSignals(HashMap<String,SpellSignal> signals){
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

        var results = function.run(this,args);

        pushSignals(results);
    }

    // check if all required signals have been received
    public boolean canExecute(){
        for(var varName : function.inputs.keySet()){
            if(!receivedSignals.containsKey(varName)) return false;
            var varObj = function.inputs.get(varName);
            var received = receivedSignals.get(varName);
            if(varObj.type != received.type) return false; // mismatching types in a signal
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
        return neighbors.containsKey(dir);
    }

    public void writeNbt(NbtCompound nbt){
        NbtList sidesNbt = new NbtList();
        for (var s : sideConfigs)
        {
            NbtCompound cComp = new NbtCompound();
            s.writeNbt(cComp);
            sidesNbt.add(cComp);
        }
        nbt.put("sides",sidesNbt);
    }

    public void readNbt(NbtCompound nbt){
        NbtList sidesNbt = nbt.getList("sides", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < 6; i++) {
            sideConfigs[i] = SideConfig.createFromNbt(i,sidesNbt.get(i).);
        }
        for (var s : sidesNbt)
        {

            NbtCompound cComp = new NbtCompound();
            s.writeNbt(cComp);
            sidesNbt.add(cComp);
        }
        nbt.put("sides",sidesNbt);
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

        public SideConfig(String dir, String varName, Mode[] modes){
            this.dir=dir;
            this.varName=varName;
            this.modes=new ArrayList<>();
            this.modes.addAll(Arrays.asList(modes));
        }

        public static SideConfig createBlocked(String dir){
            return new SideConfig(dir,"",new Mode[]{Mode.Blocked});
        }

        public boolean isOutput(){
            return activeMode() == Mode.Output;
        }

        public boolean isInput(){
            return activeMode() == Mode.Input;
        }

        public Mode activeMode(){
            return modes.get(selectedMode);
        }

        public SpellSignal.Type getSignalType(SpellBlock func){
            if(func==null||func.inputs==null||!func.inputs.containsKey(varName)) return SpellSignal.Type.None;
            return func.inputs.get(varName).type;
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
        }

        public SpellSignal getSignal(){
            return signal;
        }
    }
}
