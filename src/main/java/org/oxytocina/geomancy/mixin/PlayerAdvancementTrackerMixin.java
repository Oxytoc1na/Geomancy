package org.oxytocina.geomancy.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.networking.packet.S2C.ClientAdvancementS2CPacket;
import org.oxytocina.geomancy.progression.advancement.ModAdvancementCriterion;
import org.oxytocina.geomancy.progression.advancement.ModCriteria;
import org.oxytocina.geomancy.util.StellgeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/Advancement;getRewards()Lnet/minecraft/advancement/AdvancementRewards;"))
    private void geomancy$advancementObtained(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        ClientAdvancementS2CPacket.send(this.owner,advancement.getId());
        StellgeUtil.syncKnowledge(this.owner);
        ModCriteria.ADVANCEMENT.trigger(this.owner,advancement.getId());
    }
}
