package org.oxytocina.geomancy.blocks.blockEntities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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
import org.oxytocina.geomancy.client.screen.RitualForgeScreenHandler;
import org.oxytocina.geomancy.inventories.AutoCraftingInventory;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.ISoulStoringItem;
import org.oxytocina.geomancy.items.tools.HammerItem;
import org.oxytocina.geomancy.recipe.soulforge.ISoulForgeRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellContext;

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
        super(ModBlockEntities.RITUALISTIC_FORGE_BLOCK_ENTITY, pos, state);

        refreshAvailableIngredients();
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
                // TODO: visual and auditory flair
                // TODO: toss out all ingredients
                activeRecipe=null;
            }
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
    }

    private boolean hasCraftingFinished() {
        return progress>=1;
    }

    private void initialize(World world, BlockPos pos, BlockState state){
        initialized=true;
        if(!world.isClient){
            register();
            registerInArea(world,pos,PEDESTAL_RANGE);
        }
    }
    private void resetProgress() {
        progress=0;
        instability=0;
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
    }

    @Override
    public boolean isHammerable() {
        return isActive();
    }

    public static class ParticleData {
        public ParticleData.Type type = ParticleData.Type.PROGRESS;
        public int amount = 10;
        public float dispersion = 0.5f;
        public Vec3d pos;
        public Vec3d velMin;
        public Vec3d velMax;
        public Identifier world;

        private ParticleData(ParticleData.Type type, int amount, Vec3d pos, Vec3d velMin, Vec3d velMax, Identifier world, float dispersion){
            this.type=type;
            this.amount=amount;
            this.pos=pos;
            this.velMin=velMin;
            this.velMax=velMax;
            this.world=world;
            this.dispersion=dispersion;
        }

        public static ParticleData createProgress(SmitheryBlockEntity smithery, Vec3d pos){
            return new ParticleData(ParticleData.Type.PROGRESS, 5,pos.add(0,0.6f,0),new Vec3d(0,0,0),new Vec3d(0,0,0),smithery.getWorld().getRegistryKey().getValue(),0.3f);
        }
        public static ParticleData createComplete(SmitheryBlockEntity smithery, Vec3d pos){
            return new ParticleData(ParticleData.Type.COMPLETE, 10,pos.add(0,0.6f,0),new Vec3d(-0.2f,0,-0.2f),new Vec3d(0.2f,0.4f,0.2f),smithery.getWorld().getRegistryKey().getValue(),0.3f);
        }
        public static ParticleData createFailure(SmitheryBlockEntity smithery, Vec3d pos){
            return new ParticleData(ParticleData.Type.FAILURE, 10,pos.add(0,0.6f,0),new Vec3d(-0.2f,0,-0.2f),new Vec3d(0.1f,0.2f,0.1f),smithery.getWorld().getRegistryKey().getValue(),0.3f);
        }

        public ParticleData amount(int amount){this.amount = amount;return this;}
        public ParticleData dispersion(int dispersion){this.dispersion = dispersion;return this;}
        public ParticleData type(ParticleData.Type type){this.type = type;return this;}

        public void write(PacketByteBuf buf){
            buf.writeString(type.toString());
            buf.writeInt(amount);
            buf.writeFloat(dispersion);
            buf.writeVector3f(pos.toVector3f());
            buf.writeVector3f(velMin.toVector3f());
            buf.writeVector3f(velMax.toVector3f());
            buf.writeIdentifier(world);
        }

        public static ParticleData from(PacketByteBuf buf){
            ParticleData.Type type = ParticleData.Type.valueOf(buf.readString());
            int amount = buf.readInt();
            float dispersion = buf.readFloat();
            Vec3d pos = new Vec3d(buf.readVector3f());
            Vec3d velMin = new Vec3d(buf.readVector3f());
            Vec3d velMax = new Vec3d(buf.readVector3f());
            Identifier world = buf.readIdentifier();
            return new ParticleData(type,amount,pos,velMin,velMax,world,dispersion);
        }

        @Environment(EnvType.CLIENT)
        public void run(){
            World worldObj = MinecraftClient.getInstance().world;
            if(!worldObj.getRegistryKey().getValue().equals(world)) return; // ignore particle spawns in different worlds
            Random rand = new LocalRandom(GeomancyClient.tick);
            for (int i = 0; i < amount; i++) {
                Vec3d pPos = new Vec3d(
                        pos.x+(rand.nextFloat()*2-1)*dispersion,
                        pos.y+(rand.nextFloat()*2-1)*dispersion,
                        pos.z+(rand.nextFloat()*2-1)*dispersion);
                Vec3d vel = new Vec3d(
                        MathHelper.lerp(rand.nextFloat(),velMin.x,velMax.x),
                        MathHelper.lerp(rand.nextFloat(),velMin.y,velMax.y),
                        MathHelper.lerp(rand.nextFloat(),velMin.z,velMax.z)
                );
                switch(type){
                    case PROGRESS:{
                        worldObj.addParticle(ParticleTypes.LAVA,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        break;
                    }
                    case COMPLETE:{
                        worldObj.addParticle(ParticleTypes.LAVA,pPos.x,pPos.y,pPos.z,0,0,0);
                        worldObj.addParticle(ParticleTypes.FLAME,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        break;
                    }
                    case FAILURE:{
                        worldObj.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        break;
                    }
                }
            }
        }

        public enum Type{
            PROGRESS,
            COMPLETE,
            FAILURE
        }
    }
}