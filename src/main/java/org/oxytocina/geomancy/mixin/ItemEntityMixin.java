package org.oxytocina.geomancy.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.IStorageItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getStack();
    @Shadow
    private int pickupDelay;
    @Shadow @Nullable
    private UUID owner;

    @Inject(method = "onPlayerCollision",
            at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;"))
    private void geomancy$pickup(PlayerEntity player, CallbackInfo ci) {
        var stack = getStack();
        int i = stack.getCount();
        if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUuid()))) {
            var ent = (ItemEntity) (Object) this;
            pickup(player,ent,stack);

            player.sendPickup(ent, i);
            if (stack.isEmpty()) {
                ent.discard();
                stack.setCount(i);
                pickupDelay = 100;
            }

            player.increaseStat(Stats.PICKED_UP.getOrCreateStat(stack.getItem()), i);
            player.triggerItemPickedUpByEntityCriteria(ent);

            ent.setStack(stack);
        }
    }

    @Unique
    public void pickup(PlayerEntity player, ItemEntity entity, ItemStack stack) {
        var inv = player.getInventory();
        for(int i = 0; i<inv.size();i++) {
            var contender = inv.getStack(i);
            if(contender.isEmpty()) continue;
            if(contender.getItem() instanceof IStorageItem storageItem){
                if(!storageItem.autocollects()) continue;
                storageItem.tryCollect(contender,entity,player,stack);
                if(stack.isEmpty()) return;
            }
        }

    }
}