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
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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
import net.minecraft.sound.SoundEvents;
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
import org.oxytocina.geomancy.util.BlockHelper;
import org.oxytocina.geomancy.util.EntityUtil;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;

public class SoulForgeBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IOwnable, IPedestalListener, IHammerable {

    private final DefaultedList<ItemStack> ownInventory = DefaultedList.ofSize(SLOT_COUNT,ItemStack.EMPTY);
    public final List<PedestalBlockEntity> surroundingInventories = new ArrayList<>();
    public final List<PedestalBlockEntity> surroundingSoulInventories = new ArrayList<>();
    public final List<ItemStack> surroundingIngredients = new ArrayList<>();
    private final List<ItemStack> totalIngredients = new ArrayList<>();
    public final List<ItemStack> consumedIngredients = new ArrayList<>();
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
        if(previewingRecipe!=null)nbt.putString("previewRecipe",previewingRecipe.getIdentifier().toString());
        nbt.putFloat("progress",progress);
        nbt.putFloat("instability",instability);
        nbt.putFloat("availableSoul",availableSoul);
        NbtList consumedList = new NbtList();
        for (int i = 0; i < consumedIngredients.size(); i++) {
            NbtCompound stackNbt = new NbtCompound();
            consumedIngredients.get(i).writeNbt(stackNbt);
            consumedList.add(stackNbt);
        }
        nbt.put("consumed",consumedList);
        NbtList surroundingList = new NbtList();
        for (int i = 0; i < surroundingIngredients.size(); i++) {
            NbtCompound stackNbt = new NbtCompound();
            surroundingIngredients.get(i).writeNbt(stackNbt);
            surroundingList.add(stackNbt);
        }
        nbt.put("surrounding",surroundingList);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        clear();
        Inventories.readNbt(nbt, ownInventory);
        activeRecipeIdToLoad=null;
        previewRecipeIdToLoad=null;
        if(nbt.contains("activeRecipe")){
            activeRecipeIdToLoad=Identifier.tryParse(nbt.getString("activeRecipe"));
        }
        if(nbt.contains("previewRecipe")){
            previewRecipeIdToLoad=Identifier.tryParse(nbt.getString("previewRecipe"));
        }

        if(world!=null){
            if(activeRecipeIdToLoad!=null)
            {
                activeRecipe = (ISoulForgeRecipe) world.getRecipeManager().get(activeRecipeIdToLoad).orElse(null);
            }
            if(previewRecipeIdToLoad!=null)
            {
                previewingRecipe = (ISoulForgeRecipe) world.getRecipeManager().get(previewRecipeIdToLoad).orElse(null);
            }
        }

        progress = nbt.getFloat("progress");
        instability = nbt.getFloat("instability");
        availableSoul = nbt.getFloat("availableSoul");
        consumedIngredients.clear();
        if(nbt.contains("consumed")){
            NbtList consumedList = nbt.getList("consumed", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < consumedList.size(); i++) {
                consumedIngredients.add(ItemStack.fromNbt(consumedList.getCompound(i)));
            }
        }
        surroundingIngredients.clear();
        if(nbt.contains("surrounding")){
            NbtList surroundingList = nbt.getList("surrounding", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < surroundingList.size(); i++) {
                surroundingIngredients.add(ItemStack.fromNbt(surroundingList.getCompound(i)));
            }
        }
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
        if(world.isClient) return;
        previewingRecipe = getRecipeFor(world,totalIngredients);
        sendUpdatesToNearbyClients();
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

            // increase instability
            instability += 1/20f * 0.05f * activeRecipe.getInstability(inputInventory());

            // instability vfx
            float instabilityPowed = (float)Math.pow(instability,5);
            if(Geomancy.tick%4==0&&Toolbox.random.nextFloat() < instabilityPowed){
                Toolbox.playSound(SoundEvents.BLOCK_STONE_BREAK,world,getPos(),SoundCategory.BLOCKS,0.4f+0.6f*instabilityPowed,1+instabilityPowed);
                ParticleUtil.ParticleData.createInstability(world,getPos().toCenterPos()).send();
                CamShakeUtil.cause(world,getPos().toCenterPos(),10,0.5f+0.5f*instabilityPowed);
            }

            if(instability>=1){
                // instability too high, abort craft
                CamShakeUtil.cause(world,getPos().toCenterPos(),20,2,2,0.5f);
                // TODO: visual and auditory flair
                // TODO: toss out all ingredients
                for (int i = 0; i < surroundingInventories.size(); i++) {
                    var pedestal = surroundingInventories.get(i);
                    var stack = pedestal.getItem();
                    if(stack.isEmpty()) continue;
                    stack = stack.copyAndEmpty();
                    pedestal.markDirty();
                    final float speed = 0.2f;
                    MultiblockCrafter.spawnItemStackAsEntitySplitViaMaxCount(world,pedestal.getPos().toCenterPos().add(0,0.6f,0),stack,stack.getCount(),new Vec3d(
                            Toolbox.randomBetween(-speed,speed),
                            Toolbox.randomBetween(0,speed*2),
                            Toolbox.randomBetween(-speed,speed)
                    ),false,getOwner());
                }
                activeRecipe=null;
                this.resetProgress();
            }

