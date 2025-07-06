package org.oxytocina.geomancy.mixin;


import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.oxytocina.geomancy.entity.TouchingFluidAware;
import org.oxytocina.geomancy.registries.*;
import net.minecraft.entity.*;
import net.minecraft.fluid.*;
import net.minecraft.registry.tag.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(Entity.class)
public abstract class EntityApplyFluidsMixin implements TouchingFluidAware {

    @Final
    @Shadow
    private Set<TagKey<Fluid>> submergedFluidTag;


    @Unique
    private boolean touchingExtinguishingFluid = false;

    @Override
    public boolean geomancy$isTouchingExtinguishingFluid() {
        return this.touchingExtinguishingFluid;
    }

    @Override
    public void geomancy$setTouchingExtinguishingFluid(boolean touchingExtinguishingFluid) { this.touchingExtinguishingFluid = touchingExtinguishingFluid; }


    @Inject(method = "isSubmergedIn", at = @At("RETURN"), cancellable = true)
    public void geomancy$isSubmergedIn(TagKey<Fluid> fluidTag, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && fluidTag == FluidTags.WATER) {
            cir.setReturnValue(this.submergedFluidTag.contains(ModFluidTags.SWIMMABLE_FLUID));
        }
    }

    @Inject(method = "isSubmergedInWater", at = @At("RETURN"), cancellable = true)
    public void geomancy$isSubmergedInWater(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && this.submergedFluidTag.contains(ModFluidTags.SWIMMABLE_FLUID)) {
            this.submergedFluidTag.add(FluidTags.WATER);
            cir.setReturnValue(true);
        }
    }

    @Shadow
    abstract public World getWorld();

    @Shadow
    abstract public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed);

    // detect being in viscous fluids
    @Inject(method = "updateWaterState", at = @At("RETURN"), cancellable = true)
    protected void geomancy$updateWaterState(CallbackInfoReturnable<Boolean> cir) {
        double d = this.getWorld().getDimension().ultrawarm() ? 0.007 : 0.0023333333333333335;
        boolean inViscous = this.updateMovementInFluid(ModFluidTags.VISCOUS_FLUID, d);
        if(inViscous) cir.setReturnValue(true);
    }


}
