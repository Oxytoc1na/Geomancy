package org.oxytocina.geomancy.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.DrawHelper;
import org.oxytocina.geomancy.util.SoulUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private static boolean deltaFrame = false;
    private static long prevFrame = 0;
    private static boolean standingStill = false;
    public static void onHudRenderStatic(DrawContext drawContext, PlayerEntity playerEntity) {

        deltaFrame = GeomancyClient.tick!=prevFrame;
        prevFrame = GeomancyClient.tick;

        MinecraftClient client = MinecraftClient.getInstance();
        if(client==null||client.player==null||client.options.hudHidden||playerEntity==null||playerEntity.isSpectator()) return;

        standingStill = client.player.input.getMovementInput().length()<0.03f && (playerEntity.isOnGround() || Math.abs(playerEntity.getVelocity().getY()) < 0.5f);

        int x = 0;
        int y = 0;
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        x = width / 2;
        y = height;

        renderManaBar(drawContext,playerEntity,x - 91);
        boolean drawSoulCrosshair = false;
        SpellGrid selectedGrid = null;
        for(var stack : playerEntity.getHandItems())
        {
            if(stack.isIn(ModItemTags.CASTING_ITEM))
            {
                drawSoulCrosshair=true;

                if(stack.getItem() instanceof ISpellSelectorItem selector){
                    selectedGrid = selector.getSelectedSpell(stack);
                }

                break;
            }
        }
        if(drawSoulCrosshair)
            renderSoulCrosshair(drawContext,playerEntity,selectedGrid);
        else CROSSHAIR_ATTENTION=2; // switching to crosshair view has it at max opacity for a second

        float manaFraction = Toolbox.clampF(SoulUtil.getSoul(playerEntity) / SoulUtil.getMaxSoul(playerEntity),0,1);
        lastManaFraction=manaFraction;

    }

    final static Identifier ICON_TEXTURE = Geomancy.locate("textures/gui/icons.png");
    final static float MANA_BAR_TEXTURE_SIZE = 256;
    public static void renderManaBar(DrawContext context,PlayerEntity player, int x) {
        var client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        if (SoulUtil.getMaxSoul(player)>0) {
            float manaFraction = Toolbox.clampF(SoulUtil.getSoul(player) / SoulUtil.getMaxSoul(player),0,1);

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
                RenderSystem.enableBlend();
                DrawHelper.drawTexturedQuad(context.getMatrices(),ICON_TEXTURE,barX,barX+barWidth,barY,barY+barHeight,0,(0)/MANA_BAR_TEXTURE_SIZE,(barWidth)/MANA_BAR_TEXTURE_SIZE,(15)/MANA_BAR_TEXTURE_SIZE,(15+barHeight)/MANA_BAR_TEXTURE_SIZE,colVec.x,colVec.y,colVec.z,alpha);
            }

            String string = Toolbox.formatNumber(Math.round(SoulUtil.getSoul(player))) +" / "+ Toolbox.formatNumber(Math.round(SoulUtil.getMaxSoul(player)));
            if(showAmbientMana()){
                int t = Toolbox.floor(ambientManaArrowProgress*2.999f);
                string = Math.round(SoulUtil.getAmbientSoulsPerBlock(player.getWorld(),player.getBlockPos()))+" "+(t%3==0?">":"-")+(t%3==1?">":"-")+(t%3==2?">":"-")+" "+string;
            }
            float xPos = (width - client.textRenderer.getWidth(string)) / 2f + manaShakeX;
            float yPos = height - 31 - 4 + manaShakeY;
            if(player.experienceLevel>0)
                yPos -= 11;
            DrawHelper.drawTextOutlined(context,client.textRenderer, string, xPos, yPos, col, 0);
        }

    }
    private static List<Pair<Float,Float>> PREV_DRAWN_BLOB_FRACTIONS = new ArrayList<>(); // fraction, maxSoul
    /// if a blob gets consumed whole in a single frame, play a special "break" animation
    /// stores a value 0-1, decrements. 1: start, 0: end
    private static List<Float> BLOB_BREAKAGE = new ArrayList<>();
    /// 0: most transparent, 1: least transparent. decays over time.
    /// set to 1 when soul is used
    private static float CROSSHAIR_ATTENTION = 0;
    static final int CROSSHAIR_BLOB_SIZE = 9;
    static final float CROSSHAIR_BLOB_SCALE = 0.8f;

    public static void renderSoulCrosshair(DrawContext context,PlayerEntity player,@Nullable SpellGrid selectedGrid) {
        float maxSoul = SoulUtil.getMaxSoul(player);
        if(maxSoul<=0) {CROSSHAIR_ATTENTION=2;return;}
        RenderSystem.enableBlend();

        var client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        float centerX = width/2f;
        float centerY = height/2f;

        float soul = SoulUtil.getSoul(player);
        float soulFraction = Toolbox.clampF(soul / maxSoul,0,1);
        if(soulFraction<lastManaFraction){
            CROSSHAIR_ATTENTION = 5;
        }
        else if(!standingStill) CROSSHAIR_ATTENTION = Math.max(0,CROSSHAIR_ATTENTION-(deltaFrame?1f/20:0));
        // standing still boosts opacity
        else if(CROSSHAIR_ATTENTION<1) CROSSHAIR_ATTENTION = CROSSHAIR_ATTENTION+(deltaFrame?1f/20:0);

        // determine how many blobs to draw and how full they are
        // each next blob holds 1.5 times as much soul as the previous
        int drawnBlobs = 1;
        List<Pair<Float,Float>> drawnBlobFractions = new ArrayList<>(); // fraction, maxSoul

        switch(Geomancy.CONFIG.soulCrosshairMath.value()){
            case "linear" : {
                float workMaxSoul = 10;
                float workSoulLeft = soul;
                float workBlobMaxSoul = 10;
                workSoulLeft-=workBlobMaxSoul;
                while(workMaxSoul < maxSoul){
                    workBlobMaxSoul *= 1.5f;
                    workMaxSoul += workBlobMaxSoul;
                    drawnBlobs++;
                    workSoulLeft-=workBlobMaxSoul;
                }
                workBlobMaxSoul = maxSoul/drawnBlobs;
                workSoulLeft = soul;
                for (int i = 0; i < drawnBlobs; i++) {
                    float soulForThisBlob = Math.min(workBlobMaxSoul,workSoulLeft);
                    workSoulLeft -= soulForThisBlob;
                    drawnBlobFractions.add(new Pair<>(Toolbox.clampF(soulForThisBlob/workBlobMaxSoul,0,1),workBlobMaxSoul));
                }
                break;
            }
            case "multiplicative":
            default:
            {
                float workMaxSoul = 10;
                float workSoulLeft = soul;
                float workBlobMaxSoul = 10;
                drawnBlobFractions.add(new Pair<>(Toolbox.clampF(workSoulLeft/workBlobMaxSoul,0,1),workBlobMaxSoul));
                workSoulLeft-=workBlobMaxSoul;
                while(workMaxSoul < maxSoul){
                    workBlobMaxSoul *= 1.5f;
                    workMaxSoul += workBlobMaxSoul;
                    drawnBlobs++;

                    drawnBlobFractions.add(new Pair<>(Toolbox.clampF(workSoulLeft/workBlobMaxSoul,0,1),workBlobMaxSoul));
                    workSoulLeft-=workBlobMaxSoul;
                }
                break;
            }
        }

        // determine blob animation status (breaking orbs by fully depleting them instantly)
        List<Float> newBlobBreakage = new ArrayList<>();
        for (int i = 0; i < drawnBlobs; i++) {
            if(i>=PREV_DRAWN_BLOB_FRACTIONS.size()) break;
            float fractionDiff = drawnBlobFractions.get(i).getLeft() - PREV_DRAWN_BLOB_FRACTIONS.get(i).getLeft();
            if(fractionDiff<=-0.9f) // fully used up blob! initiate BOOM animation!!!!
                newBlobBreakage.add(1f);
            else
                // it takes 10 frames for the animation to finish
                newBlobBreakage.add(BLOB_BREAKAGE.size()>i?Math.max(0,BLOB_BREAKAGE.get(i)-(deltaFrame?(1f/10):0)):0);
        }
        BLOB_BREAKAGE=newBlobBreakage;

        // draw

        float minXOffset = -20; // for drawing menu appearance of selected spell
        float alpha = 0.2f + Toolbox.clampF(CROSSHAIR_ATTENTION,0,1) * (0.5f);

        switch(Geomancy.CONFIG.soulCrosshairType.value()){
            case "pyramid":{
                int currentBaseSize = 1;
                int currentLayer = 0;
                int layerI = 0;
                float heightPerLayer = 8;
                float yOffset = 30;
                for (int i = 0; i < drawnBlobs; i++) {
                    float offsetAmount = 10;
                    float y = centerY- CROSSHAIR_BLOB_SIZE /2f* CROSSHAIR_BLOB_SCALE +currentLayer*heightPerLayer+yOffset;
                    boolean swapped = layerI%2==0;
                    float x = centerX- CROSSHAIR_BLOB_SIZE /2f* CROSSHAIR_BLOB_SCALE -offsetAmount*(currentLayer%2==1?0.5f:0)+offsetAmount*((layerI+1)/2)*(swapped?-1:1);

                    drawCrosshairBlob(context,drawnBlobFractions,i,x,y);

                    layerI++;
                    if(layerI>=currentBaseSize){
                        currentBaseSize++;
                        layerI=0;
                        currentLayer++;
                    }
                }
                break;
            }
            case "hourglass":{
                int currentBaseSize = 1;
                int currentLayer = 0;
                int layerI = 0;
                float heightPerLayer = 8;
                float yBuffer = 30;
                for (int i = 0; i < drawnBlobs; i++) {
                    float offsetAmount = 10;
                    int yFactor = i%2==0?1:-1;
                    int sectionLayerI = layerI/2;
                    float y = centerY- CROSSHAIR_BLOB_SIZE /2f* CROSSHAIR_BLOB_SCALE +currentLayer*heightPerLayer*yFactor+yBuffer*yFactor;
                    boolean swapped = sectionLayerI%2==0;
                    float x = centerX- CROSSHAIR_BLOB_SIZE /2f* CROSSHAIR_BLOB_SCALE -offsetAmount*(currentLayer%2==1?0.5f:0)+offsetAmount*((sectionLayerI+1)/2)*(swapped?-1:1);

                    drawCrosshairBlob(context,drawnBlobFractions,i,x,y);

                    layerI++;
                    if(layerI>=currentBaseSize*2){
                        currentBaseSize++;
                        layerI=0;
                        currentLayer++;
                    }
                }
                break;
            }
            case "focus":{
                int currentBaseSize = 1;
                int currentLayer = 0;
                int layerI = 0;
                float heightPerLayer = 8;
                float yBuffer = 30;
                for (int i = 0; i < drawnBlobs; i++) {
                    float offsetAmount = 10;
                    int yFactor = i%2==0?1:-1;
                    int sectionLayerI = layerI/2;
                    float xOffset = currentLayer*heightPerLayer*yFactor+yBuffer*yFactor;
                    if(xOffset<minXOffset) minXOffset=xOffset;
                    float x = centerX- CROSSHAIR_BLOB_SIZE /2f* CROSSHAIR_BLOB_SCALE + xOffset;
                    boolean swapped = sectionLayerI%2==0;
                    float y = centerY- CROSSHAIR_BLOB_SIZE /2f* CROSSHAIR_BLOB_SCALE -offsetAmount*(currentLayer%2==1?0.5f:0)+offsetAmount*((sectionLayerI+1)/2)*(swapped?-1:1);

                    drawCrosshairBlob(context,drawnBlobFractions,i,x,y);

                    layerI++;
                    if(layerI>=currentBaseSize*2){
                        currentBaseSize++;
                        layerI=0;
                        currentLayer++;
                    }
                }
                break;
            }
            case "spiral":
            default:
            {
                for (int i = 0; i < drawnBlobs; i++) {
                    float angle = (float)Math.PI*2*i/drawnBlobs;
                    float offsetAmount = 20;
                    if(drawnBlobs > 8){
                        // make excessive blobs spiral out
                        offsetAmount = 15+i*(2/(1+i/20f));
                        angle = ((float) Math.PI * 2 * i / 8) / (1+i / (10f * (float)Math.PI));
                    }
                    var offset = Toolbox.rotateVector(new Vector2f(0,offsetAmount),angle);

                    float x = centerX+offset.x- CROSSHAIR_BLOB_SIZE /2f* CROSSHAIR_BLOB_SCALE;
                    float y = centerY+offset.y- CROSSHAIR_BLOB_SIZE /2f* CROSSHAIR_BLOB_SCALE;

                    if(offset.x<minXOffset) minXOffset=offset.x;

                    drawCrosshairBlob(context,drawnBlobFractions,i,x,y);
                }
                break;
            }
        }



        // draw selected spell menu appearance
        if(selectedGrid!=null&&selectedGrid.displayStack!=null)
        {
            float x = centerX+minXOffset-20- CROSSHAIR_BLOB_SIZE /2f;
            float y = centerY-16/2f;
            DrawHelper.drawItem(context,player,player.getWorld(),selectedGrid.displayStack,x,y,0,1,1,1,1,alpha);
        }

        PREV_DRAWN_BLOB_FRACTIONS = drawnBlobFractions;
    }

    private static void drawCrosshairBlob(DrawContext context,List<Pair<Float,Float>> drawnBlobFractions,int i,float x, float y){
        var blobFraction = drawnBlobFractions.get(i);
        float breakageAnimation = BLOB_BREAKAGE.size()>i?BLOB_BREAKAGE.get(i):0;
        boolean breaking = breakageAnimation>0;

        int fillage =  Math.round(blobFraction.getLeft() * 6);// 0-6
        if(breaking) fillage = Math.round((float)Math.floor((1-breakageAnimation) * 4));// 0-3
        int u = fillage * CROSSHAIR_BLOB_SIZE;
        int v = 32 + (breaking?16:0);

        int col = ModColorizationHandler.octanguliteItemBarNoise(blobFraction.getLeft(),1,i*4,0,0);
        var colVec = Toolbox.colorIntToVec(col);
        //RenderSystem.setShaderColor(colVec.x,colVec.y,colVec.z,alpha);
        float alpha = 0.2f + Toolbox.clampF(CROSSHAIR_ATTENTION,0,1) * (0.5f);
        DrawHelper.drawTexturedQuad(context.getMatrices(),ICON_TEXTURE,x,x+ CROSSHAIR_BLOB_SIZE * CROSSHAIR_BLOB_SCALE,y,y+ CROSSHAIR_BLOB_SIZE * CROSSHAIR_BLOB_SCALE,0,
                u/MANA_BAR_TEXTURE_SIZE,(u+ CROSSHAIR_BLOB_SIZE)/MANA_BAR_TEXTURE_SIZE,
                (v)/MANA_BAR_TEXTURE_SIZE,(v+ CROSSHAIR_BLOB_SIZE)/MANA_BAR_TEXTURE_SIZE,colVec.x,colVec.y,colVec.z,alpha);
    }

    public static void tick(){
        manaUseShake = MathHelper.lerp(0.1f,manaUseShake,0);

        manaShakeX = (int)Math.round(Math.sin(Math.PI*4*GeomancyClient.tick/20f)*manaUseShake*20);
        manaShakeY = (int)Math.round(Math.sin(Math.PI*7.31f*GeomancyClient.tick/20f)*manaUseShake*10);

        if(showAmbientMana()){
            if(MinecraftClient.getInstance() != null && MinecraftClient.getInstance().cameraEntity!=null)
            {
                ambientManaArrowProgress += 1 / 20f / 400f * SoulUtil.getAmbientSoulsPerBlock(MinecraftClient.getInstance().world, MinecraftClient.getInstance().cameraEntity.getBlockPos());
                ambientManaArrowProgress = ambientManaArrowProgress%1;
            }
        }

    }

    public static boolean showAmbientMana(){
        // TODO: unlock to see regen speed
        return true;
    }


}
