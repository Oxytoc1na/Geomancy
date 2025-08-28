package org.oxytocina.geomancy.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.armor.IListenerArmor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityUtil {

    public static final HashMap<UUID,Integer> playerjumpCooldowns = new HashMap<>();

    public static void tick(MinecraftServer server){
        var toBeRemoved = new ArrayList<>();
        for(var uuid : playerjumpCooldowns.keySet())
        {
            var spe = server.getPlayerManager().getPlayer(uuid);
            if(spe!=null){
                var c = getCooldown(spe);
                c--;
                if(c>0)
                    setCooldown(spe,c);
                else{
                    toBeRemoved.add(uuid);
                }
            }
            else{
                toBeRemoved.add(uuid);
            }
        }
        for(var uuid : toBeRemoved)
            playerjumpCooldowns.remove(uuid);
    }

    public static int getCooldown(ServerPlayerEntity spe){
        UUID uuid = spe.getUuid();
        if(playerjumpCooldowns.containsKey(uuid)) return playerjumpCooldowns.get(uuid);
        return 0;
    }

    public static void setCooldown(ServerPlayerEntity spe, int c){
        UUID uuid = spe.getUuid();
        playerjumpCooldowns.put(uuid,c);
    }

    public static void onJump(LivingEntity entity){
        if (entity instanceof MobEntity thisMobEntity) {
            for (ItemStack armorItemStack : thisMobEntity.getArmorItems()) {
                if (armorItemStack.getItem() instanceof IListenerArmor armorWithHitEffect) {
                    armorWithHitEffect.onJump(armorItemStack,thisMobEntity);
                }
            }
        } else if (entity instanceof ServerPlayerEntity spe) {
            if(getCooldown(spe) <= 0)
            {
                for (ItemStack armorItemStack : spe.getArmorItems()) {
                    if (armorItemStack.getItem() instanceof IListenerArmor armorWithHitEffect) {
                        armorWithHitEffect.onJump(armorItemStack, spe);
                    }
                }
                setCooldown(spe,10);
            }

        }
    }

    public static void onAttacking(LivingEntity thisEntity, Entity target){
        if(!(target instanceof LivingEntity targetEnt)) return;
        World world = thisEntity.getWorld();
        if (!world.isClient) {
            if (thisEntity instanceof MobEntity thisMobEntity) {
                for (ItemStack armorItemStack : thisMobEntity.getArmorItems()) {
                    if (armorItemStack.getItem() instanceof IListenerArmor armorWithHitEffect) {
                        armorWithHitEffect.onHit(armorItemStack,thisMobEntity, targetEnt);
                    }
                }
            } else if (thisEntity instanceof ServerPlayerEntity thisPlayerEntity) {
                for (ItemStack armorItemStack : thisPlayerEntity.getArmorItems()) {
                    if (armorItemStack.getItem() instanceof IListenerArmor armorWithHitEffect) {
                        armorWithHitEffect.onHit(armorItemStack, thisPlayerEntity,targetEnt);
                    }
                }
            }
        }
    }

    public static void onMessageSent(ServerPlayerEntity spe, String message)
    {
        for (ItemStack armorItemStack : spe.getArmorItems()) {
            if (armorItemStack.getItem() instanceof IListenerArmor armorWithHitEffect) {
                armorWithHitEffect.onMessageSent(armorItemStack,spe, message);
            }
        }
    }

    public static void slipItem(LivingEntity entity, ItemStack heldStack) {
        if(entity==null) return;
        if(entity instanceof ServerPlayerEntity spe){
            int slot = spe.getInventory().getSlotWithStack(heldStack);
            if(slot==-1) return;
            spe.dropItem(heldStack.copyAndEmpty(),true,true);
        }
    }

    @Nullable
    public static EntityHitResult raycast(World world, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double squaredReach) {
        double e = squaredReach;
        Entity resultEntity = null;
        Vec3d hitPos = null;

        for(Entity contenderEntity : world.getEntitiesByClass(LivingEntity.class, box, predicate)) {
            Box hitBox = contenderEntity.getBoundingBox().expand((double)contenderEntity.getTargetingMargin());
            Optional<Vec3d> hitBoxHit = hitBox.raycast(min, max);
            if (hitBox.contains(min)) {
                if (e >= (double)0.0F) {
                    resultEntity = contenderEntity;
                    hitPos = (Vec3d)hitBoxHit.orElse(min);
                    e = (double)0.0F;
                }
            } else if (hitBoxHit.isPresent()) {
                Vec3d vec3d2 = (Vec3d)hitBoxHit.get();
                double f = min.squaredDistanceTo(vec3d2);
                if (f < e || e == (double)0.0F) {
                    resultEntity = contenderEntity;
                    hitPos = vec3d2;
                    e = f;
                }
            }
        }

        if (resultEntity == null) {
            return null;
        } else {
            return new EntityHitResult(resultEntity, hitPos);
        }
    }

    public static Boolean isInRange(LivingEntity le, ServerWorld sw, Vec3d pos, float range) {
        if(le.getWorld()!=sw) return false;
        return distanceTo(le,pos)<=range;
    }

    public static double distanceTo(Entity e, Vec3d pos) {
        return pos.subtract(e.getPos()).length();
    }
}
