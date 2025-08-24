package org.oxytocina.geomancy.blocks.blockEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.ISpellSelectorBlock;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.spells.SpellBlockArgs;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.util.ManaUtil;

import java.util.List;

public class AutocasterBlockEntity extends LootableContainerBlockEntity implements ISpellSelectorBlock {
    public static final int INVENTORY_SIZE = 9;
    private DefaultedList<ItemStack> inventory;
    private final ViewerCountManager stateManager;

    protected AutocasterBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.stateManager = new ViewerCountManager() {
            protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
                syncManaContainers();
            }

            protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            }

            protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                AutocasterBlockEntity.this.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
            }

            protected boolean isPlayerViewing(PlayerEntity player) {
                if (!(player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                    return false;
                } else {
                    Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
                    return inventory == AutocasterBlockEntity.this;
                }
            }
        };
    }

    public AutocasterBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.AUTOCASTER_BLOCK_ENTITY, pos, state);
    }

    public void onOpen(PlayerEntity player) {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }

        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    public static int getPlayersLookingInChestCount(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChestBlockEntity) {
                return ((AutocasterBlockEntity)blockEntity).stateManager.getViewerCount();
            }
        }

        return 0;
    }

    public void onScheduledTick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.addSyncedBlockEvent(pos, block, 1, newViewerCount);
    }

    boolean observed(){
        return getPlayersLookingInChestCount(getWorld(),getPos())>0;
    }

    @Override
    public int size() {
        return INVENTORY_SIZE;
    }

    public void cast(){
        // fetch cast spell
        var caster = getFirstCasterItem(this);
        if(caster==null) return;
        var spells = getCastableSpellItems(this,caster);
        if(spells.isEmpty()) return;
        var spell = spells.get(getSelectedSpellIndex(this,caster));
        var grid = SpellStoringItem.readGrid(spell);
        if(grid==null) return;

        grid.run(caster,spell,null,this, SpellBlockArgs.empty(), SpellContext.SoundBehavior.Reduced);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(ManaUtil.tickStorage(world,this,pos) && observed()) syncManaContainers();
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
        syncManaContainers();
        return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
    }

    /// ISpellSelector

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

    @Override
    public ItemStack getStack(BlockEntity entity, int index) {
        return getStack(index);
    }

    @Override
    public int getStorageSize(BlockEntity entity) {
        return size();
    }

    @Override
    public void markDirty(BlockEntity entity) {
        markDirty();
    }

    public void syncManaContainers(){
        for (int i = 0; i < size(); i++) {
            ManaUtil.syncItemMana(getWorld(),getStack(i));
        }
    }

    public ItemStack tryCollect(ItemStack s) {
        for (int i = 0; i < size(); i++) {
            var onto = getStack(i);

            // onto empty stack
            if(onto.isEmpty())
            {
                setStack(i,s.copyAndEmpty());
                return s;
            }

            // trying to stack until its empty
            if(onto.isStackable() && ItemStack.canCombine(s,onto)){
                int max = onto.getMaxCount();
                int taken = Math.min(s.getCount(),max-onto.getCount());
                onto.increment(taken);
                s.decrement(taken);
                if(s.isEmpty()) return s;
            }
        }
        return s;
    }
}

