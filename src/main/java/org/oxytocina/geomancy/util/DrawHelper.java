package org.oxytocina.geomancy.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class DrawHelper {
    public static void drawLine(DrawContext context, float x1, float y1, float x2, float y2, float thickness, int color) {
        Vector2f pos1 = new Vector2f(x1,y1);
        Vector2f pos2 = new Vector2f(x2,y2);
        Vector2f direction = new Vector2f(pos2.x-pos1.x,pos2.y-pos1.y).normalize();
        Vector2f negDir = new Vector2f(direction).negate();
        Vector2f dirPep = new Vector2f(direction).perpendicular();

        Vector2f v1 = new Vector2f(pos1).add(new Vector2f(negDir).mul(thickness)).add(new Vector2f(dirPep).mul(thickness));
        Vector2f v2 = new Vector2f(pos1).add(new Vector2f(negDir).mul(thickness)).add(new Vector2f(dirPep).mul(-thickness));
        Vector2f v3 = new Vector2f(pos2).add(new Vector2f(direction).mul(thickness)).add(new Vector2f(dirPep).mul(thickness));
        Vector2f v4 = new Vector2f(pos2).add(new Vector2f(direction).mul(thickness)).add(new Vector2f(dirPep).mul(-thickness));

        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

        float f = (float) ColorHelper.Argb.getAlpha(color) / 255.0F;
        float g = (float) ColorHelper.Argb.getRed(color) / 255.0F;
        float h = (float) ColorHelper.Argb.getGreen(color) / 255.0F;
        float j = (float) ColorHelper.Argb.getBlue(color) / 255.0F;
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        vertexConsumer.vertex(matrix4f, v1.x,v1.y, 0).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, v2.x,v2.y, 0).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, v4.x,v4.y, 0).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, v3.x,v3.y, 0).color(g, h, j, f).next();
        context.draw();
    }

    public static void drawTexture(
            MatrixStack matrices, Identifier texture, float x, float y, float width, float height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
    ) {
        drawTexture(matrices,texture, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }
    public static void drawTexture(
            MatrixStack matrices,Identifier texture, float x1, float x2, float y1, float y2, float z, float regionWidth, float regionHeight, float u, float v, int textureWidth, int textureHeight
    ) {
        drawTexturedQuad(
                matrices,texture, x1, x2, y1, y2, z, (u + 0.0F) / textureWidth, (u + regionWidth) / textureWidth, (v + 0.0F) / textureHeight, (v + regionHeight) / textureHeight
        );
    }
    public static void drawTexturedQuad(MatrixStack matrices, Identifier texture, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix4f, x1, y2, z).texture(u1, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y2, z).texture(u2, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y1, z).texture(u2, v1).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public static void drawItem(DrawContext ctx,@Nullable LivingEntity entity, @Nullable World world, ItemStack stack, float x, float y, int seed, float z, float r, float g, float b) {
        if (!stack.isEmpty()) {
            BakedModel bakedModel = MinecraftClient.getInstance().getItemRenderer().getModel(stack, world, entity, seed);
            ctx.getMatrices().push();
            ctx.getMatrices().translate((x + 8), (y + 8), (150 + (bakedModel.hasDepth() ? z : 0)));

            try {
                ctx.getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
                ctx.getMatrices().scale(16.0F, 16.0F, 16.0F);
                boolean bl = !bakedModel.isSideLit();
                if (bl) {
                    DiffuseLighting.disableGuiDepthLighting();
                }

                RenderSystem.setShaderColor(r,g,b,1);
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, ctx.getMatrices(), ctx.getVertexConsumers(), 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
                ctx.draw();
                if (bl) {
                    DiffuseLighting.enableGuiDepthLighting();
                }
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Rendering item");
                CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
                crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
                crashReportSection.add("Item Damage", () -> String.valueOf(stack.getDamage()));
                crashReportSection.add("Item NBT", () -> String.valueOf(stack.getNbt()));
                crashReportSection.add("Item Foil", () -> String.valueOf(stack.hasGlint()));
                throw new CrashException(crashReport);
            }

            ctx.getMatrices().pop();
        }
    }

    /// draws a nine-sliced texture while allowing its proportions to exceed half of the entire width/height
    public static void drawNineSlicedTexture(DrawContext ctx,Identifier texture, int x, int y, int width, int height, int leftSliceWidth, int topSliceHeight, int rightSliceWidth, int bottomSliceHeight, int centerSliceWidth, int centerSliceHeight, int u, int v) {
        if (width == centerSliceWidth && height == centerSliceHeight) {
            ctx.drawTexture(texture, x, y, u, v, width, height);
        } else if (height == centerSliceHeight) {
            ctx.drawTexture(texture, x, y, u, v, leftSliceWidth, height);
            ctx.drawRepeatingTexture(texture, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, height, u + leftSliceWidth, v, centerSliceWidth - rightSliceWidth - leftSliceWidth, centerSliceHeight);
            ctx.drawTexture(texture, x + width - rightSliceWidth, y, u + centerSliceWidth - rightSliceWidth, v, rightSliceWidth, height);
        } else if (width == centerSliceWidth) {
            ctx.drawTexture(texture, x, y, u, v, width, topSliceHeight);
            ctx.drawRepeatingTexture(texture, x, y + topSliceHeight, width, height - bottomSliceHeight - topSliceHeight, u, v + topSliceHeight, centerSliceWidth, centerSliceHeight - bottomSliceHeight - topSliceHeight);
            ctx.drawTexture(texture, x, y + height - bottomSliceHeight, u, v + centerSliceHeight - bottomSliceHeight, width, bottomSliceHeight);
        } else {
            ctx.drawTexture(texture, x, y, u, v, leftSliceWidth, topSliceHeight);
            ctx.drawRepeatingTexture(texture, x + leftSliceWidth, y, width - rightSliceWidth - leftSliceWidth, topSliceHeight, u + leftSliceWidth, v, centerSliceWidth - rightSliceWidth - leftSliceWidth, topSliceHeight);
            ctx.drawTexture(texture, x + width - rightSliceWidth, y, u + centerSliceWidth - rightSliceWidth, v, rightSliceWidth, topSliceHeight);
            ctx.drawTexture(texture, x, y + height - bottomSliceHeight, u, v + centerSliceHeight - bottomSliceHeight, leftSliceWidth, bottomSliceHeight);
            ctx.drawRepeatingTexture(texture, x + leftSliceWidth, y + height - bottomSliceHeight, width - rightSliceWidth - leftSliceWidth, bottomSliceHeight, u + leftSliceWidth, v + centerSliceHeight - bottomSliceHeight, centerSliceWidth - rightSliceWidth - leftSliceWidth, bottomSliceHeight);
            ctx.drawTexture(texture, x + width - rightSliceWidth, y + height - bottomSliceHeight, u + centerSliceWidth - rightSliceWidth, v + centerSliceHeight - bottomSliceHeight, rightSliceWidth, bottomSliceHeight);
            ctx.drawRepeatingTexture(texture, x, y + topSliceHeight, leftSliceWidth, height - bottomSliceHeight - topSliceHeight, u, v + topSliceHeight, leftSliceWidth, centerSliceHeight - bottomSliceHeight - topSliceHeight);
            ctx.drawRepeatingTexture(texture, x + leftSliceWidth, y + topSliceHeight, width - rightSliceWidth - leftSliceWidth, height - bottomSliceHeight - topSliceHeight, u + leftSliceWidth, v + topSliceHeight, centerSliceWidth - rightSliceWidth - leftSliceWidth, centerSliceHeight - bottomSliceHeight - topSliceHeight);
            ctx.drawRepeatingTexture(texture, x + width - rightSliceWidth, y + topSliceHeight, leftSliceWidth, height - bottomSliceHeight - topSliceHeight, u + centerSliceWidth - rightSliceWidth, v + topSliceHeight, rightSliceWidth, centerSliceHeight - bottomSliceHeight - topSliceHeight);
        }
    }

}
