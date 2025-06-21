package org.oxytocina.geomancy.blocks.blockEntities;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.MultiblockCrafter;
import org.oxytocina.geomancy.inventories.AutoCraftingInventory;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.recipe.SmitheryRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.sound.ModSoundEvents;

import java.util.Collections;

public class SmitheryBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SLOT_COUNT,ItemStack.EMPTY);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int SLOT_COUNT = 9;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 5;

    public SmitheryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMITHERY_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> SmitheryBlockEntity.this.progress;
                    case 1 -> SmitheryBlockEntity.this.maxProgress;
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
                return 2;
            }
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt,inventory);
        nbt.putInt("smithery.progress",progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt,inventory);
        progress = nbt.getInt("smithery.progress");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("AAAA");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;


    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(world.isClient) return;


        if(isOutputSlotEmptyOrReceivable()){
            if(this.hasRecipe()){

                this.increaseCraftProgress();
                markDirty(world,pos,state);

                if(hasCraftingFinished()){
                    this.craftItem();
                    this.resetProgress();
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            markDirty(world,pos,state);
        }
    }

    private void resetProgress() {
        this.progress=0;
    }

    private void craftItem() {
        Geomancy.logInfo("craftItem");

        SmitheryRecipe recipe = getRecipe();
        if (recipe != null) {
            world.playSound(null, pos, ModSoundEvents.USE_HAMMER, SoundCategory.NEUTRAL, 1.0F, 0.9F + world.getRandom().nextFloat() * 0.2F);

            ItemStack result = craft(recipe, inventory, world);
            int count = result.getCount();
            result.setCount(count);

            MultiblockCrafter.spawnItemStackAsEntitySplitViaMaxCount(world, Vec3d.ofCenter(pos).add(new Vec3d(0,1,0)), result, count, new Vec3d(0,0.5f,0), false, null);
        }
    }

    private boolean hasCraftingFinished() {
        return progress>=maxProgress;
    }

    private boolean hasRecipe() {
        return getRecipe() != null;
    }

    private SmitheryRecipe getRecipe(){
        return getRecipeFor(world,inventory);

    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return true;
        //return this.getStack(OUTPUT_SLOT).isEmpty() ||
        //        this.getStack((OUTPUT_SLOT)).getCount() < this.getStack((OUTPUT_SLOT)).getMaxCount();
    }

    public SmitheryRecipe getRecipeFor(@NotNull World world, DefaultedList<ItemStack> inventory) {
        AUTO_INVENTORY.setInputInventory(inventory);
        return world.getRecipeManager().getFirstMatch(ModRecipeTypes.SMITHING, AUTO_INVENTORY, world).orElse(null);
    }

    public ItemStack craft(SmitheryRecipe recipe, DefaultedList<ItemStack> inventory, World world) {
        AUTO_INVENTORY.setInputInventory(inventory);
        return recipe.craft(AUTO_INVENTORY, world.getRegistryManager());
    }

    private static final AutoCraftingInventory AUTO_INVENTORY = new AutoCraftingInventory(SLOT_COUNT, 1);

}