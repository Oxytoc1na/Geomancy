package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.screen.slots.SpellComponentSelectionSlot;
import org.oxytocina.geomancy.client.screen.slots.SpellmakerAppearanceSlot;
import org.oxytocina.geomancy.client.screen.slots.SpellmakerHotbarSlot;
import org.oxytocina.geomancy.client.screen.slots.TagFilterSlot;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.DrawHelper;
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
    public SpellmakerAppearanceSlot appearanceSlot;
    private Inventory appearanceInventory;

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

        // appearance
        appearanceInventory = ImplementedInventory.of(DefaultedList.ofSize(1,ItemStack.EMPTY));
        appearanceSlot = (SpellmakerAppearanceSlot)addSlot(new SpellmakerAppearanceSlot(appearanceInventory,0,SpellmakerScreen.bgWidth+10+1,appearanceSlotYOffset+1,this));

        addProperties(arrayPropertyDelegate);

        addListener(new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
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

        if(!hasGrid() && selectedComponent!=null)
        {
            selectedComponentPosition=null;
            selectedComponentChanged();
        }

        updateAppearanceSlot();
    }

    public void updateAvailableComponents(){
        availableComponents.clear();
        var from = this.blockEntity.getComponentItemsFromPlayer(player);
        for (int i = 0; i < NEW_COMPONENTS_SLOT_COUNT; i++) {
            int index = i + NEW_COMPONENTS_WIDTH*componentSelectScroll;
            availableComponents.setStack(i,from.size() > index ? from.getStack(index) : ItemStack.EMPTY);
        }
        maxComponentSelectScroll = Math.round((float)Math.ceil((from.size()-NEW_COMPONENTS_SLOT_COUNT)/(float)NEW_COMPONENTS_WIDTH));

        if(availableComponentSlots!=null)
            for(var s : availableComponentSlots)
                s.setEnabled(hasGrid());

        if(!hasGrid() && selectedNewComponent!=null){
            selectedNewComponentIndex=-1;
            selectedNewComponentChanged();
        }
    }

    public ItemStack getOutput(){
        return blockEntity!=null?blockEntity.getOutput():null;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {

        int newCompSlotIndex = slotIndex-NEW_COMPONENTS_SLOT_OFFSET;

        // clicked on one of the component slots
        if(newCompSlotIndex < availableComponents.size() && newCompSlotIndex >= 0){
            if(hasGrid()){
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
            else{
                selectedNewComponentIndex = -1;
                selectedNewComponentChanged();
            }

        }
        // clicked on hotbar
        else{

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
            this.addSlot(new SpellmakerHotbarSlot(playerInventory, i, 8 + i * 18, 165,this));
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
            // see if we're not overriding an existing component
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

                    // select now placed component
                    selectedComponentPosition = componentPosition;
                    selectedComponentChanged();
                }
                else{
                    // error
                    selectedNewComponentIndex = -1;
                    selectedNewComponentChanged();
                }
            }
            else{
                // trying to override existing component
                // deselect new component and select existing component instead

                selectedNewComponentIndex = -1;
                selectedNewComponentChanged();

                selectedComponentPosition = currentGrid.components.containsKey(componentPosition) ? hoveredOverHexagonPos : null;
                selectedComponentChanged();
            }
        }
        else
        {
            if(selectedComponentPosition!=null&&selectedComponentPosition.equals(hoveredOverHexagonPos)){
                // deselect
                selectedComponentPosition=null;
                selectedComponentChanged();
            }
            else{
                // select if something there
                selectedComponentPosition = currentGrid.components.containsKey(componentPosition) ? hoveredOverHexagonPos : null;
                selectedComponentChanged();
            }
        }


    }

    public void selectedNewComponentChanged(){
        selectedNewComponent = selectedNewComponentIndex<0||selectedNewComponentIndex>=NEW_COMPONENTS_SLOT_COUNT?null:SpellComponentStoringItem.readComponent(availableComponents.getStack(selectedNewComponentIndex));
        if(selectedNewComponent==null)
            selectedNewComponentIndex=-1;
        if(screen!=null)
            screen.setNewComponentSelected(selectedNewComponent!=null);

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
    public static final int appearanceSlotYOffset = 70;

    @Environment(EnvType.CLIENT)
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
        Vector2f selectedDrawPosition = null;

        // render grid
        for (int y = 0; y < currentGrid.height; y++) {
            int yskew = y%2;
            for (int x = 0; x < currentGrid.width; x++) {
                if(!SpellGrid.positionIsInGrid(x,y,currentGrid.width,currentGrid.height)) continue;
                if(!currentGrid.inBounds(new Vector2i(x,y))) continue;
                drawPositionIndices.add(new Vector2i(x,y));
                var drawPos = new Vector2f(
                        bgPosX+fieldPosX+(float)fieldDrawOffsetX + (fieldDrawScale * (Math.round((x-0.5f+yskew/2f)*hexWidth))),
                        bgPosY+fieldPosY+(float)fieldDrawOffsetY + (fieldDrawScale * ((y-0.5f)*hexHeight)));
                drawPositions.add(drawPos);
                if(selectedComponentPosition!=null && selectedComponentPosition.equals(new Vector2i(x,y)))
                    selectedDrawPosition = drawPos;
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

                DrawHelper.drawTexture(context.getMatrices(),bgTexture,
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

                DrawHelper.drawTexture(context.getMatrices(),fgTexture,
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
                        DrawHelper.drawTexture(context.getMatrices(),
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

                    DrawHelper.drawTexture(context.getMatrices(),bgTexture,
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

                    DrawHelper.drawTexture(context.getMatrices(),fgTexture,
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

                    DrawHelper.drawTexture(context.getMatrices(),hexBGTexture,
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

        // render new selected component
        if(selectedNewComponent!=null && hasGrid()){
            RenderSystem.setShaderColor(1,1,1,1);

            final int infoPosX = bgPosX+SpellmakerScreen.bgWidth+10;
            final int infoPosY = bgPosY+10;
            var component = selectedNewComponent;

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
            RenderSystem.setShaderColor(1,1,1,1);

            // render placement info
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.abouttoplace1"),
                    infoPosX+previewWidth,infoPosY,0xFFFFFFFF,true);
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.abouttoplace2"),
                    infoPosX+previewWidth,infoPosY+10,0xFFFFFFFF,true);


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

                    final int posY = infoPosY
                            +6*SpellmakerScreenHandler.spacePerSideConfigButtons
                            +height
                            +SpellmakerScreenHandler.parametersYOffset
                            +SpellmakerScreenHandler.parameterEditsYOffset;

                    context.drawText(MinecraftClient.getInstance().textRenderer,
                            param.getDefaultSignal().getTypeText().append(Text.literal(" "+paramName).formatted(Formatting.GRAY)),
                            posX,posY,0xFFFFFFFF,true);
                    height += 10;
                }
            }
        }
        // render selected component
        else if(selectedComponent!=null){
            RenderSystem.setShaderColor(1,1,1,1);

            final int infoPosX = bgPosX+SpellmakerScreen.bgWidth+10;
            final int infoPosY = bgPosY+10;
            var component = selectedComponent;

            // render connection to field
            final float thicknessMod = (float)Math.sin(Math.PI*2*GeomancyClient.tick/20/3);

            if(selectedDrawPosition!=null)
            {
                var dp = new Vector2f(
                        selectedDrawPosition.x+hexWidth*fieldDrawScale/2,
                        selectedDrawPosition.y+hexHeight*fieldDrawScale/2
                );
                if( mouseInField(dp.x,dp.y))
                {
                    final int centerPreviewPosX = infoPosX + previewWidth/2;
                    final int centerPreviewPosY = infoPosY + previewHeight/2;
                    DrawHelper.drawLine(context,dp.x,dp.y,centerPreviewPosX,centerPreviewPosY,0.7f + 0.2f*thicknessMod,Toolbox.colorFromRGBA(0.8f,1,1,0.5f+0.3f*thicknessMod));
                }
            }

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

                if(screen.hoveredOverSideConfig(i))
                    DrawHelper.drawLine(context,startX,startY,endX,endY,0.5f + 0.2f*thicknessMod,Toolbox.colorFromRGBA(0.8f,1,1,0.5f+0.3f*thicknessMod));

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
                            height+=20;
                            break;
                        default:
                            context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(paramName),posX,posY,0xFFFFFFFF,true);
                            height+=25;
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

            // appearance
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.grid.appearance"),infoPosX+25,infoPosY-10+appearanceSlotYOffset+(18-10)/2,0xFFFFFFFF,true);

        }

        RenderSystem.setShaderColor(1,1,1,1);

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

    @Override
    public void setStackInSlot(int slot, int revision, ItemStack stack) {
        if(slot >= NEW_COMPONENTS_SLOT_OFFSET && slot < NEW_COMPONENTS_SLOT_OFFSET+NEW_COMPONENTS_SLOT_COUNT){
            // prevent server updates for component selection slots
            this.revision = revision;
            return;
        }
        super.setStackInSlot(slot, revision, stack);
    }

    public void trySelectComponentOf(SpellBlock function) {
        // check if this type of component is already selected
        // if so, unselect instead
        if(selectedNewComponent!=null){
            if(selectedNewComponent.function==function){
                selectedNewComponentIndex=-1;
                selectedNewComponentChanged();
                return;
            }
        }

        if(!hasGrid()) return;

        var from = this.blockEntity.getComponentItemsFromPlayer(player);
        for (int i = 0; i < from.size(); i++) {
            var stack = from.getStack(i);
            if(!(stack.getItem() instanceof SpellComponentStoringItem scsi)) continue;
            var comp = SpellComponentStoringItem.readComponent(stack);
            if(comp==null) continue;
            if(comp.function!=function) continue;

            // found fitting component
            int minScroll = Math.round((float)Math.ceil((i-NEW_COMPONENTS_SLOT_COUNT+1)/(float)NEW_COMPONENTS_WIDTH));
            int maxScroll = minScroll+1;
            int newScroll = Toolbox.clampI(componentSelectScroll,minScroll,maxScroll);
            if(newScroll!=componentSelectScroll)
            {
                // scroll to fit
                if(changeComponentViewScroll(newScroll-componentSelectScroll))
                {
                    if(screen!=null) screen.refreshScrollButtons();
                }
            }

            // set selected index
            int newSelectedIndex = i-componentSelectScroll*NEW_COMPONENTS_WIDTH;
            onSlotClick(newSelectedIndex+NEW_COMPONENTS_SLOT_OFFSET,1,SlotActionType.PICKUP,player);

            return;
        }
    }

    public ItemStack getHeldNewComponentStack() {
        if(selectedNewComponent==null) return ItemStack.EMPTY;
        return selectedNewComponent.getItemStack();
    }

    public void onAppearanceSlotClicked(SpellmakerAppearanceSlot spellmakerAppearanceSlot, ItemStack heldStack) {
        if(!(player instanceof ServerPlayerEntity)) return;
        currentGrid = SpellStoringItem.getOrCreateGrid(getOutput());
        if(!hasGrid()) return;
        currentGrid.displayStack = heldStack.copy();
        SpellStoringItem.writeGrid(getOutput(),currentGrid);
    }

    public void updateAppearanceSlot(){
        appearanceSlot.setEnabled(hasGrid());
        if(!hasGrid()){
            appearanceInventory.setStack(0,ItemStack.EMPTY);
            return;
        }
        var appearanceStack = currentGrid.displayStack;
        if(appearanceStack==null) appearanceStack=ItemStack.EMPTY;
        appearanceInventory.setStack(0,appearanceStack);
    }
}
