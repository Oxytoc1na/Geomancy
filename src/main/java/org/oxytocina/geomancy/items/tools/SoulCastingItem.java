package org.oxytocina.geomancy.items.tools;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.client.screen.SpellstorerItemScreenHandler;
import org.oxytocina.geomancy.client.screen.SpellstorerScreenHandler;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.ICastingItem;
import org.oxytocina.geomancy.items.IManaStoringItem;
import org.oxytocina.geomancy.items.IScrollListenerItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SoulCastingItem extends Item implements IManaStoringItem, ICastingItem, IScrollListenerItem, ExtendedScreenHandlerFactory {

    public static final HashMap<ItemStack,DefaultedList<ItemStack>> inventories = new HashMap<>();
    public static final HashMap<ItemStack,Inventory> actualInventories = new HashMap<>();

    public int spellStorageSize = SpellstorerScreenHandler.STORAGE_DISPLAY_SLOTS;

    public SoulCastingItem(Settings settings, int spellStorageSize) {
        super(settings);
        this.spellStorageSize=spellStorageSize;

        // TODO
        this.spellStorageSize = SpellstorerScreenHandler.STORAGE_DISPLAY_SLOTS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {

            if(user.isSneaking())
            {
                // open spell storing interface
                if(user instanceof ServerPlayerEntity sp){
                    var stack = user.getStackInHand(hand);
                    sp.openHandledScreen((SoulCastingItem) stack.getItem());
                }
            }
            else{
                cast(user.getStackInHand(hand),user);

            }


            return TypedActionResult.consume(user.getStackInHand(hand));
        } else {

        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public void cast(ItemStack key, LivingEntity user){
        int index = getSelectedSpellIndex(key);
        ItemStack spellContainer = getStack(key,index);

        if(!(spellContainer.getItem() instanceof SpellStoringItem storer)) return;

        storer.cast(key,spellContainer,user);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        var selectedSpellIndex = getSelectedSpellIndex(stack);
        var spells = getCastableSpellItems(stack);
        if(!spells.isEmpty())
            selectedSpellIndex = selectedSpellIndex%spells.size();
        for (int i = 0; i < spells.size(); i++) {
            var spell = spells.get(i);
            var grid = SpellStoringItem.readGrid(spell);
            String selectedString = "  ";
            if(selectedSpellIndex == i) selectedString = "> ";
            tooltip.add(
                    Text.literal(selectedString).formatted(Formatting.DARK_AQUA).append(
                            grid==null?Text.translatable("geomancy.spellstorage.empty").formatted(Formatting.DARK_GRAY)
                                    :grid.name==""?Text.translatable("geomancy.spellstorage.unnamed").formatted(Formatting.GRAY)
                            : Text.literal(grid.name).formatted(Formatting.GRAY)));

        }
    }

    public ArrayList<ItemStack> getAllSpellItems(ItemStack stack){
        if(!(stack.getItem() instanceof  SoulCastingItem)) return null;

        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getSize(stack); i++) {
            var spell = getStack(stack,i);
            if(!(spell.getItem() instanceof SpellStoringItem)) continue;
            res.add(spell);
        }
        return res;
    }

    public ArrayList<ItemStack> getCastableSpellItems(ItemStack stack){
        if(!(stack.getItem() instanceof  SoulCastingItem)) return null;

        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getSize(stack); i++) {
            var spell = getStack(stack,i);
            if(!(spell.getItem() instanceof SpellStoringItem storer)) continue;
            var grid = SpellStoringItem.readGrid(spell);
            if(grid==null||grid.library) continue;
            res.add(spell);
        }
        return res;
    }

    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return 0;
    }

    @Override
    public DefaultedList<ItemStack> readInventoryFromNbt(ItemStack stack) {
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(spellStorageSize,ItemStack.EMPTY);
        Inventories.readNbt(stack.getOrCreateNbt(),stacks);
        return stacks;
    }

    @Override
    public void saveInventoryToNbt(ItemStack stack) {
        stack.getOrCreateNbt().remove("Items");
        Inventories.writeNbt(stack.getOrCreateNbt(),getItems(stack));
        clearCache(stack);
    }

    public void clearCache(ItemStack stack){
        inventories.remove(stack);
        actualInventories.remove(stack);
    }

    public void setInventory(ItemStack stack, NbtCompound nbt){
        clearCache(stack);
        stack.setSubNbt("Items",nbt.getList("Items",NbtElement.COMPOUND_TYPE));
        clearCache(stack);
    }

    public int getSelectedSpellIndex(ItemStack stack){
        if(!stack.getOrCreateNbt().contains("selected", NbtElement.INT_TYPE)) return 0;
        int res = stack.getNbt().getInt("selected");
        int installed = getInstalledSpellsCount(stack);
        if(installed<=0)return 0;
        res = ((res%installed)+installed)%installed;
        return res;
    }

    public void setSelectedSpellIndex(ItemStack stack,int index){
        int installed = getInstalledSpellsCount(stack);
        if(installed<=0)index=0;
        else index = ((index%installed)+installed)%installed;
        stack.getOrCreateNbt().putInt("selected",index);
    }

    public int getInstalledSpellsCount(ItemStack stack){
        return getCastableSpellItems(stack).size();
    }

    public static SpellGrid getSpell(ItemStack casterItem,String name){
        if(!(casterItem.getItem() instanceof  SoulCastingItem caster)) return null;

        for (int i = 0; i < caster.getSize(casterItem); i++) {
            var contender = caster.getStack(casterItem,i);
            if(!(contender.getItem() instanceof SpellStoringItem storer)) continue;
            var grid = SpellStoringItem.readGrid(contender);
            if(grid==null) continue;
            if(Objects.equals(grid.name, name)) return grid;
        }

        return null;
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

    public static Inventory getInventory(ItemStack stack){
        if(actualInventories.containsKey(stack)) return actualInventories.get(stack);

        // generate and cache inventory
        Inventory inv = ImplementedInventory.of(((SoulCastingItem) stack.getItem()).getItems(stack));
        actualInventories.put(stack,inv);

        return inv;
    }

    public boolean insertSpellStorage(ItemStack key, ItemStack storage) {
        // TODO
        setStack(key,0,storage);
        return true;
    }

    /**
     * Returns the inventory size.
     *
     * <p>The default implementation returns the size of {@link #getItems(ItemStack)}.
     *
     * @return the inventory size
     */
    public int size(ItemStack key) {
        return getItems(key).size();
    }

    /**
     * @return true if this inventory has only empty stacks, false otherwise
     */
    public boolean isEmpty(ItemStack key) {
        for (int i = 0; i < size(key); i++) {
            ItemStack stack = getStack(key,i);
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the item in the slot.
     *
     * @param slot the slot
     * @return the item in the slot
     */
    public ItemStack getStack(ItemStack key,int slot) {
        return getItems(key).get(slot);
    }

    /**
     * Takes a stack of the size from the slot.
     *
     * <p>(default implementation) If there are less items in the slot than what are requested,
     * takes all items in that slot.
     *
     * @param slot the slot
     * @param count the item count
     * @return a stack
     */
    public ItemStack removeStack(ItemStack key,int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(key), slot, count);
        if (!result.isEmpty()) {
            markDirty(key);
        }

        return result;
    }

    /**
     * Removes the current stack in the {@code slot} and returns it.
     *
     * <p>The default implementation uses {@link Inventories#removeStack(List, int)}
     *
     * @param slot the slot
     * @return the removed stack
     */
    public ItemStack removeStack(ItemStack key,int slot) {
        return Inventories.removeStack(getItems(key), slot);
    }

    /**
     * Replaces the current stack in the {@code slot} with the provided stack.
     *
     * <p>If the stack is too big for this inventory ({@link Inventory#getMaxCountPerStack()}),
     * it gets resized to this inventory's maximum amount.
     *
     * @param slot the slot
     * @param stack the stack
     */
    public void setStack(ItemStack key,int slot, ItemStack stack) {
        getItems(key).set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack(key)) {
            stack.setCount(getMaxCountPerStack(key));
        }
        markDirty(key);
    }

    private int getMaxCountPerStack(ItemStack key) {
        return 1;
    }

    /**
     * Clears {@linkplain #getItems(ItemStack) the item list}}.
     */
    public void clear(ItemStack key) {
        getItems(key).clear();
    }

    public void markDirty(ItemStack key) {
        // Override if you want behavior.
        saveInventoryToNbt(key);
    }


    @Override
    public Text getName(ItemStack stack) {
        var spells = getCastableSpellItems(stack);
        MutableText spellText = null;
        if(spells.isEmpty()){
            spellText = Text.translatable("geomancy.caster.nospells").formatted(Formatting.RED);
        }
        else{
            var nextIndex=getSelectedSpellIndex(stack);
            var spellItem = spells.get(nextIndex);
            var grid = SpellStoringItem.readGrid(spellItem);
            MutableText indexText = Text.literal((nextIndex+1)+"/"+spells.size()+"/"+spellStorageSize+": ").formatted(Formatting.GRAY);
            if(grid==null)
            {
                spellText = indexText.append(Text.translatable("geomancy.spellstorage.empty").formatted(Formatting.GRAY));
            }
            else{
                spellText = indexText.append(grid.getName().formatted(Formatting.DARK_AQUA));
            }
        }

        return Text.translatable(this.getTranslationKey(stack)).append(Text.literal(" [").append(
                spellText
                ).append("]").formatted(Formatting.GRAY));
    }


    @Override
    public boolean onScrolled(ItemStack stack, float delta,PlayerEntity player) {
        if(!player.isSneaking()) return false;

        int dir = -Toolbox.sign(delta);

        int nextIndex = getSelectedSpellIndex(stack)+dir;
        setSelectedSpellIndex(stack,nextIndex);
        nextIndex=getSelectedSpellIndex(stack);

        displaySelectedSpell(stack,player,nextIndex);

        // send packet to server
        PacketByteBuf data = PacketByteBufs.create();

        data.writeItemStack(stack);
        data.writeInt(player.getInventory().indexOf(stack));
        data.writeInt(nextIndex);
        ClientPlayNetworking.send(ModMessages.CASTER_CHANGE_SELECTED_SPELL, data);

        return true;
    }

    public void displaySelectedSpell(ItemStack stack, PlayerEntity player, int index){
        // display selected spell
        var spells = getCastableSpellItems(stack);
        if(spells.isEmpty()){
            player.sendMessage(Text.translatable("geomancy.caster.nospells").formatted(Formatting.RED),true);
        }
        else{
            var spellItem = spells.get(index);
            var grid = SpellStoringItem.readGrid(spellItem);
            MutableText indexText = Text.literal((index+1)+"/"+spells.size()+"/"+spellStorageSize+": ").formatted(Formatting.GRAY);
            if(grid==null)
            {
                player.sendMessage(indexText.append(Text.translatable("geomancy.spellstorage.empty").formatted(Formatting.GRAY)),true);
            }
            else{
                player.sendMessage(indexText.append(grid.getName().formatted(Formatting.DARK_AQUA)),true);
            }
        }
    }

    // display selected spell if sneaking
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if(selected && entity instanceof PlayerEntity player && player.isSneaking())
            displaySelectedSpell(stack,player,getSelectedSpellIndex(stack));
    }

    @Override
    public boolean shouldBlockScrolling(ItemStack stack, PlayerEntity player) {
        return player.isSneaking();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.geomancy.spellstorer_block");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        var stack = player.getStackInHand(player.getActiveHand());
        if(!(stack.getItem() instanceof SoulCastingItem sci)) return null;
        return new SpellstorerItemScreenHandler(syncId,playerInventory,stack,new PropertyDelegate() {
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
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        var stack = serverPlayerEntity.getStackInHand(serverPlayerEntity.getActiveHand());
        packetByteBuf.writeInt(serverPlayerEntity.getInventory().getSlotWithStack(stack));
    }
}
