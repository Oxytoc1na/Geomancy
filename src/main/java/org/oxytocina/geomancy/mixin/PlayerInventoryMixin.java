package org.oxytocina.geomancy.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.items.ManaStoringItem;
import org.oxytocina.geomancy.util.ManaUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow public abstract ItemStack getStack(int slot);

    @Shadow @Final public PlayerEntity player;

    @Shadow public int selectedSlot;

    @Inject(method = "addStack(Lnet/minecraft/item/ItemStack;)I", at = @At("TAIL"))
    public void geomancy$addStack(ItemStack stack, CallbackInfoReturnable<Integer> cir){
        geomancy$checkManaHolders(stack);
    }

    @Inject(method = "addStack(ILnet/minecraft/item/ItemStack;)I", at = @At("TAIL"))
    public void geomancy$addStack(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir){
        geomancy$checkManaHolders(stack);
    }

    @Inject(method = "removeStack(I)Lnet/minecraft/item/ItemStack;", at = @At("TAIL"))
    public void geomancy$removeStack(int slot, CallbackInfoReturnable<ItemStack> cir){
        geomancy$checkManaHolders(this.getStack(slot));
    }

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("TAIL"))
    public void geomancy$removeStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir){
        geomancy$checkManaHolders(this.getStack(slot));
    }

    @Inject(method = "setStack", at = @At("TAIL"))
    public void geomancy$setStack(int slot, ItemStack stack, CallbackInfo ci){
        geomancy$checkManaHolders(stack);
    }

    @Inject(method = "dropSelectedItem", at = @At("TAIL"))
    public void geomancy$dropSelectedItem(boolean entireStack, CallbackInfoReturnable<ItemStack> cir){
        geomancy$checkManaHolders(this.getStack(this.selectedSlot));
    }

    @Unique
    public void geomancy$checkManaHolders(ItemStack stack)
    {
        if(!(this.player instanceof ServerPlayerEntity serverPlayer)) return;

        //if(stack.getItem() instanceof ManaStoringItem){
        ManaUtil.queueRecalculateMana(this.player);
        //}
    }
}
