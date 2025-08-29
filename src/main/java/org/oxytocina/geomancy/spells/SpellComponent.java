package org.oxytocina.geomancy.spells;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.util.ByteUtil;

import java.nio.charset.Charset;
import java.util.*;

public class SpellComponent {
    /// 0: no binary serialization, just nbt, human-readable
    /// 1: nbt, shortened names, saves space
    /// 100+: binary serialization, saves space
    public static final int CURRENT_DATA_FORMAT_VERSION = 1;

    public SideConfig[] sideConfigs;

    public HashMap<String,SpellSignal> receivedSignals = new HashMap<>();
    public HashMap<String,ConfiguredParameter> configuredParameters = new HashMap<>();
    public HashMap<Byte,SpellComponent> neighbors = new HashMap<>();

    public SpellBlock function;

    public SpellGrid parent;
    public Vector2i position;

    public SpellContext context;

    public byte rotation = 0;

    public HashMap<String,String> castClearedData = new HashMap<>();

    public SpellComponent(SpellGrid parent, Vector2i position, SpellBlock function,SideConfig[] sideConfigs,HashMap<String,ConfiguredParameter> configuredParameters){
        this.parent=parent;
        this.position=position;
        this.function=function;
        this.sideConfigs=sideConfigs;
        this.configuredParameters=configuredParameters;
    }

