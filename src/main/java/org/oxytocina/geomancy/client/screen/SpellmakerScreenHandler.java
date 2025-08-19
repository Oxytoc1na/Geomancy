package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.screen.slots.SpellComponentSelectionSlot;
import org.oxytocina.geomancy.client.screen.slots.TagFilterSlot;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;

public class SpellmakerScreenHandler extends ScreenHandler {
    public static SpellmakerScreenHandler current;

    private final Inventory inventory;
    private Inventory availableComponents;
    public boolean desireAvailableComponentsRebuild = false;
    private final PropertyDelegate propertyDelegate;
    public final SpellmakerBlockEntity blockEntity;
    public final PlayerEntity player;

    public List<SpellComponentSelectionSlot> availableComponentSlots;


    public SpellmakerScreen screen;

    private boolean dragging = false;
    private boolean dragEnabled = false; // if clicked inide the field, allow dragging
    private double draggedX = 0;
    private double draggedY = 0;

    public double fieldDrawOffsetX = 0;
    public double fieldDrawOffsetY = 0;
    public float fieldDrawScale = 1f;
    public double lastZoomMousePosX = 0;
    public double lastZoomMousePosY = 0;
    public float desiredFieldDrawScale = 1f;

    public ItemStack currentOutput;
    public SpellGrid currentGrid;

    public int hoveredOverHexagon = -1;
    public Vector2i hoveredOverHexagonPos = null;
    public int selectedNewComponentIndex = -1;
    public SpellComponent selectedNewComponent;
    public Vector2i selectedComponentPosition = null;
    public SpellComponent selectedComponent;

    public static final int NEW_COMPONENTS_SLOT_COUNT = 14;
    public static final int NEW_COMPONENTS_X = 8;
    public static final int NEW_COMPONENTS_WIDTH = 7;
    public static final int NEW_COMPONENTS_Y = 124;
    public static final int NEW_COMPONENTS_SLOT_OFFSET = 10;

    public SpellmakerScreenHandler(int syncID, PlayerInventory inventory, PacketByteBuf buf){
        this(syncID,inventory,inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(3));
    }

    public SpellmakerScreenHandler(int syncID, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SPELLMAKER_SCREEN_HANDLER,syncID);

