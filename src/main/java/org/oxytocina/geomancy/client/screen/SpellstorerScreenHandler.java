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
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SpellstorerBlockEntity;
import org.oxytocina.geomancy.client.screen.slots.SpellstorerSpellSlot;
import org.oxytocina.geomancy.client.screen.slots.TagFilterSlot;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModItemTags;

import java.util.ArrayList;
import java.util.List;

public class SpellstorerScreenHandler extends ScreenHandler {
    public static SpellstorerScreenHandler current;

    private final Inventory inventory;
    private final Inventory installedSpells;
    public List<SpellstorerSpellSlot> installedSpellsSlots;
    private final PropertyDelegate propertyDelegate;
    public final SpellstorerBlockEntity blockEntity;
    public final PlayerEntity player;

    public final Slot outputSlot;

    private boolean dirty = false;
    private boolean initialized = false;

    public static final int STORAGE_DISPLAY_SLOTS_WIDTH = 9;
    public static final int STORAGE_DISPLAY_SLOTS = STORAGE_DISPLAY_SLOTS_WIDTH*3;
    public static final int STORAGE_DISPLAY_X = 8;
    public static final int STORAGE_DISPLAY_Y = 18;

    public static final int OUTPUT_DISPLAY_X = 80;
    public static final int OUTPUT_DISPLAY_Y = 76;

    public static final int INVENTORY_DISPLAY_X = 8;
    public static final int INVENTORY_DISPLAY_Y = 98;

    public static final int HOTBAR_DISPLAY_Y = 156;

    public SpellstorerScreen screen;

    public ItemStack currentOutput;

    public SpellstorerScreenHandler(int syncID, PlayerInventory inventory, PacketByteBuf buf){
        this(syncID,inventory,inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(3));
    }

    public SpellstorerScreenHandler(int syncID, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SPELLSTORER_SCREEN_HANDLER,syncID);

        current = this;
        checkSize((Inventory)blockEntity, SpellstorerBlockEntity.SLOT_COUNT);
        this.player = playerInventory.player;;
        this.inventory = (Inventory) blockEntity;
        playerInventory.onOpen(this.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = (SpellstorerBlockEntity) blockEntity;

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);

        outputSlot = this.addSlot(new TagFilterSlot(inventory,SpellstorerBlockEntity.OUTPUT_SLOT,OUTPUT_DISPLAY_X,OUTPUT_DISPLAY_Y, ModItemTags.CASTING_ITEM));

        addProperties(arrayPropertyDelegate);

        installedSpells = ImplementedInventory.ofSize(STORAGE_DISPLAY_SLOTS);
        outputItemChanged();
        currentOutput = getOutput();
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
        var from = this.blockEntity.getStoredSpellItems();
        for (int i = 0; i < from.size(); i++) {
            if(i>=from.size()) break;
            installedSpells.setStack(i,from.getStack(i));
        }
    }

    @Override
    public void setCursorStack(ItemStack stack) {
        super.setCursorStack(stack);
    }

    public ItemStack getOutput(){
        return blockEntity!=null?blockEntity.getSlottedCasterItem():null;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if(slots.indexOf(outputSlot) == slotIndex){
            var newOutput = getOutput();
            if(currentOutput!=newOutput){
                outputItemChanged();
            }
            currentOutput = newOutput;
        }
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
        return this.inventory.canPlayerUse(player);
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

    private List<SpellstorerSpellSlot> addInventory(Inventory inventory, int slotIndexOffset, int count, int width, int x, int y) {
        List<SpellstorerSpellSlot> res =new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            if(i>= inventory.size()) return res;
            res.add((SpellstorerSpellSlot)this.addSlot(new SpellstorerSpellSlot(inventory, slotIndexOffset+i, x + (i%width) * 18, y + (i/width)*18,this)));
        }
        return res;
    }



    public void tick(){


        if(dirty){
            writeInventory();
            dirty=false;
        }

        initialized = true;

    }

    public void mouseClicked(double mouseX, double mouseY, int button) {

    }

    public static final Identifier spellstorerGuiTexture = Geomancy.locate("textures/gui/spellstorer_block_gui.png");

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int bgPosX = (screen.width-screen.getBackgroundWidth())/2;
        int bgPosY = (screen.height-screen.getBackgroundHeight())/2;



        RenderSystem.setShaderColor(1,1,1,1);

    }

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
        data.writeBlockPos(blockEntity.getPos());
        data.writeNbt(nbt);
        ClientPlayNetworking.send(ModMessages.SPELLSTORER_TRY_UPDATE_CASTER, data);
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
