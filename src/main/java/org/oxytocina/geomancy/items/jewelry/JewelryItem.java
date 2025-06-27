package org.oxytocina.geomancy.items.jewelry;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
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
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.ModItems;

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

    public ArrayList<GemSlot> getSlots(ItemStack stack) {
        NbtList nbt = stack.getOrCreateNbt().getList("gems",NbtList.COMPOUND_TYPE);
        ArrayList<GemSlot> res = new ArrayList<>();
        for (int i = 0; i < nbt.size(); i++) {
            var slotNbt = nbt.getCompound(i);
            res.add(GemSlot.fromNbt(slotNbt));
        }
        return res;
    }

    public void setSlots(ItemStack stack, ArrayList<GemSlot> slots){
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
        if(tintIndex < 0 || tintIndex >= gemSlotCount) return 0xFFFFFFFF;

        var slots = getSlots(stack);
        return slots.size()>tintIndex?slots.get(tintIndex).getColor():getEmptyColor(tintIndex);
    }

    public int getEmptyColor(int tintIndex){
        return 0xFFFFFFFF;
    }

    public float getHasGemPredicate(ItemStack stack){
        return (float)getSlots(stack).size() / gemSlotCount;
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

        var gems = getSlots(stack);
        boolean hasGems = !gems.isEmpty();
        List<Text> gemList = new ArrayList<>();
        for(var gem : gems)
            GemSlot.appendTooltip(stack,gem,world,gemList,context);

        Map<Text,Integer> textMap = new HashMap<>();
        for(Text t : gemList){
            if(textMap.containsKey(t)) textMap.put(t,textMap.get(t)+1);
            else textMap.put(t,1);
        }

        for(Text t : textMap.keySet()){
            int amount = textMap.get(t);
            list.add(Text.literal(amount>1?("x"+amount+" "):"").formatted(Formatting.YELLOW).append(t));
        }

        if(!hasGems){
            list.add(Text.translatable("tooltip.geomancy.jewelry.nogems").formatted(Formatting.GRAY));
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

    public void addSlot(ItemStack stack, GemSlot slot){
        NbtList nbt = stack.getOrCreateNbt().getList("gems",NbtList.COMPOUND_TYPE);

        nbt.add(GemSlot.toNbt(slot));

        stack.setSubNbt("gems",nbt);
    }
}
