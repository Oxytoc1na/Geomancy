package org.oxytocina.geomancy.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.PremadeSpells;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;
import java.util.Map;

public class StellgeCasterEntity extends StellgeEntity implements RangedAttackMob {

    public static Map<Pair<String,Integer>,Integer> WEAPONS = new HashMap<>();

    static{
        WEAPONS.put(new Pair<>("fireball",40),100);
        WEAPONS.put(new Pair<>("curse",20*5),30);
    }

    public StellgeCasterEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType,world);
        this.experiencePoints = 25;
    }

    public AnimationState ringAnimationState = new AnimationState();

    @Override
    protected void setupAnimationStates(){
        super.setupAnimationStates();
        this.ringAnimationState.startIfNotRunning(this.age);
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.add(2,new ProjectileAttackGoal(this,1,1,25));
    }

    @Override
    protected void playAngrySound() {
        this.playSound(ModSoundEvents.ENTITY_STELLGE_ENGINEER_ANGRY, this.getSoundVolume() * 2.0F, this.getSoundPitch() * 1.8F);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("cooldown",attackCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        attackCooldown = nbt.getInt("cooldown");
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.hasAngerTime() ? ModSoundEvents.ENTITY_STELLGE_ENGINEER_ANGRY : ModSoundEvents.ENTITY_STELLGE_ENGINEER_TALK;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ENTITY_STELLGE_ENGINEER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENTITY_STELLGE_ENGINEER_DEATH;
    }



    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        // build weapon
        var caster = new ItemStack(ModItems.STELLGE_CASTER);
        var casterItem = (SoulCastingItem) caster.getItem();

        var weapon = Toolbox.selectWeightedRandomIndex(WEAPONS,null);
        attackCooldown = weapon.getRight();
        ItemStack spellStack = PremadeSpells.SPELLS.get(weapon.getLeft()).getMiddle();
        casterItem.setStack(caster,0,spellStack.copy());
        this.equipStack(EquipmentSlot.MAINHAND, caster);
    }

    public static DefaultAttributeContainer.Builder defAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0)
                .add(ModEntityAttributes.STELLGE_SPAWN_REINFORCEMENTS);
    }

    @Nullable
    @Override
    public EntityData initialize(
            ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
    ) {
        Random random = world.getRandom();
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        float f = difficulty.getClampedLocalDifficulty();
        this.setCanPickUpLoot(random.nextFloat() < 0.55F * f);

        this.applyAttributeModifiers(f);
        return entityData;
    }

    public int currentAttackCooldown = 0;
    public int attackCooldown = 0;

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        currentAttackCooldown--;
        if(currentAttackCooldown>0) return;
        currentAttackCooldown = attackCooldown;
        // cast into target direction
        var stack = getEquippedStack(EquipmentSlot.MAINHAND);
        if(stack.isEmpty()) return;
        if(!(stack.getItem() instanceof SoulCastingItem cs)) return;
        // force looking at target
        this.lookAtEntity(target,360,360);
        cs.cast(stack,this);
    }
}
