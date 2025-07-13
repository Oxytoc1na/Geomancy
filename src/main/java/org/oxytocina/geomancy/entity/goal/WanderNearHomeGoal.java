package org.oxytocina.geomancy.entity.goal;

import java.util.EnumSet;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.entity.IMobWithHome;

public class WanderNearHomeGoal extends Goal {
    public static final int DEFAULT_CHANCE = 120;
    protected final PathAwareEntity mob;
    protected final IMobWithHome homeHaver;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected final double speed;
    protected int chance;
    protected boolean ignoringChance;
    private final boolean canDespawn;

    public WanderNearHomeGoal(PathAwareEntity mob, double speed) {
        this(mob, speed, 120);
    }

    public WanderNearHomeGoal(PathAwareEntity mob, double speed, int chance) {
        this(mob, speed, chance, true);
    }

    public WanderNearHomeGoal(PathAwareEntity entity, double speed, int chance, boolean canDespawn) {
        this.mob = entity;
        this.speed = speed;
        this.chance = chance;
        this.canDespawn = canDespawn;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        if(entity instanceof IMobWithHome homeHaver){
            this.homeHaver = homeHaver;
        }
        else this.homeHaver=null;
    }

    @Override
    public boolean canStart() {
        if (this.mob.hasPassengers()) {
            return false;
        } else {
            if (!this.ignoringChance) {
                if (this.canDespawn && this.mob.getDespawnCounter() >= 100) {
                    return false;
                }

                if (this.mob.getRandom().nextInt(toGoalTicks(this.chance)) != 0) {
                    return false;
                }
            }

            Vec3d vec3d = this.getWanderTarget();
            if (vec3d == null) {
                return false;
            } else {
                this.targetX = vec3d.x;
                this.targetY = vec3d.y;
                this.targetZ = vec3d.z;
                this.ignoringChance = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        return NoPenaltyTargeting.findTo(this.mob, 10, 7, getHome().toCenterPos(),2*Math.PI);
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle() && !this.mob.hasPassengers();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }

    public void ignoreChanceOnce() {
        this.ignoringChance = true;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    private BlockPos getHome(){
        if(homeHaver!=null) return homeHaver.getHome();
        return mob.getBlockPos();
    }
}
