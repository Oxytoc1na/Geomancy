package org.oxytocina.geomancy.client.blocks.blockEntities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.blockEntities.ShiftBlockEntity;

@Environment(EnvType.CLIENT)
public class ShiftBlockEntityRenderer implements BlockEntityRenderer<ShiftBlockEntity> {
	private final BlockRenderManager manager;

	public ShiftBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.manager = ctx.getRenderManager();
	}

	public void render(ShiftBlockEntity shiftBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		World world = shiftBlockEntity.getWorld();
		if (world != null) {
			BlockPos blockPosPushedFrom = shiftBlockEntity.getPos().offset(shiftBlockEntity.getMovementDirection().getOpposite());
			BlockState pushedState = shiftBlockEntity.getPushedBlock();
			if (!pushedState.isAir()) {
				BlockModelRenderer.enableBrightnessCache();
				matrixStack.push();
				matrixStack.translate(shiftBlockEntity.getRenderOffsetX(tickDelta), shiftBlockEntity.getRenderOffsetY(tickDelta), shiftBlockEntity.getRenderOffsetZ(tickDelta));
				this.renderModel(blockPosPushedFrom, pushedState, matrixStack, vertexConsumerProvider, world, false, j);
				matrixStack.pop();
				BlockModelRenderer.disableBrightnessCache();
			}
		}
	}

	private void renderModel(BlockPos pos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, boolean cull, int overlay) {
		RenderLayer renderLayer = RenderLayers.getMovingBlockLayer(state);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
		this.manager
			.getModelRenderer()
			.render(world, this.manager.getModel(state), state, pos, matrices, vertexConsumer, cull, Random.create(), state.getRenderingSeed(pos), overlay);
	}

	@Override
	public int getRenderDistance() {
		return 68;
	}
}
