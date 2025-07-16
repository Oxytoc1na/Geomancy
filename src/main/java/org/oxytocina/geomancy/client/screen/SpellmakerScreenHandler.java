package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.event.ScrollTracker;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class SpellmakerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private Inventory availableComponents;
    private final PropertyDelegate propertyDelegate;
    public final SpellmakerBlockEntity blockEntity;
    public final PlayerEntity player;

    public SpellmakerScreen screen;

    public double fieldDrawOffsetX = 0;
    public double fieldDrawOffsetY = 0;
    public float fieldDrawScale = 0.5f;

    public ItemStack currentOutput;
    public SpellGrid currentGrid;

    public int hoveredOverHexagon = -1;
    public int selectedNewComponentIndex = -1;
    public SpellComponent selectedNewComponent;
    public int selectedComponentIndex = -1;
    public SpellComponent selectedComponent;

    public static final int NEW_COMPONENTS_SLOT_COUNT = 14;
    public static final int NEW_COMPONENTS_X = 8;
    public static final int NEW_COMPONENTS_WIDTH = 7;
    public static final int NEW_COMPONENTS_Y = 124;

    public SpellmakerScreenHandler(int syncID, PlayerInventory inventory, PacketByteBuf buf){
        this(syncID,inventory,inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(3));
    }

    public SpellmakerScreenHandler(int syncID, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SPELLMAKER_SCREEN_HANDLER,syncID);
        checkSize((Inventory)blockEntity,SpellmakerBlockEntity.SLOT_COUNT);
        this.player = playerInventory.player;;
        this.inventory = (Inventory) blockEntity;
        playerInventory.onOpen(this.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = (SpellmakerBlockEntity) blockEntity;

        availableComponents = this.blockEntity.getComponentItems(playerInventory);

        addInventory(availableComponents,0,NEW_COMPONENTS_SLOT_COUNT,NEW_COMPONENTS_WIDTH,NEW_COMPONENTS_X,NEW_COMPONENTS_Y);

        this.addSlot(new Slot(inventory,SpellmakerBlockEntity.OUTPUT_SLOT,152,142));

        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }

    public void outputItemChanged(){
        rebuild();
    }

    public void rebuild(){
        currentGrid = SpellStoringItem.getOrCreateGrid(getOutput());
        selectedComponentChanged();
    }

    public ItemStack getOutput(){
        return blockEntity!=null?blockEntity.getOutput():null;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {


        if(slotIndex < availableComponents.size() && slotIndex >-1){
            if(selectedNewComponentIndex==slotIndex){
                // deselect
                selectedNewComponentIndex = -1;
                selectedNewComponent = null;
            }
            else{
                selectedNewComponentIndex = slotIndex;
                selectedNewComponent = SpellComponentStoringItem.readComponent(availableComponents.getStack(selectedNewComponentIndex));
            }

        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
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

    private void addInventory(Inventory inventory, int slotIndexOffset, int count, int width, int x, int y) {
        for (int i = 0; i < count; ++i) {
            if(i>= inventory.size()) return;
            this.addSlot(new SpellComponentSelectionSlot(inventory, slotIndexOffset+i, x + (i%width) * 18, y + (i/width)*18,this));
        }
    }



    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        fieldDrawOffsetX += deltaX/fieldDrawScale;
        fieldDrawOffsetY += deltaY/fieldDrawScale;
    }

    public void mouseScrolled(double mouseX, double mouseY, double amount) {
        if(amount!=0){
            // scrolling
            fieldDrawScale *= 1+0.2f*(float)amount;
            fieldDrawScale = Toolbox.clampF(fieldDrawScale,0.1f,2f);
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        int bgPosX = (screen.width-screen.getBackgroundWidth())/2;
        int bgPosY = (screen.height-screen.getBackgroundHeight())/2;

        // was it inside the field?
        if(
                hasGrid() &&
                mouseX > bgPosX+fieldPosX &&
                mouseY > bgPosY+fieldPosY &&
                mouseX < bgPosX+fieldPosX+fieldWidth &&
                mouseY < bgPosY+fieldPosY+fieldHeight
        ){
            hexClicked();
        }
    }

    private void hexClicked(){

        Vector2i componentPosition = getComponentPositionFromIndex(hoveredOverHexagon);

        // place a new component down
        if(selectedNewComponent!=null)
        {
            // check if can still afford
            // TODO
            boolean canAfford = true;

            if(canAfford){
                SpellComponent componentToBePlaced = selectedNewComponent.clone();
                componentToBePlaced.position = componentPosition;

                // send packet to server
                PacketByteBuf data = PacketByteBufs.create();
                var nbt = new NbtCompound();
                componentToBePlaced.writeNbt(nbt);
                data.writeNbt(nbt);
                data.writeBlockPos(blockEntity.getPos());
                ClientPlayNetworking.send(ModMessages.SPELLMAKER_TRY_ADD_COMPONENT, data);
            }
            else{
                // error
                selectedNewComponentIndex = -1;
                selectedNewComponent = null;
            }

        }
        else
        {
            if(selectedComponentIndex==hoveredOverHexagon){
                // deselect
                selectedComponentIndex=-1;
            }
            else{
                // select
                selectedComponentIndex = hoveredOverHexagon;
            }
            selectedComponentChanged();
        }


    }

    private void selectedComponentChanged(){
        selectedComponent = getComponentFromIndex(selectedComponentIndex);
        screen.setComponentInspected(selectedComponent!=null);
    }

    private Vector2i getComponentPositionFromIndex(int index){
        int x = index % currentGrid.width;
        int y = index / currentGrid.width;
        return new Vector2i(x,y);
    }

    private SpellComponent getComponentFromIndex(int index){
        if(!hasGrid()) return null;
        if(index < 0) return null;
        return currentGrid.getComponent(getComponentPositionFromIndex(index));
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
    public static final int sideConfigsOffset = 100;

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int bgPosX = (screen.width-screen.getBackgroundWidth())/2;
        int bgPosY = (screen.height-screen.getBackgroundHeight())/2;

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
                    bgPosX+NEW_COMPONENTS_Y-4+(selectedNewComponentIndex/NEW_COMPONENTS_WIDTH)*18,
                    176,0,24,24);
        }

        final float scaledHexWidth = hexWidth*fieldDrawScale;
        final float scaledHexHeight = hexBGTextureSize*fieldDrawScale;

        context.enableScissor(bgPosX+fieldPosX,bgPosY+fieldPosY,bgPosX+fieldPosX+fieldWidth,bgPosY+fieldPosY+fieldHeight);

        List<Vector2f> drawPositions = new ArrayList<>();

        for (int y = 0; y < currentGrid.height; y++) {
            int yskew = y%2;
            for (int x = 0; x < currentGrid.width; x++) {
                drawPositions.add(new Vector2f(
                        bgPosX+fieldPosX+(fieldDrawScale * (Math.round(fieldDrawOffsetX) + Math.round((x-0.5f+yskew/2f)*hexWidth))),
                        bgPosY+fieldPosY+(fieldDrawScale * (Math.round(fieldDrawOffsetY) + (y-0.5f)*hexHeight))
                ));
            }
        }

        // calculate selected hexagon
        int selectedHexagon = -1;
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

        }
        hoveredOverHexagon=selectedHexagon;

        // render grid
        for (int i = 0; i < drawPositions.size(); i++) {
            int x = i % currentGrid.width;
            int y = i / currentGrid.height;

            // render component
            var component = currentGrid.getComponent(new Vector2i(x,y));
            if(component!=null){
                var texture = component.getHexTexture();
                RenderSystem.setShaderColor(1,1,1,1);

                context.drawTexture(texture,
                        Math.round(drawPositions.get(i).x),
                        Math.round(drawPositions.get(i).y),
                        Math.round(scaledHexWidth),
                        Math.round(scaledHexHeight),
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
            // render empty cell
            else{
                // render new component
                if(selectedNewComponent!=null && i==selectedHexagon){
                    component = selectedNewComponent;
                    var texture = component.getHexTexture();
                    RenderSystem.setShaderColor(1,1,1,1);

                    context.drawTexture(texture,
                            Math.round(drawPositions.get(i).x),
                            Math.round(drawPositions.get(i).y),
                            Math.round(scaledHexWidth),
                            Math.round(scaledHexHeight),
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
                    if(i==selectedHexagon || i ==selectedComponentIndex) {
                        RenderSystem.setShaderColor(1,1,1,1);
                    }
                    else{
                        RenderSystem.setShaderColor(0.5f,0.5f,0.5f,1);
                    }

                    context.drawTexture(hexBGTexture,
                            Math.round(drawPositions.get(i).x),
                            Math.round(drawPositions.get(i).y),
                            Math.round(scaledHexWidth),
                            Math.round(scaledHexHeight),
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
                //context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.dir."+conf.dir),endX,endY,0xFFFFFFFF,true);
                //switch(conf.activeMode()){
                //    case Input:
                //        context.drawText(MinecraftClient.getInstance().textRenderer,
                //                Text.literal("-> ").formatted(Formatting.GRAY)
                //                .append(Text.translatable("geomancy.spellmaker.types."+conf.getSignal(component).type.toString().toLowerCase()).formatted(Formatting.DARK_AQUA))
                //                .append(Text.literal(" "+conf.varName).formatted(Formatting.GRAY)),endX,endY+10,0xFFFFFFFF,true);
                //        break;
                //    case Output:
                //        context.drawText(MinecraftClient.getInstance().textRenderer,
                //                Text.literal("<- ").formatted(Formatting.GRAY)
                //                .append(Text.translatable("geomancy.spellmaker.types."+conf.getSignal(component).type.toString().toLowerCase()).formatted(Formatting.DARK_AQUA))
                //                .append(Text.literal(" "+conf.varName).formatted(Formatting.GRAY)),endX,endY+10,0xFFFFFFFF,true);
                //        break;
                //    case Blocked:
                //        context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("x").formatted(Formatting.GRAY),endX,endY+10,0xFFFFFFFF,true);
                //        break;
                //}
            }

            // render i/o info
            var ioInfo = SpellComponentStoringItem.componentTooltip(component,true);

            for (int i = 0; i < ioInfo.size(); i++) {
                var t = ioInfo.get(i);
                context.drawText(MinecraftClient.getInstance().textRenderer, t,infoPosX+10,infoPosY+previewHeight+20+i*10,0xFFFFFFFF,true);
            }

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
}
