package org.oxytocina.geomancy.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.sound.ModSoundEvents;

import java.util.UUID;

public class StellgeEngineerEntity extends HostileEntity implements Angerable, IModMob {

    public StellgeEngineerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType,world);
        this.experiencePoints = 15;
    }

    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(
            ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.05, EntityAttributeModifier.Operation.ADDITION
    );
    private static final UniformIntProvider ANGRY_SOUND_DELAY_RANGE = TimeHelper.betweenSeconds(0, 1);
    private int angrySoundDelay;
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    private int angerTime;
    @Nullable
    private UUID angryAt;
    private static final UniformIntProvider ANGER_PASSING_COOLDOWN_RANGE = TimeHelper.betweenSeconds(4, 6);
    private int angerPassingCooldown;
    public AnimationState idleAnimationState = new AnimationState();

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public double getHeightOffset() {
        return this.isBaby() ? -0.05 : -0.45;
    }

    protected void initCustomGoals() {
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, 10, true, false, (livingEntity -> shouldAngerAt((LivingEntity) livingEntity))));
        this.targetSelector.add(3, new UniversalAngerGoal<>(this, true));
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return this.isBaby() ? 0.96999997F : 1.79F;
    }

    @Override
    protected void mobTick() {
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (this.hasAngerTime()) {
            if (!this.isBaby() && !entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }

            this.tickAngrySound();
        } else if (entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
        }

        this.tickAngerLogic((ServerWorld)this.getWorld(), true);
        if (this.getTarget() != null) {
            this.tickAngerPassing();
        }

        if (this.hasAngerTime()) {
            this.playerHitTimer = this.age;
        }

        super.mobTick();
    }

    private void tickAngrySound() {
        if (this.angrySoundDelay > 0) {
            this.angrySoundDelay--;
            if (this.angrySoundDelay == 0) {
                this.playAngrySound();
            }
        }
    }

    private void tickAngerPassing() {
        if (this.angerPassingCooldown > 0) {
            this.angerPassingCooldown--;
        } else {
            if (this.getVisibilityCache().canSee(this.getTarget())) {
                this.angerNearbyStellge();
            }

            this.angerPassingCooldown = ANGER_PASSING_COOLDOWN_RANGE.get(this.random);
        }
    }

    private void angerNearbyStellge() {
        double range = this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        Box box = Box.from(this.getPos()).expand(range, range, range);
        this.getWorld()
                .getEntitiesByClass(StellgeEngineerEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR)
                .stream()
                .filter(stellgeEngineer -> stellgeEngineer != this)
                .filter(stellgeEngineer -> stellgeEngineer.getTarget() == null)
                .filter(stellgeEngineer -> !stellgeEngineer.isTeammate(this.getTarget()))
                .forEach(stellgeEngineer -> stellgeEngineer.setTarget(this.getTarget()));
    }

    private void playAngrySound() {
        this.playSound(ModSoundEvents.ENTITY_STELLGE_ENGINEER_ANGRY, this.getSoundVolume() * 2.0F, this.getSoundPitch() * 1.8F);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (this.getTarget() == null && target != null) {
            this.angrySoundDelay = ANGRY_SOUND_DELAY_RANGE.get(this.random);
            this.angerPassingCooldown = ANGER_PASSING_COOLDOWN_RANGE.get(this.random);
        }

        if (target instanceof PlayerEntity) {
            this.setAttacking((PlayerEntity)target);
        }

        super.setTarget(target);
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    public static boolean canSpawn(EntityType<StellgeEngineerEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return true;/*world.getDifficulty() != Difficulty.PEACEFUL && !world.getBlockState(pos.down()).isOf(Blocks.NETHER_WART_BLOCK);*/
    }

    @Override
    public boolean canSpawn(WorldView world) {
        return world.doesNotIntersectEntities(this) && !world.containsFluid(this.getBoundingBox());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.getWorld(), nbt);
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
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
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }

    protected void initAttributes() {
        this.getAttributeInstance(ModEntityAttributes.STELLGE_SPAWN_REINFORCEMENTS).setBaseValue(0.0);
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public boolean isAngryAt(PlayerEntity player) {
        return this.shouldAngerAt(player);
    }

    @Override
    public boolean shouldAngerAt(LivingEntity entity) {
        if (!this.canTarget(entity)) {
            return false;
        } else {
            return this.isUniversallyAngry(entity.getWorld()) ? true : entity.getUuid().equals(this.getAngryAt());
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    public static DefaultAttributeContainer.Builder defAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0)
                .add(ModEntityAttributes.STELLGE_SPAWN_REINFORCEMENTS);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        //this.getDataTracker().startTracking(BABY, false);
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient && this.isAlive() && !this.isAiDisabled()) {

        }

        super.tick();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!super.damage(source, amount)) {
            return false;
        } else if (!(this.getWorld() instanceof ServerWorld)) {
            return false;
        } else {
            ServerWorld serverWorld = (ServerWorld)this.getWorld();
            LivingEntity livingEntity = this.getTarget();
            if (livingEntity == null && source.getAttacker() instanceof LivingEntity) {
                livingEntity = (LivingEntity)source.getAttacker();
            }

            if (livingEntity != null
                    && this.getWorld().getDifficulty() == Difficulty.HARD
                    && this.random.nextFloat() < this.getAttributeValue(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                    && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                int i = MathHelper.floor(this.getX());
                int j = MathHelper.floor(this.getY());
                int k = MathHelper.floor(this.getZ());
                ZombieEntity zombieEntity = new ZombieEntity(this.getWorld());

                for (int l = 0; l < 50; l++) {
                    int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    BlockPos blockPos = new BlockPos(m, n, o);
                    EntityType<?> entityType = zombieEntity.getType();
                    SpawnRestriction.Location location = SpawnRestriction.getLocation(entityType);
                    if (SpawnHelper.canSpawn(location, this.getWorld(), blockPos, entityType)
                            && SpawnRestriction.canSpawn(entityType, serverWorld, SpawnReason.REINFORCEMENT, blockPos, this.getWorld().random)) {
                        zombieEntity.setPosition(m, n, o);
                        if (!this.getWorld().isPlayerInRange(m, n, o, 7.0)
                                && this.getWorld().doesNotIntersectEntities(zombieEntity)
                                && this.getWorld().isSpaceEmpty(zombieEntity)
                                && !this.getWorld().containsFluid(zombieEntity.getBoundingBox())) {
                            zombieEntity.setTarget(livingEntity);
                            zombieEntity.initialize(serverWorld, this.getWorld().getLocalDifficulty(zombieEntity.getBlockPos()), SpawnReason.REINFORCEMENT, null, null);
                            serverWorld.spawnEntityAndPassengers(zombieEntity);
                            this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                                    .addPersistentModifier(new EntityAttributeModifier("Zombie reinforcement caller charge", -0.05F, EntityAttributeModifier.Operation.ADDITION));
                            zombieEntity.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                                    .addPersistentModifier(new EntityAttributeModifier("Zombie reinforcement callee charge", -0.05F, EntityAttributeModifier.Operation.ADDITION));
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        if (bl) {
            float f = this.getWorld().getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
            if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                target.setOnFireFor(2 * (int)f);
            }
        }

        return bl;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_STEP;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    public boolean onKilledOther(ServerWorld world, LivingEntity other) {
        boolean bl = super.onKilledOther(world, other);
        if ((world.getDifficulty() == Difficulty.NORMAL || world.getDifficulty() == Difficulty.HARD) && other instanceof VillagerEntity villagerEntity) {
            if (world.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return bl;
            }

            ZombieVillagerEntity zombieVillagerEntity = villagerEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
            if (zombieVillagerEntity != null) {
                zombieVillagerEntity.initialize(
                        world, world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), null
                );
                zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
                zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE));
                zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toNbt());
                zombieVillagerEntity.setXp(villagerEntity.getExperience());
                if (!this.isSilent()) {
                    world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, this.getBlockPos(), 0);
                }

                bl = false;
            }
        }

        return bl;
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

        this.idleAnimationState.start(0);

        this.applyAttributeModifiers(f);
        return entityData;
    }

    protected void applyAttributeModifiers(float chanceMultiplier) {
        this.initAttributes();

        if (this.random.nextFloat() < chanceMultiplier * 0.05F) {
            this.getAttributeInstance(ModEntityAttributes.STELLGE_SPAWN_REINFORCEMENTS)
                    .addPersistentModifier(
                            new EntityAttributeModifier("Leader bonus", this.random.nextDouble() * 0.25 + 0.5, EntityAttributeModifier.Operation.ADDITION)
                    );
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                    .addPersistentModifier(
                            new EntityAttributeModifier("Leader bonus", this.random.nextDouble() * 3.0 + 1.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
                    );
        }
    }
}
