package org.oxytocina.geomancy.client.blocks.blockEntities;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.client.screen.SpellmakerScreen;
import org.oxytocina.geomancy.client.screen.SpellmakerScreenHandler;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.lang.Math;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SpellmakerBlockEntityRenderer<T extends SpellmakerBlockEntity> implements BlockEntityRenderer<T> {

    public SpellmakerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        TexturedModelData texturedModelData = getTexturedModelData();
        root = texturedModelData.createModel();
    }

    private static final SpriteIdentifier SPRITE_IDENTIFIER = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Geomancy.locate("block/spellmaker"));
    private final ModelPart root;

    public static @NotNull TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        //ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create()
        //                .uv(0, 0)
        //                .cuboid(0, 0, 0, 3.0F, 9.0F, 3.0F, new Dilation(0.0F))
        //        , ModelTransform.pivot(0.0F, 0, 0.0F));

        // upper
        {
            final int height = 4;
            final float widthM = 1;

            // top
            var builder = ModelPartBuilder.create()
                    .uv(32-16, 0);
            builder.cuboidData.add(
                    new ModelCuboidData((String)null,
                            (float)builder.textureX,
                            (float)builder.textureY,
                            -8,8-0.05f,-8,
                            16,0,16,
                            new Dilation(0.0F),
                            builder.mirror, 1.0F, 1.0F,
                            EnumSet.of(Direction.DOWN)));
            ModelPartData top = modelPartData.addChild("upper_top",builder,
                    ModelTransform.pivot(0F, 0, 0F));

            // bottom
            builder = ModelPartBuilder.create()
                    .uv(32-16, 0);
            builder.cuboidData.add(
                    new ModelCuboidData((String)null,
                            (float)builder.textureX,
                            (float)builder.textureY,
                            -8,8+0.05f+height,-8,
                            16,0,16,
                            new Dilation(0.0F),
                            builder.mirror, 1.0F, 1.0F,
                            EnumSet.of(Direction.UP)));
            ModelPartData bottom = modelPartData.addChild("upper_bottom",builder,
                    ModelTransform.pivot(0F, 0, 0F));

            // walls
            for (int i = 0; i < 6; i++) {
                builder = ModelPartBuilder.create()
                        .uv(0, 16);
                builder.cuboidData.add(
                        new ModelCuboidData((String)null,
                                (float)builder.textureX,
                                (float)builder.textureY,
                                6.9f-2,8+height/2f,-4,
                                2,height/2f,8,
                                new Dilation(0.0F),
                                builder.mirror, 1.0F, 1.0F,
                                EnumSet.of(Direction.EAST,Direction.UP)));
                ModelPartData wall = modelPartData.addChild("upper_wall"+i,builder,
                        ModelTransform.pivot(0F, 0, 0F));
            }

            for (int i = 0; i < 6; i++) {
                builder = ModelPartBuilder.create()
                        .uv(0, 0);
                builder.cuboidData.add(
                        new ModelCuboidData((String)null,
                                (float)builder.textureX,
                                (float)builder.textureY,
                                6.9f-2,8,-4,
                                2,height/2f,8,
                                new Dilation(0.0F),
                                builder.mirror, 1.0F, 1.0F,
                                EnumSet.of(Direction.EAST,Direction.DOWN)));
                ModelPartData wall = modelPartData.addChild("upper_wall_oct"+i,builder,
                        ModelTransform.pivot(0F, 0, 0F));
            }
        }

        // middle
        {
            final int height = 16-4*2;
            final int width = 7;
            final int y = 4;
            final int wallDepth = 0;

            ModelPartBuilder builder = null;

            // walls
            for (int i = 0; i < 6; i++) {
                builder = ModelPartBuilder.create()
                        .uv(0, 48);
                builder.cuboidData.add(
                        new ModelCuboidData((String)null,
                                (float)builder.textureX,
                                (float)builder.textureY,
                                (6.9f-wallDepth)*(width/8f),y+8,-width/2f,
                                wallDepth,height,width,
                                new Dilation(0.0F),
                                builder.mirror, 1.0F, 1.0F,
                                EnumSet.of(Direction.EAST)));
                ModelPartData wall = modelPartData.addChild("middle_wall"+i,builder,
                        ModelTransform.pivot(0F, 0, 0F));
            }
        }

        // lower
        {
            final int height = 4;
            final float widthM = 1;
            final int y = 16-height;

            // top
            var builder = ModelPartBuilder.create()
                    .uv(32-16, 16);
            builder.cuboidData.add(
                    new ModelCuboidData((String)null,
                            (float)builder.textureX,
                            (float)builder.textureY,
                            -8,y+8+-0.05f,-8,
                            16,0,16,
                            new Dilation(0.0F),
                            builder.mirror, 1.0F, 1.0F,
                            EnumSet.of(Direction.DOWN)));
            ModelPartData top = modelPartData.addChild("lower_top",builder,
                    ModelTransform.pivot(0F, 0, 0F));

            // bottom
            builder = ModelPartBuilder.create()
                    .uv(32-16, 16);
            builder.cuboidData.add(
                    new ModelCuboidData((String)null,
                            (float)builder.textureX,
                            (float)builder.textureY,
                            -8,y+8+0.05f+height,-8,
                            16,0,16,
                            new Dilation(0.0F),
                            builder.mirror, 1.0F, 1.0F,
                            EnumSet.of(Direction.UP)));
            ModelPartData bottom = modelPartData.addChild("lower_bottom",builder,
                    ModelTransform.pivot(0F, 0, 0F));

            // walls
            for (int i = 0; i < 6; i++) {
                builder = ModelPartBuilder.create()
                        .uv(0, 32);
                builder.cuboidData.add(
                        new ModelCuboidData((String)null,
                                (float)builder.textureX,
                                (float)builder.textureY,
                                6.9f-2,y+8,-4,
                                2,height,8,
                                new Dilation(0.0F),
                                builder.mirror, 1.0F, 1.0F,
                                EnumSet.of(Direction.EAST,Direction.UP,Direction.DOWN)));
                ModelPartData wall = modelPartData.addChild("lower_wall"+i,builder,
                        ModelTransform.pivot(0F, 0, 0F));
            }
        }

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        boolean bl = world != null;
        BlockState blockState = bl ? entity.getCachedState() : ModBlocks.SPELLMAKER.getDefaultState();

        // render model
        matrixStack.push();
        float f = blockState.contains(ChestBlock.FACING) ? blockState.get(ChestBlock.FACING).asRotation() : 0;
        matrixStack.translate(0.5D, 1.5D, 0.5D);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        // rotate walls
        for (int i = 0; i < 6; i++) {
            var part = root.getChild("upper_wall"+i);
            part.yaw = (float)Math.PI*2*i/6f + (float)Math.PI*0.5f;
        }
        for (int i = 0; i < 6; i++) {
            var part = root.getChild("lower_wall"+i);
            part.yaw = (float)Math.PI*2*i/6f + (float)Math.PI*0.5f;
        }
        for (int i = 0; i < 6; i++) {
            var part = root.getChild("middle_wall"+i);
            part.yaw = (float)Math.PI*2*i/6f + (float)Math.PI*0.5f;
        }

        // render octangulite models
        VertexConsumer vertexConsumer = SPRITE_IDENTIFIER.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
        var colVec = Toolbox.colorIntToVec(ModColorizationHandler.octanguliteItemBarNoise(entity.hasOutput()?1:0,0.5f));

        for (int i = 0; i < 6; i++) {
            var part = root.getChild("upper_wall_oct"+i);
            part.yaw = (float)Math.PI*2*i/6f + (float)Math.PI*0.5f;
            part.visible=true;
            part.render(matrixStack,vertexConsumer,light,overlay,colVec.x,colVec.y,colVec.z,1);
            part.visible=false;
        }
        var part = root.getChild("upper_top");
        part.visible=true;
        part.render(matrixStack,vertexConsumer,light,overlay,colVec.x,colVec.y,colVec.z,1);
        part.visible=false;



        root.render(matrixStack, vertexConsumer, light, overlay);

        matrixStack.pop();

        // render spell storage
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack baseStack = entity.getOutput();
        light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        if(!baseStack.isEmpty()) {
            matrixStack.push();

            float time = entity.getWorld().getTime() % 50000 + tickDelta;
            double height = 1 + Math.sin((time) / 8.0) / 6.0; // item height

            matrixStack.translate(0.5, 0.5 + height, 0.5);
            matrixStack.multiply(client.getBlockEntityRenderDispatcher().camera.getRotation());
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            MinecraftClient.getInstance().getItemRenderer().renderItem(baseStack, ModelTransformationMode.GROUND, light, overlay, matrixStack, vertexConsumers, entity.getWorld(), 0);
            matrixStack.pop();
        }

        // render grid
        var grid = SpellStoringItem.readGrid(baseStack);
        if(grid!=null){
            for (var compPos : grid.components.keySet()){
                var comp = grid.getComponent(compPos);
                matrixStack.push();

                double height = 1; // item height

                float scale = 0.6f/grid.width;

                float hexWidth = 1;
                float hexHeight = 1;

                float fieldDrawOffsetX = (1-(grid.height >> 1)%2)*hexWidth*0.5f;
                float fieldDrawOffsetY = 0;

                int yskew = compPos.y%2;


                float offsetX = scale * (fieldDrawOffsetX + (compPos.x-grid.width/2f+yskew/2f)*hexWidth);
                float offsetY = scale * (fieldDrawOffsetY + (compPos.y+0.5f-grid.height/2f)*hexHeight);

                ItemStack compStack = comp.getItemStack();

                matrixStack.translate(0.5 + offsetX, height, 0.5 + offsetY);
                matrixStack.scale(scale,scale,scale);
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90F));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                MinecraftClient.getInstance().getItemRenderer().renderItem(
                        compStack,
                        ModelTransformationMode.FIXED,
                        light, overlay, matrixStack, vertexConsumers,
                        entity.getWorld(), 0);
                matrixStack.pop();
            }
        }
    }

    public void renderQuads(ModelPart.Quad[] quads, MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        Matrix4f position = entry.getPositionMatrix();
        Matrix3f normal = entry.getNormalMatrix();

        for (ModelPart.Quad quad : quads) {
            renderQuad(quad,normal,position,vertexConsumer,light, overlay, red, green, blue, alpha);
        }
    }

    public void renderQuad(ModelPart.Quad quad, Matrix3f normal, Matrix4f position,VertexConsumer vertexConsumer,int light, int overlay, float red, float green, float blue, float alpha){
        Vector3f vector3f = normal.transform(new Vector3f(quad.direction));
        float f = vector3f.x();
        float g = vector3f.y();
        float h = vector3f.z();

        for (ModelPart.Vertex vertex : quad.vertices) {
            float i = vertex.pos.x() / 16.0F;
            float j = vertex.pos.y() / 16.0F;
            float k = vertex.pos.z() / 16.0F;
            Vector4f vector4f = position.transform(new Vector4f(i, j, k, 1.0F));
            vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
        }
    }

    @Override
    public int getRenderDistance() {
        return 64;
    }
}
