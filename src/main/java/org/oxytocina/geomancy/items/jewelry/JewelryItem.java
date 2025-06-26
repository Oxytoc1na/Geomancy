package org.oxytocina.geomancy.items.jewelry;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.ModItems;

import java.util.ArrayList;
import java.util.UUID;

public class JewelryItem extends TrinketItem implements DyeableItem {

    public static final ArrayList<JewelryItem> List = new ArrayList<>();

    public final int gemSlotCount;

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

    public static boolean ItemIsGem(ItemStack item){
        return item.isIn(TagKey.of(RegistryKeys.ITEM, Geomancy.locate("jewelry_gems")));
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
}
