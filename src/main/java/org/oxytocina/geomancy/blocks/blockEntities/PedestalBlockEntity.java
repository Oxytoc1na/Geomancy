package org.oxytocina.geomancy.blocks.blockEntities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
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
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
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
import org.oxytocina.geomancy.blocks.MultiblockCrafter;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.screen.SmitheryScreenHandler;
import org.oxytocina.geomancy.inventories.AutoCraftingInventory;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.tools.HammerItem;
import org.oxytocina.geomancy.networking.packet.S2C.SmitheryParticlesS2CPacket;
import org.oxytocina.geomancy.recipe.smithery.SmitheryRecipeI;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;
import java.util.List;

public class PedestalBlockEntity extends BlockEntity implements ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SLOT_COUNT,ItemStack.EMPTY);
    public static final int SLOT_COUNT = 1;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public ItemStack getItem(){return getStack(0);}
    public void setItem(ItemStack stack){setStack(0,stack);}

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

    public void tick(World world, BlockPos pos,BlockState state){
        initialize();
    }

    private boolean initized=false;
    public void initialize(){
        if(initized) return;
        initized=true;
        if(world!=null && !world.isClient)
            IPedestalListener.onPedestalCreated(this);

    }

    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // take, put or swap item
        var handStack = player.getStackInHand(hand);
        var ownStack = getItem();
        if(ownStack.isEmpty() && handStack.isEmpty()) return; // both empty
        if(ItemStack.areEqual(handStack,ownStack)) return; // both same
        if(ownStack.isEmpty()){
            setItem(handStack.copyAndEmpty());
            Toolbox.playSound(SoundEvents.ENTITY_ITEM_PICKUP,world,pos,SoundCategory.BLOCKS,0.3f,Toolbox.randomPitch());
            return; // put hand item into pedestal
        }
        if(handStack.isEmpty()){
            player.setStackInHand(hand,ownStack);
            setItem(ItemStack.EMPTY);
            Toolbox.playSound(SoundEvents.ENTITY_ITEM_PICKUP,world,pos,SoundCategory.BLOCKS,0.3f,Toolbox.randomPitch());
            return; // take item from pedestal
        }

        // swap items
        setItem(handStack);
        player.setStackInHand(hand,ownStack);
        Toolbox.playSound(SoundEvents.ENTITY_ITEM_PICKUP,world,pos,SoundCategory.BLOCKS,0.3f,Toolbox.randomPitch());
    }
}