package org.oxytocina.geomancy.items.tools;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.*;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoulBoreItem extends StorageItem implements ISoulStoringItem, ICustomRarityItem {

    public static final HashMap<Item,Float> fuelItems = new HashMap<>();
    public static void registerFuel(Item item, float fuel){
        fuelItems.put(item,fuel);
    }
    public static float getFuel(Item item){
        if(isFuel(item)) return fuelItems.get(item);
        return 0;
    }
    public static boolean isFuel(Item item){
        return fuelItems.containsKey(item);
    }
    public static void register(){}
    public static final float INGOT_FUEL_VALUE = 10000f;
    static{
        registerFuel(ModItems.OCTANGULITE_INGOT, INGOT_FUEL_VALUE);
        registerFuel(ModItems.RAW_OCTANGULITE, INGOT_FUEL_VALUE *0.9f);
        registerFuel(ModItems.OCTANGULITE_NUGGET, INGOT_FUEL_VALUE /9f);
        registerFuel(ModBlocks.OCTANGULITE_BLOCK.asItem(), INGOT_FUEL_VALUE *9f);
        registerFuel(ModBlocks.RAW_OCTANGULITE_BLOCK.asItem(), INGOT_FUEL_VALUE *9f);
    }

    @Override
    public int depletionPriority(ItemStack stack) {
        return 5;
    }

    public SoulBoreItem(Settings settings, int storageSize) {
        super(settings, storageSize,ModItemTags.FITS_IN_SOUL_BORE,false);
    }

    @Override
    public float getMana(World world, ItemStack stack) {
        float res = getLeftoverMana(stack);
        var fuels = getContainedSoulFuelItems(stack);
        for(var fuel : fuels)
            res+=getFuel(fuel.getItem())*fuel.getCount();
        return res;
    }

    public float getLeftoverMana(ItemStack stack){
        var nbt = stack.getOrCreateNbt();
        if(!nbt.contains("leftover")) return 0;
        return nbt.getFloat("leftover");
    }

    public float getLeftoverManaCap(ItemStack stack){
        var nbt = stack.getOrCreateNbt();
        if(!nbt.contains("leftoverCap")) return 0;
        return nbt.getFloat("leftoverCap");
    }

    public void setLeftoverMana(ItemStack stack, float amount){
        stack.getOrCreateNbt().putFloat("leftover",amount);
    }

    public void setLeftoverManaCap(ItemStack stack, float amount){
        stack.getOrCreateNbt().putFloat("leftoverCap",amount);
    }

    public void setFuelStackInUse(ItemStack stack, ItemStack fuel){
        stack.getOrCreateNbt().putString("fuel", Registries.ITEM.getId(fuel.getItem()).toString());
    }

    public ItemStack getFuelStackInUse(ItemStack stack){
        Identifier id = Identifier.tryParse(stack.getOrCreateNbt().getString("fuel"));
        if(id==null) return null;
        Item item = Registries.ITEM.get(id);
        return new ItemStack(item);
    }

    @Override
    public void takeMana(World world, ItemStack stack, float amount, @Nullable SpellContext ctx) {
        float leftOvers = getLeftoverMana(stack);
        float taken = Math.min(leftOvers,amount);
        amount -= taken;
        if(amount<=0) { setLeftoverMana(stack,leftOvers-taken); return; }

        // consume fuel until satisfied
        var fuels = getContainedSoulFuelItems(stack);
        for (int i = 0; i < fuels.size(); i++) {
            if(amount<=0) break;
            var fuelStack = fuels.get(i);
            do
            {
                float newFuel = getFuel(fuelStack.getItem());
                setFuelStackInUse(stack,fuelStack);
                setLeftoverManaCap(stack,newFuel);
                leftOvers = newFuel;
                taken = Math.min(leftOvers,amount);
                leftOvers-=taken;
                amount-=taken;
                fuelStack.decrement(1);
                if(amount<=0) break;
            }
            while(!fuelStack.isEmpty());
        }

        onTakenFuel(world,ctx);
        setLeftoverMana(stack,leftOvers);
        markDirty(stack);
    }

    public void onTakenFuel(World world, @Nullable SpellContext ctx) {
        if(ctx==null) return;
        Toolbox.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,world,ctx.getOriginBlockPos(), ctx.getSoundCategory(),0.5f,Toolbox.randomPitch());
    }

    @Override
    public float getCapacity(World world, ItemStack stack) {
        return getMana(world,stack);
    }

    public ArrayList<ItemStack> getContainedSoulFuelItems(ItemStack stack){
        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getSize(stack); i++) {
            var cont = getStack(stack,i);
            if(cont.isEmpty()||!isFuel(cont.getItem())) continue;
            res.add(cont);
        }
        return res;
    }

    @Override
    public boolean autocollects() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("geomancy.soul_bore.tooltip").formatted(Formatting.GRAY));

        float manaTotal = getMana(world,stack);

        tooltip.add(Text.translatable("geomancy.soul_bore.tooltip.2",Text.literal(Toolbox.formatNumber(manaTotal))).formatted(Formatting.GRAY));

        var fuelStack = getFuelStackInUse(stack);
        if(!fuelStack.isEmpty())
            tooltip.add(Text.translatable("geomancy.soul_bore.tooltip.1",fuelStack.getName()).formatted(Formatting.GRAY));
    }

    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return 0;
    }

    @Override
    public Text getName(ItemStack stack) {
        return ((MutableText)colorizeName(stack,Text.translatable(this.getTranslationKey(stack))));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(this.getTranslationKey());
    }

    @Override
    public Rarity getRarity() {
        return Rarity.Octangulite;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ((ISoulStoringItem)stack.getItem()).getBarColor(stack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getItemBarStep(ItemStack stack) {
        if(MinecraftClient.getInstance()==null) return 0;
        float cap = getLeftoverManaCap(stack);
        if(cap<=0) return 0;
        float mana = getLeftoverMana(stack);
        return (int)(mana/cap*13);
    }
}
