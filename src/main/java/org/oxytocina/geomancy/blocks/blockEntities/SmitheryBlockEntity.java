package org.oxytocina.geomancy.blocks.blockEntities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.screen.SmitheryScreenHandler;
import org.oxytocina.geomancy.client.util.CamShakeUtil;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;
import org.oxytocina.geomancy.blocks.MultiblockCrafter;
import org.oxytocina.geomancy.inventories.AutoCraftingInventory;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.tools.HammerItem;
import org.oxytocina.geomancy.recipe.smithery.SmitheryRecipeI;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.sound.ModSoundEvents;

import java.util.HashMap;
import java.util.List;

public class SmitheryBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IHammerable {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SLOT_COUNT,ItemStack.EMPTY);
    private final CheckedRandom mishapRandom = new CheckedRandom(1);

    public static final int BASE_SLOT = 4;
    public static final int OUTPUT_SLOT = 9;
    public static final int INPUT_SLOT_COUNT = 9;
    public static final int SLOT_COUNT = 10;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;
    public SmitheryRecipeI currentRecipe = null;
    public ItemStack currentResult = ItemStack.EMPTY;
    private boolean initialized = false;

    private ItemStack lastHammerStack = ItemStack.EMPTY;
    private PlayerEntity lastHammerer = null;

    private int automatedHitCooldown = 0;

    public SmitheryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMITHERY_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> SmitheryBlockEntity.this.progress;
                    case 1 -> SmitheryBlockEntity.this.maxProgress;
                    case 2 -> SmitheryBlockEntity.this.currentRecipe!=null?SmitheryBlockEntity.this.currentRecipe.getDifficulty(SmitheryBlockEntity.this.inputInventory(),SmitheryBlockEntity.this.getLastHammerStack(),SmitheryBlockEntity.this.getLastHammerer()):-1;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> SmitheryBlockEntity.this.progress = value;
                    case 1 -> SmitheryBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 3;
            }
        };

        //recipeChanged(state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt,inventory);
        nbt.putInt("progress",progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        clear();
        Inventories.readNbt(nbt,inventory);
        progress = nbt.getInt("progress");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container."+Geomancy.MOD_ID+".smithery_block");
    }

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SmitheryScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(!initialized) initialize(world,pos,state);

        SmitheryRecipeI prevRecipe = currentRecipe;
        this.currentRecipe=getRecipe();
        ItemStack previousResult = currentResult;
        if(this.hasRecipe()){
            currentResult = currentRecipe.getPreviewOutput(inputInventory());
            if(hasCraftingFinished()){
                this.craftItem();
                this.resetProgress();
                markDirty();
            }
        }
        else
            currentResult = ItemStack.EMPTY;

        if(prevRecipe!=currentRecipe){
            recipeChanged();
        }

        if(!Toolbox.itemStacksAreEqual(previousResult, currentResult)){
            recipeChanged();
        }

        if(automatedHitCooldown>0)automatedHitCooldown--;
    }

    private void initialize(World world, BlockPos pos, BlockState state){
        recipeChanged();
        initialized=true;
    }
    private void recipeChanged(){
        if(world==null) return;

        this.currentRecipe=getRecipe();
        this.resetProgress();
        this.maxProgress=currentRecipe!=null?this.currentRecipe.getProgressRequired(inputInventory()):10000;
        setOutput(currentRecipe!=null?this.currentRecipe.getPreviewOutput(inputInventory()):ItemStack.EMPTY);

    }

    private void resetProgress() {
        this.progress=0;
    }

    private void craftItem() {
        SmitheryRecipeI recipe = getRecipe();
        if (recipe != null) {

            List<ItemStack> results = craft(recipe, inventory, world);
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
        return progress>=maxProgress;
    }

    private boolean hasRecipe() {
        return currentRecipe != null;
    }

    private SmitheryRecipeI getRecipe(){
        return getRecipeFor(world,getItems());

    }

    private void increaseCraftProgress(int amount) {
        progress+=amount;
    }

    public SmitheryRecipeI getRecipeFor(@NotNull World world, DefaultedList<ItemStack> inventory) {
        AUTO_INVENTORY.setInputInventory( inputInventory().getItems() );

        SmitheryRecipeI res = world.getRecipeManager().getFirstMatch(ModRecipeTypes.SMITHING, AUTO_INVENTORY, world).orElse(null);
        if(res==null) res = world.getRecipeManager().getFirstMatch(ModRecipeTypes.JEWELRY, AUTO_INVENTORY, world).orElse(null);
        if(res==null) res = world.getRecipeManager().getFirstMatch(ModRecipeTypes.GEODE, AUTO_INVENTORY, world).orElse(null);
        return res;
    }

    public ImplementedInventory inputInventory(){
        ImplementedInventory inputInventory = ImplementedInventory.ofSize(INPUT_SLOT_COUNT);
        for (int i = 0; i < INPUT_SLOT_COUNT; i++) {
            inputInventory.setStack(i,getStack(i));
        }
        return inputInventory;
    }

    public void setOutput(ItemStack stack){
        setStack(OUTPUT_SLOT,stack);
    }

    public List<ItemStack> craft(SmitheryRecipeI recipe, DefaultedList<ItemStack> inventory, World world) {
        AUTO_INVENTORY.setInputInventory(inventory);
        return recipe.getSmithingResult(AUTO_INVENTORY, true,false,getLastHammerStack(),getLastHammerer());
    }

    private static final AutoCraftingInventory AUTO_INVENTORY = new AutoCraftingInventory(SLOT_COUNT, 1);

    @Override
    public void onHitWithHammer(@Nullable PlayerEntity player, ItemStack hammer,float skill){
        if(world==null) return;
        if(player==null && automatedHitCooldown>0) return;

        HammerItem hammerItem = ((HammerItem)hammer.getItem());
        lastHammerer=player;
        lastHammerStack=hammer;

        if(player==null) automatedHitCooldown=hammerItem.getCooldown(null);

        // skill check
        if(skillcheckPassed(skill,hammer,player)){
            // success
            increaseCraftProgress(hammerItem.getHitProgress(player));

            if(!world.isClient)
            {
                Vec3d particlePos = Vec3d.ofCenter(pos);
                if(!hasCraftingFinished())
                {
                    world.playSound(null, pos, ModSoundEvents.USE_HAMMER, SoundCategory.NEUTRAL, 0.7F, 0.9F + world.getRandom().nextFloat() * 0.2F);
                    // send progress particle packet
                    ParticleUtil.ParticleData.createSmithingProgress(world,particlePos).send();
                    CamShakeUtil.cause(world,getPos().toCenterPos(),10,0.5f);
                }
                else{
                    // send finish particle packet
                    ParticleUtil.ParticleData.createSmithingComplete(world,particlePos).send();
                    CamShakeUtil.cause(world,getPos().toCenterPos(),10,0.5f);
                }
            }
        }
        else{
            // failure
            {
                boolean playFailSound = true;

                // mishaps

                int[] mishapTypeWeights = {10,1,10,player!=null?10:0,5};
                int mishapType = Toolbox.selectWeightedRandomIndex(mishapTypeWeights);

                switch(mishapType){
                    // drop ingredient
                    case 0:
                    {
                        int slotID = getMishapInputItemSlotIndex();
                        ItemStack stackToDrop = getStack(slotID);
                        setStack(slotID,ItemStack.EMPTY);
                        Vec3d spos = Vec3d.ofCenter(pos).add(0,0.6f,0);
                        ItemScatterer.spawn(world,spos.x,spos.y,spos.z,stackToDrop);
                        CamShakeUtil.cause(world,getPos().toCenterPos(),10,0.5f);

                        break;
                    }

                    // break ingredient
                    case 1:
                    {
                        int slotID = getMishapInputItemSlotIndex();
                        setStack(slotID,ItemStack.EMPTY);
                        if(!world.isClient)
                        {
                            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 2F, 0.5F + world.getRandom().nextFloat() * 0.2F);
                            CamShakeUtil.cause(world,getPos().toCenterPos(),10,0.5f);
                        }

                        if(player!=null)
                            player.sendMessage(Text.translatable("message."+Geomancy.MOD_ID+".smithery_block.json.fail.break"), false);

                        break;
                    }

                    // remove progress
                    case 2:
                        increaseCraftProgress(-hammerItem.getHitProgress(player));
                        break;
                    // Hammer slip
                    case 3:
                        if(player==null) break;
                        player.dropItem(hammer.copy(),true,true);
                        player.getMainHandStack().setCount(0);
                        playFailSound = false;
                        if(!world.isClient)
                            world.playSound(null, pos, ModSoundEvents.USE_HAMMER_SLIP, SoundCategory.NEUTRAL, 0.9F, 0.9F + world.getRandom().nextFloat() * 0.2F);

                        break;
                    // reset progress
                    case 4:
                        resetProgress();
                        break;
                    default:break;
                }

                if(playFailSound && !world.isClient)
                {
                    world.playSound(null, pos, ModSoundEvents.USE_HAMMER_FAIL, SoundCategory.NEUTRAL, 0.9F, 0.9F + world.getRandom().nextFloat() * 0.2F);
                    Vec3d particlePos = Vec3d.ofCenter(pos);
                    // send finish particle packet
                    ParticleUtil.ParticleData.createSmithingFailure(world,particlePos).send();
                    CamShakeUtil.cause(world,getPos().toCenterPos(),10,0.5f);
                }
            }

            markDirty();
        }


    }

    public int getMishapInputItemSlotIndex(){

        if(currentRecipe==null) return -1;

        var inputs = inputInventory();
        HashMap<Integer,Integer> weights = new HashMap<>();

        for (int i = 0; i < inputs.size(); i++) {
            for(SmithingIngredient ing : currentRecipe.getSmithingIngredients(inputs)){
                if(!ing.test(inputs.getStack(i))) continue;
                weights.put(i,ing.mishapWeight);
                break;
            }
        }

        return Toolbox.selectWeightedRandomIndex(weights,-1);
    }

    public boolean skillcheckPassed(float skill, ItemStack hammer, LivingEntity hammerer){

        if(currentRecipe==null) return false;

        float fraction = skill / currentRecipe.getDifficulty(inputInventory(),hammer,hammerer);
        float random = mishapRandom.nextFloat();

        return fraction > random;
    }

    public ItemStack getBaseStack() {
        if(currentRecipe==null || !currentRecipe.hasBaseStack()) return ItemStack.EMPTY;
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
    public ItemStack getLastHammerStack(){
        return lastHammerStack;
    }

    @Override
    public PlayerEntity getLastHammerer(){
        return lastHammerer;
    }

    @Override
    public boolean isHammerable() {
        return currentRecipe!=null;
    }
}