package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.Objects;

public class SpellmakerScreen extends HandledScreen<SpellmakerScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(Geomancy.MOD_ID,"textures/gui/spellmaker_block_gui.png");

    private final SpellmakerScreenHandler handler;

    public final static int bgWidth=176;
    public final static int bgHeight=189;

    private boolean inspecting = false;



    public SpellmakerScreen(SpellmakerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        handler.screen = this;

        //addDrawableChild()
    }

    @Override
    protected void init() {
        this.backgroundWidth = bgWidth + (inspecting?200:0);
        this.backgroundHeight = bgHeight;

        //titleX = 0;
        //titleY = 0;
        //playerInventoryTitleX = 0;
        this.playerInventoryTitleY = -1000;//backgroundHeight - 94;

        super.init();

        clearChildren();
        // instantiate buttons
        if(inspecting){
            /*
            // render selected component
            if(selectedComponent!=null){
                RenderSystem.setShaderColor(1,1,1,1);


                var component = selectedComponent;
                final float previewScale = 2;
                final int previewWidth = Math.round(hexWidth * previewScale);
                final int previewHeight = Math.round(hexBGTextureSize * previewScale);

                // render component
                {
                    var bgTexture = component.getHexTexture();
                    RenderSystem.setShaderColor(1,1,1,1);

                    context.drawTexture(bgTexture,
                            infoPosX,
                            infoPosY,
                            previewWidth,
                            previewHeight,
                            (hexBGTextureSize-hexWidth)/2f,
                            0,
                            hexWidth,
                            hexBGTextureSize,
                            hexBGTextureSize,
                            hexBGTextureSize
                    );

                    // render side configs
                    for (var conf : component.sideConfigs){
                        var tex = conf.getTexture();
                        if(tex!=null){
                            conf.setShaderColor();
                            context.drawTexture(
                                    tex,
                                    infoPosX,
                                    infoPosY,
                                    Math.round(hexWidth*previewScale),
                                    Math.round(hexBGTextureSize*previewScale),
                                    (hexBGTextureSize-hexWidth)/2f,
                                    0,
                                    hexWidth,
                                    hexBGTextureSize,
                                    hexBGTextureSize,
                                    hexBGTextureSize);
                        }


                    }
                }

                // render side configs
                final int sideConfigsOffset = 100;
                RenderSystem.setShaderColor(1,1,1,1);
                for (int i = 0; i < component.sideConfigs.length; i++) {
                    var conf = component.sideConfigs[i];

                    Vector2f offset = Toolbox.rotateVector(new Vector2f(previewWidth/2f,0),Math.PI*2*(i-1)/6f);

                    final float startX = infoPosX+previewWidth/2f+offset.x;
                    final float startY = infoPosY+previewHeight/2f+offset.y;
                    final int endX = infoPosX+sideConfigsOffset;
                    final int endY = infoPosY+i*30;

                    drawLine(context,startX,startY,endX,endY,0.5f,Toolbox.colorFromRGBA(0.8f,1,1,0.3f));

                    // draw conf info
                    context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.dir."+conf.dir),endX,endY,0xFFFFFFFF,true);
                    switch(conf.activeMode()){
                        case Input:
                            context.drawText(MinecraftClient.getInstance().textRenderer,
                                    Text.literal("-> ").formatted(Formatting.GRAY)
                                            .append(Text.translatable("geomancy.spellmaker.types."+conf.getSignal(component).type.toString().toLowerCase()).formatted(Formatting.DARK_AQUA))
                                            .append(Text.literal(" "+conf.varName).formatted(Formatting.GRAY)),endX,endY+10,0xFFFFFFFF,true);
                            break;
                        case Output:
                            context.drawText(MinecraftClient.getInstance().textRenderer,
                                    Text.literal("<- ").formatted(Formatting.GRAY)
                                            .append(Text.translatable("geomancy.spellmaker.types."+conf.getSignal(component).type.toString().toLowerCase()).formatted(Formatting.DARK_AQUA))
                                            .append(Text.literal(" "+conf.varName).formatted(Formatting.GRAY)),endX,endY+10,0xFFFFFFFF,true);
                            break;
                        case Blocked:
                            context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("x").formatted(Formatting.GRAY),endX,endY+10,0xFFFFFFFF,true);
                            break;
                    }
                }

                // render i/o info
                var ioInfo = SpellComponentStoringItem.componentTooltip(component,true);

                for (int i = 0; i < ioInfo.size(); i++) {
                    var t = ioInfo.get(i);
                    context.drawText(MinecraftClient.getInstance().textRenderer, t,infoPosX+10,infoPosY+previewHeight+20+i*10,0xFFFFFFFF,true);
                }

            }*/

            if(handler.selectedComponent!=null){
                int bgPosX = (width-getBackgroundWidth())/2;
                int bgPosY = (height-getBackgroundHeight())/2;

                final SpellComponent component = handler.selectedComponent;
                final int infoPosX = bgPosX+SpellmakerScreen.bgWidth+10;
                final int infoPosY = bgPosY+10;

                for (int i = 0; i < component.sideConfigs.length; i++) {
                    var conf = component.sideConfigs[i];

                    Vector2f offset = Toolbox.rotateVector(new Vector2f(SpellmakerScreenHandler.previewWidth/2f,0),Math.PI*2*(i-1)/6f);

                    final float startX = infoPosX+SpellmakerScreenHandler.previewWidth/2f+offset.x;
                    final float startY = infoPosY+SpellmakerScreenHandler.previewHeight/2f+offset.y;
                    final int endX = infoPosX+SpellmakerScreenHandler.sideConfigsOffset;
                    final int endY = infoPosY+i*30;

                    // change type
                    String typeButtonText = switch(conf.activeMode()){
                        case Input -> "->";
                        case Output -> "<-";
                        case Blocked -> "x";
                    };
                    final int sideIndex = i;
                    final int newMode = (conf.selectedMode+1)%conf.modes.size();
                    SpellmakerButton typeBtn = new SpellmakerButton(this,endX,endY,0,0,20,15,Text.literal(typeButtonText),button -> {
                        // cycle type
                        // send packet to server
                        PacketByteBuf data = PacketByteBufs.create();
                        var nbt = new NbtCompound();
                        component.writeNbt(nbt);
                        data.writeNbt(nbt);
                        data.writeBlockPos(handler.blockEntity.getPos());
                        data.writeInt(sideIndex);
                        data.writeInt(newMode);
                        ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_CHANGE_TYPE, data);
                    });
                    typeBtn.active = conf.modes.size()>1;
                    addDrawableChild(typeBtn);

                    // change type
                    String nextVar = "";
                    switch(conf.activeMode())
                    {
                        case Input :
                        {
                            String first = null;
                            boolean nextIsRes = false;
                            for (var name : component.function.inputs.keySet())
                            {
                                if(nextIsRes){nextVar=name;break;}
                                if(first==null) first=name;
                                if(Objects.equals(name, conf.varName))
                                    nextIsRes = true;
                            }
                            if(nextVar==null)nextVar=first;
                            break;
                        }
                        case Output:
                        {
                            String first = null;
                            boolean nextIsRes = false;
                            for (var name : component.function.outputs.keySet())
                            {
                                if(nextIsRes){nextVar=name;break;}
                                if(first==null) first=name;
                                if(Objects.equals(name, conf.varName))
                                    nextIsRes = true;
                            }
                            if(nextVar==null)nextVar=first;
                            break;
                        }
                    }
                    final String nextVar2 = nextVar;

                    SpellmakerButton varBtn = new SpellmakerButton(this,endX+20,endY,0,0,80,15,
                            Text.translatable("geomancy.spellmaker.types."+conf.getSignal(component).type.toString().toLowerCase()).formatted(Formatting.DARK_AQUA)
                                    .append(Text.literal(" "+conf.varName).formatted(Formatting.GRAY)),button -> {
                        // cycle variable
                        // send packet to server
                        PacketByteBuf data = PacketByteBufs.create();
                        var nbt = new NbtCompound();
                        component.writeNbt(nbt);
                        data.writeNbt(nbt);
                        data.writeBlockPos(handler.blockEntity.getPos());
                        data.writeInt(sideIndex);
                        data.writeString(nextVar2);
                        ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_CHANGE_VAR, data);
                    });
                    varBtn.visible = conf.activeMode()!= SpellComponent.SideConfig.Mode.Blocked;
                    varBtn.active =
                            conf.isInput()?component.function.inputs.size()>1
                            : conf.isOutput() && component.function.outputs.size() > 1;
                    addDrawableChild(varBtn);
                }
            }

            SpellmakerButton test = new SpellmakerButton(this,bgWidth,0,0,0,50,50,Text.literal("test!"),button -> {
                MinecraftClient.getInstance().player.sendMessage(Text.literal("test!"));
            });

            addDrawableChild(test);
        }

    }

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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        handler.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        handler.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        handler.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);//,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
        handler.render(context, mouseX, mouseY, delta);
    }

    public void setComponentInspected(boolean inspected){
        this.inspecting = inspected;
        init();
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    public int getBackgroundHeight() {
        return backgroundHeight;
    }
}
