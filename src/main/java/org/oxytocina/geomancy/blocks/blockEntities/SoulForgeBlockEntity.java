package org.oxytocina.geomancy.blocks.blockEntities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.MultiblockCrafter;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.screen.SoulForgeScreenHandler;
import org.oxytocina.geomancy.client.util.CamShakeUtil;
import org.oxytocina.geomancy.inventories.AutoCraftingInventory;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.ISoulStoringItem;
import org.oxytocina.geomancy.items.tools.HammerItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.recipe.soulforge.ISoulForgeRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;

public class SoulForgeBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IOwnable, IPedestalListener, IHammerable {

    private final DefaultedList<ItemStack> ownInventory = DefaultedList.ofSize(SLOT_COUNT,ItemStack.EMPTY);
    private final List<PedestalBlockEntity> surroundingInventories = new ArrayList<>();
    private final List<PedestalBlockEntity> surroundingSoulInventories = new ArrayList<>();
    private final List<ItemStack> surroundingIngredients = new ArrayList<>();
    private final List<ItemStack> totalIngredients = new ArrayList<>();
    private ItemStack baseIngredient = ItemStack.EMPTY;

    private PropertyDelegate propertyDelegate;


    public static final int BASE_SLOT = 0;
    public static final int PREVIEW_SLOT = 1;
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
        super(ModBlockEntities.SOULFORGE_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new ArrayPropertyDelegate(3);
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
        return Text.translatable("container."+Geomancy.MOD_ID+".soulforge");
    }

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SoulForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public boolean isActive(){return activeRecipe!=null;}


    @Override
    public DefaultedList<ItemStack> getItems() {
        return ownInventory;
    }

    public void refreshAvailableIngredients(){
        surroundingIngredients.clear();
        totalIngredients.clear();
        surroundingSoulInventories.clear();

        baseIngredient = getStack(0);
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            var ing = getStack(i);
            totalIngredients.add(ing);
        }

        for(var inv : surroundingInventories){
            for (int i = 0; i < inv.size(); i++) {
                var ing = inv.getStack(i);
                if(ing.isEmpty()) continue;
                surroundingIngredients.add(ing);
                totalIngredients.add(ing);
            }
        }

        for(var inv : surroundingInventories) {
            if(inv.getStack(0).getItem() instanceof ISoulStoringItem)
                surroundingSoulInventories.add(inv);
        }