            if(Geomancy.tick%4==0)
            {
                sendUpdatesToNearbyClients();
            }

            markDirty();
        }
        else if(previewingRecipe!=null)
            currentResult = previewingRecipe.getPreviewOutput(inputInventory());
        else currentResult = ItemStack.EMPTY;

        if(!ItemStack.areEqual(currentResult,getStack(PREVIEW_SLOT))){
            setStack(PREVIEW_SLOT,currentResult);
        }

        if(Geomancy.tick%(isActive()?4:20)==0){
            updateAvailableSoul();
        }
    }

    private void finishCrafting() {
        this.spawnResult();
        this.resetProgress();
        activeRecipe=null;
        // TODO: visual and auditory flair
        Toolbox.playSound(SoundEvents.ENTITY_WITHER_DEATH,world,getPos(), SoundCategory.NEUTRAL, 0.5F, Toolbox.randomPitch());
        CamShakeUtil.cause(world,getPos().toCenterPos(),20,2,2,0.5f);
        sendUpdatesToNearbyClients();
        markDirty();
    }

    private boolean hasCraftingFinished() {
        return progress>=1;
    }

    private Identifier activeRecipeIdToLoad = null;
    private Identifier previewRecipeIdToLoad = null;
    private void initialize(World world, BlockPos pos, BlockState state){
        initialized=true;
        if(activeRecipeIdToLoad!=null)
        {
            activeRecipe = (ISoulForgeRecipe) world.getRecipeManager().get(activeRecipeIdToLoad).orElse(null);
        }
        if(previewRecipeIdToLoad!=null)
        {
            previewingRecipe = (ISoulForgeRecipe) world.getRecipeManager().get(previewRecipeIdToLoad).orElse(null);
        }
        if(!world.isClient){
            register();
            registerInArea(world,pos,PEDESTAL_RANGE);

            refreshAvailableIngredients();
        }
    }
    private void resetProgress() {
        progress=0;
        instability=0;
        consumedIngredients.clear();
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
        if(world.isClient) return;
        refreshAvailableIngredients();
        markDirty();
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
        if(!BlockHelper.withinCube(pedestal.getPos().subtract(getPos()),PEDESTAL_RANGE)) return;
        if(surroundingInventories.contains(pedestal)) return;
        surroundingInventories.add(pedestal);

        refreshAvailableIngredients();
        sendUpdatesToNearbyClients();
        markDirty();
    }

    @Override
    public void pedestalRemoved(PedestalBlockEntity pedestal) {
        if(removed) {return;}
        if(!surroundingInventories.contains(pedestal)) return;
        surroundingInventories.remove(pedestal);

        refreshAvailableIngredients();
        sendUpdatesToNearbyClients();
        markDirty();
    }

    @Override
    public void pedestalChanged(PedestalBlockEntity pedestal) {
        if(removed) {return;}
        if(!surroundingInventories.contains(pedestal)) return;

        refreshAvailableIngredients();
        sendUpdatesToNearbyClients();
        markDirty();
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
        if(world==null||world.isClient) return;

        HammerItem hammerItem = (HammerItem) hammer.getItem();

        // progress

        // take soul
        float availableSoul = getAvailableSoul();
        float soulToConsume = Math.min(availableSoul,hammerItem.getHitProgress(player) * 10 + skill);
        float left = soulToConsume;
        int soulContainers = surroundingSoulInventories.size();
        List<PedestalBlockEntity> takenFrom = new ArrayList<>();
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
                takenFrom.add(pedestal);
                left-=taken;
                changed=true;
            }
            if(left<=0) break;
        }
        float taken = soulToConsume-left;

        progress += taken/activeRecipe.getSoulCost(inputInventory());

        // try to consume ingredients
        if(progress>0){
            boolean consumedAllIngredients = true;

            var ingredients = activeRecipe.getNbtIngredients(null);
            int shouldHaveConsumedCount = Math.round((float)Math.ceil(progress*ingredients.size()));
            boolean changed2=true;
            while(consumedIngredients.size()<shouldHaveConsumedCount)
            {
                if(!changed2) break;
                changed2=false;
                List<Integer> accountedForConsumedIngredients = new ArrayList<>();

                for (int i = 1; i < ingredients.size(); i++) {
                    var ingredient = ingredients.get(i);
                    // check if this ingredient is consumed
                    boolean consumedAlready = false;
                    for (int j = 0; j < consumedIngredients.size(); j++) {
                        if(accountedForConsumedIngredients.contains(j)) continue;
                        if(ingredient.test(consumedIngredients.get(j)))
                        {
                            consumedAlready = true;
                            accountedForConsumedIngredients.add(j);
                            break;
                        }
                    }
                    if(consumedAlready)
                        continue;

                    // try to consume ingredient
                    boolean consumed = false;
                    for (int j = 0; j < surroundingInventories.size(); j++) {
                        var pedestal = surroundingInventories.get(j);
                        if(pedestal==null) continue;
                        var pedestalStack = pedestal.getStack(0);
                        if(!ingredient.test(pedestalStack)) continue;
                        // consume stack
                        consumedIngredients.add(pedestalStack.copyAndEmpty());
                        pedestal.markDirty();
                        // TODO: visual and auditory flair
                        consumed=true;
                        changed2=true;
                        break;
                    }
                    if(consumed)
                        break;

                    consumedAllIngredients = false;
                }

                // consume final ingredient
                if(progress >= 1 && shouldHaveConsumedCount>=ingredients.size() && consumedIngredients.size() >= ingredients.size()-1)
                {
                    var baseIng = ingredients.get(0);
                    // check if this ingredient is consumed
                    boolean consumedAlready = false;
                    for (int j = 0; j < consumedIngredients.size(); j++) {
                        if(accountedForConsumedIngredients.contains(j)) continue;
                        if(baseIng.test(consumedIngredients.get(j)))
                        {
                            // already consumed...??
                            consumedAlready = true;
                            accountedForConsumedIngredients.add(j);
                            break;
                        }
                    }
                    if(consumedAlready)
                        continue;

                    // try to consume ingredient
                    boolean consumed = false;
                    for (int i = 0; i < size(); i++) {
                        var baseStack = getStack(i);
                        if(!baseIng.test(baseStack)) continue;
                        // consume stack
                        consumedIngredients.add(baseStack.copyAndEmpty());
                        markDirty();
                        // TODO: visual and auditory flair
                        changed2=true;
                        consumed=true;
                        break;
                    }
                    if(consumed)
                        continue;

                    consumedAllIngredients = false;
                }
            }

            if(hasCraftingFinished() && consumedAllIngredients){
                finishCrafting();
            }
        }



        if(taken<=0){
            // didnt make progress, soul storers are empty!
            // make a dud sound
            Toolbox.playSound(ModSoundEvents.USE_HAMMER_FAIL,world,getPos(), SoundCategory.NEUTRAL, 0.7F, Toolbox.randomPitch());
            for(var pedestal : surroundingSoulInventories){
                ParticleUtil.ParticleData.createSoulDud(world,pedestal.getPos().toCenterPos()).send();
            }
        }
        else{
            // made progress
            Toolbox.playSound(ModSoundEvents.USE_HAMMER,world,getPos(), SoundCategory.NEUTRAL, 0.7F, Toolbox.randomPitch());
            CamShakeUtil.cause(world,getPos().toCenterPos(),20,0.5f);
            ParticleUtil.ParticleData.createSmithingProgress(world,getPos().toCenterPos()).send();
            for(var pedestal : takenFrom){
                ParticleUtil.ParticleData.createSoulFlare(world,pedestal.getPos().toCenterPos()).send();
            }

        }
    }

    @Override
    public boolean isHammerable() {
        return isActive();
    }

    public void updateAvailableSoul(){
        float prev = availableSoul;

        availableSoul = 0;
        for (PedestalBlockEntity pedestal : surroundingSoulInventories) {
            var stack = pedestal.getItem();
            if (!(stack.getItem() instanceof ISoulStoringItem storer)) continue;
            availableSoul += storer.getMana(world, stack);
        }

        if(prev!=availableSoul){
            markDirty();
            sendUpdatesToNearbyClients();
        }
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
        buf.writeBoolean(previewingRecipe!=null);
        if(previewingRecipe!=null)
            buf.writeIdentifier(previewingRecipe.getIdentifier());
        buf.writeFloat(progress);
        buf.writeFloat(instability);
        buf.writeFloat(availableSoul);

        ModMessages.sendToAllClients(sw.getServer(),ModMessages.UPDATE_SOULFORGE,buf, spe-> EntityUtil.isInRange(spe,sw,getPos().toCenterPos(),100));
    }

    @Environment(EnvType.CLIENT)
    public void setStatus(Identifier recipe,Identifier previewRecipe, float progress, float instability, float soul) {
        if(world==null) return;
        activeRecipe = recipe==null?null:(ISoulForgeRecipe) world.getRecipeManager().get(recipe).orElse(null);
        this.previewingRecipe = previewRecipe==null?null:(ISoulForgeRecipe) world.getRecipeManager().get(previewRecipe).orElse(null);
        this.progress=progress;
        this.instability=instability;
        this.availableSoul=soul;
    }
}