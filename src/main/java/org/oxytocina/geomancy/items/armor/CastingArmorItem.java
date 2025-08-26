package org.oxytocina.geomancy.items.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.registries.ModModelLayers;
import org.oxytocina.geomancy.client.rendering.armor.OctanguliteArmorModel;
import org.oxytocina.geomancy.client.screen.StorageItemScreenHandler;
import org.oxytocina.geomancy.items.*;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.items.tools.StorageItem;
import org.oxytocina.geomancy.spells.SpellBlockArgs;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.spells.SpellSignal;

import java.util.ArrayList;
import java.util.List;

import static org.oxytocina.geomancy.items.tools.StorageItem.getInventoryStatic;

public class CastingArmorItem extends ArmorItem implements IMaddeningItem, IStorageItem, ExtendedScreenHandlerFactory, IManaStoringItem, ICustomRarityItem, IListenerArmor {
    @Environment(EnvType.CLIENT)
    private BipedEntityModel<LivingEntity> model;

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
        var spells = getCastableSpellItems(key);
        if(spells.isEmpty()) return;
        ItemStack spellContainer = spells.get(index);

        if(!(spellContainer.getItem() instanceof SpellStoringItem storer)) return;

        storer.cast(key,spellContainer,user,args, SpellContext.SoundBehavior.Reduced);
    }

    public int getSelectedSpellIndex(ItemStack stack){
        return 0;
    }

    public int getInstalledSpellsCount(ItemStack stack){
        return getCastableSpellItems(stack).size();
    }

    public ArrayList<ItemStack> getCastableSpellItems(ItemStack stack){
        if(!(stack.getItem() instanceof CastingArmorItem)) return null;

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
        if(stack.getItem()!=this) return null;
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

    @Override
    public void onMessageSent(ItemStack armorItemStack, ServerPlayerEntity spe, String message) {
        if(type==Type.BOOTS)
        {
            var args = new SpellBlockArgs();
            args.vars.put("message", SpellSignal.createText(message));
            cast(armorItemStack,spe,args);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        var spells = getCastableSpellItems(stack);
        if(!spells.isEmpty())
        {
            var spell = spells.get(0);
            var grid = SpellStoringItem.readGrid(spell);
            tooltip.add(getTriggerText().formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.translatable("geomancy.caster.willcast",SpellGrid.getName(grid)).formatted(Formatting.DARK_GRAY));
        }
        else{
            tooltip.add(Text.translatable("geomancy.caster.emptyhint1").formatted(Formatting.DARK_GRAY));
            tooltip.add(getTriggerText().formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.translatable("geomancy.caster.emptyhint4").formatted(Formatting.DARK_GRAY));
        }

    }

    public MutableText getTriggerText(){
        return Text.translatable("geomancy.caster.trigger."+switch(type){
            case BOOTS -> "chat";
            case LEGGINGS -> "jump";
            case CHESTPLATE -> "gethit";
            case HELMET -> "hit";
            default->"ERROR";
        });
    }

    @Environment(EnvType.CLIENT)
    public BipedEntityModel<LivingEntity> getArmorModel() {
        if (model == null) {
            model = provideArmorModelForSlot(getSlotType());
        }
        return model;
    }

    // this takes the "unused" stack, so addons can mixin into it
    public RenderLayer getRenderLayer(ItemStack stack) {
        return RenderLayer.getEntitySolid(ModModelLayers.CASTING_ARMOR_MAIN_ID);
    }

    @NotNull
    public Identifier getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        return Geomancy.locate("textures/armor/casting_armor_main.png");
    }

    @Environment(EnvType.CLIENT)
    protected BipedEntityModel<LivingEntity> provideArmorModelForSlot(EquipmentSlot slot) {
        var models = MinecraftClient.getInstance().getEntityModelLoader();
        var root = models.getModelPart(ModModelLayers.MAIN_CASTING_LAYER);
        return new OctanguliteArmorModel(root, slot);
    }
}
