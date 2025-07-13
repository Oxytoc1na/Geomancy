package org.oxytocina.geomancy.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.util.ManaUtil;

public class ModHudRenderer {

    private static final Identifier FILLED_THIRST = new Identifier(Geomancy.MOD_ID,
            "textures/item/artifact_of_gold.png");
    private static final Identifier EMPTY_THIRST = new Identifier(Geomancy.MOD_ID,
            "textures/item/empty_artifact.png");

    public static void onHudRender(DrawContext drawContext, PlayerEntity playerEntity) {

        MinecraftClient client = MinecraftClient.getInstance();
        if(client==null||client.options.hudHidden||client.player==null) return;
        ClientPlayerEntity player = client.player;
        int x = 0;
        int y = 0;
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        x = width / 2;
        y = height;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EMPTY_THIRST);
        for(int i = 0; i < 10; i++) {
            drawContext.drawTexture(EMPTY_THIRST,x - 94 + (i * 9),y - 54,0,0,12,12,
                    12,12);
        }

        RenderSystem.setShaderTexture(0, FILLED_THIRST);
        for(int i = 0; i < 10; i++) {
            if(ManaUtil.getMana(player) > i) {
                drawContext.drawTexture(FILLED_THIRST,x - 94 + (i * 9),y - 54,0,0,12,12,
                        12,12);
            } else {
                break;
            }
        }

        /*
        drawContext.drawText(MinecraftClient.getInstance().textRenderer,
                "vpb: "+ManaUtil.getAmbientSoulsPerBlock(player.getWorld(),player.getBlockPos())+
                " cv: "+ManaUtil.getMana(player)+"/"+ManaUtil.getMaxMana(player)
                +" leadspeed: "+ LeadUtil.getPoisoningSpeed(player)+" lead: "+LeadUtil.getPoisoning(player)
                +" madspeed: "+ MadnessUtil.getMadnessSpeed(player)+" mad: "+ MadnessUtil.getMadness(player),x-100,y-70,0xFFFFFFFF,true);
        */

    }
}
