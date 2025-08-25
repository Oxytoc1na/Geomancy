package org.oxytocina.geomancy.entity;

import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.goal.WanderNearHomeGoal;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellBlockArgs;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.UUID;

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

        if(delay-- <= 0){
            cast();
        }
    }

    public void cast(){

        // make sure the parents still exist
        if(parent.casterBlock!=null && parent.casterBlock.isRemoved()) {destroy(); return;}
        if(parent.caster!=null && parent.caster.isRemoved()) {destroy(); return;}
        // make sure the caster item still exists
        if(parent.casterItem.isEmpty()) {destroy(); return;}

        grid.run(parent.casterItem,parent.spellStorage,parent.caster,parent.casterBlock,this, SpellBlockArgs.empty(), SpellContext.SoundBehavior.Reduced);
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

}
