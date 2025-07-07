package org.oxytocina.geomancy.items.jewelry;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.items.ExtraItemSettings;
import org.oxytocina.geomancy.items.ModItems;

import java.lang.reflect.Array;
import java.util.*;

public class JewelryItem extends TrinketItem implements DyeableItem {

    public static final ArrayList<JewelryItem> List = new ArrayList<>();

    public final int gemSlotCount;
    private final JewelryItemSettings jewelrySettings;

    public JewelryItem(Settings settings, JewelryItemSettings jewelryItemSettings) {
        super(settings);
        this.gemSlotCount=jewelryItemSettings.gemSlotCount;
        List.add(this);

        switch(jewelryItemSettings.slot){
            case ANY:ModItems.JewelryAnySlotItems.add(this);break;
            case RING:ModItems.JewelryRingItems.add(this);break;
            case NECKLACE:ModItems.JewelryNecklaceItems.add(this);break;
            default:break;
        }

        this.jewelrySettings=jewelryItemSettings;
    }

    public static ArrayList<GemSlot> getSlots(ItemStack stack) {
        NbtList nbt = stack.getOrCreateNbt().getList("gems",NbtList.COMPOUND_TYPE);
        ArrayList<GemSlot> res = new ArrayList<>();
        for (int i = 0; i < nbt.size(); i++) {
            var slotNbt = nbt.getCompound(i);
            res.add(GemSlot.fromNbt(slotNbt));
        }
        return res;
    }

    public static void setSlots(ItemStack stack, ArrayList<GemSlot> slots){
        NbtList nbt = new NbtList();
        for(GemSlot slot : slots){
            nbt.add(GemSlot.toNbt(slot));
        }
        stack.setSubNbt("gems",nbt);
    }

    @Override
    public int getColor(ItemStack stack) {
        return getColor(stack,0);
    }

    public int getColor(ItemStack stack, int tintIndex){
        tintIndex--;
        if(tintIndex < 0 || tintIndex >= gemSlotCount) return getBaseColor(stack);

        var slots = getSlots(stack);
        return slots.size()>tintIndex?slots.get(tintIndex).getColor():getEmptyColor(tintIndex);
    }

    public int getBaseColor(ItemStack stack){

        if(Registries.ITEM.getId(stack.getItem()).getPath().contains("octangulite"))
            return ModColorizationHandler.octanguliteItemNoise(stack,0,0.03f,true);

        return 0xFFFFFFFF;
    }

    public int getEmptyColor(int tintIndex){
        return 0xFFFFFFFF;
    }

