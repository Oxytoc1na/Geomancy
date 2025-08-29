package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.Optional;

public class StorageItemScreen extends HandledScreen<StorageItemScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(Geomancy.MOD_ID,"textures/gui/storageitem.png");
    private static final Identifier SLOT_BG = new Identifier(Geomancy.MOD_ID,"textures/gui/slot.png");

    private final StorageItemScreenHandler handler;

    public final static int bgWidth=176;
    public final static int bgHeight=91;
    public final static int bgPadding=4;
    public final static int bgPlayerInventoryBuffer=91-bgPadding-1;

    public StorageItemScreen(StorageItemScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        handler.screen = this;
        //addDrawableChild()
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        handler.tick();
    }

    @Override
    protected void init() {
        this.backgroundWidth = bgWidth;
        this.backgroundHeight = handler.getScreenHeight();

        this.playerInventoryTitleY = this.backgroundHeight - 94;

        super.init();

        clearChildren();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width-backgroundWidth)/2;
        int y = (height-backgroundHeight)/2;

        context.drawNineSlicedTexture(TEXTURE,x,y,backgroundWidth,backgroundHeight,
                bgPadding,bgPadding,bgPadding,bgPlayerInventoryBuffer,bgWidth,bgHeight,0,0);

        if(Optional.of(handler.getOutput()).orElse(ItemStack.EMPTY).isEmpty()){
            context.drawTexture(TEXTURE,x+7,y+17,0,180,162,54);
        }

        // render slot bgs
        for(var slot : handler.slots){
            context.drawTexture(SLOT_BG,slot.x,slot.y,0,0,18,18);
        }

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);//,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    public int getBackgroundHeight() {
        return backgroundHeight;
    }
}
