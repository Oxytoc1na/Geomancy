package org.oxytocina.geomancy.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.oxytocina.geomancy.items.armor.IListenerArmor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
}