    public @NotNull World world(){
        return context.getWorld();
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

    private SpellComponent(SpellGrid parent, PacketByteBuf buf){
        this.parent=parent;
        deserialize(buf);
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

    public boolean tryAcceptSignalFrom(byte dir, SpellSignal signal){
        byte sideConfDir = mirrorDirection(dir);
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

    public void pushSignal(byte dir, SpellSignal signal){
        if(!hasNeighbor(dir)) return; // no neighbor to push to
        getNeighbor(dir).tryAcceptSignalFrom(dir,signal);
    }

    public void tryExecute(){
        if(!canExecute()) return;
        if(context.timedOut()) return;

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

            // sub results
            if(iterationResult.subResults!=null){
                for(var subRes : iterationResult.subResults){
                    pushSignals(subRes.vars);
                }
            }
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

    public SideConfig getSideConfig(byte dir){
        return sideConfigs[ByteUtil.byteToInt(dir)];
    }

    public void setNeighbor(byte dir, SpellComponent comp){
        neighbors.put(dir,comp);
    }

    public SpellComponent getNeighbor(byte dir){
        return hasNeighbor(dir)?neighbors.get(dir):null;
    }

    public boolean hasNeighbor(byte dir){
        return neighbors.containsKey(dir) && neighbors.get(dir) != null;
    }

    public PacketByteBuf serialize(){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(CURRENT_DATA_FORMAT_VERSION);

        buf.writeByte(rotation);
        buf.writeIdentifier(function.identifier);
        buf.writeBoolean(position!=null);
        if(position!=null){
            buf.writeInt(position.x);
            buf.writeInt(position.y);
        }
        buf.writeInt(sideConfigs.length);
        for (var s : sideConfigs)
        {
            s.serialize(buf);
        }

        buf.writeInt(configuredParameters.size());
        for (var s : configuredParameters.values())
        {
            s.serialize(buf);
        }

        return buf;
    }

    public static SpellComponent deserialize(SpellGrid parent, PacketByteBuf buf){
        return new SpellComponent(parent,buf);
    }

    public void deserialize(PacketByteBuf buf){
        int formatVersion = buf.readInt();

        rotation = buf.readByte();
        function = SpellBlocks.get(buf.readIdentifier());
        sideConfigs = function.getDefaultSideConfigs(this);

        boolean hasPos = buf.readBoolean();
        if(hasPos){
            position = new Vector2i(
                    buf.readInt(),
                    buf.readInt()
            );
        }

        int confLength = buf.readInt();
        for (int i = 0; i < confLength; i++) {
            sideConfigs[i].deserialize(buf);
        }

        int paramsLength = buf.readInt();
        for (int i = 0; i < paramsLength; i++)
        {
            ConfiguredParameter param = ConfiguredParameter.deserialize(function,buf);
            configuredParameters.put(param.parameter.name,param);
        }
    }

    public void writeNbt(NbtCompound nbt){
        // experimental serialization
        if(CURRENT_DATA_FORMAT_VERSION>=100){
            nbt.putString("data",ByteUtil.bufToString(serialize()));
            return;
        }

        nbt.putInt("v",CURRENT_DATA_FORMAT_VERSION);

        String rotKey = CURRENT_DATA_FORMAT_VERSION>=1?"r":"rotation";
        String funcKey = CURRENT_DATA_FORMAT_VERSION>=1?"f":"func";
        String sidesKey = CURRENT_DATA_FORMAT_VERSION>=1?"s":"sides";
        String paramsKey = CURRENT_DATA_FORMAT_VERSION>=1?"p":"params";

        if(rotation!=0) nbt.putInt(rotKey,rotation);
        nbt.putString(funcKey,function.identifier.toString());
        if(position!=null){
            nbt.putInt("x",position.x);
            nbt.putInt("y",position.y);
        }
        NbtList sidesNbt = new NbtList();
        var defaultSides = function.getDefaultSideConfigs(this);
        for (int i = 0; i < 6; i++) {
            // dont write default configs
            var s = sideConfigs[i];
            if(s.equals(defaultSides[i])) continue;

            NbtCompound cComp = new NbtCompound();
            s.writeNbt(cComp,CURRENT_DATA_FORMAT_VERSION);
            sidesNbt.add(cComp);
        }
        if(!sidesNbt.isEmpty()) nbt.put(sidesKey,sidesNbt);
        NbtList paramsNbt = new NbtList();
        for (var s : configuredParameters.values())
        {
            NbtCompound cComp = new NbtCompound();
            s.writeNbt(cComp,CURRENT_DATA_FORMAT_VERSION);
            paramsNbt.add(cComp);
        }
        if(!paramsNbt.isEmpty()) nbt.put(paramsKey,paramsNbt);
    }

    public void readNbt(NbtCompound nbt){

        // experimental deserialization
        if(nbt.contains("data") && CURRENT_DATA_FORMAT_VERSION>=100){
            deserialize(ByteUtil.stringToBuf(nbt.getString("data")));
            return;
        }

        int version = nbt.getInt("v");

        String rotKey = version>=1?"r":"rotation";
        String funcKey = version>=1?"f":"func";
        String sidesKey = version>=1?"s":"sides";
        String paramsKey = version>=1?"p":"params";

        rotation = nbt.contains(rotKey) ? ByteUtil.intToByte(nbt.getInt(rotKey)) : 0;
        function = SpellBlocks.get(nbt.getString(funcKey));
        if(
                nbt.contains("x",NbtElement.INT_TYPE) &&
                nbt.contains("y",NbtElement.INT_TYPE)
        )
            position = new Vector2i(
                nbt.getInt("x"),
                nbt.getInt("y")
                );
        else position=null;

        NbtList sidesNbt = nbt.contains(sidesKey) ? nbt.getList(sidesKey, NbtElement.COMPOUND_TYPE) : new NbtList();
        sideConfigs = function.getDefaultSideConfigs(this);
        for (int i = 0; i < sidesNbt.size(); i++) {
            if(!(sidesNbt.get(i) instanceof NbtCompound comp)) {
                continue;
            }
            var conf = SideConfig.createFromNbt(this,comp,version);
            sideConfigs[ByteUtil.byteToInt(conf.dir)] = conf;
        }

        // read configured parameters
        NbtList paramsNbt = nbt.contains(paramsKey) ? nbt.getList(paramsKey, NbtElement.COMPOUND_TYPE) : new NbtList();
        configuredParameters.clear();
        var params = function.parameters;
        for (int i = 0; i < paramsNbt.size(); i++) {
            if(!(paramsNbt.get(i) instanceof NbtCompound comp)) {
                continue;
            }
            var param = ConfiguredParameter.fromNbt(function,comp,version);
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

    public static byte mirrorDirection(byte dir){
        return rotateDirection(dir,3);
    }

    public final static String[] directions = new String[]{"ne","e","se","sw","w","nw"};

    public static byte rotateDirection(byte dir, int clockwiseTicks){
        return ByteUtil.intToByte((ByteUtil.byteToInt(dir)+clockwiseTicks)%directions.length);
    }

    public static int getDirIndex(String dir){
        for (int i = 0; i < directions.length; i++) {
            if(Objects.equals(dir, directions[i])) return i;
        }
        return 0;
    }

    public static String getDirString(int index){return directions[((index%6)+6)%6];}

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
        rotation = ByteUtil.intToByte((ByteUtil.byteToInt(rotation)-(amount%6)+6)%6);

        SideConfig[] newConfigs = new SideConfig[6];
        for(int i = 0; i < 6; i++)
        {
            newConfigs[i] = sideConfigs[(i-(amount%6)+6)%6];
            newConfigs[i].dir = ByteUtil.intToByte(i);
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
        public byte dir;
        public String varName;
        public byte selectedMode = 0;
        public ArrayList<Mode> modes;
        public SpellComponent parent;

        private SideConfig(SpellComponent parent, NbtCompound nbt, int version){
            this.parent=parent;
            readNbt(nbt,version);
            modes = parent.function.getDefaultSideConfigs(parent)[
                    (ByteUtil.byteToInt(dir)+parent.rotation+6)%6
                    ].modes;
            sanityCheckVarName(false);
        }

        public SideConfig(SpellComponent parent,String dir, String varName, Mode[] modes){
            this.parent=parent;
            this.dir=ByteUtil.intToByte(getDirIndex(dir));
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

        public static SideConfig createFromNbt(SpellComponent parent, NbtCompound nbt, int version) {
            return new SideConfig(parent,nbt,version);
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

        public void setMode(int index){setMode(ByteUtil.intToByte(index));}

        public void setMode(byte index){
            if(selectedMode==index) return;
            selectedMode=index;
            // set variable to fitting one
            sanityCheckVarName(false);
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

        public void serialize(PacketByteBuf buf) {
            buf.writeByte(dir);
            buf.writeByte(selectedMode);
            buf.writeString(varName);
        }

        public void deserialize(PacketByteBuf buf) {
            dir=buf.readByte();
            selectedMode=buf.readByte();
            varName=buf.readString();
        }

        public void writeNbt(NbtCompound nbt,int version){

            String dKey = version>=1?"d":"dir";
            String vKey = version>=1?"v":"var";
            String mKey = version>=1?"m":"mode";

            if(dir!=0)nbt.putByte(dKey,dir);
            if(varName!=null&&!varName.isEmpty()) nbt.putString(vKey,varName);
            if(selectedMode!=0) nbt.putByte(mKey,selectedMode);
        }

        public String dirString() {
            return directions[ByteUtil.byteToInt(dir)];
        }

        public void readNbt(NbtCompound nbt,int version){

            String dKey = version>=1?"d":"dir";
            String vKey = version>=1?"v":"var";
            String mKey = version>=1?"m":"mode";

            if(version<1)
            {
                setDirFromString(nbt.getString(dKey));
                varName = nbt.getString(vKey);
                selectedMode = ByteUtil.intToByte(nbt.getInt(mKey));
            }
            else{
                dir=nbt.contains(dKey)?nbt.getByte(dKey):0;
                varName = nbt.contains(vKey)?nbt.getString(vKey):"";
                selectedMode = nbt.contains(mKey)?ByteUtil.intToByte(nbt.getByte(mKey)):0;
            }

        }

        public void setDirFromString(String dirString) {
            dir=ByteUtil.intToByte(getDirIndex(dirString));
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
            return Geomancy.locate("textures/gui/spells/"+(activeMode()==Mode.Input?"in":"out")+"_"+getDirString(dir)+".png");
        }

        public SpellSignal getSignal(SpellComponent component){
            var res = switch (activeMode()) {
                case Blocked -> SpellSignal.createNone();
                case Input -> component.function.inputs.get(varName);
                case Output -> component.function.outputs.get(varName);
            };
            if(res==null){
                // something fishy is going on...
                sanityCheckVarName(true);
                return SpellSignal.createNone();
            }
            return res;
        }

        private void sanityCheckVarName(boolean reportErrors) {
            switch(activeMode()){
                case Input:
                    if(parent.function.inputs.containsKey(varName)) return;
                    if(reportErrors)
                        Geomancy.logError("variable name sanity check failed! in: "+varName+", parent: "+parent.function.identifier.toString()+", side: "+dir);
                    setVar(
                            parent.function.inputs.keySet().stream().findFirst().orElse(""));
                    return;
                case Output:
                    if(parent.function.outputs.containsKey(varName)) return;
                    if(reportErrors)
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

        public boolean equals(SideConfig o){
            return
                    Objects.equals(o.varName, varName)
                    && dir==o.dir
                    && this.selectedMode==o.selectedMode
                    && modesEqual(this.modes,o.modes)
                    ;
        }
        public static boolean modesEqual(List<Mode> a, List<Mode> b){
            if(a==null&&b==null) return true;
            if(a==null||b==null) return false;
            if(a.size()!=b.size()) return false;
            for (int i = 0; i < a.size(); i++) {
                if(!a.get(i).equals(b.get(i))) return false;
            }
            return true;
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

        public void serialize(PacketByteBuf buf) {
            buf.writeString(parameter.name);
            signal.serialize(buf);
        }

        public static ConfiguredParameter deserialize(SpellBlock parent, PacketByteBuf buf) {
            String name = buf.readString();
            var res = new ConfiguredParameter(parent.getParameter(name));
            res.setSignal(SpellSignal.deserialize(buf));
            return res;
        }

        public void writeNbt(NbtCompound nbt, int v){

            String pKey = v>=1?"p":"param";
            String sKey = v>=1?"s":"signal";

            nbt.putString(pKey,parameter.name);
            NbtCompound sigComp = new NbtCompound();
            signal.writeNbt(sigComp,v,false);
            nbt.put(sKey,sigComp);
        }

        private ConfiguredParameter(){}
        public static ConfiguredParameter fromNbt(SpellBlock parent, NbtCompound nbt, int v){
            ConfiguredParameter res = new ConfiguredParameter();
            res.readNbt(parent,nbt,v);
            return res;
        }

        public void readNbt(SpellBlock parent, NbtCompound nbt, int v){

            String pKey = v>=1?"p":"param";
            String sKey = v>=1?"s":"signal";

            this.parameter = parent.getParameter(nbt.getString(pKey));
            setSignal(SpellSignal.fromNBT(nbt.getCompound(sKey),v).named(nbt.getString(pKey)));
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

    public static Builder builder(SpellBlock func){return new Builder(func);}
    public static Builder.ConfBuilder confBuilder(String dir,String name){return new Builder.ConfBuilder(dir,name);}
    public static Builder.ConfBuilder confBuilder(String dir){return new Builder.ConfBuilder(dir,"");}
    public static class Builder{
        public SpellBlock func;
        public Vector2i pos;
        public ConfBuilder[] confs;
        public HashMap<String,ConfiguredParameter> params;

        public Builder(SpellBlock func){
            this.func=func;
            confs = new ConfBuilder[6];
            params=new HashMap<>();
        }

        public SpellComponent build(SpellGrid parent){
            var res = new SpellComponent(parent,pos,func,null,params);
            SideConfig[] confs2 = func.getDefaultSideConfigs(res);
            for (int i = 0; i < 6; i++) {
                if(confs[i]==null){
                    continue;
                }
                confs[i].dir = i;
                confs2[i]=confs[i].build(res,confs2[i].modes.toArray(new SideConfig.Mode[0]));
            }
            res.sideConfigs=confs2;
            return res;
        }

        public Builder pos(Vector2i p){
            this.pos=p; return this;
        }

        public Builder pos(int x, int y){
            return pos(new Vector2i(x,y));
        }

        public Builder conf(ConfBuilder conf){
            this.confs[conf.dir]=conf; return this;
        }

        public Builder param(String name,SpellSignal sig){
            var p = new ConfiguredParameter(func.getParameter(name));
            p.setSignal(sig);
            this.params.put(name,p);
            return this;
        }

        public Builder param(String name,float num){
            return param(name,SpellSignal.createNumber(num).named(name));
        }

        public Builder param(String name,String text){
            return param(name,SpellSignal.createText(text).named(name));
        }

        public Builder param(String name,boolean bool){
            return param(name,SpellSignal.createBoolean(bool).named(name));
        }

        public static class ConfBuilder{

            public String name;
            public int dir;
            public SideConfig.Mode mode;

            public ConfBuilder(String dir,String name){
                this.dir=getDirIndex(dir);
                this.name=name;
            }

            public ConfBuilder mode(SideConfig.Mode mode){
                this.mode=mode; return this;
            }

            protected SideConfig build(SpellComponent comp,SideConfig.Mode[] modes){
                var res = new SideConfig(comp,getDirString(dir),name,modes);
                res.sanityCheckVarName(false);
                if(mode!=null)res.setMode(mode);
                return res;
            }
        }
    }
}
