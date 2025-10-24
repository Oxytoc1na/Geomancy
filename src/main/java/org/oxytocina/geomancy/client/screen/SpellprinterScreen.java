package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerButton;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerTextInput;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.*;

public class SpellprinterScreen extends HandledScreen<SpellprinterScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(Geomancy.MOD_ID,"textures/gui/spellprinter_block_gui.png");

    private final SpellprinterScreenHandler handler;

    public final static int bgWidth=176;
    public final static int bgHeight=189;

    public List<SpellmakerTextInput> textInputs;
    public SpellmakerTextInput printerInput;

    public List<Widget> widgets = new ArrayList<>();

    public SpellprinterScreen(SpellprinterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        handler.screen = this;
        textInputs = new ArrayList<>();
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        handler.tick();
        for(var t : textInputs) t.tick();
    }

    private void ensureTextEditFinish(){
        for(var t : textInputs){
            if(t.isFocused())
            {
                t.onEditFinished();
                t.setFocused(false);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if(keyCode == GLFW.GLFW_KEY_ESCAPE
                || keyCode == GLFW.GLFW_KEY_ENTER
        )
        {
            for(var t: textInputs){
                if(t.isFocused())
                {
                    ensureTextEditFinish();
                    return true;
                }
            }
        }

        // prevent E from closing the screen while typing
        if(this.client.options.inventoryKey.matchesKey(keyCode, scanCode)){
            for(var t: textInputs){
                if(t.isFocused())
                {
                    t.keyPressed(keyCode,scanCode,modifiers);
                    return true;
                }
            }
        }


        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        ensureTextEditFinish();

        this.desiredBgWidth = bgWidth;
        this.backgroundWidth = desiredBgWidth;
        this.backgroundHeight = bgHeight;

        super.init();

        //titleX = 0;
        titleY = -1000;
        //playerInventoryTitleX = 0;
        this.playerInventoryTitleY = -1000;//backgroundHeight - 94;

        clearChildren();
        widgets.clear();

        // instructions field
        int infoPosX = 100;
        int infoPosY = 100;
        SpellmakerTextInput textInput = new SpellmakerTextInput(MinecraftClient.getInstance().textRenderer,infoPosX,infoPosY,100,15,Text.empty());
        textInput.setText("");
        textInput.setChangedListener(s -> {
            Toolbox.playUISound(textInput.prevText.length() < s.length() ? ModSoundEvents.SPELLMAKER_TYPE:ModSoundEvents.SPELLMAKER_TYPE_BACK);
            textInput.prevText = s;
        });
        textInput.onEditFinished = (s -> {

            // set value
            Toolbox.playUISound(ModSoundEvents.SPELLMAKER_TEXTFIELD_FINISHED);
            // send packet to server
            PacketByteBuf data = PacketByteBufs.create();
            data.writeBlockPos(handler.blockEntity.getPos());
            data.writeString(s);
            ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_CHANGE_GRIDNAME, data);
            textInput.validInput=true;

        });
        textInputs.add(textInput);
        addDrawableChild(textInput);
        widgets.add(textInput);
        printerInput = textInput;


        // print button
        int printPosX = 100;
        int printPosY = 200;
        SpellmakerButton printButton = new SpellmakerButton(printPosX,printPosY,0,0,50,20,Text.translatable("geomancy.spellprinter.print"),button -> {
            // send packet to server
            PacketByteBuf data = PacketByteBufs.create();
            data.writeBlockPos(handler.blockEntity.getPos());
            data.writeString(printerInput.getText());
            ClientPlayNetworking.send(ModMessages.SPELLPRINTER_DESIRE_PRINT, data);

        },ModSoundEvents.SPELLMAKER_REMOVE_COMPONENT);
        addDrawableChild(printButton);
        widgets.add(printButton);
    }

    public int desiredBgWidth = bgWidth;

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width-backgroundWidth)/2;
        int y = (height-backgroundHeight)/2;

        context.drawTexture(TEXTURE,x,y,0,0,bgWidth,bgHeight);

        renderProgressArrow(context,x,y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);//,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);
        handler.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    public int getBackgroundHeight() {
        return backgroundHeight;
    }


}
