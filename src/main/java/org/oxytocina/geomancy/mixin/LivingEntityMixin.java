package org.oxytocina.geomancy.mixin;

import com.llamalad7.mixinextras.injector.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.sound.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.oxytocina.geomancy.entity.TouchingWaterAware;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    @Nullable
    protected PlayerEntity attackingPlayer;

    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow
    public abstract ItemStack getMainHandStack();

    @Shadow
    @Nullable
    public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow
    public abstract boolean canHaveStatusEffect(StatusEffectInstance effect);

    @Shadow
    public abstract void readCustomDataFromNbt(NbtCompound nbt);

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Shadow
    public abstract boolean removeStatusEffect(StatusEffect type);

    @Shadow
    public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow
    public abstract ItemStack getOffHandStack();

    @Shadow
    public abstract int getArmor();

    @Shadow
    public abstract double getAttributeValue(EntityAttribute attribute);

    @Shadow public abstract void remove(Entity.RemovalReason reason);

    @Shadow
    public abstract void travel(Vec3d movementInput);

    @Shadow protected ItemStack activeItemStack;

    @Shadow
    protected abstract @Nullable SoundEvent getDeathSound();

    @Shadow
    protected abstract float getSoundVolume();

    @Shadow
    protected boolean dead;

    // FabricDefaultAttributeRegistry seems to only allow adding full containers and only single entity types?
    //@Inject(method = "createLivingAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", require = 1, allow = 1, at = @At("RETURN"))
    //private static void geomancy$addAttributes(final CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
    //    cir.getReturnValue().add(GeomancyEntityAttributes.MENTAL_PRESENCE);
    //}

    @ModifyArg(method = "dropXp()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"), index = 2)
    protected int geomancy$applyExtraXPDrops(int originalXP) {
        return (int) (originalXP * geomancy$getXpDropMultiplier(this.attackingPlayer));
    }

    @Unique
    private float geomancy$getXpDropMultiplier(PlayerEntity attackingPlayer) {
        //if (attackingPlayer != null && ModEnchantments.EXUBERANCE.canEntityUse(attackingPlayer)) {
        //    int exuberanceLevel = EnchantmentHelper.getEquipmentLevel(ModEnchantments.EXUBERANCE, attackingPlayer);
        //    return 1.0F + exuberanceLevel * GeomancyCommon.CONFIG.ExuberanceBonusExperiencePercentPerLevel;
        //} else {
        //    return 1.0F;
        //}
        return 1;
    }
    



    /*
    @ModifyReturnValue(method = "canWalkOnFluid", at = @At("RETURN"))
    private boolean geomancy$modifyFluidWalking(boolean original) {
        var entity = (LivingEntity) (Object) this;

        if (GeomancyTrinketItem.hasEquipped((LivingEntity) (Object) this, GeomancyItems.RING_OF_AERIAL_GRACE))
            return !entity.isSubmergedInWater();

        return original;
    }
    */

    /*
    @ModifyVariable(method = "damageArmor(Lnet/minecraft/entity/damage/DamageSource;F)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float geomancy$damageArmor(float amount, DamageSource source) {
        if (source.isIn(GeomancyDamageTypeTags.DOES_NOT_DAMAGE_ARMOR)) {
            return 0;
        }
        else if (source.isIn(GeomancyDamageTypeTags.INCREASED_ARMOR_DAMAGE)) {
            return amount * 10;
        }
        return amount;
    }
    */
    

    @Unique
    private float getToughness() {
        return (float) this.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
    }

    @ModifyExpressionValue(method = "handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;computeFallDamage(FF)I"))
    private int geomancy$puffCircletDamageNegation(int original) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        return original;
    }

    @ModifyVariable(at = @At("HEAD"), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", argsOnly = true)
    private float geomancy$modifyDamage(float amount, DamageSource source) {
        //@Nullable StatusEffectInstance vulnerability = getStatusEffect(ModStatusEffects.VULNERABILITY);
        //if (vulnerability != null) {
        //    amount *= 1 + (ModStatusEffects.VULNERABILITY_ADDITIONAL_DAMAGE_PERCENT_PER_LEVEL * vulnerability.getAmplifier());
        //}
        return amount;
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", ordinal = 0), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    private void geomancy$applyDamage1(LivingEntity instance, DamageSource source, float amount, Operation<Void> original) {
        //if (source.isIn(GeomancyDamageTypeTags.BYPASSES_DIKE)) {
            original.call(instance, source, amount);
            return;
        //}
        //instance.applyDamage(source, AzureDikeProvider.absorbDamage(instance, amount));
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", ordinal = 1), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    private void geomancy$applyDamage2(LivingEntity instance, DamageSource source, float amount, Operation<Void> original) {
        //if (source.isIn(GeomancyDamageTypeTags.BYPASSES_DIKE)) {
            original.call(instance, source, amount);
            return;
        //}
        //instance.applyDamage(source, AzureDikeProvider.absorbDamage(instance, amount));
    }

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isDead()Z", ordinal = 1))
    private void geomancy$TriggerArmorWithHitEffect(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        //LivingEntity thisEntity = (LivingEntity) (Object) this;
        //World world = thisEntity.getWorld();
        //if (!world.isClient) {
        //    if (thisEntity instanceof MobEntity thisMobEntity) {
        //        for (ItemStack armorItemStack : thisMobEntity.getArmorItems()) {
        //            if (armorItemStack.getItem() instanceof ArmorWithHitEffect armorWithHitEffect) {
        //                armorWithHitEffect.onHit(armorItemStack, source, thisMobEntity, amount);
        //            }
        //        }
        //    } else if (thisEntity instanceof ServerPlayerEntity thisPlayerEntity) {
        //        for (ItemStack armorItemStack : thisPlayerEntity.getArmorItems()) {
        //            if (armorItemStack.getItem() instanceof ArmorWithHitEffect armorWithHitEffect) {
        //                armorWithHitEffect.onHit(armorItemStack, source, thisPlayerEntity, amount);
        //            }
        //        }
        //    }
        //}
    }

    @Inject(at = @At("TAIL"), method = "applyFoodEffects(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)V")
    private void geomancy$eat(ItemStack stack, World world, LivingEntity targetEntity, CallbackInfo ci) {
        //Item item = stack.getItem();
        //if (item instanceof ApplyFoodEffectsCallback foodWithCallback) {
        //    foodWithCallback.afterConsumption(world, stack, (LivingEntity) (Object) this);
        //}
    }

    @Inject(method = "drop(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("HEAD"), cancellable = true)
    protected void drop(DamageSource source, CallbackInfo ci) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
    }

    @Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isWet()Z"))
    private boolean geomancy$isWet(LivingEntity livingEntity) {
        return livingEntity.isTouchingWater() ? ((TouchingWaterAware) livingEntity).geomancy$isActuallyTouchingWater() : livingEntity.isWet();
    }

}