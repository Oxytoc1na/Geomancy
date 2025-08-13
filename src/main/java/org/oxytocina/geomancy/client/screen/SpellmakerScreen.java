package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerButton;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerCheckbox;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerTextInput;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.util.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpellmakerScreen extends HandledScreen<SpellmakerScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(Geomancy.MOD_ID,"textures/gui/spellmaker_block_gui.png");
    private static final Logger log = LoggerFactory.getLogger(SpellmakerScreen.class);

    private final SpellmakerScreenHandler handler;

    public final static int bgWidth=176;
    public final static int bgHeight=189;

    private boolean inspecting = false;

    public List<SpellmakerTextInput> textInputs;

    public SpellmakerButton[] sideConfigButtons = new SpellmakerButton[12];


    public SpellmakerScreen(SpellmakerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        handler.screen = this;
        textInputs = new ArrayList<>();
        //addDrawableChild()
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

        this.backgroundWidth = bgWidth + (true||handler.hasGrid()?200:0);
        this.backgroundHeight = bgHeight;

        //titleX = 0;
        titleY = -1000;
        //playerInventoryTitleX = 0;
        this.playerInventoryTitleY = -1000;//backgroundHeight - 94;

        super.init();

        clearChildren();

        // grid properties
        if(!inspecting && handler.hasGrid()){
            int bgPosX = (width-getBackgroundWidth())/2;
            int bgPosY = (height-getBackgroundHeight())/2;
            final int infoPosX = bgPosX+SpellmakerScreen.bgWidth+10;
            final int infoPosY = bgPosY+10+10;

            // naming the grid
            SpellmakerTextInput textInput = new SpellmakerTextInput(this, MinecraftClient.getInstance().textRenderer,infoPosX,infoPosY,100,15,Text.literal(handler.currentGrid.name));
            textInput.setText(handler.currentGrid.name);
            textInput.setChangedListener(s -> {
                playUISound(textInput.prevText.length() < s.length() ? ModSoundEvents.SPELLMAKER_TYPE:ModSoundEvents.SPELLMAKER_TYPE_BACK);
                textInput.prevText = s;
            });
            textInput.onEditFinished = (s -> {

                // set value
                playUISound(ModSoundEvents.SPELLMAKER_TEXTFIELD_FINISHED);
                // send packet to server
                PacketByteBuf data = PacketByteBufs.create();
                data.writeBlockPos(handler.blockEntity.getPos());
                data.writeString(s);
                ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_CHANGE_GRIDNAME, data);
                textInput.validInput=true;

            });
            textInputs.add(textInput);
            addDrawableChild(textInput);

            // lib
            SpellmakerCheckbox libCheckBox = new SpellmakerCheckbox(this,infoPosX,infoPosY+20,20,20,Text.translatable("geomancy.spellmaker.grid.lib"),handler.currentGrid.library);
            libCheckBox.onPressed = ()->{
                // set value
                // send packet to server
                PacketByteBuf data = PacketByteBufs.create();
                data.writeBlockPos(handler.blockEntity.getPos());
                data.writeBoolean(libCheckBox.isChecked());
                ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_CHANGE_GRIDLIB, data);
                return true;
            };
            addDrawableChild(libCheckBox);
        }

        // instantiate buttons
        if(inspecting){
            if(handler.selectedComponent!=null){
                int bgPosX = (width-getBackgroundWidth())/2;
                int bgPosY = (height-getBackgroundHeight())/2;

                final SpellComponent component = handler.selectedComponent;
                final int infoPosX = bgPosX+SpellmakerScreen.bgWidth+10;
                final int infoPosY = bgPosY+10;

                // side configs
                sideConfigButtons = new SpellmakerButton[12];
                for (int i = 0; i < component.sideConfigs.length; i++) {
                    var conf = component.sideConfigs[i];

                    //Vector2f offset = Toolbox.rotateVector(new Vector2f(SpellmakerScreenHandler.previewWidth/2f,0),Math.PI*2*(i-1)/6f);

                    //final float startX = infoPosX+SpellmakerScreenHandler.previewWidth/2f+offset.x;
                    //final float startY = infoPosY+SpellmakerScreenHandler.previewHeight/2f+offset.y;
                    final int endX = infoPosX+SpellmakerScreenHandler.sideConfigsOffset;
                    final int endY = infoPosY+i*SpellmakerScreenHandler.spacePerSideConfigButtons;

                    // change type
                    String typeButtonText = switch(conf.activeMode()){
                        case Input -> "->";
                        case Output -> "<-";
                        case Blocked -> "x";
                    };
                    final int sideIndex = i;
                    final int newMode = (conf.selectedMode+1)%conf.modes.size();
                    SpellmakerButton typeBtn = new SpellmakerButton(this,endX,endY,0,0,20,15,Text.literal(typeButtonText), button -> {
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
                    },ModSoundEvents.SPELLMAKER_CHANGE_VAR);
                    typeBtn.active = conf.modes.size()>1;
                    addDrawableChild(typeBtn);
                    sideConfigButtons[i*2] = typeBtn;

                    // change variable
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
                            if(nextVar=="")nextVar=first!=null?first:"";
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
                            if(nextVar=="")nextVar=first!=null?first:"";
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
                    },ModSoundEvents.SPELLMAKER_CHANGE_VAR);
                    varBtn.visible = conf.activeMode()!= SpellComponent.SideConfig.Mode.Blocked;
                    varBtn.active =
                            conf.isInput()?component.function.inputs.size()>1
                            : conf.isOutput() && component.function.outputs.size() > 1;
                    addDrawableChild(varBtn);
                    sideConfigButtons[i*2+1] = varBtn;
                }

                // parameters
                textInputs.clear();
                List<String> paramNames = component.function.parameters.keySet().stream().toList();
                if(!paramNames.isEmpty()){
                    int height = 0;
                    for(int i = 0; i < paramNames.size();i++){
                        final String paramName = paramNames.get(i);
                        final var param = component.function.parameters.get(paramName);
                        final var configuredParam = component.getParam(paramName);

                        final int posX = infoPosX+SpellmakerScreenHandler.sideConfigsOffset;
                        final int posY = infoPosY+6*SpellmakerScreenHandler.spacePerSideConfigButtons
                                +height
                                +SpellmakerScreenHandler.parametersYOffset
                                +SpellmakerScreenHandler.parameterEditsYOffset
                                +10;

                        switch(param.type){
                            // checkbox for booleans
                            case ConstantBoolean:
                                SpellmakerCheckbox checkbox = new SpellmakerCheckbox(this, posX,posY,100,20,Text.literal(param.name),configuredParam.getSignal().getBooleanValue());
                                checkbox.onPressed = ()->{
                                    // set value
                                    // send packet to server
                                    PacketByteBuf data = PacketByteBufs.create();
                                    var nbt = new NbtCompound();
                                    component.writeNbt(nbt);
                                    data.writeNbt(nbt);
                                    data.writeBlockPos(handler.blockEntity.getPos());
                                    data.writeString(paramName);
                                    data.writeString(checkbox.isChecked()?"1":"0");
                                    ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_CHANGE_PARAM, data);
                                    return true;
                                };
                                addDrawableChild(checkbox);
                                height+=20;
                                break;

                            // text input for the rest
                            default:
                                SpellmakerTextInput textInput = new SpellmakerTextInput(this, MinecraftClient.getInstance().textRenderer,posX,posY,100,15,Text.literal(param.name));
                                textInput.setText(configuredParam.getSignal().getTextValue());
                                textInput.setChangedListener(s -> {
                                    textInput.validInput = configuredParam.canAccept(s);
                                    playUISound(textInput.prevText.length() < s.length() ? ModSoundEvents.SPELLMAKER_TYPE:ModSoundEvents.SPELLMAKER_TYPE_BACK);
                                    textInput.prevText = s;
                                });
                                textInput.onEditFinished = (s -> {

                                    // check if text is parseable
                                    if(configuredParam.canAccept(s)){
                                        // set value
                                        playUISound(ModSoundEvents.SPELLMAKER_TEXTFIELD_FINISHED);
                                        // send packet to server
                                        PacketByteBuf data = PacketByteBufs.create();
                                        var nbt = new NbtCompound();
                                        component.writeNbt(nbt);
                                        data.writeNbt(nbt);
                                        data.writeBlockPos(handler.blockEntity.getPos());
                                        data.writeString(paramName);
                                        data.writeString(s);
                                        ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_CHANGE_PARAM, data);
                                        textInput.validInput=true;
                                    }
                                    else{
                                        textInput.validInput=false;
                                    }

                                });
                                textInputs.add(textInput);
                                addDrawableChild(textInput);
                                height+=25;
                                break;
                        }


                    }
                }


                // delete button
                SpellmakerButton removeBtn = new SpellmakerButton(this,infoPosX,infoPosY+SpellmakerScreenHandler.previewHeight+10,0,0,35,15,Text.translatable("geomancy.spellmaker.delete"),button -> {
                    // send packet to server
                    PacketByteBuf data = PacketByteBufs.create();
                    var nbt = new NbtCompound();
                    component.writeNbt(nbt);
                    data.writeNbt(nbt);
                    data.writeBlockPos(handler.blockEntity.getPos());
                    ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_REMOVE_COMPONENT, data);

                    handler.selectedComponentPosition = null;
                    handler.selectedComponentChanged();

                },ModSoundEvents.SPELLMAKER_REMOVE_COMPONENT);
                addDrawableChild(removeBtn);

                // rotate button
                SpellmakerButton rotateBtn = new SpellmakerButton(this,infoPosX+35,infoPosY+SpellmakerScreenHandler.previewHeight+10,0,0,35,15,Text.translatable("geomancy.spellmaker.rotate"),button -> {
                    // send packet to server
                    PacketByteBuf data = PacketByteBufs.create();
                    var nbt = new NbtCompound();
                    component.writeNbt(nbt);
                    data.writeNbt(nbt);
                    data.writeBlockPos(handler.blockEntity.getPos());
                    data.writeInt(1);
                    ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_ROTATE_COMPONENT, data);
                },ModSoundEvents.SPELLMAKER_ROTATE);
                addDrawableChild(rotateBtn);
            }
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
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        handler.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
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

    public void playUISound(SoundEvent event) {
        if(handler.blockEntity.getWorld() instanceof ClientWorld){
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(event, 1.0F));
        }
    }

    protected boolean sideConfigButtonHovered(int i){
        return sideConfigButtons[i] != null && sideConfigButtons[i].isHovered();
    }

    public boolean hoveredOverSideConfig(int i) {
        return sideConfigButtonHovered(i*2) || sideConfigButtonHovered(i*2+1);
    }

    public boolean hoveredOverAnySideConfig(){
        for (int i = 0; i < 12; i++) {
            if(sideConfigButtonHovered(i)) return true;
        }
        return false;
    }
}
