package org.oxytocina.geomancy.client.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.oxytocina.geomancy.client.screen.slots.SpellSelectSlot;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.items.tools.StorageItem;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpellSelectScreenHandler extends ScreenHandler {
    public static SpellSelectScreenHandler current;

    private final Inventory containedItems;
    public List<SpellSelectSlot> containedItemSlots;
    public HashMap<SpellSelectSlot,SpellGrid> grids;
    public final ItemStack parent;
    public final ItemStack initialParent;
    public final int parentSlot;
    public final PlayerEntity player;

    private boolean dirty = false;
    private boolean initialized = false;

    public SpellSelectScreen screen;

    public ItemStack currentOutput;

    public SpellSelectScreenHandler(int syncID, PlayerInventory inventory, PacketByteBuf buf){
        this(syncID,inventory,inventory.getStack(buf.readInt()));
    }

    public SpellSelectScreenHandler(int syncID, PlayerInventory playerInventory, ItemStack parent) {
        super(ModScreenHandlers.SPELL_SELECT_SCREEN_HANDLER,syncID);

        current = this;
        this.player = playerInventory.player;
        playerInventory.onOpen(this.player);
        this.parent = parent;
        this.initialParent=parent.copy();
        this.parentSlot = playerInventory.getSlotWithStack(parent);
        grids=new HashMap<>();
        containedItems = getDisplayedInventory(parent);

        ItemStack storageStack = new ItemStack(Items.CHEST);
        addSlot(new SpellSelectSlot(ImplementedInventory.of(DefaultedList.ofSize(1,storageStack)),0,CENTER_POS_X-16/2,CENTER_POS_Y-16/2,this));

        outputItemChanged();
        currentOutput = getOutput();
    }

    public Inventory getDisplayedInventory(ItemStack from){
        var sps = (ISpellSelectorItem) from.getItem();
        var spellItems = sps.getCastableSpellItems(from);
        DefaultedList<ItemStack> defList = DefaultedList.ofSize(spellItems.size(),ItemStack.EMPTY);
        int selectedIndex = sps.getSelectedSpellIndex(from);
        for(int i = 0; i < spellItems.size();i++){
            var stack = spellItems.get(i);
            var grid = SpellStoringItem.readGrid(stack);
            ItemStack displayStack = grid.getDisplayStack(stack);
            // make the selected spell glint
            if(i==selectedIndex) displayStack.addEnchantment(Enchantments.UNBREAKING,1);
            defList.set(i,displayStack);
        }

        return ImplementedInventory.of(defList);
    }

    public Inventory getInventory(){
        return containedItems;
    }

    public void outputItemChanged(){
        rebuildSlots();
    }

    public void rebuildSlots(){
        if(containedItemSlots ==null){
            containedItemSlots = addCircularInventory(containedItems,0);
        }
    }

    public ItemStack getOutput(){
        return parent;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.getInventory().canPlayerUse(player);
    }

    public static final int CENTER_POS_X = SpellSelectScreen.bgWidth/2;
    public static final int CENTER_POS_Y = SpellSelectScreen.bgHeight/2;
    public static final int[] DISTANCES_FROM_CENTER = new int[]{25,50,75,100};
    private List<SpellSelectSlot> addCircularInventory(Inventory inventory, int slotIndexOffset) {

        var spellItems = ((ISpellSelectorItem) getOutput().getItem()).getCastableSpellItems(getOutput());
        List<SpellSelectSlot> res =new ArrayList<>();

        int[] ringCounts = new int[]{
                Math.min(2,inventory.size()),
                Math.min(8,inventory.size()-2),
                Math.min(18,inventory.size()-2-8),
                inventory.size()-2-8-18
        };
        int j = 0;
        for(int ring = 0; ring < 4; ring++)
        {
            for (int i = 0; i <ringCounts[ring] ; ++i)
            {
                float angle = (float)(((float)i/ringCounts[ring]+0.25f)*Math.PI*2);
                Vector2f offset = Toolbox.rotateVector(new Vector2f(DISTANCES_FROM_CENTER[ring],0),angle);
                var slot = (SpellSelectSlot)this.addSlot(new SpellSelectSlot(inventory, slotIndexOffset+j,
                        CENTER_POS_X + (int)offset.x-16/2,
                        CENTER_POS_Y + (int)offset.y-16/2, this));
                res.add(slot);
                grids.put(slot,SpellStoringItem.readGrid(spellItems.get(j)));
                j++;
            }
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
            dirty=false;
        }

        initialized = true;

    }

    @Override
    public void onClosed(PlayerEntity player) {

        if(dirty){
            dirty=false;
        }

        super.onClosed(player);
    }

    public void markDirty(){
        if(!initialized) return;
        dirty=true;
    }

    public void onSlotClicked(SpellSelectSlot spellSelectSlot) {
        if(!(player instanceof ServerPlayerEntity spe)) return;

        var grid = grids.get(spellSelectSlot);
        if(grid!=null){
            // select spell and close
            ((ISpellSelectorItem) getOutput().getItem()).setSelectedSpell(getOutput(),grid.name);

            if(getOutput().getItem() instanceof SoulCastingItem sci)
                sci.displaySelectedSpell(getOutput(),player,sci.getSelectedSpellIndex(getOutput()));
            player.closeHandledScreen();
        }
        else{
            // open storage screen
            spe.openHandledScreen(new StorageScreenFactory(getOutput(),spe));
        }
    }

    public static class StorageScreenFactory implements ExtendedScreenHandlerFactory {

        public TagKey<Item> storableTag;
        public final ServerPlayerEntity player;

        public StorageScreenFactory(ItemStack parent,ServerPlayerEntity player){
            storableTag = ((SoulCastingItem) parent.getItem()).getStorableTag();
            this.player=player;
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf packetByteBuf) {
            var stack = player.getStackInHand(player.getActiveHand());
            packetByteBuf.writeInt(player.getInventory().getSlotWithStack(stack));
            packetByteBuf.writeIdentifier(storableTag.id());
        }

        @Override
        public Text getDisplayName() {
            return Text.empty();
        }

        @Override
        public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
            var stack = player.getStackInHand(player.getActiveHand());
            if(!(stack.getItem() instanceof StorageItem sci)) return null;
            return new StorageItemScreenHandler(syncId,playerInventory,stack,storableTag,new PropertyDelegate() {
                @Override
                public int get(int index) {
                    return switch(index) {
                        //case 0 -> SpellmakerBlockEntity.this.progress;
                        //case 1 -> SpellmakerBlockEntity.this.maxProgress;
                        //case 2 -> SpellmakerBlockEntity.this.currentRecipe!=null? SpellmakerBlockEntity.this.currentRecipe.getDifficulty(SpellmakerBlockEntity.this.inputInventory(), SpellmakerBlockEntity.this.getLastHammerStack(), SpellmakerBlockEntity.this.getLastHammerer()):-1;
                        default -> 0;
                    };
                }

                @Override
                public void set(int index, int value) {
                    switch(index) {
                        //case 0 -> SpellmakerBlockEntity.this.progress = value;
                        //case 1 -> SpellmakerBlockEntity.this.maxProgress = value;
                    }
                }

                @Override
                public int size() {
                    return 3;
                }
            });
        }
    }
}
