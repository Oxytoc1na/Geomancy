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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellprinterBlockEntity;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerButton;
import org.oxytocina.geomancy.client.screen.widgets.SpellmakerTextInput;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellBlock;
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

    public List<Text> errors = new ArrayList<>();

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
        readRecipeFromBlockEntity();
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

        int outputX = getBgPosX()+ SpellprinterScreenHandler.OUTPUT_SLOT_X;
        int outputY = getBgPosY()+ SpellprinterScreenHandler.OUTPUT_SLOT_Y;

        // instructions field
        int instructionsPosX = outputX+20;
        int instructionsPosY = outputY-25;
        SpellmakerTextInput textInput = new SpellmakerTextInput(MinecraftClient.getInstance().textRenderer,instructionsPosX,instructionsPosY,135,20,Text.empty());
        textInput.setMaxLength(50000);
        textInput.setText(handler.blockEntity.getRecipe());
        textInput.setChangedListener(s -> {
            Toolbox.playUISound(textInput.prevText.length() < s.length() ? ModSoundEvents.SPELLMAKER_TYPE:ModSoundEvents.SPELLMAKER_TYPE_BACK);
            textInput.prevText = s;
            // update spell recipe
            setRecipe(s);
            textInput.validInput=recipeGrid!=null;
        });
        textInput.onEditFinished = (s -> {

            // set value
            Toolbox.playUISound(ModSoundEvents.SPELLMAKER_TEXTFIELD_FINISHED);

            // update spell recipe
            setRecipe(s);
            textInput.validInput=recipeGrid!=null;

        });
        textInputs.add(textInput);
        addDrawableChild(textInput);
        widgets.add(textInput);
        printerInput = textInput;


        // print button
        int printPosX = outputX+20;
        int printPosY = outputY;
        SpellmakerButton printButton = new SpellmakerButton(printPosX,printPosY,0,0,65,18,Text.translatable("geomancy.spellprinter.print"),button -> {
            recalculateError();

            if(!hasGrid())
            {
                // nothing to print
                logError(Text.translatable("geomancy.spellprinter.error.norecipe"));
                return;
            }
            if(!canAffordRecipe()){
                // not enough "ink"
                logError(Text.translatable("geomancy.spellprinter.error.broke"));
                return;
            }
            if(getOutput().isEmpty()){
                // no "paper"
                logError(Text.translatable("geomancy.spellprinter.error.nocradle"));
                return;
            }
            if(!(getOutput().getItem() instanceof SpellStoringItem)){
                // "paper" isnt made of paper / incorrect format
                logError(Text.translatable("geomancy.spellprinter.error.wrongitem"));
                return;
            }
            var existingGrid = SpellStoringItem.readGrid(getOutput());
            if(existingGrid!=null && !existingGrid.isEmpty())
            {
                // "paper" already has something on it
                logError(Text.translatable("geomancy.spellprinter.error.cradlefull"));
                return;
            }

            // send packet to server
            PacketByteBuf data = PacketByteBufs.create();
            data.writeBlockPos(handler.blockEntity.getPos());
            data.writeString(printerInput.getText());
            ClientPlayNetworking.send(ModMessages.SPELLPRINTER_DESIRE_PRINT, data);
            Toolbox.playUISound(ModSoundEvents.SPELLPRINTER_PRINT);

        },ModSoundEvents.SPELLMAKER_REMOVE_COMPONENT);
        addDrawableChild(printButton);
        widgets.add(printButton);

        // scan button
        int scanPosX = printPosX+70;
        int scanPosY = printPosY;
        SpellmakerButton scanButton = new SpellmakerButton(scanPosX,scanPosY,0,0,65,18,Text.translatable("geomancy.spellprinter.scan"),button -> {
            // scan inserted spellcradle

            String recipe = getScanText().getString();
            printerInput.setText(recipe);
            // update spell recipe
            setRecipe(recipe);
            Toolbox.playUISound(ModSoundEvents.SPELLPRINTER_SCAN);

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

    }

    public ItemStack previousOutput = ItemStack.EMPTY;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);//,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);

        // render grid info
        if(getOutput()!=previousOutput){
            previousOutput=getOutput();
            recalculateError();
        }

        int bgPosX = getBgPosX();
        int bgPosY = getBgPosY();

        final int infoPosX = bgPosX+5;
        final int infoPosY = bgPosY+5;

        boolean hasDrawnTooltip = false;

        RenderSystem.setShaderColor(1,1,1,1);
        DrawHelper.drawTextOutlined(context,MinecraftClient.getInstance().textRenderer,
                Text.translatable("geomancy.spellprinter.info")
                ,infoPosX,infoPosY,0xFFFFFFFF,0);

        // draw errors
        int errorI = 0;
        final int maxErrorsShown = 1;
        for(var errorMessage : errors){
            boolean last = errorI>=maxErrorsShown-1;
            DrawHelper.drawTextOutlined(context,MinecraftClient.getInstance().textRenderer,
                    Text.empty().append(errorMessage).append(last&&errors.size()>maxErrorsShown?(" [+"+(errors.size()-maxErrorsShown)+"]"):"")
                    ,infoPosX,infoPosY+(errorI+1)*10,0xFF7070,0x704040);
            errorI++;
            if(last) break;
        }

        // render grid info
        if(hasGrid()){

            // cradle
            if(recipeItem!=null)
            {
                boolean matches = recipeItem == getOutput().getItem();
                int dx = infoPosX;
                int dy = infoPosY+20;
                DrawHelper.drawItem(context,null,null,recipeItem.getDefaultStack(),dx,dy,0,0,1,matches?1:0,matches?1:0);
                // tooltip
                if(!hasDrawnTooltip && DrawHelper.mouseInRect(mouseX,mouseY,dx,dy,18,18)){
                    List<Text> texts = new ArrayList<>();
                    texts.add(recipeItem.getName());
                    texts.add(Text.translatable("geomancy.spellprinter.tooltip.base").formatted(Formatting.GRAY));
                    if(!matches) texts.add(Text.translatable("geomancy.spellprinter.tooltip.base.missing").formatted(Formatting.RED));
                    context.drawTooltip(MinecraftClient.getInstance().textRenderer,texts,mouseX,mouseY);
                    hasDrawnTooltip = true;
                }
            }

            // appearance
            var previewStack = recipeGrid.displayStack;
            if((previewStack==null||previewStack.isEmpty()) && recipeItem!=null) previewStack=recipeItem.getDefaultStack();
            if(previewStack!=null&&!previewStack.isEmpty())
            {
                int dx = infoPosX+18;
                int dy = infoPosY+20;
                DrawHelper.drawItem(context,null,null,previewStack,dx,dy,0,0,1,1,1);
                // tooltip
                if(!hasDrawnTooltip && DrawHelper.mouseInRect(mouseX,mouseY,dx,dy,18,18)){
                    List<Text> texts = List.of(
                            previewStack.getName(),
                            Text.translatable("geomancy.spellprinter.tooltip.appearance").formatted(Formatting.GRAY)
                    );
                    context.drawTooltip(MinecraftClient.getInstance().textRenderer,texts,mouseX,mouseY);
                    hasDrawnTooltip = true;
                }
            }
            context.drawText(MinecraftClient.getInstance().textRenderer, recipeGrid.getName(),infoPosX+18+20,infoPosY+23,0xFFFFFFFF,true);

            // ingredient list
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellprinter.ingredients"),infoPosX,infoPosY+46,0xFFFFFFFF,true);

            var ingredients = recipeGrid.getIngredients();
            boolean creative = MinecraftClient.getInstance().player.isCreative();
            var ownedIngredients = creative?null:SpellmakerBlockEntity.getComponentAmountsIn(MinecraftClient.getInstance().player.getInventory());

            final int columns = 9;
            final int rows = 3;
            List<Pair<SpellBlock,Integer>> deficits = new ArrayList<>();
            for(var func : ingredients.keySet()){
                int needed = ingredients.get(func);
                int owned = creative?1000000: ownedIngredients.getOrDefault(func, 0);
                int deficit = needed-owned;
                deficits.add(new Pair<>(func,deficit));

            }
            deficits.sort(Comparator.comparingInt(Pair::getRight));
            deficits = Toolbox.reverseList(deficits);
            int i = 0;
            for(var deficitPair : deficits){
                int ix = i % columns;
                int iy = i / columns;
                var func = deficitPair.getLeft();
                boolean canAfford = deficitPair.getRight() <= 0;
                int dx = infoPosX+ix*18;
                int dy = infoPosY+46+18+iy*18;
                context.drawItem(func.getItemStack(),dx,dy);
                DrawHelper.drawCustomItemCount(context,dx,dy,ingredients.get(func).toString(),canAfford?0xFFFFFF:0xFF0000);

                int needs = ingredients.get(func);
                int has = creative?10000:ownedIngredients.getOrDefault(func,0);

                // tooltip
                if(!hasDrawnTooltip && DrawHelper.mouseInRect(mouseX,mouseY,dx,dy,18,18)){
                    List<Text> texts = canAfford ? List.of(
                            func.getNameWithObfuscation(),
                            Text.literal(creative ? ""+needs : has+"/"+needs)

                    ) : List.of(
                            func.getNameWithObfuscation().formatted(Formatting.RED),
                            Text.translatable("geomancy.spellprinter.tooltip.missing",deficitPair.getRight().toString()).formatted(Formatting.RED)
                    );
                    context.drawTooltip(MinecraftClient.getInstance().textRenderer,texts,mouseX,mouseY);
                    hasDrawnTooltip = true;
                }

                i++;
                if(i/columns >= rows) break;
            }

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
        var parsed = SpellprinterBlockEntity.parseGrid(recipe);
        recipeGrid = parsed.getLeft();
        recipeItem = parsed.getRight();
        recalculateError();

        if(!Objects.equals(handler.blockEntity.getRecipe(), recipe))
        {
            handler.blockEntity.setRecipe(recipe);
            // send packet to server
            var buf = PacketByteBufs.create();
            buf.writeBlockPos(handler.blockEntity.getPos());
            buf.writeString(recipe);
            ClientPlayNetworking.send(ModMessages.SPELLPRINTER_SET_RECIPE,buf);
        }
    }

    public void readRecipeFromBlockEntity(){
        setRecipe(handler.blockEntity.getRecipe());
    }

    public boolean canAffordRecipe(){
        if(recipeGrid==null) return false;
        boolean creative = MinecraftClient.getInstance().player.isCreative();
        if(creative) return true;
        var ingredients = recipeGrid.getIngredients();
        var ownedIngredients = creative?null:SpellmakerBlockEntity.getComponentAmountsIn(MinecraftClient.getInstance().player.getInventory());
        boolean canAfford = true;
            for(var func : ingredients.keySet()){
                int needed = ingredients.get(func);
                int owned = ownedIngredients.getOrDefault(func, 0);
                int deficit = needed-owned;
                if(deficit>0){canAfford=false;break;}
            }
        return canAfford;
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

        return Text.literal(SpellprinterBlockEntity.serializeGrid(grid,output));
    }

    public void logError(Text error){
    }

    public void recalculateError() {
        errors.clear();

        if(!hasGrid())
        {
            // nothing to print
            errors.add(Text.translatable("geomancy.spellprinter.error.norecipe"));
        }
        else if(!canAffordRecipe()){
            // not enough "ink"
            errors.add(Text.translatable("geomancy.spellprinter.error.broke"));
        }
        if(getOutput().isEmpty()){
            // no "paper"
            errors.add(Text.translatable("geomancy.spellprinter.error.nocradle"));
        }
        else if(!(getOutput().getItem() instanceof SpellStoringItem) || (recipeItem!=null && getOutput().getItem()!=recipeItem)){
            // "paper" isnt made of paper / incorrect format
            errors.add(Text.translatable("geomancy.spellprinter.error.wrongitem"));
        }
        var existingGrid = SpellStoringItem.readGrid(getOutput());
        if(existingGrid!=null && !existingGrid.isEmpty())
        {
            // "paper" already has something on it
            errors.add(Text.translatable("geomancy.spellprinter.error.cradlefull"));
        }
    }
}
