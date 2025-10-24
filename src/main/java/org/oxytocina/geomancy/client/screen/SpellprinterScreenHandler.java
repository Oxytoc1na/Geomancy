package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SpellprinterBlockEntity;
import org.oxytocina.geomancy.client.screen.slots.SpellComponentSelectionSlot;
import org.oxytocina.geomancy.client.screen.slots.SpellmakerHotbarSlot;
import org.oxytocina.geomancy.client.screen.slots.TagFilterSlot;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;

public class SpellprinterScreenHandler extends ScreenHandler {
    public static SpellprinterScreenHandler current;

    private final Inventory inventory;
    private Inventory availableComponents;
    private final PropertyDelegate propertyDelegate;
    public final SpellprinterBlockEntity blockEntity;
    public final PlayerEntity player;

    public SpellprinterScreen screen;

    public ItemStack currentOutput;
    public SpellGrid currentGrid;

    public static final int NEW_COMPONENTS_SLOT_COUNT = 14;
    public static final int NEW_COMPONENTS_X = 8;
    public static final int NEW_COMPONENTS_WIDTH = 7;
    public static final int NEW_COMPONENTS_Y = 124;
    public static final int NEW_COMPONENTS_SLOT_OFFSET = 10;

    public SpellprinterScreenHandler(int syncID, PlayerInventory inventory, PacketByteBuf buf){
        this(syncID,inventory,inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(3));
    }

    public SpellprinterScreenHandler(int syncID, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SPELLPRINTER_SCREEN_HANDLER,syncID);

        current = this;
        checkSize((Inventory)blockEntity,SpellprinterBlockEntity.SLOT_COUNT);
        this.player = playerInventory.player;;
        this.inventory = (Inventory) blockEntity;
        playerInventory.onOpen(this.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = (SpellprinterBlockEntity) blockEntity;

        availableComponents = ImplementedInventory.ofSize(NEW_COMPONENTS_SLOT_COUNT);

        // 0
        this.addSlot(new TagFilterSlot(inventory,SpellprinterBlockEntity.OUTPUT_SLOT,152,142, ModItemTags.SPELL_STORING,1));

        // 1-9
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }

    public void outputItemChanged(){
        rebuild();
    }


    private ItemStack prevOutput = ItemStack.EMPTY;
    public void rebuild(){
        var newOutput = getOutput();

        if(prevOutput.isEmpty() && !newOutput.isEmpty())
            Toolbox.playUISound(ModSoundEvents.SPELLMAKER_INSERT_CRADLE);
        else if(!prevOutput.isEmpty() && newOutput.isEmpty())
            Toolbox.playUISound(ModSoundEvents.SPELLMAKER_REMOVE_CRADLE);

        prevOutput = newOutput.copy();
        currentGrid = SpellStoringItem.getOrCreateGrid(newOutput);
    }

    public ItemStack getOutput(){
        return blockEntity!=null?blockEntity.getOutput():null;
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
            } else{
                var toStack = getOutput();
                if(toStack.isEmpty())
                {
                    this.slots.get(0).setStack(res.copyWithCount(1));
                    this.slots.get(0).markDirty();
                    res.decrement(1);
                    fromSlot.setStack(res);
                    fromSlot.markDirty();
                    return res;
                }
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

    public void tick(){

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

    public boolean hasGrid(){return currentGrid!=null;}

    public static final int fieldPosX = 8;
    public static final int fieldPosY = 8;
    public static final int fieldWidth = 160;
    public static final int fieldHeight = 112;
    public static final Identifier spellprinterGuiTexture = Geomancy.locate("textures/gui/spellprinter_block_gui.png");
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

        // render grid info
        if(hasGrid()){
            final int infoPosX = bgPosX+SpellprinterScreen.bgWidth+10;
            final int infoPosY = bgPosY+10;
            RenderSystem.setShaderColor(1,1,1,1);
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.grid.name"),infoPosX,infoPosY,0xFFFFFFFF,true);

            // appearance
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("geomancy.spellmaker.grid.appearance"),infoPosX+25,infoPosY-10+appearanceSlotYOffset+(18-10)/2,0xFFFFFFFF,true);

        }

        RenderSystem.setShaderColor(1,1,1,1);

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
}
