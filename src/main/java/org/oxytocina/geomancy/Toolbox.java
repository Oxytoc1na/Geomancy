package org.oxytocina.geomancy;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Toolbox {

    public static Random random = new Random();

    public static Vec3d RandomItemDropVelocity(float speed){
        float angle = random.nextFloat()*2*(float)Math.PI;
        return new Vec3d(1,0.5,0).rotateY(angle).multiply(speed);
    }

    public static ItemEntity spawnItemStackAsEntity(World world, Vec3d pos, ItemStack itemStack) {
        return spawnItemStackAsEntity(world, pos, itemStack, new Vec3d(0,0,0));
    }

    public static ItemEntity spawnItemStackAsEntity(World world, Vec3d pos, ItemStack itemStack, Vec3d velocity) {
        return spawnItemStackAsEntity(world, pos, itemStack, velocity, true, null);
    }

    public static ItemEntity spawnItemStackAsEntity(World world, Vec3d pos, ItemStack itemStack, Vec3d velocity, boolean neverDespawn, @Nullable Entity owner) {

            ItemStack resultStack = itemStack.copy();
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), resultStack);
            itemEntity.setVelocity(velocity);
            itemEntity.setPickupDelay(20);
            if (neverDespawn) {
                itemEntity.setNeverDespawn();
            }
            if (owner != null) {
                itemEntity.setOwner(owner.getUuid());
            }
            world.spawnEntity(itemEntity);
            return itemEntity;
    }

    public static int SelectWeightedRandomIndex(int[] weights){
        Map<Integer,Integer> m = new HashMap<>();
        for (int i = 0; i < weights.length; i++) {
            m.put(i,weights[i]);
        }

        return SelectWeightedRandomIndex(m,-1);
    }


    public static <T> T SelectWeightedRandomIndex(Map<T,Integer> weights, T def){
        int weightsum = weights.values().stream().mapToInt(a->a).sum();
        int weightpick = random.nextInt(weightsum);
        for(T key : weights.keySet()){
            weightpick-=weights.get(key);
            if(weightpick<=0) return key;
        }
        return def;
    }

    public static void grantAdvancementCriterion(@NotNull ServerPlayerEntity serverPlayerEntity, Identifier advancementIdentifier, String criterion) {
        if (serverPlayerEntity.getServer() == null) {
            return;
        }
        ServerAdvancementLoader sal = serverPlayerEntity.getServer().getAdvancementLoader();
        PlayerAdvancementTracker tracker = serverPlayerEntity.getAdvancementTracker();

        Advancement advancement = sal.get(advancementIdentifier);
        if (advancement == null) {
            Geomancy.logError("Trying to grant a criterion \"" + criterion + "\" for an advancement that does not exist: " + advancementIdentifier);
        } else {
            if (!tracker.getProgress(advancement).isDone()) {
                tracker.grantCriterion(advancement, criterion);
            }
        }
    }

    public static void grantAdvancementCriterion(@NotNull ServerPlayerEntity serverPlayerEntity, String advancementString, String criterion) {
        grantAdvancementCriterion(serverPlayerEntity, Geomancy.locate(advancementString), criterion);
    }

    public static Identifier locate(String string){return Geomancy.locate(string);}
}
