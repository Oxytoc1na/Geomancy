package org.oxytocina.geomancy.blocks.blockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.oxytocina.geomancy.items.ISpellSelectorItem;

import java.util.List;

public class AutocasterBlockEntity extends LootableContainerBlockEntity implements ISpellSelectorItem {
    public static final int INVENTORY_SIZE = 9;
    private DefaultedList<ItemStack> inventory;

    protected AutocasterBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    }

    public AutocasterBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.AUTOCASTER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public int size() {
        return INVENTORY_SIZE;
    }

    public void cast(){
        // fetch castable spells
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("geomancy.autocaster");
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }

    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }

    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
    }

    /// ISpellSelector

    @Override
    public ItemStack getStack(ItemStack storage, int index) {
        return null;
    }

    @Override
    public int getStorageSize(ItemStack stack) {
        return 0;
    }

    @Override
    public void markDirty(ItemStack casterItem) {

    }

    public int getSlotWithStack(ItemStack casterItem) {
        for (int i = 0; i < size(); i++) {
            var contender = getStack(i);
            if(contender.equals(casterItem)) return i;
        }
        return -1;
    }

    public Direction getDirection() {
        return getCachedState().get(AutocasterBlock.FACING);
    }
}

