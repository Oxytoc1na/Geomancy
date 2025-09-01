package org.oxytocina.geomancy.blocks.blockEntities;

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
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.MultiblockCrafter;
import org.oxytocina.geomancy.client.screen.RitualForgeScreenHandler;
import org.oxytocina.geomancy.inventories.AutoCraftingInventory;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.recipe.soulforge.ISoulForgeRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;

public class SoulForgeBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IOwnable, IPedestalListener {

    private final DefaultedList<ItemStack> ownInventory = DefaultedList.ofSize(SLOT_COUNT,ItemStack.EMPTY);
    private final List<PedestalBlockEntity> surroundingInventories = new ArrayList<>();
    private final List<ItemStack> surroundingIngredients = new ArrayList<>();

    private PropertyDelegate propertyDelegate;


    public static final int BASE_SLOT = 0;
    public static final int SLOT_COUNT = 2;
    public static final int INPUT_SLOT_COUNT = 1;
    public static final int PEDESTAL_RANGE = 5;

    public ISoulForgeRecipe previewingRecipe = null;
    public ISoulForgeRecipe activeRecipe = null;
    public ItemStack currentResult = ItemStack.EMPTY;
    public float progress;
    public float instability;
    public float availableSoul;

    private boolean initialized = false;
    private PlayerEntity owner = null;

    public SoulForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUALISTIC_FORGE_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, ownInventory);
        if(isActive())nbt.putString("activeRecipe",activeRecipe.getIdentifier().toString());
        nbt.putFloat("progress",progress);
        nbt.putFloat("instability",instability);
        nbt.putFloat("availableSoul",availableSoul);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        clear();
        Inventories.readNbt(nbt, ownInventory);
        if(nbt.contains("activeRecipe")){
            activeRecipe=(ISoulForgeRecipe) world.getRecipeManager().get(Identifier.tryParse(nbt.getString("activeRecipe"))).get();
        }
        progress = nbt.getFloat("progress");
        instability = nbt.getFloat("instability");
        availableSoul = nbt.getFloat("availableSoul");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container."+Geomancy.MOD_ID+".ritualistic_forge");
    }

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new RitualForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public boolean isActive(){return activeRecipe!=null;}


    public DefaultedList<ItemStack> getItems() {
        return ownInventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(!initialized) initialize(world,pos,state);

        ISoulForgeRecipe prevRecipe = activeRecipe;
        this.activeRecipe =getRecipe();
        ItemStack previousResult = currentResult;
        if(this.hasRecipe()){
            currentResult = activeRecipe.getPreviewOutput(inputInventory());
            if(hasCraftingFinished()){
                this.craftItem();
                this.resetProgress();
                markDirty();
            }
        }
        else
            currentResult = ItemStack.EMPTY;

        if(prevRecipe!= activeRecipe){
            recipeChanged();
        }

        if(!Toolbox.itemStacksAreEqual(previousResult, currentResult)){
            recipeChanged();
        }

    }

    private void initialize(World world, BlockPos pos, BlockState state){
        recipeChanged();
        initialized=true;
        if(!world.isClient){
            register();
            registerInArea(world,pos,PEDESTAL_RANGE);
        }
    }
    private void recipeChanged(){
        if(world==null) return;
        this.activeRecipe = getRecipe();
        this.resetProgress();
    }

    private void resetProgress() {
        activeRecipe=null;
        progress=0;
        instability=0;
    }

    private void craftItem() {
        ISoulForgeRecipe recipe = getRecipe();
        if (recipe != null) {

            List<ItemStack> results = craft(recipe, ownInventory, world);
            for(ItemStack result : results)
            {
                int count = result.getCount();
                //result.setCount(count); ??

                if(!world.isClient)
                {
                    MultiblockCrafter.spawnItemStackAsEntitySplitViaMaxCount(world, Vec3d.ofCenter(pos).add(new Vec3d(0,1,0)), result, count, new Vec3d(0,0.25f,0), false, null);
                }
            }
            world.playSound(null, pos, ModSoundEvents.SMITHERY_FINISHED, SoundCategory.NEUTRAL, 1.0F, 0.6F + world.getRandom().nextFloat() * 0.2F);

        }

        recipeChanged();
    }

    private boolean hasCraftingFinished() {
        return progress>=getRequiredProgress();
    }

    private float getRequiredProgress(){
        return activeRecipe==null?1000000:activeRecipe.getProgressRequired(this);
    }

    private boolean hasRecipe() {
        return activeRecipe != null;
    }

    private ISoulForgeRecipe getRecipe(){
        return getRecipeFor(world,getItems());

    }

    private void increaseCraftProgress(int amount) {
        progress+=amount;
    }

    public ISoulForgeRecipe getRecipeFor(@NotNull World world, DefaultedList<ItemStack> inventory) {
        AUTO_INVENTORY.setInputInventory( inputInventory().getItems() );

        ISoulForgeRecipe res = world.getRecipeManager().getFirstMatch(ModRecipeTypes.SOULFORGE_SIMPLE, AUTO_INVENTORY, world).orElse(null);
        return res;
    }

    public ImplementedInventory inputInventory(){
        ImplementedInventory inputInventory = ImplementedInventory.ofSize(INPUT_SLOT_COUNT);
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            inputInventory.setStack(i,getStack(i));
        }
        return inputInventory;
    }

    public List<ItemStack> craft(ISoulForgeRecipe recipe, DefaultedList<ItemStack> inventory, World world) {
        AUTO_INVENTORY.setInputInventory(inventory);
        return recipe.getResult(AUTO_INVENTORY, true,false,getOwner());
    }

    private static final AutoCraftingInventory AUTO_INVENTORY = new AutoCraftingInventory(SLOT_COUNT, 1);

    public ItemStack getBaseStack() {
        if(activeRecipe ==null || !activeRecipe.hasBaseStack()) return ItemStack.EMPTY;
        return getStack(BASE_SLOT);
    }

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public void inventoryChanged() {
        recipeChanged();
    }

    @Override
    public void markDirty() {
        if(world==null) return;
        world.updateListeners(pos, getCachedState(),getCachedState(), 3);
        super.markDirty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        if(slot>=INPUT_SLOT_COUNT) return false;
        return ImplementedInventory.super.canExtract(slot, stack, side);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        if(slot>=INPUT_SLOT_COUNT) return false;
        return ImplementedInventory.super.canInsert(slot, stack, side);
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        if(slot>=INPUT_SLOT_COUNT) return false;
        return ImplementedInventory.super.canTransferTo(hopperInventory, slot, stack);
    }

    @Override
    public ItemStack removeStack(int slot) {

        if(slot<INPUT_SLOT_COUNT)
            inventoryChanged();

        return ImplementedInventory.super.removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int count) {

        if(slot<INPUT_SLOT_COUNT)
            inventoryChanged();

        return ImplementedInventory.super.removeStack(slot, count);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

        if(slot<INPUT_SLOT_COUNT)
            inventoryChanged();

        ImplementedInventory.super.setStack(slot, stack);
    }

    @Override
    public PlayerEntity getOwner() {
        return owner;
    }

    @Override
    public void registerPedestal(PedestalBlockEntity pedestal) {
        if(removed) {return;}
        surroundingInventories.add(pedestal);
    }

    @Override
    public void pedestalRemoved(PedestalBlockEntity pedestal) {
        if(removed) {return;}
        surroundingInventories.remove(pedestal);
    }
}