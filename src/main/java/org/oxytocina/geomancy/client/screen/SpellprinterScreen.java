package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerButton;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerTextInput;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.DrawHelper;
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

    public SpellGrid recipeGrid = null;
    public Item recipeItem = null;

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
        int infoPosX = getBgPosX()+10;
        int infoPosY = getBgPosY()+10;
        SpellmakerTextInput textInput = new SpellmakerTextInput(MinecraftClient.getInstance().textRenderer,infoPosX,infoPosY,100,20,Text.empty());
        textInput.setText("");
        textInput.setChangedListener(s -> {
            Toolbox.playUISound(textInput.prevText.length() < s.length() ? ModSoundEvents.SPELLMAKER_TYPE:ModSoundEvents.SPELLMAKER_TYPE_BACK);
            textInput.prevText = s;
        });
        textInput.onEditFinished = (s -> {

            // set value
            Toolbox.playUISound(ModSoundEvents.SPELLMAKER_TEXTFIELD_FINISHED);

            // update spell recipe
            setRecipe(s);

            textInput.validInput=true;

        });
        textInputs.add(textInput);
        addDrawableChild(textInput);
        widgets.add(textInput);
        printerInput = textInput;


        // print button
        int printPosX = infoPosX;
        int printPosY = infoPosY+100;
        SpellmakerButton printButton = new SpellmakerButton(printPosX,printPosY,0,0,50,20,Text.translatable("geomancy.spellprinter.print"),button -> {
            // send packet to server
            PacketByteBuf data = PacketByteBufs.create();
            data.writeBlockPos(handler.blockEntity.getPos());
            data.writeString(printerInput.getText());
            ClientPlayNetworking.send(ModMessages.SPELLPRINTER_DESIRE_PRINT, data);

        },ModSoundEvents.SPELLMAKER_REMOVE_COMPONENT);
        addDrawableChild(printButton);
        widgets.add(printButton);

        // scan button
        int scanPosX = printPosX;
        int scanPosY = printPosY+50;
        SpellmakerButton scanButton = new SpellmakerButton(scanPosX,scanPosY,0,0,50,20,Text.translatable("geomancy.spellprinter.scan"),button -> {
            // scan inserted spellcradle

            printerInput.setText(getScanText().getString());

        },ModSoundEvents.SPELLMAKER_REMOVE_COMPONENT);
        addDrawableChild(scanButton);
        widgets.add(scanButton);
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

        // render grid info

        int bgPosX = getBgPosX();
        int bgPosY = getBgPosY();

        int outputX = SpellprinterScreenHandler.OUTPUT_SLOT_X;
        int outputY = SpellprinterScreenHandler.OUTPUT_SLOT_Y;

        // render grid info
        if(hasGrid()){
            final int infoPosX = bgPosX+SpellprinterScreen.bgWidth+10;
            final int infoPosY = bgPosY+10;
            RenderSystem.setShaderColor(1,1,1,1);
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.grid.name"),infoPosX,infoPosY,0xFFFFFFFF,true);

            // appearance
            var previewStack = recipeGrid.displayStack;
            if((previewStack==null||previewStack.isEmpty()) && recipeItem!=null) previewStack=recipeItem.getDefaultStack();
            if(previewStack!=null&&!previewStack.isEmpty())
            {
                DrawHelper.drawItem(context,null,null,previewStack,outputX,outputY,0,0,1,1,1);
            }
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.grid.appearance"),infoPosX+25,infoPosY-10+appearanceSlotYOffset+(18-10)/2,0xFFFFFFFF,true);


        }

        RenderSystem.setShaderColor(1,1,1,1);


        drawMouseoverTooltip(context,mouseX,mouseY);
    }

    public ItemStack getOutput(){
        return handler.getOutput();
    }

    public boolean hasGrid(){
        return recipeGrid!=null;
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    public int getBackgroundHeight() {
        return backgroundHeight;
    }

    public int getBgPosX(){
        return (width-getBackgroundWidth())/2;
    }
    public int getBgPosY(){
        return (height-getBackgroundHeight())/2;
    }

    public void setRecipe(String recipe){
        var parsed = parseGrid(recipe);
        recipeGrid = parsed.getLeft();
        recipeItem = parsed.getRight();
    }

    public static Pair<SpellGrid, Item> parseGrid(String recipe){
        try{
            var recipeNbt = StringNbtReader.parse(recipe);
            Identifier itemID = Identifier.tryParse(recipeNbt.getString("item"));
            Item item = itemID!=null?Registries.ITEM.get(itemID):null;
            return new Pair<>(new SpellGrid(ItemStack.EMPTY,recipeNbt),item);

        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    public static String serializeGrid(SpellGrid grid,ItemStack on){
        var nbt = new NbtCompound();
        grid.writeNbt(nbt);
        nbt.putString("item", Registries.ITEM.getId(on.getItem()).toString());
        return nbt.asString();
    }

    public Text getScanText(){
        var output = handler.getOutput();
        if(output.isEmpty())
            return Text.translatable("geomancy.spellprinter.scan.empty");
        if(!(output.getItem() instanceof SpellStoringItem storer))
            return Text.translatable("geomancy.spellprinter.scan.notcradle");

        var grid = SpellStoringItem.readGrid(output);
        if(grid==null)
            return Text.translatable("geomancy.spellprinter.scan.cradleempty");

        return Text.literal(serializeGrid(grid,output));
    }
}
