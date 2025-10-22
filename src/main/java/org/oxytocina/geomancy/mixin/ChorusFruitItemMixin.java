package org.oxytocina.geomancy.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.spells.SpellContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChorusFruitItem.class)
public class ChorusFruitItemMixin {

    @Inject(method="finishUsing",at=@At(value= "HEAD"),cancellable = true)
    public void geomancy$finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if(world instanceof ServerWorld sw && user instanceof ServerPlayerEntity spe){
            var playerRestrictions = RestrictorBlockEntity.getRestrictionsFor(spe);
            if(playerRestrictions.allowsTeleports()) return;
            RestrictorBlockEntity.registerPFA(RestrictorBlockEntity.PotentiallyForbiddenAction.createTeleport(
                    SpellContext.ofCaster(user),
                    user.getPos(),user.getPos()
            ));
            cir.setReturnValue(user.eatFood(world,stack));
        }
    }
}
