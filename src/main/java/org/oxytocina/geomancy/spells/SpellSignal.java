package org.oxytocina.geomancy.spells;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
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

    public static SpellSignal createUUID() { return createUUID((UUID)null);}
    public static SpellSignal createUUID(Entity entity) { return createUUID(entity.getUuid());}
    public static SpellSignal createUUID(UUID defaultValue) {
        return new SpellSignal(Type.UUID,"uuid",0,"",defaultValue,null,null);
    }

    public static SpellSignal createVector() { return createVector((Vec3d)null);}
    public static SpellSignal createVector(Vec3i vec) { return createVector(new Vec3d(vec.getX(), vec.getY(), vec.getZ()));}
    public static SpellSignal createVector(Vector3f vec) { return createVector(new Vec3d(vec.x(), vec.y(), vec.z()));}
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
                if(ctx!=null && !(ctx.getWorld() instanceof ServerWorld)){

                    ent = ctx.getWorld().getPlayerByUuid(uuidValue);
                    if(ent==null)
                    {
                        var ents = ctx.getWorld().getEntitiesByClass(Entity.class, Box.from(ctx.caster.getPos()).expand(1000),entity -> true);
                        for(var contender : ents)
                            if(contender.getUuid().equals(uuidValue))
                            {
                                ent = contender;
                                break;
                            }
                    }

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
        return /*type.toString()+":"+*/getTextValue();
    }

    public String toString(SpellContext ctx) {
        return /*type.toString()+":"+*/getTextValue(ctx);
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

    public NbtCompound toNBT(boolean writeName) {
        NbtCompound res = new NbtCompound();
        writeNbt(res,SpellComponent.CURRENT_DATA_FORMAT_VERSION,writeName);
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

    public void serialize(PacketByteBuf buf) {
        buf.writeString(type.toString());
        buf.writeString(name);
        switch(type){
            case Number: buf.writeFloat(getNumberValue()); break;
            case Boolean: buf.writeBoolean(getBooleanValue()); break;
            case Text: buf.writeString(getTextValue()); break;
            case UUID: buf.writeUuid(getUUIDValue()); break;
            case Vector: buf.writeVector3f(getVectorValue().toVector3f()); break;
            case List: serializeList(getListValue(),buf); break;
        }
    }
    private static void serializeList(List<SpellSignal> list, PacketByteBuf buf){
        buf.writeInt(list.size());
        for(var s : list){
            s.serialize(buf);
        }
    }

    public static SpellSignal deserialize(PacketByteBuf buf) {
        String typeS = buf.readString();
        Type type = Enum.valueOf(Type.class,typeS);
        String name = buf.readString();
        switch(type){
            case Number: return SpellSignal.createNumber(buf.readFloat()).named(name);
            case Boolean: return SpellSignal.createBoolean(buf.readBoolean()).named(name);
            case Text: return SpellSignal.createText(buf.readString()).named(name);
            case UUID: return SpellSignal.createUUID(buf.readUuid()).named(name);
            case Vector: return SpellSignal.createVector(buf.readVector3f()).named(name);
            case List: return SpellSignal.createList(deserializeList(buf)).named(name);
        }

        return SpellSignal.createNone();
    }
    private static List<SpellSignal> deserializeList(PacketByteBuf buf){
        int size = buf.readInt();
        List<SpellSignal> res = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            res.add(SpellSignal.deserialize(buf));
        }
        return res;
    }

    public void writeNbt(NbtCompound nbt, int version,boolean writeName)
    {
        String tKey = version>=1?"t":"type";
        String nKey = version>=1?"n":"name";
        String vKey = version>=1?"v":"val";

        nbt.putString(tKey,type.toString());
        if(writeName)nbt.putString(nKey,name);
        switch(type){
            case Number: nbt.putFloat(vKey,getNumberValue()); break;
            case Boolean: nbt.putBoolean(vKey,getBooleanValue()); break;
            case Text: nbt.putString(vKey,getTextValue()); break;
            case UUID: nbt.putUuid(vKey,getUUIDValue()); break;
            case Vector: nbt.put(vKey, NbtHelper.vectorToNbt(getVectorValue())); break;
            case List: nbt.put(vKey, listToNbt(getListValue(),version)); break;
        }

    }
    private static NbtList listToNbt(List<SpellSignal> list,int v){
        NbtList res = new NbtList();
        for(var s : list){
            NbtCompound nbt = new NbtCompound();
            s.writeNbt(nbt,v,true);
            res.add(nbt);
        }
        return res;
    }
    private static List<SpellSignal> listFromNbt(NbtList nbtl, int v){
        List<SpellSignal> res = new ArrayList<>();
        for(int i = 0; i < nbtl.size(); i++){
            NbtCompound nbt = nbtl.getCompound(i);
            var sig = fromNBT(nbt,v);
            res.add(sig);
        }
        return res;
    }

    public static SpellSignal fromNBT(NbtCompound nbt, int version){

        String tKey = version>=1?"t":"type";
        String nKey = version>=1?"n":"name";
        String vKey = version>=1?"v":"val";

        String typeString = nbt.getString(tKey);
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
            case Number: numberValue = nbt.getFloat(vKey); break;
            case Boolean: numberValue = nbt.getBoolean(vKey)?1:0; break;
            case Text: textValue = nbt.getString(vKey); break;
            case UUID: uuidValue = nbt.getUuid(vKey); break;
            case Vector: vectorValue = NbtHelper.vectorFromNbt(nbt.getCompound(vKey)); break;
            case List: listValue = listFromNbt(nbt.getList(vKey, NbtElement.COMPOUND_TYPE),version);
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

    public boolean softEquals(SpellSignal o){
        if(o==null) return false;
        return type==o.type
                && Float.compare(numberValue, o.numberValue) == 0
                && Objects.equals(textValue, o.textValue)
                && Objects.equals(uuidValue, o.uuidValue)
                && Objects.equals(vectorValue, o.vectorValue)
                && listValuesAreEqual(this,o);

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