        refreshPreviewRecipe();
    }

    public void refreshPreviewRecipe(){
        previewingRecipe = getRecipeFor(world,totalIngredients);
    }

    public void activate(SpellContext ctx){
        if(!isActive() && previewingRecipe!=null){
            startCrafting();
        }
    }

    public float getAvailableSoul(){
        float res = 0;
        for(var inv : surroundingSoulInventories){
            var stack = inv.getStack(0);
            if(stack.isEmpty() || !(stack.getItem() instanceof ISoulStoringItem storer)) continue;
            res+=storer.getMana(world,stack);
        }
        return res;
    }

    public void startCrafting(){
        activeRecipe=previewingRecipe;
        resetProgress();
        // TODO: visual and auditory flair
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(!initialized) initialize(world,pos,state);
        if(world.isClient) return;

        if(this.isActive()){
            currentResult = activeRecipe.getPreviewOutput(inputInventory());
            if(hasCraftingFinished()){
                finishCrafting();
            }

            // increase instability
            instability += 1/20f * 0.05f * activeRecipe.getInstability(inputInventory());
            if(instability>=1){
                // instability too high, abort craft
                this.resetProgress();
                CamShakeUtil.cause(world,getPos().toCenterPos(),20,2,2,0.5f);
                // TODO: visual and auditory flair
                // TODO: toss out all ingredients
                activeRecipe=null;
            }

            if(Geomancy.tick%4==0)
                sendUpdatesToNearbyClients();
        }
        else if(previewingRecipe!=null)
            currentResult = previewingRecipe.getPreviewOutput(inputInventory());
        else currentResult = ItemStack.EMPTY;

        if(!ItemStack.areEqual(currentResult,getStack(PREVIEW_SLOT))){
            setStack(PREVIEW_SLOT,currentResult);
        }
    }

    private void finishCrafting() {
        this.spawnResult();
        this.resetProgress();
        activeRecipe=null;
        markDirty();
        // TODO: visual and auditory flair
        CamShakeUtil.cause(world,getPos().toCenterPos(),20,2,2,0.5f);
        sendUpdatesToNearbyClients();
    }

    private boolean hasCraftingFinished() {
        return progress>=1;
    }

    private void initialize(World world, BlockPos pos, BlockState state){
        initialized=true;
        if(!world.isClient){
            register();
            registerInArea(world,pos,PEDESTAL_RANGE);

            refreshAvailableIngredients();
        }
    }
    private void resetProgress() {
        progress=0;
        instability=0;
        sendUpdatesToNearbyClients();
    }

    private void spawnResult() {
        if (activeRecipe != null) {

            List<ItemStack> results = getCraftingResult(activeRecipe, ownInventory, world);
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
    }

    private ISoulForgeRecipe getRecipe(){
        return getRecipeFor(world,getItems());
    }


    public ISoulForgeRecipe getRecipeFor(@NotNull World world, List<ItemStack> inventory) {
        AUTO_INVENTORY.setInputInventory( inventory );
        ISoulForgeRecipe res = world.getRecipeManager().getFirstMatch(ModRecipeTypes.SOULFORGE_SIMPLE, AUTO_INVENTORY, world).orElse(null);
        return res;
    }

    public Inventory inputInventory(){
        Inventory inputInventory = ImplementedInventory.ofSize(totalIngredients.size());
        for (int i = 0; i < totalIngredients.size(); i++) {
            inputInventory.setStack(i,totalIngredients.get(i));
        }
        return inputInventory;
    }

    public List<ItemStack> getCraftingResult(ISoulForgeRecipe recipe, DefaultedList<ItemStack> inventory, World world) {
        AUTO_INVENTORY.setInputInventory(inventory);
        return recipe.getResult(AUTO_INVENTORY, true,false,getOwner());
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

    public void inventoryChanged() {
        refreshAvailableIngredients();
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
        var res = ImplementedInventory.super.removeStack(slot);
        if(slot<INPUT_SLOT_COUNT)
            inventoryChanged();
        return res;
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        var res = ImplementedInventory.super.removeStack(slot, count);
        if(slot<INPUT_SLOT_COUNT)
            inventoryChanged();
        return res;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ImplementedInventory.super.setStack(slot, stack);
        if(slot<INPUT_SLOT_COUNT)
            inventoryChanged();
    }

    @Override
    public PlayerEntity getOwner() {
        return owner;
    }

    @Override
    public void registerPedestal(PedestalBlockEntity pedestal) {
        if(removed) {return;}
        if(surroundingInventories.contains(pedestal)) return;
        surroundingInventories.add(pedestal);

        refreshAvailableIngredients();
    }

    @Override
    public void pedestalRemoved(PedestalBlockEntity pedestal) {
        if(removed) {return;}
        if(!surroundingInventories.contains(pedestal)) return;
        surroundingInventories.remove(pedestal);

        refreshAvailableIngredients();
    }

    @Override
    public void pedestalChanged(PedestalBlockEntity pedestal) {
        if(removed) {return;}
        if(!surroundingInventories.contains(pedestal)) return;

        refreshAvailableIngredients();
    }

    private ItemStack lastHammerStack = ItemStack.EMPTY;
    private PlayerEntity lastHammerer = null;
    @Override
    public ItemStack getLastHammerStack(){
        return lastHammerStack;
    }

    @Override
    public PlayerEntity getLastHammerer(){
        return lastHammerer;
    }

    @Override
    public void onHitWithHammer(@Nullable PlayerEntity player, ItemStack hammer, float skill) {
        lastHammerStack=hammer;
        if(player!=null) lastHammerer = player;
        if(!isActive()) return;

        HammerItem hammerItem = (HammerItem) hammer.getItem();

        // progress

        // take soul
        float availableSoul = getAvailableSoul();
        float soulToConsume = Math.min(availableSoul,hammerItem.getHitProgress(player) * 10 + skill);
        float left = soulToConsume;
        int soulContainers = surroundingSoulInventories.size();
        boolean changed = true;
        while(changed){
            changed=false;
            for(var pedestal : surroundingSoulInventories){
                var stack = pedestal.getStack(0);
                if(stack.isEmpty() || !(stack.getItem() instanceof ISoulStoringItem storer)) continue;
                float thisMana = storer.getMana(world,stack);
                if(thisMana<=0) continue;
                float taken = Math.min(thisMana,soulToConsume/soulContainers);
                storer.takeSoul(world,stack,taken,null);
                left-=taken;
                changed=true;
            }
            if(left<=0) break;
        }
        float taken = soulToConsume-left;

        progress += taken/activeRecipe.getSoulCost(inputInventory());

        if(hasCraftingFinished()){
            finishCrafting();
            return;
        }

        // TODO: visual and auditory flair
        if(taken<=0){
            // didnt make progress, soul storers are empty!
            // make a dud sound
        }
        else{
            // made progress
        }
    }

    @Override
    public boolean isHammerable() {
        return isActive();
    }

    public Inventory getDroppedItems() {
        Inventory res = ImplementedInventory.ofSize(INPUT_SLOT_COUNT);
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            res.setStack(i,getStack(i));
        }
        return res;
    }

    public void sendUpdatesToNearbyClients(){
        if(world==null||!(world instanceof ServerWorld sw)) return;
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(getPos());
        buf.writeBoolean(isActive());
        if(isActive())
            buf.writeIdentifier(activeRecipe.getIdentifier());
        buf.writeFloat(progress);
        buf.writeFloat(instability);

        ModMessages.sendToAllClients(sw.getServer(),ModMessages.UPDATE_SOULFORGE,buf, spe-> EntityUtil.isInRange(spe,sw,getPos().toCenterPos(),100));
    }

    @Environment(EnvType.CLIENT)
    public void setStatus(Identifier recipe, float progress, float instability) {
        if(world==null) return;
        activeRecipe = recipe==null?null:(ISoulForgeRecipe) world.getRecipeManager().get(recipe).orElse(null);
        this.progress=progress;
        this.instability=instability;
    }
}