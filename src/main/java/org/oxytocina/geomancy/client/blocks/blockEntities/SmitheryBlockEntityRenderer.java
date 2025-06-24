package org.oxytocina.geomancy.client.blocks.blockEntities;

import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;

@Environment(EnvType.CLIENT)
public class SmitheryBlockEntityRenderer<T extends SmitheryBlockEntity> implements BlockEntityRenderer<T> {

    public SmitheryBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(SmitheryBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack baseStack = entity.getBaseStack();
        light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        if(!baseStack.isEmpty()) {
            matrices.push();

            float time = entity.getWorld().getTime() % 50000 + tickDelta;
            double height = 1 + Math.sin((time) / 8.0) / 6.0; // item height

            matrices.translate(0.5, 0.5 + height, 0.5);
            matrices.multiply(client.getBlockEntityRenderDispatcher().camera.getRotation());
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            MinecraftClient.getInstance().getItemRenderer().renderItem(baseStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }

        for (int i = 0; i < SmitheryBlockEntity.INPUT_SLOT_COUNT; i++) {
            if(i==SmitheryBlockEntity.BASE_SLOT && !baseStack.isEmpty()) continue;
            ItemStack ingredientStack = entity.inputInventory().getStack(i);
            if (!ingredientStack.isEmpty()) {
                matrices.push();

                int count = ingredientStack.getCount();
                if (count > 0) {
                    matrices.translate(getXOffset(i), 1, getZOffset(i));
                    matrices.scale(0.3f,0.3f,0.3f);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(35*i));
                    MinecraftClient.getInstance().getItemRenderer().renderItem(ingredientStack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);

                    /*if (count > 4) {
                        matrices.translate(0.45, 0.0, 0.01);
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(140));
                        MinecraftClient.getInstance().getItemRenderer().renderItem(ingredientStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);

                        if (count > 16) {
                            matrices.translate(0.2, 0.5, 0.01);
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(100));
                            MinecraftClient.getInstance().getItemRenderer().renderItem(ingredientStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);

                            if (count > 32) {
                                matrices.translate(-0.55, 0.0, 0.01);
                                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40));
                                MinecraftClient.getInstance().getItemRenderer().renderItem(ingredientStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);

                                if (count > 48) {
                                    matrices.translate(0.6, 0.0, 0.01);
                                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(170));
                                    MinecraftClient.getInstance().getItemRenderer().renderItem(ingredientStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
                                }
                            }
                        }
                    }*/
                }

                matrices.pop();
            }
        }


    }

    @Override
    public int getRenderDistance() {
        return 16;
    }

    private float getXOffset(int index){
        return 0.5f+((index%3)-1)*((index/3)==1?0.3f:0.25f);
    }

    private float getZOffset(int index){
        return 0.5f+((index/3)-1)*((index%3)==1?0.3f:0.25f);
    }

}
