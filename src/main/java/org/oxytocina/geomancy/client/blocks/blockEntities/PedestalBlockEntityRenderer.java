package org.oxytocina.geomancy.client.blocks.blockEntities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.PedestalBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.client.GeomancyClient;

@Environment(EnvType.CLIENT)
public class PedestalBlockEntityRenderer<T extends PedestalBlockEntity> implements BlockEntityRenderer<T> {

    public PedestalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(PedestalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack baseStack = entity.getStack(0);
        light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        if(!baseStack.isEmpty()) {
            matrices.push();

            float time = entity.getWorld().getTime() % 50000 + tickDelta;
            double height = 1 + Math.sin((time) / 8.0) / 6.0; // item height

            matrices.translate(0.5, 0.5 + height, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(GeomancyClient.tick/20f/10f*360));
            MinecraftClient.getInstance().getItemRenderer().renderItem(baseStack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }

    }

    @Override
    public int getRenderDistance() {
        return 32;
    }

}
