package org.oxytocina.geomancy.entity;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.oxytocina.geomancy.client.util.CamShakeUtil;
import org.oxytocina.geomancy.registries.ModBlockTags;
import org.oxytocina.geomancy.registries.ModDamageTypes;
import org.oxytocina.geomancy.util.EntityUtil;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.List;

public class LaunchedBlockEntity extends FallingBlockEntity {
    public LaunchedBlockEntity(EntityType<? extends LaunchedBlockEntity> entityType, World world) {
        super(entityType, world);
    }

    public float health = 10;

    private LaunchedBlockEntity(World world, double x, double y, double z, Vec3d velocity,boolean destroyedOnLanding, BlockState block, float health) {
        this(ModEntityTypes.LAUNCHED_BLOCK, world);
        this.destroyedOnLanding = destroyedOnLanding;
        this.block = block;
        this.intersectionChecked = true;
        this.setPosition(x, y, z);
        this.setVelocity(velocity);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.health=health;
        this.setFallingBlockPos(this.getBlockPos());
    }
    public static LaunchedBlockEntity spawnFromBlock(World world, BlockPos pos, Vec3d velocity, BlockState state, boolean destroyedOnLanding) {
        LaunchedBlockEntity launchedBlockEntity = new LaunchedBlockEntity(
                world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, velocity,destroyedOnLanding,
                state.contains(Properties.WATERLOGGED) ? state.with(Properties.WATERLOGGED, false) : state,
                getHealth(state)
        );
        if(!destroyedOnLanding)
            world.setBlockState(pos, state.getFluidState().getBlockState(), Block.NOTIFY_ALL);
        world.spawnEntity(launchedBlockEntity);
        return launchedBlockEntity;
    }

    public Vec3d prevVelocity = null;
    @Override
    public void tick() {
        prevVelocity = getVelocity();
        super.tick();
        if(isRemoved()) return;
        tickMovement();
        if(isRemoved()) return;
        if(getWorld().isClient) tickParticles();
    }

    public void tickMovement() {
        Box box= this.getBoundingBox();//.expand((double)1.0F, (double)0.5F, (double)1.0F);

        List<Entity> otherEntities = this.getWorld().getOtherEntities(this, box);

        for(int i = 0; i < otherEntities.size(); ++i) {
            Entity entity = (Entity)otherEntities.get(i);
            if (!entity.isRemoved()) {
                this.collideWithEntity(entity);
                return;
            }
        }

        if(timeFalling < 5) return;

        // "detect" block collision (really janky because collision code isnt very user friendly)
        double prevXMovement = Math.abs(prevVelocity.x);
        double prevYMovement = Math.abs(prevVelocity.y);
        double prevZMovement = Math.abs(prevVelocity.z);
        var vel = getVelocity();
        double currentXMovement = Math.abs(vel.x);
        double currentYMovement = Math.abs(vel.y);
        double currentZMovement = Math.abs(vel.z);
        if(
                (prevXMovement > 0 && currentXMovement == 0)||
                (prevYMovement > 0 && currentYMovement == 0)||
                (prevZMovement > 0 && currentZMovement == 0)
        )
        {
            takeDamage((float)prevVelocity.distanceTo(vel)*4);
            if(isFragile()) destroy();
        }
    }

    public float particleCooldown = 0;
    public static final float TRAIL_PARTICLE_DISPERSION = 0.2f;
    public void tickParticles(){
        if(doesRenderOnFire()){
            // leave flame particles
            float movement = (float)getVelocity().length()*10;
            particleCooldown -= movement+1;
            while(particleCooldown<0){
                particleCooldown++;
                Vec3d pos = getPos();
                Vec3d pPos = new Vec3d(
                        pos.x+(Toolbox.random.nextFloat()*2-1)* TRAIL_PARTICLE_DISPERSION,
                        pos.y+0.6f+(Toolbox.random.nextFloat()*2-1)* TRAIL_PARTICLE_DISPERSION,
                        pos.z+(Toolbox.random.nextFloat()*2-1)* TRAIL_PARTICLE_DISPERSION);
                getWorld().addParticle(ParticleTypes.FLAME,pPos.x,pPos.y,pPos.z,0,0,0);
            }
        }
    }

    @Override
    public void onDestroyedOnLanding(Block block, BlockPos pos) {
        super.onDestroyedOnLanding(block, pos);
        crumble();
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
    }

    public void collideWithEntity(Entity entity) {
        impactEntity(entity);
    }

