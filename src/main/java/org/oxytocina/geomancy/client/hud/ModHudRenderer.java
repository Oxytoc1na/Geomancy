package org.oxytocina.geomancy.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.util.ManaUtil;
import org.oxytocina.geomancy.util.Toolbox;

public class ModHudRenderer {

    public static float lastManaFraction = 0;
    public static float manaUseShake = 0;

    static float manaShakeX = 0;
    static float manaShakeY = 0;

    static float ambientManaArrowProgress = 0;

    private static final Identifier FILLED_THIRST = new Identifier(Geomancy.MOD_ID,
            "textures/item/artifact_of_gold.png");
    private static final Identifier EMPTY_THIRST = new Identifier(Geomancy.MOD_ID,
            "textures/item/empty_artifact.png");

    public static void onHudRender(DrawContext drawContext, PlayerEntity playerEntity) {

        MinecraftClient client = MinecraftClient.getInstance();
        if(client==null||client.options.hudHidden||playerEntity==null) return;
        int x = 0;
        int y = 0;
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        x = width / 2;
        y = height;

        renderManaBar(drawContext,playerEntity,x - 91);

    }

    final static Identifier MANA_BAR_TEXTURE = Geomancy.locate("textures/gui/icons.png");
    final static float MANA_BAR_TEXTURE_SIZE = 256;
    public static void renderManaBar(DrawContext context,PlayerEntity player, int x) {
        var client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        if (ManaUtil.getMaxMana(player)>0) {
            float manaFraction = Toolbox.clampF(ManaUtil.getMana(player) / ManaUtil.getMaxMana(player),0,1);

            float fractionDiff = manaFraction-lastManaFraction;

            if(fractionDiff<0)
                manaUseShake -= fractionDiff;


            int barWidth = (int)(manaFraction * 183.0F);
            final int barHeight = 5;
            int y = height - 32 + 3;
            // background
            //context.drawTexture(MANA_BAR_TEXTURE, x, l, 0, 64, 182, 5);

            int col = ModColorizationHandler.octanguliteItemBarNoise(manaFraction);
            var colVec = Toolbox.colorIntToVec(col);

            float barX = x +manaShakeX;
            float barY = y +manaShakeY;

            if (barWidth > 0) {
                float alpha = 0.3f+0.4f*(1+(float)Math.sin(GeomancyClient.tick/20f/3*2*Math.PI))/2f;
                drawTexturedQuad(MANA_BAR_TEXTURE,context.getMatrices(),barX,barX+barWidth,barY,barY+barHeight,0,(0)/MANA_BAR_TEXTURE_SIZE,(barWidth)/MANA_BAR_TEXTURE_SIZE,(15)/MANA_BAR_TEXTURE_SIZE,(15+barHeight)/MANA_BAR_TEXTURE_SIZE,colVec.x,colVec.y,colVec.z,alpha);
            }

            String string = Math.round(ManaUtil.getMana(player)) +" / "+ Math.round(ManaUtil.getMaxMana(player));
            if(showAmbientMana()){
                int t = Toolbox.floor(ambientManaArrowProgress*2.999f);
                string = Math.round(ManaUtil.getAmbientSoulsPerBlock(player.getWorld(),player.getBlockPos()))+" "+(t%3==0?">":"-")+(t%3==1?">":"-")+(t%3==2?">":"-")+" "+string;
            }
            float xPos = (width - client.textRenderer.getWidth(string)) / 2f + manaShakeX;
            float yPos = height - 31 - 4 + manaShakeY;
            if(player.experienceLevel>0)
                yPos -= 11;
            drawText(client.textRenderer, context, string, xPos + 1, yPos, 0, false);
            drawText(client.textRenderer, context, string, xPos - 1, yPos, 0, false);
            drawText(client.textRenderer, context, string, xPos, yPos + 1, 0, false);
            drawText(client.textRenderer, context, string, xPos, yPos - 1, 0, false);
            drawText(client.textRenderer, context, string, xPos, yPos, col, false);
            lastManaFraction=manaFraction;
        }

    }

    public static void tick(){
        manaUseShake = MathHelper.lerp(0.1f,manaUseShake,0);

        manaShakeX = (int)Math.round(Math.sin(Math.PI*4*GeomancyClient.tick/20f)*manaUseShake*20);
        manaShakeY = (int)Math.round(Math.sin(Math.PI*7.31f*GeomancyClient.tick/20f)*manaUseShake*10);

        if(showAmbientMana()){
            if(MinecraftClient.getInstance() != null && MinecraftClient.getInstance().cameraEntity!=null)
            {
                ambientManaArrowProgress += 1 / 20f / 400f * ManaUtil.getAmbientSoulsPerBlock(MinecraftClient.getInstance().world, MinecraftClient.getInstance().cameraEntity.getBlockPos());
                ambientManaArrowProgress = ambientManaArrowProgress%1;
            }
        }

    }

    public static boolean showAmbientMana(){
        // TODO: unlock to see regen speed
        return true;
    }

    static void drawTexturedQuad(Identifier texture, MatrixStack matrices, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2, float red, float green, float blue, float alpha) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).color(red, green, blue, alpha).texture(u1, v1).next();
        bufferBuilder.vertex(matrix4f, x1, y2, z).color(red, green, blue, alpha).texture(u1, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y2, z).color(red, green, blue, alpha).texture(u2, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y1, z).color(red, green, blue, alpha).texture(u2, v1).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static int drawText(TextRenderer textRenderer,DrawContext context, @Nullable String text, float x, float y, int color, boolean shadow) {
        if (text == null) {
            return 0;
        } else {
            int i = textRenderer.draw(text, x, y, color, shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880, textRenderer.isRightToLeft());
            context.draw();
            return i;
        }
    }
}
