package org.oxytocina.geomancy.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.screen.slots.StorageItemContainedSlot;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.IStorageItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModItemTags;

import java.util.ArrayList;
import java.util.List;

public class StorageItemScreenHandler extends ScreenHandler {
    public static StorageItemScreenHandler current;

    //private final Inventory inventory;
    private final Inventory containedItems;
    public List<StorageItemContainedSlot> containedItemSlots;
    private final PropertyDelegate propertyDelegate;
    public final ItemStack parent;
    public final ItemStack initialParent;
    public final int parentSlot;
    public final PlayerEntity player;
    public final TagKey<Item> storableKey;

    private boolean dirty = false;
    private boolean initialized = false;

    public static final int STORAGE_DISPLAY_SLOTS_WIDTH = 9;
    public static final int STORAGE_DISPLAY_SLOTS = STORAGE_DISPLAY_SLOTS_WIDTH*3;
    public static final int STORAGE_DISPLAY_X = 8;
    public static final int STORAGE_DISPLAY_Y = 18;

    public static final int INVENTORY_DISPLAY_X = 8;
    public static final int INVENTORY_DISPLAY_Y = 98;

    public static final int HOTBAR_DISPLAY_Y = 156;

    public StorageItemScreen screen;

    public ItemStack currentOutput;

    public StorageItemScreenHandler(int syncID, PlayerInventory inventory, PacketByteBuf buf){
        this(syncID,inventory,inventory.getStack(buf.readInt()), TagKey.of(RegistryKeys.ITEM,buf.readIdentifier()),
                new ArrayPropertyDelegate(3));
    }

    public StorageItemScreenHandler(int syncID, PlayerInventory playerInventory, ItemStack parent, TagKey<Item> storableKey, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.STORAGE_ITEM_SCREEN_HANDLER,syncID);

        current = this;
        this.player = playerInventory.player;
        playerInventory.onOpen(this.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.parent = parent;
        this.initialParent=parent.copy();
        this.parentSlot = playerInventory.getSlotWithStack(parent);
        this.storableKey=storableKey;
        containedItems = ((IStorageItem)parent.getItem()).getInventory(parent);

        checkSize(getInventory(), ((IStorageItem)parent.getItem()).getStorageSize(parent));

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);

        addProperties(arrayPropertyDelegate);

        outputItemChanged();
        currentOutput = getOutput();
    }

    public Inventory getInventory(){
        return containedItems;
    }

    public void outputItemChanged(){
        rebuildSlots();
    }

    public void rebuildSlots(){
        if(containedItemSlots ==null){
            containedItemSlots = addInventory(containedItems,0,STORAGE_DISPLAY_SLOTS,STORAGE_DISPLAY_SLOTS_WIDTH, STORAGE_DISPLAY_X, STORAGE_DISPLAY_Y);
        }
    }

    @Override
    public void setCursorStack(ItemStack stack) {
        super.setCursorStack(stack);
    }

    public ItemStack getOutput(){
        return parent;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(invSlot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (invSlot < 9*4) {
                if (!this.insertItem(itemStack2, 9*4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 9*4, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.getInventory().canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, INVENTORY_DISPLAY_X + l * 18, INVENTORY_DISPLAY_Y + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, INVENTORY_DISPLAY_X + i * 18, HOTBAR_DISPLAY_Y));
        }
    }

    private List<StorageItemContainedSlot> addInventory(Inventory inventory, int slotIndexOffset, int count, int width, int x, int y) {
        List<StorageItemContainedSlot> res =new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            if(i>= inventory.size()) return res;
            res.add((StorageItemContainedSlot)this.addSlot(new StorageItemContainedSlot(inventory, slotIndexOffset+i, x + (i%width) * 18, y + (i/width)*18, this, storableKey)));
        }
        return res;
    }



    public void tick(){

        // check if somehow, the caster item got removed or changed
        // in that case, panic and close the screen
        if(parent.getItem() != initialParent.getItem()
        || parent.getCount() != initialParent.getCount()
        )
        {
            screen.close();
        }

        if(dirty){
            writeInventory();
            dirty=false;
        }

        initialized = true;

    }

    @Override
    public void onClosed(PlayerEntity player) {

        if(dirty){
            writeInventory();
            dirty=false;
        }

        super.onClosed(player);
    }

    public static final Identifier spellstorerGuiTexture = Geomancy.locate("textures/gui/spellstorer_block_gui.png");


    public void markDirty(){
        if(!initialized) return;
        dirty=true;
    }

    public void writeInventory(){
        if(MinecraftClient.getInstance()==null) return; // client only method

        var output = getOutput();

        if(output == null || output.isEmpty() || (!(output.getItem() instanceof IStorageItem storage))) return;

        for (int i = 0; i < containedItems.size(); i++) {
            if(i>=storage.getStorageSize(output)) break;
            storage.setStack(output,i, containedItems.getStack(i));
        }

        // send packet to server
        PacketByteBuf data = PacketByteBufs.create();

        var nbt = output.getNbt();
        data.writeInt(parentSlot);
        data.writeNbt(nbt);
        ClientPlayNetworking.send(ModMessages.STORAGE_ITEM_TRY_UPDATE, data);
    }


}
