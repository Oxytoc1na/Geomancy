package org.oxytocina.geomancy.client.blocks.blockEntities;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;

@Environment(EnvType.CLIENT)
public class SpellmakerBlockEntityRenderer<T extends SpellmakerBlockEntity> implements BlockEntityRenderer<T> {

    public SpellmakerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        TexturedModelData texturedModelData = getTexturedModelData();
        root = texturedModelData.createModel();
        var fakeRoot = root.getChild("root");
        driver = fakeRoot.getChild("driver");
        piston = fakeRoot.getChild("piston");
        cap = fakeRoot.getChild("cap");
    }

    private static final SpriteIdentifier SPRITE_IDENTIFIER = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Geomancy.locate("block/compacting_chest"));
    private final ModelPart root;
    private final ModelPart driver;
    private final ModelPart piston;
    private final ModelPart cap;

    public static @NotNull TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(81, 44).cuboid(-1.5F, -10.0F, -1.5F, 3.0F, 9.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-7.0F, -11.0F, -7.0F, 14.0F, 10.0F, 14.0F, new Dilation(0.0F))
                .uv(0, 60).cuboid(-5.0F, -11.0F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(0, 43).cuboid(-7.5F, -2.0F, -7.5F, 15.0F, 2.0F, 15.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        //ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(81, 44).cuboid(-1.5F, -10.0F, -1.5F, 3.0F, 9.0F, 3.0F, new Dilation(0.0F))
        //        .uv(0, 0).cuboid(-7.0F, -11.0F, -7.0F, 14.0F, 10.0F, 14.0F, new Dilation(0.0F))
        //        .uv(0, 60).cuboid(-5.0F, -11.0F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F))
        //        .uv(0, 43).cuboid(-7.5F, -2.0F, -7.5F, 15.0F, 2.0F, 15.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        for (int i = 0; i < 3; i++) {
            ModelPartData driver = root.addChild("driver",
                    ModelPartBuilder.create()
                            .uv(53, 38)
                            .cuboid(-3.5F, -36.0F, -3.5F, 7.0F, 11.0F, 7.0F, new Dilation(0.0F)),
                    ModelTransform.pivot(0.0F, 21.0F, 0.0F));
        }
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        boolean bl = world != null;
        BlockState blockState = bl ? entity.getCachedState() : ModBlocks.SPELLMAKER.getDefaultState();
        matrixStack.push();
        float f = blockState.contains(ChestBlock.FACING) ? blockState.get(ChestBlock.FACING).asRotation() : 0;
        matrixStack.translate(0.5D, 1.5D, 0.5D);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        var pistonPos = 14;
        var driverPos = 6;
        var capPos = 5;
        piston.pivotY = -22 - pistonPos;
        driver.pivotY = 21 - driverPos;
        cap.pivotY = 21 - capPos;

        VertexConsumer vertexConsumer = SPRITE_IDENTIFIER.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutoutNoCull);
        root.render(matrixStack, vertexConsumer, light, overlay);

        matrixStack.pop();
    }

    @Override
    public int getRenderDistance() {
        return 64;
    }
}
