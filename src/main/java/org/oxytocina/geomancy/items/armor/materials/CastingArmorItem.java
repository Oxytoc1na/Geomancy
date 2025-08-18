package org.oxytocina.geomancy.items.armor.materials;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.client.screen.StorageItemScreenHandler;
import org.oxytocina.geomancy.items.*;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.items.tools.StorageItem;
import org.oxytocina.geomancy.spells.SpellBlockArgs;
import org.oxytocina.geomancy.spells.SpellSignal;

import java.util.ArrayList;

import static org.oxytocina.geomancy.items.tools.StorageItem.getInventoryStatic;

public class CastingArmorItem extends ArmorItem implements IMaddeningItem, IStorageItem, ExtendedScreenHandlerFactory, IManaStoringItem, ICustomRarityItem, IListenerArmor {

    private final float maddeningSpeed;

    public int storageSize = StorageItemScreenHandler.STORAGE_DISPLAY_SLOTS;
    public final TagKey<Item> storableTag;
    public final boolean showContentsInTooltip;

    public CastingArmorItem(ArmorMaterial material, Type type, Settings settings, float maddeningSpeed, int storageSize, TagKey<Item> storableTag, boolean showContentsInTooltip) {
        super(material, type, settings);
        this.maddeningSpeed = maddeningSpeed;

        this.storageSize = storageSize;
        this.storableTag=storableTag;
        this.showContentsInTooltip=showContentsInTooltip;

        // TODO
        this.storageSize = StorageItemScreenHandler.STORAGE_DISPLAY_SLOTS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.isSneaking()){
            if(user instanceof ServerPlayerEntity sp){
                var stack = user.getStackInHand(hand);
                sp.openHandledScreen((CastingArmorItem) stack.getItem());
            }
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }

    public void cast(ItemStack key, LivingEntity user, SpellBlockArgs args){
        int index = getSelectedSpellIndex(key);
        ItemStack spellContainer = getStack(key,index);

        if(!(spellContainer.getItem() instanceof SpellStoringItem storer)) return;

        storer.cast(key,spellContainer,user,args);
    }

    public int getSelectedSpellIndex(ItemStack stack){
        if(!stack.getOrCreateNbt().contains("selected", NbtElement.INT_TYPE)) return 0;
        int res = stack.getNbt().getInt("selected");
        int installed = getInstalledSpellsCount(stack);
        if(installed<=0)return 0;
        res = ((res%installed)+installed)%installed;
        return res;
    }

    public int getInstalledSpellsCount(ItemStack stack){
        return getCastableSpellItems(stack).size();
    }

    public ArrayList<ItemStack> getCastableSpellItems(ItemStack stack){
        if(!(stack.getItem() instanceof SoulCastingItem)) return null;

        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getStorageSize(stack); i++) {
            var spell = getStack(stack,i);
            if(!(spell.getItem() instanceof SpellStoringItem storer)) continue;
            var grid = SpellStoringItem.readGrid(spell);
            if(grid==null||grid.library) continue;
            res.add(spell);
        }
        return res;
    }

    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
    }

    @Override
    public float getWornMaddeningSpeed() {
        return maddeningSpeed*2;
    }

    @Override
    public Text getName(ItemStack stack) {
        return colorizeName(stack,super.getName(stack));
    }

    @Override
    public Rarity getRarity() {
        return Rarity.Octangulite;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        var stack = serverPlayerEntity.getStackInHand(serverPlayerEntity.getActiveHand());
        packetByteBuf.writeInt(serverPlayerEntity.getInventory().getSlotWithStack(stack));
        packetByteBuf.writeIdentifier(getStorableTag().id());
    }

    @Override
    public Text getDisplayName() {
        return Text.empty();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        var stack = player.getStackInHand(player.getActiveHand());
        if(!(stack.getItem() instanceof StorageItem sci)) return null;
        return new StorageItemScreenHandler(syncId,playerInventory,stack,getStorableTag(),new PropertyDelegate() {
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
        });
    }

    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return 0;
    }

    @Override
    public DefaultedList<ItemStack> readInventoryFromNbt(ItemStack stack) {
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(storageSize,ItemStack.EMPTY);
        Inventories.readNbt(stack.getOrCreateNbt(),stacks);
        return stacks;
    }

    @Override
    public void saveInventoryToNbt(ItemStack stack) {
        stack.getOrCreateNbt().remove("Items");
        Inventories.writeNbt(stack.getOrCreateNbt(),getItems(stack));
        clearCache(stack);
    }

    @Override
    public int getStorageSize(ItemStack stack) {
        return getSize(stack);
    }

    public ItemStack getStack(ItemStack key,int slot) {
        return getItems(key).get(slot);
    }

    public void clearCache(ItemStack stack){
        inventories.remove(stack);
        actualInventories.remove(stack);
    }

    @Override
    public void setInventory(ItemStack stack, NbtCompound nbt){
        clearCache(stack);
        stack.setSubNbt("Items",nbt.getList("Items", NbtElement.COMPOUND_TYPE));
        clearCache(stack);
    }

    // Inventory

    public int getSize(ItemStack stack){
        return getItems(stack).size();
    }

    @Override
    public DefaultedList<ItemStack> getItems(ItemStack stack) {
        if(inventories.containsKey(stack)) return inventories.get(stack);

        // generate and cache inventory
        DefaultedList<ItemStack> inv = readInventoryFromNbt(stack);
        inventories.put(stack,inv);

        return inv;
    }

    @Override
    public Inventory getInventory(ItemStack stack) {
        return getInventoryStatic(stack);
    }

    @Override
    public void setStack(ItemStack key,int slot, ItemStack stack) {
        getItems(key).set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack(key)) {
            stack.setCount(getMaxCountPerStack(key));
        }
        markDirty(key);
    }

    protected int getMaxCountPerStack(ItemStack key) {
        return 64;
    }


    @Override
    public TagKey<Item> getStorableTag() {
        return storableTag;
    }

    @Override
    public boolean autocollects() {
        return false;
    }

    @Override
    public void onGotHit(ItemStack stack, LivingEntity wearer, DamageSource source, float amount) {
        if(type==Type.CHESTPLATE)
        {
            var attacker = source.getAttacker();
            if(attacker instanceof LivingEntity le)
            {
                var args = new SpellBlockArgs();
                args.vars.put("attacker", SpellSignal.createUUID(le.getUuid()));
                cast(stack,wearer,args);
            }
        }
    }

    @Override
    public void onJump(ItemStack stack, LivingEntity wearer) {
        if(type==Type.LEGGINGS){
            cast(stack,wearer,SpellBlockArgs.empty());
        }
    }

    @Override
    public void onHit(ItemStack stack, LivingEntity wearer, LivingEntity target) {
        if(type==Type.HELMET)
        {
            var args = new SpellBlockArgs();
            args.vars.put("target", SpellSignal.createUUID(target.getUuid()));
            cast(stack,wearer,args);
        }
    }
}
