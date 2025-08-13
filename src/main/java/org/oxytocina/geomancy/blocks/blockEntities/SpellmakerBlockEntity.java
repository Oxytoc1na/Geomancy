package org.oxytocina.geomancy.blocks.blockEntities;

import com.mojang.datafixers.util.Function4;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.screen.SpellmakerScreenHandler;
import org.oxytocina.geomancy.inventories.AutoCraftingInventory;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.spells.SpellBlock;

import java.util.HashMap;

public class SpellmakerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SLOT_COUNT,ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private boolean initialized = false;

    public static final int SLOT_COUNT = 10;
    public static final int OUTPUT_SLOT = 9;

    public SpellmakerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPELLMAKER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
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
        };

        markDirty();

        //recipeChanged(state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt,inventory);
        //nbt.putInt("Progress",progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        clear();
        Inventories.readNbt(nbt,inventory);
        //progress = nbt.getInt("Progress");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container."+Geomancy.MOD_ID+".spellmaker_block");
    }

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return getHandler.apply(syncId, playerInventory, this, this.propertyDelegate);
    }
    public static Function4<Integer, PlayerInventory, SpellmakerBlockEntity, PropertyDelegate,ScreenHandler> getHandler = (a, b, c, d) -> null;
    public static void SetScreenHandler(Function4<Integer, PlayerInventory, SpellmakerBlockEntity, PropertyDelegate,ScreenHandler> f){getHandler=f;}

    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(!initialized) initialize(world,pos,state);
    }

    private void initialize(World world, BlockPos pos, BlockState state){
        initialized=true;
    }

    public Inventory getComponentItems(Inventory inventory){
        var comps = getComponentAmountsIn(inventory);
        DefaultedList<ItemStack> stacksComposed = DefaultedList.ofSize(SpellmakerScreenHandler.NEW_COMPONENTS_SLOT_COUNT,ItemStack.EMPTY);
        int i = 0;
        for(var block : comps.keySet())
        {
            if(i>=stacksComposed.size()) break;
            var stack = block.getItemStack();
            stack.setCount(comps.get(block));
            stacksComposed.set(i++, stack);
        }
        return ImplementedInventory.of(stacksComposed);
    }

    public static HashMap<SpellBlock, Integer> getComponentAmountsIn(Inventory inv){
        HashMap<SpellBlock, Integer> res = new HashMap<>();

        for (int i = 0; i < inv.size(); i++) {
            ItemStack contender = inv.getStack(i);
            if(contender.isEmpty()) continue;

            // add component
            if(contender.getItem() instanceof SpellComponentStoringItem)
            {
                var component = SpellComponentStoringItem.readComponent(contender);
                if(component==null) continue;
                var key = component.function;

                if(res.containsKey(key)) res.put(key,res.get(key)+contender.getCount());
                else res.put(key,contender.getCount());
            }

            // recursively add components
            if(contender.getItem() == ModItems.COMPONENT_POUCH){
                var inv2 = ModItems.COMPONENT_POUCH.getInventory(contender);
                var recursiveRes = getComponentAmountsIn(inv2);
                for(var key : recursiveRes.keySet()){
                    if(res.containsKey(key)) res.put(key,res.get(key)+recursiveRes.get(key));
                    else res.put(key,recursiveRes.get(key));
                }
            }
        }

        return res;
    }

    public static int removeComponentFrom(SpellBlock func, int count, Inventory inv){
        if(count<=0) return count;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack contender = inv.getStack(i);
            if(contender.isEmpty()) continue;

            // check
            if(contender.getItem() instanceof SpellComponentStoringItem)
            {
                var component = SpellComponentStoringItem.readComponent(contender);
                if(component==null) continue;
                if(component.function==func)
                {
                    int taken = Math.min(count,contender.getCount());
                    contender.decrement(taken);
                    count -= taken;
                    if(count <= 0) return 0;
                }
            }

            // recursively check
            if(contender.getItem() == ModItems.COMPONENT_POUCH){
                var inv2 = ModItems.COMPONENT_POUCH.getInventory(contender);
                count = removeComponentFrom(func,count,inv2);
                if(count <= 0) return count;
            }
        }

        return count;
    }


    private static final AutoCraftingInventory AUTO_INVENTORY = new AutoCraftingInventory(SLOT_COUNT, 1);

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public void markDirty() {
        if(world==null) return;
        world.updateListeners(pos, getCachedState(),getCachedState(), 3);
        super.markDirty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return false;
        //return ImplementedInventory.super.canExtract(slot, stack, side);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return false;
        //return ImplementedInventory.super.canInsert(slot, stack, side);
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return false;
        //return ImplementedInventory.super.canTransferTo(hopperInventory, slot, stack);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return ImplementedInventory.super.removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        return ImplementedInventory.super.removeStack(slot, count);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        // ensure there's a grid in the storage item
        if(slot == OUTPUT_SLOT){
            if(stack.getItem() instanceof SpellStoringItem storer)
            {
                SpellStoringItem.getOrCreateGrid(stack);
            }
        }
        ImplementedInventory.super.setStack(slot, stack);
    }

    public ItemStack getOutput(){
        return getStack(SpellmakerBlockEntity.OUTPUT_SLOT);
    }
}