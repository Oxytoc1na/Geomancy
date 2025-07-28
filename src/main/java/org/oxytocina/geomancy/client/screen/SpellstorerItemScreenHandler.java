package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SpellstorerBlockEntity;
import org.oxytocina.geomancy.client.screen.slots.SpellstorerItemSpellSlot;
import org.oxytocina.geomancy.client.screen.slots.SpellstorerSpellSlot;
import org.oxytocina.geomancy.client.screen.slots.TagFilterSlot;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModItemTags;

import java.util.ArrayList;
import java.util.List;

public class SpellstorerItemScreenHandler extends ScreenHandler {
    public static SpellstorerItemScreenHandler current;

    //private final Inventory inventory;
    private final Inventory installedSpells;
    public List<SpellstorerItemSpellSlot> installedSpellsSlots;
    private final PropertyDelegate propertyDelegate;
    public final ItemStack parent;
    public final ItemStack initialParent;
    public final int parentSlot;
    public final PlayerEntity player;

    private boolean dirty = false;
    private boolean initialized = false;

    public static final int STORAGE_DISPLAY_SLOTS_WIDTH = 9;
    public static final int STORAGE_DISPLAY_SLOTS = STORAGE_DISPLAY_SLOTS_WIDTH*3;
    public static final int STORAGE_DISPLAY_X = 8;
    public static final int STORAGE_DISPLAY_Y = 18;

    public static final int INVENTORY_DISPLAY_X = 8;
    public static final int INVENTORY_DISPLAY_Y = 98;

    public static final int HOTBAR_DISPLAY_Y = 156;

    public SpellstorerItemScreen screen;

    public ItemStack currentOutput;

    public SpellstorerItemScreenHandler(int syncID, PlayerInventory inventory, PacketByteBuf buf){
        this(syncID,inventory,inventory.getStack(buf.readInt()),
                new ArrayPropertyDelegate(3));
    }

    public SpellstorerItemScreenHandler(int syncID, PlayerInventory playerInventory, ItemStack parent, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SPELLSTORER_ITEM_SCREEN_HANDLER,syncID);

        current = this;
        this.player = playerInventory.player;;
        playerInventory.onOpen(this.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.parent = parent;
        this.initialParent=parent.copy();
        this.parentSlot = playerInventory.getSlotWithStack(parent);
        checkSize(getInventory(), SpellstorerBlockEntity.SLOT_COUNT);

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);

        addProperties(arrayPropertyDelegate);

        installedSpells = ImplementedInventory.ofSize(STORAGE_DISPLAY_SLOTS);
        outputItemChanged();
        currentOutput = getOutput();
    }

    public Inventory getInventory(){
        return SoulCastingItem.getInventory(parent);
    }

    public void outputItemChanged(){
        rebuild();
        rebuildSlots();
    }

    public void rebuild(){
        updateInstalledSpells();
    }

    public void rebuildSlots(){
        if(installedSpellsSlots==null){
            installedSpellsSlots = addInventory(installedSpells,0,STORAGE_DISPLAY_SLOTS,STORAGE_DISPLAY_SLOTS_WIDTH, STORAGE_DISPLAY_X, STORAGE_DISPLAY_Y);
        }

        int i = 0;
        for(var s : installedSpellsSlots){
            s.enabled = hasOutput();
            s.markDirty();
        }
    }

    public boolean hasOutput(){
        return getOutput()!=null&&!getOutput().isEmpty();
    }

    public void updateInstalledSpells(){
        var from = getStoredSpellItems();
        for (int i = 0; i < from.size(); i++) {
            if(i>=from.size()) break;
            installedSpells.setStack(i,from.getStack(i));
        }
    }

    public Inventory getStoredSpellItems(){

        ItemStack caster = parent;
        if(caster.getItem() instanceof SoulCastingItem castingItem){
            DefaultedList<ItemStack> res = DefaultedList.ofSize(SpellstorerScreenHandler.STORAGE_DISPLAY_SLOTS,ItemStack.EMPTY);
            for (int i = 0; i < castingItem.getSize(caster); i++) {
                res.set(i,castingItem.getStack(caster,i));
            }
            return ImplementedInventory.of(res);
        }

        return ImplementedInventory.ofSize(SpellstorerScreenHandler.STORAGE_DISPLAY_SLOTS);
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
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            // transfer from main inventory
            if (invSlot < 9*4) {
                // insert into output slot
                if (!this.insertItem(originalStack, 9*4, 9*4+1, true)) {

                    var newOutput = getOutput();
                    if(currentOutput!=newOutput){
                        outputItemChanged();
                    }
                    currentOutput = newOutput;

                    return ItemStack.EMPTY;
                }

                // TODO: fix inserting into spell slots
            }
            // transfer from slot to main inventory
            else if (!this.insertItem(originalStack, 0, 9*4-1, false)) {

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

    private List<SpellstorerItemSpellSlot> addInventory(Inventory inventory, int slotIndexOffset, int count, int width, int x, int y) {
        List<SpellstorerItemSpellSlot> res =new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            if(i>= inventory.size()) return res;
            res.add((SpellstorerItemSpellSlot)this.addSlot(new SpellstorerItemSpellSlot(inventory, slotIndexOffset+i, x + (i%width) * 18, y + (i/width)*18,this)));
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

    public static final Identifier spellstorerGuiTexture = Geomancy.locate("textures/gui/spellstorer_block_gui.png");


    public void markDirty(){
        if(!initialized) return;
        dirty=true;
    }

    public void writeInventory(){
        var output = getOutput();

        if(output == null || output.isEmpty() || (!(output.getItem() instanceof SoulCastingItem caster))) return;

        for (int i = 0; i < installedSpells.size(); i++) {
            if(i>=caster.getSize(output)) break;
            caster.setStack(output,i,installedSpells.getStack(i));
        }

        // send packet to server
        PacketByteBuf data = PacketByteBufs.create();

        var nbt = output.getNbt();
        data.writeInt(parentSlot);
        data.writeNbt(nbt);
        ClientPlayNetworking.send(ModMessages.SPELLSTORER_ITEM_TRY_UPDATE_CASTER, data);
    }


}
