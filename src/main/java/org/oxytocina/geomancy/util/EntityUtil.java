package org.oxytocina.geomancy.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.items.armor.IListenerArmor;

import java.util.HashMap;
import java.util.UUID;

public class EntityUtil {

    public static final HashMap<UUID,Integer> playerjumpCooldowns = new HashMap<>();

    public static void tick(MinecraftServer server){
        for(var uuid : playerjumpCooldowns.keySet())
        {
            var spe = server.getPlayerManager().getPlayer(uuid);
            if(spe!=null){
                var c = getCooldown(spe);
                c--;
                if(c>0)
                    setCooldown(spe,c);
                else{
                    playerjumpCooldowns.remove(uuid);
                }
            }
            else{
                playerjumpCooldowns.remove(uuid);
            }
        }
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
}
