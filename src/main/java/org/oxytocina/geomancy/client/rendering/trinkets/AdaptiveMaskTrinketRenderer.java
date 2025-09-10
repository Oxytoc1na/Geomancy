package org.oxytocina.geomancy.client.rendering.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.oxytocina.geomancy.effects.ModStatusEffect;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.oxytocina.geomancy.items.ModItems;

public class AdaptiveMaskTrinketRenderer implements TrinketRenderer {
    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if(entity instanceof ClientPlayerEntity player){
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            TrinketRenderer.translateToFace(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>) contextModel, player, headYaw, headPitch);
            matrices.scale(0.55f,0.55f,0.55f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            ItemStack displayedStack = stack;
            float healthFraction = player.getHealth() / player.getMaxHealth();
            if(player.hasStatusEffect(ModStatusEffects.ECSTATIC)) displayedStack = ModItems.MANIA_MASK.getDefaultStack();
            else if(player.hasStatusEffect(ModStatusEffects.PARANOIA)) displayedStack = ModItems.PARANOIA_MASK.getDefaultStack();
            else if(player.hasStatusEffect(ModStatusEffects.MOURNING)) displayedStack = ModItems.SORROW_MASK.getDefaultStack();
            else if(player.hasStatusEffect(ModStatusEffects.REGRETFUL)) displayedStack = ModItems.MELANCHOLY_MASK.getDefaultStack();
            else if(healthFraction <= 0.25f) displayedStack = ModItems.PARANOIA_MASK.getDefaultStack();
            else if(healthFraction <= 0.5f) displayedStack = ModItems.SORROW_MASK.getDefaultStack();
            else if(healthFraction <= 0.75f) displayedStack = ModItems.MELANCHOLY_MASK.getDefaultStack();
            itemRenderer.renderItem(displayedStack, ModelTransformationMode.FIXED,light, OverlayTexture.DEFAULT_UV,matrices,vertexConsumers,entity.getWorld(),0);
        }

    }
}
