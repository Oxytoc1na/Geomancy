package org.oxytocina.geomancy.items.tools;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.client.screen.StorageItemScreenHandler;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.*;

import java.util.ArrayList;
import java.util.List;

public class StorageItem extends Item implements IStorageItem, ExtendedScreenHandlerFactory {

    public int storageSize = StorageItemScreenHandler.STORAGE_DISPLAY_SLOTS;
    public TagKey<Item> storableTag;
    public StorageItem(Settings settings, int storageSize, TagKey<Item> storableTag) {
        super(settings);
        this.storageSize = storageSize;
        this.storableTag=storableTag;

        // TODO
        this.storageSize = StorageItemScreenHandler.STORAGE_DISPLAY_SLOTS;
    }

    @Override
    public TagKey<Item> getStorableTag() {
        return storableTag;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if(user instanceof ServerPlayerEntity sp){
                var stack = user.getStackInHand(hand);
                sp.openHandledScreen((StorageItem) stack.getItem());
            }
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        var containedStacks = getAllItems(stack);
        for (int i = 0; i < containedStacks.size(); i++) {
            var containedStack = containedStacks.get(i);
            tooltip.add(Text.literal("").append(containedStack.getName()).formatted(Formatting.GRAY));
        }
    }

    public ArrayList<ItemStack> getAllItems(ItemStack stack){
        if(!(stack.getItem() instanceof StorageItem)) return null;
        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getSize(stack); i++) {
            var item = getStack(stack,i);
            if(item==null||item.isEmpty()) continue;
            res.add(item);
        }
        return res;
    }

    @Override
    public Text getDisplayName() {
        return Text.empty();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        var stack = player.getStackInHand(player.getActiveHand());
        if(!(stack.getItem() instanceof StorageItem sci)) return null;
        return new StorageItemScreenHandler(syncId,playerInventory,stack,getStorableTag(),new PropertyDelegate() {
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

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        var stack = serverPlayerEntity.getStackInHand(serverPlayerEntity.getActiveHand());
        packetByteBuf.writeInt(serverPlayerEntity.getInventory().getSlotWithStack(stack));
        packetByteBuf.writeIdentifier(getStorableTag().id());
    }

    @Override
    public DefaultedList<ItemStack> readInventoryFromNbt(ItemStack stack) {
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(storageSize,ItemStack.EMPTY);
        Inventories.readNbt(stack.getOrCreateNbt(),stacks);
        return stacks;
    }

    @Override
    public void saveInventoryToNbt(ItemStack stack) {
        stack.getOrCreateNbt().remove("Items");
        Inventories.writeNbt(stack.getOrCreateNbt(),getItems(stack));
        clearCache(stack);
    }

    @Override
    public int getStorageSize(ItemStack stack) {
        return getSize(stack);
    }

    public void clearCache(ItemStack stack){
        inventories.remove(stack);
        actualInventories.remove(stack);
    }

    @Override
    public void setInventory(ItemStack stack, NbtCompound nbt){
        clearCache(stack);
        stack.setSubNbt("Items",nbt.getList("Items",NbtElement.COMPOUND_TYPE));
        clearCache(stack);
    }

    // Inventory

    public int getSize(ItemStack stack){
        return getItems(stack).size();
    }

    @Override
    public DefaultedList<ItemStack> getItems(ItemStack stack) {
        if(inventories.containsKey(stack)) return inventories.get(stack);

        // generate and cache inventory
        DefaultedList<ItemStack> inv = readInventoryFromNbt(stack);
        inventories.put(stack,inv);

        return inv;
    }

    @Override
    public Inventory getInventory(ItemStack stack) {
        return getInventoryStatic(stack);
    }

    protected static Inventory getInventoryStatic(ItemStack stack){
        if(actualInventories.containsKey(stack)) return actualInventories.get(stack);

        // generate and cache inventory
        Inventory inv = ImplementedInventory.of(((IStorageItem) stack.getItem()).getItems(stack));
        actualInventories.put(stack,inv);

        return inv;
    }


    /**
     * Returns the inventory size.
     *
     * <p>The default implementation returns the size of {@link #getItems(ItemStack)}.
     *
     * @return the inventory size
     */
    public int size(ItemStack key) {
        return getItems(key).size();
    }

    /**
     * @return true if this inventory has only empty stacks, false otherwise
     */
    public boolean isEmpty(ItemStack key) {
        for (int i = 0; i < size(key); i++) {
            ItemStack stack = getStack(key,i);
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the item in the slot.
     *
     * @param slot the slot
     * @return the item in the slot
     */
    public ItemStack getStack(ItemStack key,int slot) {
        return getItems(key).get(slot);
    }

    /**
     * Takes a stack of the size from the slot.
     *
     * <p>(default implementation) If there are less items in the slot than what are requested,
     * takes all items in that slot.
     *
     * @param slot the slot
     * @param count the item count
     * @return a stack
     */
    public ItemStack removeStack(ItemStack key,int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(key), slot, count);
        if (!result.isEmpty()) {
            markDirty(key);
        }

        return result;
    }

    /**
     * Removes the current stack in the {@code slot} and returns it.
     *
     * <p>The default implementation uses {@link Inventories#removeStack(List, int)}
     *
     * @param slot the slot
     * @return the removed stack
     */
    public ItemStack removeStack(ItemStack key,int slot) {
        return Inventories.removeStack(getItems(key), slot);
    }

    /**
     * Replaces the current stack in the {@code slot} with the provided stack.
     *
     * <p>If the stack is too big for this inventory ({@link Inventory#getMaxCountPerStack()}),
     * it gets resized to this inventory's maximum amount.
     *
     * @param slot the slot
     * @param stack the stack
     */
    @Override
    public void setStack(ItemStack key,int slot, ItemStack stack) {
        getItems(key).set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack(key)) {
            stack.setCount(getMaxCountPerStack(key));
        }
        markDirty(key);
    }

    protected int getMaxCountPerStack(ItemStack key) {
        return 64;
    }

    /**
     * Clears {@linkplain #getItems(ItemStack) the item list}}.
     */
    public void clear(ItemStack key) {
        getItems(key).clear();
    }

    public void markDirty(ItemStack key) {
        // Override if you want behavior.
        saveInventoryToNbt(key);
    }



}
