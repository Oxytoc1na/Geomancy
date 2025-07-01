package org.oxytocina.geomancy.mixin;


import org.oxytocina.geomancy.entity.TouchingWaterAware;
import org.oxytocina.geomancy.registries.*;
import net.minecraft.entity.*;
import net.minecraft.fluid.*;
import net.minecraft.registry.tag.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(Entity.class)
public abstract class EntityApplyFluidsMixin implements TouchingWaterAware {

    @Final
    @Shadow
    private Set<TagKey<Fluid>> submergedFluidTag;


    @Unique
    private boolean actuallyTouchingWater = false;

    @Override
    public boolean geomancy$isActuallyTouchingWater() {
        return this.actuallyTouchingWater;
    }

    @Override
    public void geomancy$setActuallyTouchingWater(boolean actuallyTouchingWater) { this.actuallyTouchingWater = actuallyTouchingWater; }


    @Inject(method = "isSubmergedIn", at = @At("RETURN"), cancellable = true)
    public void geomancy$isSubmergedIn(TagKey<Fluid> fluidTag, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && fluidTag == FluidTags.WATER) {
            cir.setReturnValue(this.submergedFluidTag.contains(ModFluidTags.SWIMMABLE_FLUID));
        }
    }

    @Inject(method = "isSubmergedInWater", at = @At("RETURN"), cancellable = true)
    public void geomancy$isSubmergedInWater(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && this.submergedFluidTag.contains(ModFluidTags.SWIMMABLE_FLUID)) {
            //this.submergedFluidTag.add(FluidTags.WATER);
            cir.setReturnValue(true);
        }
    }







}
