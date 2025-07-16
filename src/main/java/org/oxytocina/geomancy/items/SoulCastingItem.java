package org.oxytocina.geomancy.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.joml.Vector2i;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;

import java.util.HashMap;
import java.util.List;

public class SoulCastingItem extends Item implements IManaStoringItem, ICastingItem {

    public static final HashMap<ItemStack,DefaultedList<ItemStack>> inventories = new HashMap<>();

    public int spellStorageSize = 1;

    public SoulCastingItem(Settings settings, int spellStorageSize) {
        super(settings);
        this.spellStorageSize=spellStorageSize;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {

            cast(user.getStackInHand(hand),user);

            return TypedActionResult.success(user.getStackInHand(hand));
        } else {

        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public void cast(ItemStack key, LivingEntity user){
        int index = getSelectedSpellIndex(key);
        ItemStack spellContainer = getStack(key,index);

        // DEBUG
        // TODO: remove
        if(spellContainer.isEmpty()){
            spellContainer = new ItemStack(ModItems.SPELLSTORAGE_SMALL);
            SpellStoringItem.getOrCreateGrid(spellContainer);
            insertSpellStorage(key,spellContainer);
        }

        if(!(spellContainer.getItem() instanceof SpellStoringItem storer)) return;

        storer.cast(key,spellContainer,user);
    }


    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return 0;
    }

    @Override
    public DefaultedList<ItemStack> readInventoryFromNbt(ItemStack stack) {
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(spellStorageSize,ItemStack.EMPTY);
        Inventories.readNbt(stack.getOrCreateNbt(),stacks);
        return stacks;
    }

    @Override
    public void saveInventoryToNbt(ItemStack stack) {
        Inventories.writeNbt(stack.getOrCreateNbt(),getItems(stack));
    }

    public int getSelectedSpellIndex(ItemStack stack){
        if(!stack.getOrCreateNbt().contains("selected", NbtElement.INT_TYPE)) return 0;
        return stack.getNbt().getInt("selected");
    }

    public void setSelectedSpellIndex(ItemStack stack,int index){
        stack.getOrCreateNbt().putInt("selected",index);
    }

    // Inventory

    @Override
    public DefaultedList<ItemStack> getItems(ItemStack stack) {
        if(inventories.containsKey(stack)) return inventories.get(stack);

        // generate and cache inventory
        DefaultedList<ItemStack> inv = readInventoryFromNbt(stack);
        inventories.put(stack,inv);

        return inv;
    }

    public boolean insertSpellStorage(ItemStack key, ItemStack storage) {
        // TODO
        setStack(key,0,storage);
        return true;
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
    public void setStack(ItemStack key,int slot, ItemStack stack) {
        getItems(key).set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack(key)) {
            stack.setCount(getMaxCountPerStack(key));
        }
        markDirty(key);
    }

    private int getMaxCountPerStack(ItemStack key) {
        return 1;
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
