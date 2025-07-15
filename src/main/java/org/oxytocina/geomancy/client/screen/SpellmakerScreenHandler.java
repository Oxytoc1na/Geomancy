package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.event.ScrollTracker;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

public class SpellmakerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final SpellmakerBlockEntity blockEntity;

    public double fieldDrawOffsetX = 0;
    public double fieldDrawOffsetY = 0;
    public float fieldDrawScale = 0.5f;

    public ItemStack currentOutput;
    public SpellGrid currentGrid;

    public SpellmakerScreenHandler(int syncID, PlayerInventory inventory, PacketByteBuf buf){
        this(syncID,inventory,inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(3));
    }

    public SpellmakerScreenHandler(int syncID, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SPELLMAKER_SCREEN_HANDLER,syncID);
        checkSize((Inventory)blockEntity,SpellmakerBlockEntity.SLOT_COUNT);
        this.inventory = (Inventory) blockEntity;
        playerInventory.onOpen(playerInventory.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = (SpellmakerBlockEntity) blockEntity;

        Inventory availableComponents = this.blockEntity.getComponentItems(playerInventory);

        addInventory(availableComponents,0,14,7,8,124);

        this.addSlot(new Slot(inventory,SpellmakerBlockEntity.OUTPUT_SLOT,152,142));

        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }

    public void outputItemChanged(){
        rebuild();
    }

    public void rebuild(){
        currentGrid = SpellStoringItem.getOrCreateGrid(getOutput());
    }

    public ItemStack getOutput(){
        return this.inventory!=null?this.inventory.getStack(SpellmakerBlockEntity.OUTPUT_SLOT):ItemStack.EMPTY;
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
            this.addSlot(new PreviewSlot(inventory, slotIndexOffset+i, x + (i%width) * 18, y + (i/width)*18));
        }
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        fieldDrawOffsetX += deltaX/fieldDrawScale;
        fieldDrawOffsetY += deltaY/fieldDrawScale;
    }

    public void mouseScrolled(double mouseX, double mouseY, double amount) {
        if(amount!=0){
            // scrolling
            fieldDrawScale *= 1+0.2f*Toolbox.sign((float)amount);
            fieldDrawScale = Toolbox.clampF(fieldDrawScale,0.1f,2f);
        }
    }


    public void render(SpellmakerScreen screen, DrawContext context, int mouseX, int mouseY, float delta) {
        int bgPosX = (screen.width-SpellmakerScreen.bgWidth)/2;
        int bgPosY = (screen.height-SpellmakerScreen.bgHeight)/2;

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



        final Identifier hexBGTexture = Geomancy.locate("textures/gui/spellmaker_hex_bg.png");
        final int hexBGTextureSize = 32;
        final int hexWidth = 30;
        final int hexHeight = 26;
        final int fieldPosX = 8;
        final int fieldPosY = 8;
        final int fieldWidth = 160;
        final int fieldHeight = 112;
        final int hexBgUvX = 0;
        final int hexBgUvY = 0;

        context.enableScissor(bgPosX+fieldPosX,bgPosY+fieldPosY,bgPosX+fieldPosX+fieldWidth,bgPosY+fieldPosY+fieldHeight);

        for (int x = 0; x < currentGrid.width; x++) {
            for (int y = 0; y < currentGrid.height; y++) {
                int yskew = y%2;

                int drawPosX = bgPosX+fieldPosX+Math.round(fieldDrawScale * (Math.round(fieldDrawOffsetX) + Math.round((x-0.5f+yskew/2f)*hexWidth)));
                int drawPosY = bgPosY+fieldPosY+Math.round(fieldDrawScale * (Math.round(fieldDrawOffsetY) + (y-0.5f)*hexHeight));

                //Identifier texture, int x, int y, int width, int height,
                //float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight

                context.drawTexture(hexBGTexture,
                        drawPosX,
                        drawPosY,
                        Math.round(hexWidth*fieldDrawScale),
                        Math.round(hexBGTextureSize*fieldDrawScale),
                        (hexBGTextureSize-hexWidth)/2f,
                        0,
                        hexWidth,
                        hexBGTextureSize,
                        hexBGTextureSize,
                        hexBGTextureSize
                );
            }
        }

        context.disableScissor();
    }
}
