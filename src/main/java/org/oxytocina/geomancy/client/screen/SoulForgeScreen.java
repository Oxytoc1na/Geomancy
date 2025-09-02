package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector2f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.recipe.soulforge.ISoulForgeRecipe;
import org.oxytocina.geomancy.util.DrawHelper;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;

public class SoulForgeScreen extends HandledScreen<SoulForgeScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(Geomancy.MOD_ID,"textures/gui/soulforge_gui.png");
    private static final Identifier CHECKMARK_TEXTURE = new Identifier("textures/gui/checkmark.png");

    public SoulForgeScreen(SoulForgeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        //titleX = 0;
        //titleY = 0;
        //playerInventoryTitleX = 0;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width-backgroundWidth)/2;
        int y = (height-backgroundHeight)/2;

        context.drawTexture(TEXTURE,x,y,0,0,backgroundWidth,backgroundHeight);

        renderProgressArrow(context,x,y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        float soulFraction = handler.blockEntity.availableSoul>0?1:0;
        ISoulForgeRecipe recipe = handler.blockEntity.previewingRecipe;
        if(handler.isCrafting()) recipe = handler.blockEntity.activeRecipe;
        if(recipe!=null)
            soulFraction = Toolbox.clampF(handler.blockEntity.availableSoul/recipe.getSoulCost(null),0,1);

        // draw soul
        if(soulFraction>0f){
            var col = Toolbox.colorIntToVec(ModColorizationHandler.octanguliteItemBarNoise(soulFraction));
            int v = 68;
            if(soulFraction>=0.5f)
                v = 91;
            final int height = 22;
            final int width = 35;
            DrawHelper.drawTexture(context.getMatrices(),TEXTURE,x+85,y+28,width,height,176,v,width,height,256,256,col.x,col.y,col.z,soulFraction);
        }

        if(handler.isCrafting()){

            // draw progress
            final int progressHeight = 18;
            context.drawTexture(TEXTURE,x+87,y+30,176,0,getScaledProgress(handler.blockEntity.progress,35),progressHeight);

            // draw instability
            {
                int v = 18;
                int maxheight = 14;
                int height = getScaledProgress(handler.blockEntity.instability,maxheight);
                DrawHelper.drawTexture(context.getMatrices(),TEXTURE,x+101,y+48+maxheight-height,
                        21,height,190,v+maxheight-height,21,height,256,256,0.8f,0,0,1);
            }
        }
    }

    final int INPUT_SLOT_OFFSET_X = 25+18+8;
    final int INPUT_SLOT_OFFSET_Y = 18+18+8;
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);//,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);

        int x = (width-backgroundWidth)/2;
        int y = (height-backgroundHeight)/2;

        // render ingredients

        List<Pair<ItemStack,Boolean>> stacksToShow = new ArrayList<>();

        if(handler.blockEntity.previewingRecipe != null || handler.blockEntity.isActive()){
            ISoulForgeRecipe recipe = handler.blockEntity.isActive() ? handler.blockEntity.activeRecipe : handler.blockEntity.previewingRecipe;
            var ingredients = recipe.getNbtIngredients(null);
            List<Integer> accountedForIngredients = new ArrayList<>();
            for (int i = 1; i < ingredients.size();i++) {
                var ingredient = ingredients.get(i);
                var stack = ingredient.getStack();
                boolean alreadyConsumed = false;
                // check if this ingredient was already consumed
                if (handler.blockEntity.isActive()) {
                    for (int j = 0; j < handler.blockEntity.consumedIngredients.size(); j++) {
                        if (accountedForIngredients.contains(j)) continue;
                        var testStack = handler.blockEntity.consumedIngredients.get(j);
                        if (ingredient.test(testStack)) {
                            stack = testStack;
                            accountedForIngredients.add(j);
                            alreadyConsumed = true;
                            break;
                        }
                    }
                }
                stacksToShow.add(new Pair<>(stack, alreadyConsumed));
            }
        }
        // show pedestal items
        else{
            for (int i = 0; i < handler.blockEntity.surroundingIngredients.size(); i++) {
                var s = handler.blockEntity.surroundingIngredients.get(i);
                stacksToShow.add(new Pair<>(s,false));
            }
        }

        boolean alreadyShowingTooltip = this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack();

        for (int i = 0; i < stacksToShow.size(); i++) {
            float angle = (float)Math.PI*2*((float)i/ stacksToShow.size());
            var offset = Toolbox.rotateVector(new Vector2f(24,0),angle);
            int drawPosX = x+INPUT_SLOT_OFFSET_X+(int)offset.x-8;
            int drawPosY = y+INPUT_SLOT_OFFSET_Y+(int)offset.y-8;
            var stack = stacksToShow.get(i).getLeft();
            boolean alreadyConsumed = stacksToShow.get(i).getRight();
            DrawHelper.drawItem(context,MinecraftClient.getInstance().player,handler.blockEntity.getWorld(),stack,
                    drawPosX,drawPosY,0,0,alreadyConsumed?1:0.5f,alreadyConsumed?1:0.5f,alreadyConsumed?1:0.5f);
            RenderSystem.setShaderColor(1,1,1,1);
            if(alreadyConsumed)
            {
                // draw checkmark
                context.drawTexture(CHECKMARK_TEXTURE,drawPosX+10,drawPosY+10,0,0,0,8,9,8,9);
            }
            // tooltip
            if(!alreadyShowingTooltip && DrawHelper.mouseInRect(mouseX,mouseY,drawPosX,drawPosY,16,16))
            {
                alreadyShowingTooltip = true;
                context.drawTooltip(this.textRenderer, this.getTooltipFromItem(stack), stack.getTooltipData(), mouseX, mouseY);
            }
        }

        context.drawText(MinecraftClient.getInstance().textRenderer, "i: "+handler.blockEntity.instability,0,0, Toolbox.colorFromRGB(0,0,0),true);
        context.drawText(MinecraftClient.getInstance().textRenderer, "p: "+handler.blockEntity.progress,0,10, Toolbox.colorFromRGB(0,0,0),true);
        drawMouseoverTooltip(context,mouseX,mouseY);
    }

    public int getScaledProgress(float progress, int size) {
        //int progressArrowSize = 35; // This is the width in pixels of your arrow

        return (int)(progress * size);
    }
}
