package org.oxytocina.geomancy.client.screen;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.oxytocina.geomancy.blocks.blockEntities.SpellprinterBlockEntity;
import org.oxytocina.geomancy.client.screen.slots.TagFilterSlot;
import org.oxytocina.geomancy.registries.ModItemTags;

public class SpellprinterScreenHandler extends ScreenHandler {
    public static SpellprinterScreenHandler current;

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final SpellprinterBlockEntity blockEntity;
    public final PlayerEntity player;

    public SpellprinterScreen screen;

    public static final int NEW_COMPONENTS_SLOT_COUNT = 14;
    public static final int NEW_COMPONENTS_SLOT_OFFSET = 10;

    public SpellprinterScreenHandler(int syncID, PlayerInventory inventory, PacketByteBuf buf){
        this(syncID,inventory,inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(3));
    }

    public static final int OUTPUT_SLOT_X = 8;
    public static final int OUTPUT_SLOT_Y = 142;

    public SpellprinterScreenHandler(int syncID, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SPELLPRINTER_SCREEN_HANDLER,syncID);

        current = this;
        checkSize((Inventory)blockEntity,SpellprinterBlockEntity.SLOT_COUNT);
        this.player = playerInventory.player;
        this.inventory = (Inventory) blockEntity;
        playerInventory.onOpen(this.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = (SpellprinterBlockEntity) blockEntity;

        // 0
        this.addSlot(new TagFilterSlot(inventory,SpellprinterBlockEntity.OUTPUT_SLOT,OUTPUT_SLOT_X,OUTPUT_SLOT_Y, ModItemTags.SPELL_STORING,1));

        // 1-9
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
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
