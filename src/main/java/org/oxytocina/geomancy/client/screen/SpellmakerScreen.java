package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerButton;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerCheckbox;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerTextInput;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.util.TextUtil;
import org.oxytocina.geomancy.util.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SpellmakerScreen extends HandledScreen<SpellmakerScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(Geomancy.MOD_ID,"textures/gui/spellmaker_block_gui.png");
    private static final Logger log = LoggerFactory.getLogger(SpellmakerScreen.class);

    private final SpellmakerScreenHandler handler;

    public final static int bgWidth=176;
    public final static int bgHeight=189;

    private boolean inspecting = false;
    private boolean placingNewComponent = false;

    public List<SpellmakerTextInput> textInputs;

    public SpellmakerButton[] sideConfigButtons = new SpellmakerButton[12];
    public List<Widget> widgets = new ArrayList<>();
    public HashMap<Widget,Integer> initialWidgetOffsets = new HashMap<>();
    public SpellmakerButton[] selectNewCompScrollBtns = new SpellmakerButton[2];

    public boolean moving = !Geomancy.CONFIG.noSpellmakerMove.value();

    public static final HashMap<ItemStack,String> hints = new LinkedHashMap<>();
    public Pair<ItemStack,String> currentHint = null;

    static{
        hints.put(SpellBlocks.FUNCTION.copyItemStack(),"references");
        hints.put(SpellBlocks.SUM.copyItemStack(),"sum");
        hints.put(SpellBlocks.DIMHOP.copyItemStack(),"dimhop");
        hints.put(ModItems.CASTER_LEGGINGS.getDefaultStack(),"casterleggings");
        hints.put(ModItems.CASTER_BOOTS.getDefaultStack(),"casterboots");
        hints.put(ModItems.CASTER_HELMET.getDefaultStack(),"casterhelmet");
        hints.put(ModItems.CASTER_CHESTPLATE.getDefaultStack(),"casterchestplate");
        hints.put(SpellBlocks.DEGRADE_BLOCK.copyItemStack(),"degrading");
        hints.put(SpellBlocks.ENTITIES_NEAR.copyItemStack(),"nearbyentities");
        hints.put(SpellBlocks.DEBUG.copyItemStack(),"debugging");
        hints.put(SpellBlocks.ACTIVATE.copyItemStack(),"activate");
        hints.put(ModItems.VARSTORAGE_SMALL.getDefaultStack(),"varpots");
        hints.put(ModItems.VARSTORAGE_LARGE.getDefaultStack(),"varpots2");

        hints.put(Items.STONE.getDefaultStack(),"rockandstone");
    }

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
        tick2();
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

        this.desiredBgWidth = bgWidth + (handler.hasGrid()||!moving?200:0);
        this.backgroundWidth = moving ? (int)currentBgWidth : desiredBgWidth;
        this.backgroundHeight = bgHeight;

        super.init();

        //titleX = 0;
        titleY = -1000;
        //playerInventoryTitleX = 0;
        this.playerInventoryTitleY = -1000;//backgroundHeight - 94;

        clearChildren();
        widgets.clear();

        // new component preview
        if(placingNewComponent && handler.hasGrid())
        {
            // sorry! no buttons!
        }

        // grid properties
        if(!placingNewComponent && !inspecting && handler.hasGrid()){
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
            widgets.add(textInput);

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
            widgets.add(libCheckBox);
        }

        // instantiate buttons
        if(!placingNewComponent && inspecting){
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
                    widgets.add(typeBtn);
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
                    widgets.add(varBtn);
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
                                widgets.add(textInput);
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
                widgets.add(removeBtn);

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
                widgets.add(rotateBtn);
            }
        }

        // silly placeholder
        if(!handler.hasGrid() && !moving){
            repickHint();

            int bgPosX = (width-getBackgroundWidth())/2;
            int bgPosY = (height-getBackgroundHeight())/2;

            final int infoPosX = bgPosX+SpellmakerScreen.bgWidth+10;
            final int infoPosY = bgPosY+10;

            var tipWidget = new TextWidget(infoPosX,infoPosY-10,150,15,Text.translatable("geomancy.spellmaker.tip").formatted(Formatting.DARK_AQUA),MinecraftClient.getInstance().textRenderer);
            addDrawableChild(tipWidget);
            widgets.add(tipWidget);

            var nameWidget = new TextWidget(infoPosX,infoPosY,150,15,Text.translatable("geomancy.spellmaker.tip."+currentHint.getSecond()),MinecraftClient.getInstance().textRenderer);
            addDrawableChild(nameWidget);
            widgets.add(nameWidget);

            int i = 0;
            for(var t : TextUtil.wrapString(Text.translatable("geomancy.spellmaker.tip."+currentHint.getSecond()+".desc").getString(),150,MinecraftClient.getInstance().textRenderer))
            {
                var textWidget = new TextWidget(infoPosX,infoPosY+48+10*i,150,10,Text.literal(t).formatted(Formatting.GRAY),MinecraftClient.getInstance().textRenderer);
                addDrawableChild(textWidget);
                widgets.add(textWidget);
                i++;
            }

        }

        // hint text for insertion
        if(!handler.hasGrid()){

            int bgPosX = (width-getBackgroundWidth())/2;
            int bgPosY = (height-getBackgroundHeight())/2;

            final int gridPosX = bgPosX+SpellmakerScreenHandler.fieldPosX;
            final int gridPosY = bgPosY+SpellmakerScreenHandler.fieldPosY;

            var tipWidget = new TextWidget(gridPosX,gridPosY,SpellmakerScreenHandler.fieldWidth,SpellmakerScreenHandler.fieldHeight,Text.translatable("geomancy.spellmaker.insertiontip"),MinecraftClient.getInstance().textRenderer);
            addDrawableChild(tipWidget);
            widgets.add(tipWidget);
        }

        // instantiate component scroll buttons
        {
            int bgPosX = (width-getBackgroundWidth())/2;
            int bgPosY = (height-getBackgroundHeight())/2;

            final int spellCompInvPosX = bgPosX+SpellmakerScreenHandler.NEW_COMPONENTS_X + SpellmakerScreenHandler.NEW_COMPONENTS_WIDTH*18;
            final int spellCompInvPosY = bgPosY+SpellmakerScreenHandler.NEW_COMPONENTS_Y;
            final int endX = spellCompInvPosX;

            for (int i = 0; i < 2; i++) {

                final int endY = spellCompInvPosY + i *18;
                String btnText = i==0?"/\\":"\\/";
                final int scroll = i==0?-1:1;
                SpellmakerButton scrollBtn = new SpellmakerButton(this,endX,endY,0,0,16,16,Text.literal(btnText), button -> {
                    handler.changeComponentViewScroll(scroll);
                    refreshScrollButtons();
                },ModSoundEvents.SPELLMAKER_ROTATE);
                scrollBtn.visible = handler.componentScrollVisible();
                scrollBtn.active = handler.componentScrollActive(i);
                addDrawableChild(scrollBtn);
                widgets.add(scrollBtn);
                selectNewCompScrollBtns[i] = scrollBtn;
            }
        }
    }

    public int desiredBgWidth = bgWidth;
    public float currentBgWidth = bgWidth;
    public int shiftHoldOver = 0;
    void tick2(){
        if(!moving) return;

        int prevW = (int) currentBgWidth;
        currentBgWidth = MathHelper.lerp(Toolbox.clampF(Geomancy.CONFIG.spellmakerUiSpeed.value(),0.1f,1),currentBgWidth,desiredBgWidth);
        int newW = (int) currentBgWidth;

        if(newW!=prevW){

            int shift = newW-prevW;

            // wait for a two pixel shift for widgets since x = width/2
            shiftHoldOver += shift;
            if(Math.abs(shiftHoldOver) > 1)
            {
                int widgetShift = shiftHoldOver/2;
                shiftHoldOver-=widgetShift*2;

                // shift background
                this.backgroundWidth = (int) currentBgWidth;
                super.init();

                // shift widgets
                for(var w : widgets)
                {
                    w.setX(w.getX()-widgetShift);
                }
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
        handler.appearanceSlot.setEnabled(!inspecting);
        super.render(context, mouseX, mouseY, delta);

        // render cursor stack for new components...
        ItemStack cursorStack = this.touchDragStack.isEmpty() ? this.handler.getCursorStack() : this.touchDragStack;
        if (cursorStack.isEmpty()) {

            RenderSystem.disableDepthTest();
            context.getMatrices().push();
            context.getMatrices().translate((float)this.x, (float)this.y, 0.0F);

            // ...only if there isnt already a cursor stack being drawn
            cursorStack = handler.getHeldNewComponentStack();
            String text = Formatting.DARK_AQUA + "+";
            //int section = (int)((GeomancyClient.tick/5)%4);
            //text += switch(section){
            //    case 0->"/";
            //    case 1->"-";
            //    case 2->"\\";
            //    case 3->"|";
            //    default->"";
            //};
            this.drawItem(context, cursorStack, mouseX - this.x - 8, mouseY - this.y - 8, text);

            context.getMatrices().pop();
            RenderSystem.enableDepthTest();
        }

        // render hint item
        if (currentHint!=null && !handler.hasGrid() && !moving) {
            RenderSystem.disableDepthTest();

            int bgPosX = (width-getBackgroundWidth())/2;
            int bgPosY = (height-getBackgroundHeight())/2;

            final int infoPosX = bgPosX+SpellmakerScreen.bgWidth+10;
            final int infoPosY = bgPosY+10;

            var stack = currentHint.getFirst();
            this.drawItem(context, stack, infoPosX-8+150/2, infoPosY+15, null);

            RenderSystem.enableDepthTest();
        }

        // appearance slot
        if(handler.hasGrid() && !inspecting){
            final int infoPosX = x+SpellmakerScreen.bgWidth+10;

            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1,1,1,1);
            RenderSystem.setShaderTexture(0, SpellSelectScreen.SLOT_BG);

            context.drawTexture(SpellSelectScreen.SLOT_BG,infoPosX,y+SpellmakerScreenHandler.appearanceSlotYOffset,0,0,18,18,18,18);
            if (focusedSlot==handler.appearanceSlot) {
                drawSlotHighlight(context, infoPosX+1,y+SpellmakerScreenHandler.appearanceSlotYOffset+1, 0);
            }
        }

        handler.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
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

    public void refreshScrollButtons() {
        for (int j = 0; j < 2; j++) {
            selectNewCompScrollBtns[j].active = handler.componentScrollActive(j);
        }
    }

    public void setNewComponentSelected(boolean b) {
        this.placingNewComponent = b;
        init();
    }

    public void repickHint(){
        var list = hints.keySet().stream().toList();
        var key= list.get(Toolbox.random.nextInt(list.size()));
        currentHint = new Pair<>(key,hints.get(key));
    }
}
