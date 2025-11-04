package org.oxytocina.geomancy.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method= "canModifyBlocks()Z",cancellable = true,at=@At(value="HEAD"))
    public void geomancy$canModifyBlocks(CallbackInfoReturnable<Boolean> cir){
        if(geomancy$hasCreativeBlock()) cir.setReturnValue(false);
    }

    @Inject(method= "canPlaceOn",cancellable = true,at=@At(value="HEAD"))
    public void geomancy$canPlaceOn(CallbackInfoReturnable<Boolean> cir){
        if(geomancy$hasCreativeBlock()) cir.setReturnValue(false);
    }

    @Inject(method="isBlockBreakingRestricted",cancellable = true,at=@At(value="HEAD"))
    public void geomancy$isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir){
        if(geomancy$hasCreativeBlock()) cir.setReturnValue(true);
    }

    @Unique public boolean geomancy$hasCreativeBlock(){
        var player = (PlayerEntity)(Object)this;
        return player.hasStatusEffect(ModStatusEffects.CREATIVE_SHOCK);
    }
}
