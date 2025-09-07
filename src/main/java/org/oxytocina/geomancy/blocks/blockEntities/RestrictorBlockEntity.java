package org.oxytocina.geomancy.blocks.blockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.registries.ModDamageTypes;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RestrictorBlockEntity extends BlockEntity {

    public static final HashMap<UUID, Pair<RestrictorBlockEntity,Integer>> influences = new HashMap<>();

    private static List<PotentiallyForbiddenAction> PFAS = new ArrayList<>();

    public static void registerPFA(PotentiallyForbiddenAction pfa) {
        PFAS.add(pfa);
    }

    public static class PotentiallyForbiddenAction{
        public Type type = Type.Teleport;
        public Vec3d from;
        public Vec3d to;
        public Identifier fromID;
        public Identifier toID;
        public long tick = 0;
        public SpellContext ctx;

        private PotentiallyForbiddenAction(SpellContext ctx){
            tick = Geomancy.tick;
            this.ctx=ctx;
        }

        public static PotentiallyForbiddenAction createTeleport(SpellContext ctx, Vec3d from, Vec3d to){
            PotentiallyForbiddenAction res = new PotentiallyForbiddenAction(ctx);
            res.type = Type.Teleport;
            res.from=from;
            res.to=to;
            return res;
        }

        public static PotentiallyForbiddenAction createDimhop(SpellContext ctx, Identifier from, Identifier to){
            PotentiallyForbiddenAction res = new PotentiallyForbiddenAction(ctx);
            res.type = Type.Dimhop;
            res.fromID=from;
            res.toID=to;
            return res;
        }

        public void undo(){
            switch(type){
                case Teleport : {
                    if(ctx.caster==null || ctx.caster.isRemoved()) break;
                    ctx.caster.teleport(from.x,from.y,from.z);
                    ctx.caster.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,5));
                    ctx.caster.damage(ModDamageTypes.of(ctx.getWorld(),ModDamageTypes.RESTRICTED_ACTION),4);
                    ParticleUtil.ParticleData.createRestrictedAction(ctx.getWorld(),ctx.caster.getEyePos()).send();
                    break;
                }
                case Dimhop: {
                    if(ctx.caster==null || ctx.caster.isRemoved()) break;
                    ServerWorld destination = (ServerWorld) ctx.getWorld();
                    if(destination==null) break;
                    from = ctx.getOriginPos();
                    var ent = ctx.caster;
                    ent.teleport(destination,from.x,from.y,from.z,null,ent.getYaw(),ent.getPitch());
                    ctx.caster.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,5));
                    ctx.caster.damage(ModDamageTypes.of(ctx.getWorld(),ModDamageTypes.RESTRICTED_ACTION),4);
                    ParticleUtil.ParticleData.createRestrictedAction(ctx.getWorld(),ctx.caster.getEyePos()).send();
                    break;
                }
            }
        }

        public boolean offends(SpellContext.Restrictions restrictions) {
            switch(type){
                case Dimhop:
                case Teleport :{
                    if(!restrictions.allowsTeleports()) return true;
                    break;
                }
            }
            return false;
        }

        public enum Type {
            Teleport,
            Dimhop
        }
    }

    public static float RANGE = 100;
    public static int DURATION = 20;

    public static void clear(){
        influences.clear();
    }

    public static void tick() {
        // tick down influences
        var toRemove = new ArrayList<>();
        for(var uuid : influences.keySet()){
            var pair = influences.get(uuid);
            if(pair==null){toRemove.add(uuid);continue;}
            var be = pair.getLeft();
            if(be==null){toRemove.add(uuid);continue;}
            int ticksLeft = pair.getRight();
            ticksLeft--;
            if(ticksLeft<=0) {toRemove.add(uuid);continue;}
            influences.put(uuid,new Pair(be,ticksLeft));
        }
        for(var uuid : toRemove)
            influences.remove(uuid);

        // tick PFAs
        List<PotentiallyForbiddenAction> newPFAs = new ArrayList<>();
        for(var pfa : PFAS){
            var caster = pfa.ctx.caster;
            // dont keep PFAs that no longer have a potential offender
            if(caster == null || !(caster instanceof ServerPlayerEntity spe)) continue;

            if(pfa.offends(getRestrictionsFor(spe))){
                pfa.undo();
                // dont keep PFAs that have been undone
                continue;
            }

            // dont keep PFAs that are older than one second
            if(Geomancy.tick - pfa.tick > 20*1) continue;
            newPFAs.add(pfa);
        }
        PFAS = newPFAs;
    }

    public static SpellContext.Restrictions getRestrictionsFor(ServerPlayerEntity spe){
        if(!influences.containsKey(spe.getUuid())) return SpellContext.Restrictions.NONE;
        return influences.get(spe.getUuid()).getLeft().getRestrictions();
    }

    public RestrictorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESTRICTOR_BLOCK_ENTITY, pos, state);
    }

    public SpellContext.Restrictions getRestrictions(){
        return SpellContext.Restrictions.DUNGEON;
    }

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public void markDirty() {
        if(world==null) return;
        world.updateListeners(pos, getCachedState(),getCachedState(), 3);
        super.markDirty();
    }

    public void tick(World world, BlockPos pos,BlockState state){
        initialize();
        if(world==null) return;
        if(!(world instanceof ServerWorld sw)) return;
        for(var spe : sw.getPlayers()){
            // check if in range
            if(!EntityUtil.isInRange(spe,sw,getPos().toCenterPos(),RANGE)) continue;

            var uuid = spe.getUuid();

            // check if other BE already influencing
            // if so, replace only if closer
            var prevBE = influences.containsKey(uuid) ? influences.get(uuid).getLeft() : null;
            if(prevBE!=null && EntityUtil.distanceTo(spe,prevBE.getPos().toCenterPos()) < EntityUtil.distanceTo(spe,this.getPos().toCenterPos())) continue;

            // set new influence
            influences.put(uuid,new Pair<>(this,DURATION));
        }
    }

    private boolean initized=false;
    public void initialize(){
        if(initized) return;
        initized=true;
        //if(world!=null && !world.isClient)
        //    IPedestalListener.onPedestalCreated(this);

    }
}