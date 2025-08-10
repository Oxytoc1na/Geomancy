package org.oxytocina.geomancy.items.jewelry;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.items.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface IJewelryItem {

    public static final ArrayList<Item> List = new ArrayList<>();

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

    public int getGemSlotCount();
    public JewelryItemSettings getSettings();

    public default int getColor(ItemStack stack, int tintIndex){
        tintIndex--;
        if(tintIndex < 0 || tintIndex >= getGemSlotCount()) return getBaseColor(stack);

        var slots = getSlots(stack);
        return slots.size()>tintIndex?slots.get(tintIndex).getColor():getEmptyColor(tintIndex);
    }

    public default int getBaseColor(ItemStack stack){

        if(Registries.ITEM.getId(stack.getItem()).getPath().contains("octangulite"))
            return ModColorizationHandler.octanguliteItemNoise(stack,0,0.03f,true);

        return 0xFFFFFFFF;
    }

    public default int getEmptyColor(int tintIndex){
        return 0xFFFFFFFF;
    }

    public default float getHasGemPredicate(ItemStack stack){
        return  getSlots(stack).size() / (float)getGemSlotCount();
    }

    public default int getMishapWeight(){
        return getSettings().baseMishapWeight;
    }

    public default List<ItemStack> UnSmith(ItemStack stack, boolean preview){
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

    public default ItemStack addSlot(ItemStack stack, GemSlot slot){
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

        IJewelryItem jewelryItem = (IJewelryItem) stack.getItem();
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
                if(stack.getItem() instanceof IJewelryItem){
                    res.add(stack);
                }
            }
        }

        // armors
        for (int i = 0; i < 4; i++) {
            var stack = wearer.getEquippedStack(
                    switch(i){
                        case 0 -> EquipmentSlot.FEET;
                        case 1 -> EquipmentSlot.LEGS;
                        case 2 -> EquipmentSlot.CHEST;
                        default -> EquipmentSlot.HEAD;
                    }
                    );
            if(stack.isEmpty() || !(stack.getItem() instanceof IJewelryItem je)) continue;
            res.add(stack);
        }

        return res;
    }


    public static float getManaRegenMultiplier(LivingEntity wearer){
        float res = 1;

        // TODO: speed multiplier stat seperate from per-item amethysts
        //var wornJewelry = getAllWornJewelryItems(wearer);
        //for(ItemStack jewelryItem : wornJewelry){
        //    res += getManaRegenMultiplier(jewelryItem,wearer);
        //}

        return res;
    }

    public static float getManaRegenMultiplier(ItemStack stack,LivingEntity wearer){
        float res = 0;

        IJewelryItem jewelryItem = (IJewelryItem) stack.getItem();
        if(jewelryItem.isPendant()) return res;
        var gems = getSlots(stack);

        for(var gem : gems){
            res += gem.getManaRegenMultiplier(stack,wearer);
        }

        return res;
    }

    public static float getManaCapacityMultiplier(ItemStack stack, LivingEntity entity){
        float res = 1;

        IJewelryItem jewelryItem = (IJewelryItem) stack.getItem();
        if(jewelryItem.isPendant()) return res;
        var gems = getSlots(stack);

        for(var gem : gems){
            res += gem.getManaCapacityMultiplier(stack,entity);
        }

        return res;
    }


    public static float getGemQualityMultiplierFor(GemSlot gem,ItemStack onItem,LivingEntity wearer){
        float res = 1;
        if(wearer == null) return res;

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

    public default boolean isPendant(){
        return getSettings().pendant;
    }

    public static boolean isPendant(ItemStack stack){
        return stack.getItem() instanceof IJewelryItem j && j.isPendant();
    }

    public static float getFortuneBonus(ItemStack jewelryItem,LivingEntity wearer){
        if(wearer==null) return 0;
        float res = 0;

        if(!(jewelryItem.getItem() instanceof IJewelryItem ji) || ji.isPendant()) return res;
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
            for(Item i : List){

                // generate full jewelry for every gem type
                for(var gem : GemSlot.settingsMap.keySet())
                {
                    ItemStack fullJewelry = new ItemStack(i);
                    IJewelryItem jewelryItem = (IJewelryItem) i;
                    for (int j = 0; j < jewelryItem.getGemSlotCount(); j++) {
                        jewelryItem.addSlot(fullJewelry,new GemSlot(gem,1));
                    }
                    itemGroup.add(fullJewelry);
                }

            }
        });
    }
}