        current = this;
        checkSize((Inventory)blockEntity,SpellmakerBlockEntity.SLOT_COUNT);
        this.player = playerInventory.player;;
        this.inventory = (Inventory) blockEntity;
        playerInventory.onOpen(this.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = (SpellmakerBlockEntity) blockEntity;

        availableComponents = ImplementedInventory.ofSize(NEW_COMPONENTS_SLOT_COUNT);
        updateAvailableComponents();

        // 0
        this.addSlot(new TagFilterSlot(inventory,SpellmakerBlockEntity.OUTPUT_SLOT,152,142, ModItemTags.SPELL_STORING));

        // 1-9
        addPlayerHotbar(playerInventory);

        // 10-(10+14) // NEW_COMPONENTS_SLOT_OFFSET
        availableComponentSlots = addInventory(availableComponents,0,NEW_COMPONENTS_SLOT_COUNT,NEW_COMPONENTS_WIDTH,NEW_COMPONENTS_X,NEW_COMPONENTS_Y);

        addProperties(arrayPropertyDelegate);

        addListener(new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                Geomancy.logInfo("slot update "+slotId+" "+stack.getName().getString());
                ((SpellmakerScreenHandler)handler).desireAvailableComponentsRebuild = true;
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

            }
        });
    }

    public void outputItemChanged(){
        rebuild();
    }

    public void refresh(){

    }

    private ItemStack prevOutput = ItemStack.EMPTY;
    public void rebuild(){
        var newOutput = getOutput();

        if(prevOutput.isEmpty() && !newOutput.isEmpty())
            playUISound(ModSoundEvents.SPELLMAKER_INSERT_CRADLE);
        else if(!prevOutput.isEmpty() && newOutput.isEmpty())
            playUISound(ModSoundEvents.SPELLMAKER_REMOVE_CRADLE);

        prevOutput = newOutput.copy();
        currentGrid = SpellStoringItem.getOrCreateGrid(newOutput);
        selectedComponentChanged();
        updateAvailableComponents();
    }

    public void updateAvailableComponents(){
        availableComponents.clear();
        var from = player.isCreative() ? this.blockEntity.getCreativeComponentItems() : this.blockEntity.getComponentItems(player.getInventory());
        for (int i = 0; i < NEW_COMPONENTS_SLOT_COUNT; i++) {
            int index = i + NEW_COMPONENTS_WIDTH*componentSelectScroll;
            availableComponents.setStack(i,from.size() > index ? from.getStack(index) : ItemStack.EMPTY);
        }
        maxComponentSelectScroll = Math.round((float)Math.ceil((from.size()-14)/(float)NEW_COMPONENTS_WIDTH));
    }

    public ItemStack getOutput(){
        return blockEntity!=null?blockEntity.getOutput():null;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {

        int newCompSlotIndex = slotIndex-NEW_COMPONENTS_SLOT_OFFSET;

        if(newCompSlotIndex < availableComponents.size() && slotIndex >= 0){
            if(selectedNewComponentIndex==newCompSlotIndex){
                // deselect
                selectedNewComponentIndex = -1;
                selectedNewComponentChanged();
            }
            else{
                // select
                selectedNewComponentIndex = newCompSlotIndex;
                selectedNewComponentChanged();
            }

        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack res = ItemStack.EMPTY;
        Slot fromSlot = this.slots.get(invSlot);
        if (fromSlot != null && fromSlot.hasStack()) {
            ItemStack fromStack = fromSlot.getStack();
            res = fromStack.copy();
            // from storage to player
            if (invSlot < 1) {
                if (!this.insertItem(fromStack, 1, 1+9, true)) {
                    return ItemStack.EMPTY;
                }
                // from player to storage
            } else if (!this.insertItem(fromStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (fromStack.isEmpty()) {
                fromSlot.setStack(ItemStack.EMPTY);
            } else {
                fromSlot.markDirty();
            }
        }

        return res;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 165));
        }
    }

    private List<SpellComponentSelectionSlot> addInventory(Inventory inventory, int slotIndexOffset, int count, int width, int x, int y) {
        List<SpellComponentSelectionSlot> res =new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            if(i>= inventory.size()) return res;
            res.add((SpellComponentSelectionSlot)this.addSlot(new SpellComponentSelectionSlot(inventory, slotIndexOffset+i, x + (i%width) * 18, y + (i/width)*18,this)));
        }
        return res;
    }

    public void tick(){

        float newZoom = MathHelper.lerp(0.3f,fieldDrawScale,desiredFieldDrawScale);
        if(newZoom!=fieldDrawScale)
        {
            float factor = newZoom/fieldDrawScale;
            zoomTick(factor);
        }

        if(desireAvailableComponentsRebuild)
        {
            desireAvailableComponentsRebuild=false;
            updateAvailableComponents();
        }
    }


    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(!dragEnabled) return;

        draggedX+=deltaX;
        draggedY+=deltaY;

        if(Math.abs(draggedX) + Math.abs(draggedY) > 2)
            dragging = true;

        fieldDrawOffsetX += deltaX;
        fieldDrawOffsetY += deltaY;

        checkOffsetBounds();
    }

    public void mouseScrolled(double mouseX, double mouseY, double amount) {
        if(amount==0) return;

        if(mouseInField(mouseX,mouseY))
        {
            // zooming
            zoom(1+0.2f*(float)amount,mouseX,mouseY);
        }
        else if(mouseInNewComponents(mouseX,mouseY)){
            // scrolling through new components
            if(changeComponentViewScroll(-Toolbox.sign((float)amount))){
                if(screen!=null) screen.refreshScrollButtons();
                Toolbox.playUISound(ModSoundEvents.SPELLMAKER_ROTATE);
            }
        }

    }

    private void zoomTick(float factor){
        var oldScale = fieldDrawScale;
        var newScale = Toolbox.clampF(oldScale * factor,0.5f,2);

        int bgPosX = getBgPosX();
        int bgPosY = getBgPosY();

        var mouse_x = lastZoomMousePosX-bgPosX-fieldPosX-fieldDrawOffsetX*oldScale;
        var mouse_y = lastZoomMousePosY-bgPosY-fieldPosY-fieldDrawOffsetY*oldScale;

        float view_width = fieldWidth;
        float view_height = fieldHeight;

        var side_ratio_x = (mouse_x - (view_width / 2)) / view_width + 0.5f;
        var side_ratio_h = (mouse_y - (view_height / 2)) / view_height + 0.5f;

        // newoff = pff + offoff
        // newoff = off + (scaled - newscaled) * shiftfac
        // newoff= off + (length*prevscale - length*prevscale*scale) * shiftFac
        fieldDrawOffsetX = fieldDrawOffsetX + (fieldWidth*oldScale - fieldWidth*oldScale*factor) * side_ratio_x;
        fieldDrawOffsetY = fieldDrawOffsetY + (fieldHeight*oldScale - fieldHeight*oldScale*factor) * side_ratio_h;

        checkOffsetBounds();

        fieldDrawScale = newScale;
    }

    public void zoom(float factor,double mouseX, double mouseY){
        var oldScale = desiredFieldDrawScale;
        var newScale = Toolbox.clampF(oldScale * factor,0.5f,2);
        desiredFieldDrawScale = newScale;
        lastZoomMousePosX = mouseX;
        lastZoomMousePosY = mouseY;
    }

    private void checkOffsetBounds(){
        float minX = 0-fieldWidth/fieldDrawScale;
        float minY = 0-fieldHeight/fieldDrawScale;

        float maxX = fieldWidth;
        float maxY = fieldHeight;

        fieldDrawOffsetX = Math.min(Math.max(minX,fieldDrawOffsetX),maxX);
        fieldDrawOffsetY = Math.min(Math.max(minY,fieldDrawOffsetY),maxY);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        dragEnabled = mouseInField(mouseX,mouseY);
    }

    public void mouseReleased(double mouseX, double mouseY, int button){
        // was it inside the field?
        if(
                hasGrid() &&
                        mouseInField(mouseX,mouseY)
        ){
            if(!dragging) hexClicked();
        }

        dragging = false;
        dragEnabled = false;
        draggedX = 0;
        draggedY=0;
    }

    public int getBgPosX(){
        return (screen.width-screen.getBackgroundWidth())/2;
    }
    public int getBgPosY(){
        return (screen.height-screen.getBackgroundHeight())/2;
    }

    public boolean mouseInField(double x, double y){
        int bgPosX = getBgPosX();
        int bgPosY = getBgPosY();

        return  x > bgPosX+fieldPosX &&
                y > bgPosY+fieldPosY &&
                x < bgPosX+fieldPosX+fieldWidth &&
                y < bgPosY+fieldPosY+fieldHeight;
    }
    public boolean mouseInNewComponents(double x, double y){
        int bgPosX = getBgPosX();
        int bgPosY = getBgPosY();

        return  x > bgPosX+NEW_COMPONENTS_X-1 &&
                y > bgPosY+NEW_COMPONENTS_Y-1 &&
                x < bgPosX+NEW_COMPONENTS_X-1+(NEW_COMPONENTS_WIDTH*18) &&
                y < bgPosY+NEW_COMPONENTS_Y-1+Math.round((float)Math.ceil(NEW_COMPONENTS_SLOT_COUNT/(float)NEW_COMPONENTS_WIDTH))*18;
    }

    private void hexClicked(){

        Vector2i componentPosition = hoveredOverHexagonPos;

        // place a new component down
        if(selectedNewComponent!=null)
        {
            if(!currentGrid.components.containsKey(componentPosition))
            {
                // check if can still afford
                // TODO
                boolean canAfford = true;

                if(canAfford){
                    SpellComponent componentToBePlaced = selectedNewComponent.clone();
                    componentToBePlaced.position = componentPosition;

                    playUISound(ModSoundEvents.SPELLMAKER_INSERT_COMPONENT);

                    // send packet to server
                    PacketByteBuf data = PacketByteBufs.create();
                    var nbt = new NbtCompound();
                    componentToBePlaced.writeNbt(nbt);
                    data.writeNbt(nbt);
                    data.writeBlockPos(blockEntity.getPos());
                    ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_ADD_COMPONENT, data);

                    // deselect
                    selectedNewComponentIndex = -1;
                    selectedNewComponentChanged();
                }
                else{
                    // error
                    selectedNewComponentIndex = -1;
                    selectedNewComponentChanged();
                }
            }
        }
        else
        {
            if(selectedComponentPosition==hoveredOverHexagonPos){
                // deselect
                selectedComponentPosition=null;
                selectedComponentChanged();
            }
            else{
                // select if something there
                selectedComponentPosition = currentGrid.components.containsKey(componentPosition) ? hoveredOverHexagonPos : null;
                selectedComponentChanged();
            }
            selectedComponentChanged();
        }


    }

    public void selectedNewComponentChanged(){
        selectedNewComponent = selectedNewComponentIndex<0||selectedNewComponentIndex>=NEW_COMPONENTS_SLOT_COUNT?null:SpellComponentStoringItem.readComponent(availableComponents.getStack(selectedNewComponentIndex));
        if(selectedNewComponent==null)
            selectedNewComponentIndex=-1;
    }

    public void selectedComponentChanged(){
        selectedComponent = getComponentFromPosition(selectedComponentPosition);
        screen.setComponentInspected(selectedComponent!=null);
    }

    private SpellComponent getComponentFromPosition(Vector2i index){
        if(!hasGrid()) return null;
        if(index==null) return null;
        return currentGrid.getComponent(index);
    }

    public boolean hasGrid(){return currentGrid!=null;}

    public static final int fieldPosX = 8;
    public static final int fieldPosY = 8;
    public static final int fieldWidth = 160;
    public static final int fieldHeight = 112;
    public static final Identifier spellmakerGuiTexture = Geomancy.locate("textures/gui/spellmaker_block_gui.png");
    public static final Identifier hexBGTexture = Geomancy.locate("textures/gui/spellmaker_hex_bg.png");
    public static final int hexBGTextureSize = 32;
    public static final int hexWidth = 32;
    public static final int hexHeight = 28;
    public static final float previewScale = 2;
    public static final int previewWidth = Math.round(hexWidth * previewScale);
    public static final int previewHeight = Math.round(hexBGTextureSize * previewScale);
    public static final int sideConfigsOffset = 85;
    public static final int spacePerSideConfigButtons = 15;
    public static final int componentInfoYOffset = 30;
    public static final int parametersYOffset = 10;
    public static final int parameterEditsYOffset = 15;
    public static final int gridPropXOffset = -100;

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int bgPosX = getBgPosX();
        int bgPosY = getBgPosY();

        var newOutput = getOutput();
        if(currentOutput!=newOutput){
            outputItemChanged();
        }
        currentOutput = newOutput;

        if(currentGrid==null) {
            fieldDrawScale=1;
            fieldDrawOffsetX=0;
            fieldDrawOffsetY=0;
            return;
        }

        RenderSystem.setShaderColor(1,1,1,1);

        // render selection outline
        if(selectedNewComponentIndex>-1){
            context.drawTexture(spellmakerGuiTexture,
                    bgPosX+NEW_COMPONENTS_X-4+(selectedNewComponentIndex%NEW_COMPONENTS_WIDTH)*18,
                    bgPosY+NEW_COMPONENTS_Y-4+(selectedNewComponentIndex/NEW_COMPONENTS_WIDTH)*18,
                    176,0,24,24);
        }

        final float scaledHexWidth = hexWidth*fieldDrawScale;
        final float scaledHexHeight = hexBGTextureSize*fieldDrawScale;

        context.enableScissor(bgPosX+fieldPosX,bgPosY+fieldPosY,bgPosX+fieldPosX+fieldWidth,bgPosY+fieldPosY+fieldHeight);

        List<Vector2f> drawPositions = new ArrayList<>();
        List<Vector2i> drawPositionIndices = new ArrayList<>();

        // render grid
        for (int y = 0; y < currentGrid.height; y++) {
            int yskew = y%2;
            for (int x = 0; x < currentGrid.width; x++) {
                if(!SpellGrid.positionIsInGrid(x,y,currentGrid.width,currentGrid.height)) continue;
                if(!currentGrid.inBounds(new Vector2i(x,y))) continue;
                drawPositionIndices.add(new Vector2i(x,y));
                drawPositions.add(new Vector2f(
                        bgPosX+fieldPosX+(float)fieldDrawOffsetX + (fieldDrawScale * (Math.round((x-0.5f+yskew/2f)*hexWidth))),
                        bgPosY+fieldPosY+(float)fieldDrawOffsetY + (fieldDrawScale * ((y-0.5f)*hexHeight))
                ));
            }
        }

        // calculate selected hexagon
        int selectedHexagon = -1;
        hoveredOverHexagonPos = null;
        if(mouseInField(mouseX,mouseY)){
            float minDist = 10000000;
            for (int i = 0; i < drawPositions.size(); i++) {
                float dist = Vector2f.distance(
                        mouseX,
                        mouseY,
                        drawPositions.get(i).x+scaledHexWidth/2,
                        drawPositions.get(i).y+scaledHexHeight/2
                );
                if(dist>= minDist)continue;
                minDist=dist;
                selectedHexagon=i;
                hoveredOverHexagonPos = drawPositionIndices.get(i);

            }
        }
        hoveredOverHexagon=selectedHexagon;

        // render grid
        for (int i = 0; i < drawPositions.size(); i++) {
            // render component
            var component = currentGrid.getComponent(drawPositionIndices.get(i));
            if(component!=null){
                var fgTexture = component.getHexFrontTexture();
                var bgTexture = component.getHexBackTexture();
                RenderSystem.setShaderColor(1,1,1,1);

                drawTexture(context.getMatrices(),bgTexture,
                        drawPositions.get(i).x,
                        drawPositions.get(i).y,
                        scaledHexWidth,
                        scaledHexHeight,
                        (hexBGTextureSize-hexWidth)/2f,
                        0,
                        hexWidth,
                        hexBGTextureSize,
                        hexBGTextureSize,
                        hexBGTextureSize
                );

                drawTexture(context.getMatrices(),fgTexture,
                        drawPositions.get(i).x,
                        drawPositions.get(i).y,
                        scaledHexWidth,
                        scaledHexHeight,
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
                        drawTexture(context.getMatrices(),
                                tex,
                                drawPositions.get(i).x,
                                drawPositions.get(i).y,
                                scaledHexWidth,
                                scaledHexHeight,
                                (hexBGTextureSize-hexWidth)/2f,
                                0,
                                hexWidth,
                                hexBGTextureSize,
                                hexBGTextureSize,
                                hexBGTextureSize);
                    }


                }
            }
            // render empty cell
            else{
                // render new component
                if(selectedNewComponent!=null && i==selectedHexagon){
                    component = selectedNewComponent;
                    var fgTexture = component.getHexFrontTexture();
                    var bgTexture = component.getHexBackTexture();
                    RenderSystem.setShaderColor(1,1,1,1);

                    drawTexture(context.getMatrices(),bgTexture,
                            drawPositions.get(i).x,
                            drawPositions.get(i).y,
                            scaledHexWidth,
                            scaledHexHeight,
                            (hexBGTextureSize-hexWidth)/2f,
                            0,
                            hexWidth,
                            hexBGTextureSize,
                            hexBGTextureSize,
                            hexBGTextureSize
                    );

                    drawTexture(context.getMatrices(),fgTexture,
                            drawPositions.get(i).x,
                            drawPositions.get(i).y,
                            scaledHexWidth,
                            scaledHexHeight,
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
                                    Math.round(drawPositions.get(i).x),
                                    Math.round(drawPositions.get(i).y),
                                    Math.round(scaledHexWidth),
                                    Math.round(scaledHexHeight),
                                    (hexBGTextureSize-hexWidth)/2f,
                                    0,
                                    hexWidth,
                                    hexBGTextureSize,
                                    hexBGTextureSize,
                                    hexBGTextureSize);
                        }


                    }
                }
                // actually render an empty cell
                else{
                    if(i==selectedHexagon || drawPositionIndices.get(i) ==selectedComponentPosition) {
                        RenderSystem.setShaderColor(1,1,1,1);
                    }
                    else{
                        RenderSystem.setShaderColor(0.5f,0.5f,0.5f,1);
                    }

                    drawTexture(context.getMatrices(),hexBGTexture,
                            drawPositions.get(i).x,
                            drawPositions.get(i).y,
                            scaledHexWidth,
                            scaledHexHeight,
                            (hexBGTextureSize-hexWidth)/2f,
                            0,
                            hexWidth,
                            hexBGTextureSize,
                            hexBGTextureSize,
                            hexBGTextureSize
                    );
                }

            }


        }

        context.disableScissor();

        // render selected component
        if(selectedComponent!=null){
            RenderSystem.setShaderColor(1,1,1,1);

            final int infoPosX = bgPosX+SpellmakerScreen.bgWidth+10;
            final int infoPosY = bgPosY+10;
            var component = selectedComponent;

            // render component
            {
                var fgTexture = component.getHexFrontTexture();
                var bgTexture = component.getHexBackTexture();
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

                context.drawTexture(fgTexture,
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
            RenderSystem.setShaderColor(1,1,1,1);
            for (int i = 0; i < component.sideConfigs.length; i++) {
                var conf = component.sideConfigs[i];

                Vector2f offset = Toolbox.rotateVector(new Vector2f(previewWidth/2f,0),Math.PI*2*(i-1)/6f);

                final float startX = infoPosX+previewWidth/2f+offset.x;
                final float startY = infoPosY+previewHeight/2f+offset.y;
                final int endX = infoPosX+sideConfigsOffset;
                final float endY = infoPosY+(i+0.5f)*spacePerSideConfigButtons;
                final float thicknessMod = (float)Math.sin(Math.PI*2*GeomancyClient.tick/20/3);

                if(screen.hoveredOverSideConfig(i))
                    drawLine(context,startX,startY,endX,endY,0.5f + 0.2f*thicknessMod,Toolbox.colorFromRGBA(0.8f,1,1,0.5f+0.3f*thicknessMod));

            }

            // render i/o info
            var ioInfo = SpellComponentStoringItem.componentTooltip(component,true);
            for (int i = 0; i < ioInfo.size(); i++) {
                var t = ioInfo.get(i);
                // leave room for control buttons
                context.drawText(MinecraftClient.getInstance().textRenderer, t,infoPosX,infoPosY+previewHeight+componentInfoYOffset+i*10,0xFFFFFFFF,true);
            }

            // render parameters
            List<String> paramNames = component.function.parameters.keySet().stream().toList();
            if(!paramNames.isEmpty()){
                final int posX = infoPosX+SpellmakerScreenHandler.sideConfigsOffset;

                final int headlinePosY = infoPosY+6*SpellmakerScreenHandler.spacePerSideConfigButtons+SpellmakerScreenHandler.parametersYOffset;
                context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.parameter"+(paramNames.size()>1?"s":"")).formatted(Formatting.UNDERLINE),posX,headlinePosY,0xFFFFFFFF,true);

                int height = 0;
                for(int i = 0; i < paramNames.size();i++){
                    final String paramName = paramNames.get(i);
                    final var param = component.function.parameters.get(paramName);
                    final var configuredParam = component.getParam(paramName);

                    final int posY = infoPosY
                            +6*SpellmakerScreenHandler.spacePerSideConfigButtons
                            +height
                            +SpellmakerScreenHandler.parametersYOffset
                            +SpellmakerScreenHandler.parameterEditsYOffset;

                    switch(param.type){
                        case ConstantBoolean:
                            i+=20;
                            break;
                        default:
                            context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(paramName),posX,posY,0xFFFFFFFF,true);
                            i+=25;
                            break;
                    }


                }
            }
        }
        // render grid info
        else if(hasGrid()){
            final int infoPosX = bgPosX+SpellmakerScreen.bgWidth+10;
            final int infoPosY = bgPosY+10;
            RenderSystem.setShaderColor(1,1,1,1);
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.grid.name"),infoPosX,infoPosY,0xFFFFFFFF,true);

        }

        RenderSystem.setShaderColor(1,1,1,1);

    }

    void drawLine(DrawContext context, float x1, float y1, float x2, float y2, float thickness, int color) {
        Vector2f pos1 = new Vector2f(x1,y1);
        Vector2f pos2 = new Vector2f(x2,y2);
        Vector2f direction = new Vector2f(pos2.x-pos1.x,pos2.y-pos1.y).normalize();
        Vector2f negDir = new Vector2f(direction).negate();
        Vector2f dirPep = new Vector2f(direction).perpendicular();

        Vector2f v1 = new Vector2f(pos1).add(new Vector2f(negDir).mul(thickness)).add(new Vector2f(dirPep).mul(thickness));
        Vector2f v2 = new Vector2f(pos1).add(new Vector2f(negDir).mul(thickness)).add(new Vector2f(dirPep).mul(-thickness));
        Vector2f v3 = new Vector2f(pos2).add(new Vector2f(direction).mul(thickness)).add(new Vector2f(dirPep).mul(thickness));
        Vector2f v4 = new Vector2f(pos2).add(new Vector2f(direction).mul(thickness)).add(new Vector2f(dirPep).mul(-thickness));

        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

        float f = (float) ColorHelper.Argb.getAlpha(color) / 255.0F;
        float g = (float) ColorHelper.Argb.getRed(color) / 255.0F;
        float h = (float) ColorHelper.Argb.getGreen(color) / 255.0F;
        float j = (float) ColorHelper.Argb.getBlue(color) / 255.0F;
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        vertexConsumer.vertex(matrix4f, v1.x,v1.y, 0).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, v2.x,v2.y, 0).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, v4.x,v4.y, 0).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, v3.x,v3.y, 0).color(g, h, j, f).next();
        context.draw();
    }

    void drawTexture(
            MatrixStack matrices,Identifier texture, float x, float y, float width, float height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
    ) {
        drawTexture(matrices,texture, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }
    void drawTexture(
            MatrixStack matrices,Identifier texture, float x1, float x2, float y1, float y2, float z, float regionWidth, float regionHeight, float u, float v, int textureWidth, int textureHeight
    ) {
        drawTexturedQuad(
                matrices,texture, x1, x2, y1, y2, z, (u + 0.0F) / textureWidth, (u + regionWidth) / textureWidth, (v + 0.0F) / textureHeight, (v + regionHeight) / textureHeight
        );
    }
    void drawTexturedQuad(MatrixStack matrices, Identifier texture, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix4f, x1, y2, z).texture(u1, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y2, z).texture(u2, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y1, z).texture(u2, v1).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public void playUISound(SoundEvent event) {
        if(screen!=null)
            screen.playUISound(event);
        //Toolbox.playSound(event,blockEntity.getWorld(),blockEntity.getPos(), SoundCategory.PLAYERS,1,1);
    }

    public int componentSelectScroll = 0;
    public int maxComponentSelectScroll = 0;
    public boolean changeComponentViewScroll(int scroll) {
        int prevScroll;
        int initialScroll = componentSelectScroll;
        do{
            prevScroll = componentSelectScroll;
            componentSelectScroll = Toolbox.clampI(prevScroll + scroll,0,maxComponentSelectScroll);
            if(selectedNewComponent!=null){
                selectedNewComponentIndex-=scroll*NEW_COMPONENTS_WIDTH;
                if(selectedNewComponentIndex<0||selectedNewComponentIndex>=NEW_COMPONENTS_SLOT_COUNT)
                    selectedNewComponentIndex=-1;
                selectedNewComponentChanged();
            }
            scroll = 0;
            updateAvailableComponents();
        }
        while(prevScroll != componentSelectScroll);
        return initialScroll!=componentSelectScroll;
    }

    public boolean componentScrollVisible() {
        return maxComponentSelectScroll>0;
    }

    public boolean componentScrollActive(int i) {
        if(i==0){
            return componentSelectScroll >0;
        }
        else{
            return componentSelectScroll<maxComponentSelectScroll;
        }
    }
}