    public void impactEntity(Entity entity){
        if(!(entity instanceof LivingEntity le)) return;

        // damage mobs in small area
        var entities = getWorld().getOtherEntities(this,this.getBoundingBox().expand(1),e->e instanceof LivingEntity);
        for(var ent : entities){
            if(!(ent instanceof LivingEntity le2)) continue;
            damageEntity(le2,le==le2?1f:0.7f);
            knockbackEntity(le2);
        }

        takeDamage(getEntityWeight(le)*4);
    }

    public static float getHealth(BlockState state){
        return state.getBlock().getHardness() + state.getBlock().getBlastResistance();
    }

    public void takeDamage(float amount){
        health -= amount;
        if(!getWorld().isClient) crumble();
        if(health <= 0) destroy();
    }

    public void destroy(){
        if(this.isRemoved()) return;

        if(destroyedOnLanding)
        {
            //this.onDestroyedOnLanding(block.getBlock(), getBlockPos());
        }
        else{
            if (this.dropItem && this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                //this.onDestroyedOnLanding(block, blockPos);
                this.dropItem(block.getBlock());
            }
        }

        if(getWorld().isClient){

        }
        else{
            CamShakeUtil.cause(getWorld(),getPos(),15 + getWeight(),0.1f * getWeight());
        }

        this.discard();
    }

    public void crumble(){
        if(block.hasBlockBreakParticles()){
            ParticleUtil.ParticleData.createBlock(getWorld(),block,getPos().add(0,0.5f,0),prevVelocity.multiply(2.5f),200,0.5f).send();
        }
        Toolbox.playSound(isAnvil() ? SoundEvents.BLOCK_ANVIL_LAND : block.getSoundGroup().getBreakSound(),getWorld(),getBlockPos(), SoundCategory.BLOCKS,1);
    }

    public void damageEntity(LivingEntity target, float multiplier){
        target.damage(ModDamageTypes.of(getWorld(),ModDamageTypes.LAUNCH),(float)getDeltaVelocity(target).length() * getDamageMultiplier() * multiplier);

        if(isHot()){
            if (!target.isFireImmune()) {
                target.setOnFireFor(8);
                target.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }
        }

        if(isCold()){
            EntityUtil.freezeEntity(target,0,20*5);
        }
    }

    /// returns the damage dealt per differing velocity length
    public float getDamageMultiplier(){
        return 10*(
                block.isIn(ModBlockTags.LAUNCHED_STRONG) ? 4 : block.isIn(ModBlockTags.LAUNCHED_WEAK) ? 0.5f : block.isIn(ModBlockTags.LAUNCHED_HARMLESS) ? 0 : 1
        );
    }

    public float getKnockbackMultiplier(){
        return block.isIn(ModBlockTags.LAUNCHED_SUPERHEAVY) ? 3 : block.isIn(ModBlockTags.LAUNCHED_HEAVY) ? 2 : 1;
    }

    public float getWeight(){
        return block.isIn(ModBlockTags.LAUNCHED_SUPERHEAVY) ? 20 : block.isIn(ModBlockTags.LAUNCHED_HEAVY) ? 5 : 1;
    }

    public float getEntityWeight(LivingEntity le){
        float res = 1;
        res += (float) (10*le.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
        return res;
    }

    public boolean isAnvil(){
        return block.getBlock() instanceof AnvilBlock;
    }

    public boolean isHot(){
        return block.isIn(ModBlockTags.LAUNCHED_HOT);
    }

    public boolean isCold(){
        return block.isIn(ModBlockTags.LAUNCHED_COLD);
    }

    public boolean isFragile(){
        return getHealth(block) < 7;
    }

    public void knockbackEntity(LivingEntity target){
        double dominance = getWeight() / (getWeight() + getEntityWeight(target));
        Vec3d newVel = target.getVelocity().lerp(target.getVelocity().add(getDeltaVelocity(target).multiply(getKnockbackMultiplier())),dominance);
        target.setVelocity(newVel);
    }

    public Vec3d getDeltaVelocity(LivingEntity target){
        return getVelocity().subtract(target.getVelocity());
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        setVelocity(attacker.getRotationVector());
        return true;
    }

    @Override
    public boolean isOnGround() {
        if(timeFalling < 5) return false;
        return super.isOnGround();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("h",health);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        health = nbt.getFloat("h");
    }

    @Override
    public boolean doesRenderOnFire() {
        return isHot();
    }
}
