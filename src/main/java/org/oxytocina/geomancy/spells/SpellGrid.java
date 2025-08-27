package org.oxytocina.geomancy.spells;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2i;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.AutocasterBlockEntity;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.oxytocina.geomancy.entity.CasterDelegateEntity;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.util.ByteUtil;
import org.oxytocina.geomancy.util.ManaUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SpellGrid {
    public static final int CURRENT_DATA_FORMAT_VERSION = SpellComponent.CURRENT_DATA_FORMAT_VERSION;

    public int width;
    public int height;
    public String name;
    public boolean library; // if true, hides spell from spell selection
    public HashMap<Vector2i,SpellComponent> components;
    public float soulCostMultiplier = 1;
    public ItemStack displayStack = null;

    public SpellGrid(ItemStack stack, NbtCompound nbt){
        this.components=new LinkedHashMap<>();
        readNbt(nbt);
        // soul saver
        if(stack.getItem() instanceof SpellStoringItem storer)
            soulCostMultiplier=storer.getSoulCostMultiplier(stack);
    }

    public SpellGrid(int width, int height){
        this.width=width;
        this.height=height;
        this.name="";
        this.library=false;
        this.components=new LinkedHashMap<>();
    }

    private SpellGrid(PacketByteBuf buf){
        deserializeInstance(buf);
    }

    public CasterDelegateEntity spawnDelegate(SpellContext parent, Vec3d pos, Vec2f rot, int delay){
        CasterDelegateEntity res = new CasterDelegateEntity(parent,this,pos,rot,delay);
        parent.getWorld().spawnEntity(res);
        return res;
    }

    public SpellBlockResult runReferenced(SpellContext parent,SpellComponent casterComp,SpellBlockArgs args){
        SpellContext context = parent.createReferenced(casterComp);
        context.internalVars = args;
        context.grid = this;

        context.refreshAvailableSoul();
        for (var comp : components.values())
            comp.preRunSetup(context);

        context.stage = SpellContext.Stage.Run;
        for (var comp : components.values())
            comp.run();

        context.refreshAvailableSoul();
        for (var comp : components.values())
            comp.postRun();

        return context.referenceResult;
    }

    public void run(ItemStack casterItem, ItemStack spellStorage, LivingEntity casterEntity, AutocasterBlockEntity blockEntity,CasterDelegateEntity delegate, SpellBlockArgs args, SpellContext.SoundBehavior soundBehavior){
        long startTime = System.nanoTime();

        float costMultiplier = soulCostMultiplier;
        if(casterEntity!=null&&casterEntity.hasStatusEffect(ModStatusEffects.REGRETFUL))
        {
            var amp = casterEntity.getStatusEffect(ModStatusEffects.REGRETFUL).getAmplifier();
            costMultiplier *= 1+(amp+1)*0.5f;
        }

        SpellContext context = new SpellContext(this,casterEntity,blockEntity,delegate,casterItem,spellStorage,0,costMultiplier,0,soundBehavior);
        context.refreshAvailableSoul();
        context.internalVars = args;

        try{
            for (var comp : components.values())
                comp.preRunSetup(context);

            context.stage = SpellContext.Stage.Run;
            for (var comp : components.values())
                comp.run();

            for (var comp : components.values())
                comp.postRun();
        }
        catch(Exception ignored){
            Geomancy.logError("AAAAA!!!! Spells threw an exception! DEBUG ME!");
            Geomancy.logError(ignored.getMessage());
            Geomancy.logError(Arrays.toString(ignored.getStackTrace()));
        }

        if(context.depthLimitReached && context.debugging){
            SpellBlocks.tryLogDebugDepthLimitReached(context);
        }

        // casting a spell that takes too long
        if(context.timedOut()){
            SpellBlocks.tryLogDebugTimedOut(context);
            // spawn lightning
            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT,context.getWorld());
            var pos = context.getOriginPos();
            if(context.caster!=null)
                pos = context.caster.getPos();
            lightning.setPos(pos.x,pos.y,pos.z);
            switch(context.sourceType){
                case Caster:
                    // TODO: drop caster item
                    break;
                case Block:
                    if(Toolbox.random.nextFloat() < 0.2f)
                    {
                        // break the caster block
                        if(context.getWorld() instanceof ServerWorld sw){
                            sw.breakBlock(context.getOriginBlockPos(),true);
                        }
                    }
                    break;
                default :break;
            }
            context.getWorld().spawnEntity(lightning);
        }

        if(context.soulConsumed > 0){
            switch(context.sourceType)
            {
                case Caster :
                    ManaUtil.syncMana((PlayerEntity) casterEntity); break;
                case Block:
                    break;
            }
        }
        SpellBlocks.playCastSound(context);

        long msTaken = TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-startTime);
        if(msTaken>10){
            Geomancy.logWarning(name+" Spell execution time: "+msTaken);
        }
    }

    public boolean tryRemoveComponent(Vector2i position){
        if(!components.containsKey(position)) return false;
        components.remove(position);
        return true;
    }

    public boolean tryAddComponent(SpellComponent component){
        if(components.containsKey(component.position)) return false; // occupied
        if(!inBounds(component.position)) return false; // out of bounds

        component.parent=this;
        components.put(component.position,component);
        recalculateNeighbors(component.position);

        return true;
    }

    public SpellComponent getComponent(Vector2i pos){
        if(!components.containsKey(pos)) return null;
        return components.get(pos);
    }

    public void recalculateNeighbors(Vector2i position){
        var componentAtThisPosition = getComponent(position);
        var neighborPosistions = getNeighboringPositions(position);
        for (int i = 0; i < 6; i++) {
            var comp = getComponent(neighborPosistions.get(i));
            byte dir = ByteUtil.intToByte(i);
            if(componentAtThisPosition!=null){
                componentAtThisPosition.setNeighbor(dir,comp);
            }
            if(comp==null) continue;
            comp.setNeighbor(SpellComponent.mirrorDirection(dir),componentAtThisPosition);

        }
    }

    public ArrayList<Vector2i> getNeighboringPositions(Vector2i pos){
        ArrayList<Vector2i> res = new ArrayList<>();

        int ySkew = pos.y%2;

        /*

          o   6   1
            5   o   2
          o   4   3
         */

        res.add(new Vector2i(pos.x+ySkew,pos.y-1));
        res.add(new Vector2i(pos.x+1,pos.y));

        res.add(new Vector2i(pos.x+ySkew,pos.y+1));
        res.add(new Vector2i(pos.x-1+ySkew,pos.y+1));

        res.add(new Vector2i(pos.x-1,pos.y));
        res.add(new Vector2i(pos.x-1+ySkew,pos.y-1));

        return res;
    }

    public boolean inBounds(Vector2i position){
        return
                position.x>=0 &&
                        position.y>=0&&
                        position.x<width&&
                        position.y<height&&
                positionIsInGrid(position.x,position.y,width,height);
    }

    public MutableText getName(){
        if(Objects.equals(name, "")) return Text.translatable("geomancy.spellstorage.unnamed");
        return Text.literal(name);
    }

    public MutableText getRuntimeName(SpellContext context){
        MutableText res = getName();
        if(context.isChild()) res = context.parentCall.grid.getRuntimeName(context.parentCall).append(" -> ").append(res);
        return res;
    }

    public PacketByteBuf serialize(){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(CURRENT_DATA_FORMAT_VERSION);

        buf.writeInt(width);
        buf.writeInt(height);
        buf.writeString(name);
        buf.writeBoolean(library);

        buf.writeInt(components.size());
        for (var c:components.values())
        {
            buf.writeString(ByteUtil.bufToString(c.serialize()));
        }

        buf.writeBoolean(displayStack!=null&&!displayStack.isEmpty());
        if(displayStack!=null&&!displayStack.isEmpty())
        {
            buf.writeItemStack(displayStack);
        }

        return buf;
    }

    public static SpellGrid deserialize(PacketByteBuf buf){
        return new SpellGrid(buf);
    }

    public void deserializeInstance(PacketByteBuf buf){
        int formatVersion = buf.readInt();

        width=buf.readInt();
        height=buf.readInt();
        name=buf.readString();
        library = buf.readBoolean();

        int compsSize = buf.readInt();
        for (int i = 0; i < compsSize; i++) {
            try{
                PacketByteBuf tmpBuf = ByteUtil.stringToBuf(buf.readString());
                tryAddComponent(SpellComponent.deserialize(this,tmpBuf));
            }
            catch(Throwable ignored){
                return;
            }
        }

        if(buf.readBoolean())
        {
            displayStack=buf.readItemStack();
        }
    }

    public void writeNbt(NbtCompound nbt){

        nbt.putInt("v",CURRENT_DATA_FORMAT_VERSION);

        // experimental serialization
        if(CURRENT_DATA_FORMAT_VERSION>=100){
            nbt.putString("data",ByteUtil.bufToString(serialize()));
            return;
        }

        String wKey = CURRENT_DATA_FORMAT_VERSION>=1?"w":"width";
        String hKey = CURRENT_DATA_FORMAT_VERSION>=1?"h":"height";
        String nKey = CURRENT_DATA_FORMAT_VERSION>=1?"n":"name";
        String lKey = CURRENT_DATA_FORMAT_VERSION>=1?"l":"lib";
        String cKey = CURRENT_DATA_FORMAT_VERSION>=1?"c":"components";
        String dKey = CURRENT_DATA_FORMAT_VERSION>=1?"d":"displayStack";

        nbt.putInt(wKey,width);
        nbt.putInt(hKey,height);
        if(name!=null&& !name.isEmpty())nbt.putString(nKey,name);
        if(library) nbt.putBoolean(lKey,library);
        NbtList compsNbt = new NbtList();
        for (var c:components.values())
        {
            NbtCompound cComp = new NbtCompound();
            c.writeNbt(cComp);
            compsNbt.add(cComp);
        }
        if(!compsNbt.isEmpty())nbt.put(cKey,compsNbt);

        if(displayStack!=null&&!displayStack.isEmpty())
        {
            var displayNbt = new NbtCompound();
            displayStack.writeNbt(displayNbt);
            nbt.put(dKey,displayNbt);
        }
    }

    public void readNbt(NbtCompound nbt){
        int version = nbt.getInt("v");

        if(nbt.contains("data") && version>=100){
            deserializeInstance(ByteUtil.stringToBuf(nbt.getString("data")));
            return;
        }

        String wKey = version>=1?"w":"width";
        String hKey = version>=1?"h":"height";
        String nKey = version>=1?"n":"name";
        String lKey = version>=1?"l":"lib";
        String cKey = version>=1?"c":"components";
        String dKey = version>=1?"d":"displayStack";

        width=nbt.getInt(wKey);
        height=nbt.getInt(hKey);
        name=nbt.contains(nKey)?nbt.getString(nKey):"";
        library= nbt.contains(lKey) && nbt.getBoolean(lKey);
        NbtList compsNbt = nbt.contains(cKey)?nbt.getList(cKey, NbtElement.COMPOUND_TYPE):new NbtList();
        for (var c:compsNbt)
        {
            if(!(c instanceof NbtCompound nbtComp)) continue;

            SpellComponent comp = new SpellComponent(this,nbtComp);
            tryAddComponent(comp);
        }

        if(nbt.contains(dKey))
            displayStack = ItemStack.fromNbt(nbt.getCompound(dKey));
    }

    // cuts off hexagonal edges
    public static boolean positionIsInGrid(int x, int y, int width, int height){
        int ySkew = y%2;
        float midYIndex = height/2f-0.5f;
        float diffToMidYIndex = Math.abs(y-midYIndex);

        if(x>width/2f){
            // to the right of the middle
            int xDiffToEdges = width-x;
            return xDiffToEdges>=(diffToMidYIndex+1+ySkew)/2;
        }
        else{
            // to the left of the middle
            int xDiffToEdges = x;
            return xDiffToEdges>=(diffToMidYIndex-ySkew)/2;
        }
    }

    public static MutableText getName(SpellGrid grid){
        if(grid==null) return Text.translatable("geomancy.spellstorage.empty").formatted(Formatting.DARK_GRAY);
        return grid.getName().formatted(Formatting.GRAY);
    }

    public ItemStack getDisplayStack(ItemStack storage) {
        var base = displayStack;
        if(base==null||base.isEmpty()) base = storage;
        base = base.copy();
        base.setNbt(null);
        base.setCustomName(getName());
        return base;
    }

    public static Builder builder(String name){return new Builder(name);}
    public static class Builder{
        public String name = "";
        public int width = 3;
        public int height = 3;
        public HashMap<Vector2i,SpellComponent.Builder> components;

        public Builder(String name){
            this.name=name;
            components=new LinkedHashMap<>();
        }

        public SpellGrid build(){
            SpellGrid res = new SpellGrid(width,height);
            res.name=name;
            for(var b : components.values()){
                res.tryAddComponent(b.build(res));
            }
            return res;
        }

        public Builder dim(SpellStoringItem storer){
            width=storer.getWidth();
            height=storer.getHeight();
            return this;
        }

        public Builder add(SpellComponent.Builder builder){
            components.put(builder.pos,builder);
            return this;
        }
    }
}