    public float getHasGemPredicate(ItemStack stack){
        return  getSlots(stack).size() / (float)gemSlotCount;
    }



    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var gems = getSlots(stack);
        for(var gem : gems)
            GemSlot.tick(stack,gem,slot,entity);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var gems = getSlots(stack);
        for(var gem : gems)
            GemSlot.equip(stack,gem,slot,entity);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var gems = getSlots(stack);
        for(var gem : gems)
            GemSlot.unequip(stack,gem,slot,entity);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);

        var gems = getSlots(stack);
        for(var gem : gems)
            modifiers = GemSlot.modifyModifiers(stack,gem,slot,entity,uuid,modifiers);

        return modifiers;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> list, TooltipContext context) {

        if(isPendant())
        {
            list.add(Text.translatable("tooltip.geomancy.jewelry.pendant1").formatted(Formatting.DARK_GRAY));
            list.add(Text.translatable("tooltip.geomancy.jewelry.pendant2").formatted(Formatting.DARK_GRAY));
        }

        var gems = getSlots(stack);
        boolean hasGems = !gems.isEmpty();
        LivingEntity wearer = MinecraftClient.getInstance().player;
        //LivingEntity wearer = stack.getHolder() instanceof LivingEntity l ? l : null;

        // consolidate tooltips
        // collect
        ArrayList<List<Text>> tooltips = new ArrayList<>();
        for(var gem : gems)
        {
            List<Text> texts = new ArrayList<>();
            GemSlot.appendTooltip(stack,gem,wearer,world,texts,context);
            if(!texts.isEmpty())
                tooltips.add(texts);
        }
        // sort in
        ArrayList<Pair<List<Text>,Integer>> textsCounted = new ArrayList<>();
        for(var l : tooltips){
            boolean sortedIn = false;
            for (int i = 0; i < textsCounted.size(); i++) {
                if(textsCounted.get(i).getLeft().stream().findFirst().equals(l.stream().findFirst()))
                {
                    textsCounted.get(i).setRight(textsCounted.get(i).getRight()+1);
                    sortedIn=true;
                    break;
                }
            }
            if(!sortedIn)
                textsCounted.add(new Pair<>(l,1));
        }

        // append
        for(var pair : textsCounted){
            int amount = pair.getRight();
            var texts = pair.getLeft();
            for(Text t : texts){
                list.add(Text.literal(amount>1?("x"+amount+" "):"").formatted(Formatting.YELLOW).append(t));
            }
        }

        if(!hasGems){
            list.add(Text.translatable("tooltip.geomancy.jewelry.nogems").formatted(Formatting.DARK_GRAY));
        }

        if(stack.hasNbt() && stack.getNbt().contains("preview",NbtElement.BYTE_TYPE) && stack.getNbt().getBoolean("preview"))
            list.add(Text.translatable("tooltip.geomancy.jewelry.unsmith").formatted(Formatting.DARK_GREEN));

        super.appendTooltip(stack, world, list, context);
    }

    public int getMishapWeight(){
        return jewelrySettings.baseMishapWeight;
    }

    public List<ItemStack> UnSmith(ItemStack stack, boolean preview){
        List<ItemStack> res = new ArrayList<>();
        ItemStack base = stack.copy();
        base.removeSubNbt("gems");

        if(preview) base.setSubNbt("preview",NbtByte.of(true));

        res.add(base);

        // add gems
        var gems = getSlots(stack);
        for(var gem : gems){
            res.add(new ItemStack(gem.gemItem));
        }

        return res;
    }

    public ItemStack addSlot(ItemStack stack, GemSlot slot){
        NbtList nbt = stack.getOrCreateNbt().getList("gems",NbtList.COMPOUND_TYPE);

        nbt.add(GemSlot.toNbt(slot));

        stack.setSubNbt("gems",nbt);
        return stack;
    }

    public static float getXPMultiplier(LivingEntity wearer){
        float res = 1;

        var wornJewelry = getAllWornJewelryItems(wearer);
        for(ItemStack jewelryItem : wornJewelry){
            res += getXPMultiplier(jewelryItem,wearer);
        }

        return res;
    }

    public static float getXPMultiplier(ItemStack stack,LivingEntity wearer){
        float res = 0;

        JewelryItem jewelryItem = (JewelryItem) stack.getItem();
        if(jewelryItem.isPendant()) return res;
        var gems = getSlots(stack);

        for(var gem : gems){
            res += gem.getXPMultiplier(stack,wearer);
        }

        return res;
    }

    public static List<ItemStack> getAllWornJewelryItems(LivingEntity wearer){
        List<ItemStack> res = new ArrayList<>();

        var trinkComp = TrinketsApi.getTrinketComponent(wearer);
        if(trinkComp.isPresent()){
            var pairs = trinkComp.get().getAllEquipped();
            for(var pair : pairs){
                ItemStack stack = pair.getRight();
                if(stack.getItem() instanceof JewelryItem){
                    res.add(stack);
                }
            }
        }

        return res;
    }

    public static float getGemQualityMultiplierFor(GemSlot gem,ItemStack onItem,LivingEntity wearer){
        float res = 1;

        if(isPendant(onItem)) return res;

        var wornJewelry = getAllWornJewelryItems(wearer);
        for(ItemStack jewelryItem : wornJewelry){
            if(isPendant(jewelryItem)){
                res += getGemQualityMultiplier(gem,jewelryItem,wearer);
            }
        }

        return res;
    }

    public static float getGemQualityMultiplier(GemSlot refgem,ItemStack stack,LivingEntity wearer){
        float res = 0;

        var gems = getSlots(stack);

        for(var gem : gems){
            if(gem.gemItem == refgem.gemItem)
                res += gem.getEffectiveQuality(stack,wearer);
        }

        return res;
    }

    public boolean isPendant(){
        return jewelrySettings.pendant;
    }

    public static boolean isPendant(ItemStack stack){
        return stack.getItem() instanceof JewelryItem j && j.isPendant();
    }

    public static float getFortuneBonus(ItemStack jewelryItem,LivingEntity wearer){
        if(wearer==null) return 0;
        float res = 0;

        if(!(jewelryItem.getItem() instanceof JewelryItem ji) || ji.isPendant()) return res;
        var gsls = getSlots(jewelryItem);
        for(var gsl : gsls)
        {
            if(gsl.gemItem == Items.EMERALD){
                res+=gsl.getEffectiveQuality(jewelryItem,wearer);
            }
        }

        return res;
    }

    public static float getFortuneBonus(LivingEntity entity){
        float res = 0;

        var jis = getAllWornJewelryItems(entity);
        for(var ji : jis)
            res+=getFortuneBonus(ji,entity);

        return res;
    }

    public static void populateItemGroup(){
        // Register items to the custom item group.
        ItemGroupEvents.modifyEntriesEvent(ModItems.JEWELRY_ITEM_GROUP_KEY).register(itemGroup -> {
            for(JewelryItem i : List){

                // generate full jewelry for every gem type
                for(var gem : GemSlot.settingsMap.keySet())
                {
                    ItemStack fullJewelry = new ItemStack(i);
                    for (int j = 0; j < i.gemSlotCount; j++) {
                        i.addSlot(fullJewelry,new GemSlot(gem,1));
                    }
                    itemGroup.add(fullJewelry);
                }

            }
        });
    }
}
