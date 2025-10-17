package org.oxytocina.geomancy.entity;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.oxytocina.geomancy.spells.SpellBlockArgs;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.ParticleUtil;

import java.util.Objects;

public class CasterDelegateEntity extends Entity {

    protected CasterDelegateEntity(EntityType<?> entityType, World world) {
        super(entityType,world);
        this.noClip = true;
    }

    public CasterDelegateEntity(SpellContext parent, SpellGrid grid, Vec3d pos, Vec2f rot, int delay){
        this(ModEntityTypes.CASTER_DELEGATE,parent.getWorld());
        this.grid = grid;
        this.delay = delay;
        this.parent = parent;
        setPosition(pos);
        setRotation(rot.x,rot.y);
    }

    private static final String DATA_KEY = "data";
    private NbtCompound data = new NbtCompound();

    public int delay;
    public SpellGrid grid;
    public SpellContext parent;

    @Override
    public void tick() {
        if(isRemoved()) return;

        if(getWorld() instanceof ServerWorld sw && age%10==0){
            ParticleUtil.ParticleData.createGeneric(sw, ParticleTypes.SCULK_SOUL,getPos(),getVelocity(),2,0.3f).send();
        }

        if(delay-- <= 0){
            cast();
        }
    }

    public void cast(){
        // make sure the parents still exist
        if(parent==null || parent.grid==null) {destroy();return;}
        if(parent.casterBlock!=null && parent.casterBlock.isRemoved()) {destroy(); return;}
        if(parent.caster!=null && parent.caster.isRemoved()) {destroy(); return;}
        // make sure the caster item still exists
        // not necessary anymore, hopefully
        // if(!parent.hasCasterItem()) {destroy(); return;}

        // threading
        if(parent.delegate!=null)
            SpellBlocks.tryUnlockSpellAdvancement(parent.caster,"threading");

        grid.run(parent.casterItem,parent.spellStorage,parent.caster,parent.casterBlock,this, SpellBlockArgs.empty(),
                SpellContext.SoundBehavior.Reduced,false,parent.baseDepth+1);
        destroy();
    }

    private void destroy() {
        remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.data = nbt.getCompound("data");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.put("data", this.data.copy());
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return false;
    }

    @Override
    protected void addPassenger(Entity passenger) {
        throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.NORMAL;
    }

    @Override
    public boolean canAvoidTraps() {
        return true;
    }

    public Vec3d getRotationVec3d(){
        var base = getRotationVector();
        return new Vec3d(-base.x,-base.y,base.z);
    }

}
