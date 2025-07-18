package org.oxytocina.geomancy.client.blocks.blockEntities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellstorerBlockEntity;

@Environment(EnvType.CLIENT)
public class SpellstorerBlockEntityRenderer<T extends SpellstorerBlockEntity> implements BlockEntityRenderer<T> {

    public SpellstorerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(SpellstorerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
    }

    @Override
    public int getRenderDistance() {
        return 16;
    }

}
