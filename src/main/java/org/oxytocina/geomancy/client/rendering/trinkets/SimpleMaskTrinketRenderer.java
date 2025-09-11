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
import org.oxytocina.geomancy.util.Toolbox;

public class SimpleMaskTrinketRenderer implements TrinketRenderer {
    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if(entity instanceof ClientPlayerEntity player){
            var model = Toolbox.safeCast(contextModel,(PlayerEntityModel<AbstractClientPlayerEntity>) null);
            if(model==null) return;
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            TrinketRenderer.translateToFace(matrices, model, player, headYaw, headPitch);
            matrices.scale(0.55f,0.55f,0.55f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            itemRenderer.renderItem(stack, ModelTransformationMode.FIXED,light, OverlayTexture.DEFAULT_UV,matrices,vertexConsumers,entity.getWorld(),0);
        }

    }
}
