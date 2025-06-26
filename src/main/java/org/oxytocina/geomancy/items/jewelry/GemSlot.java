package org.oxytocina.geomancy.items.jewelry;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function6;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.Util.Toolbox;

import java.util.HashMap;
import java.util.UUID;

public class GemSlot {

    public final Item gemItem;
    public float quality;
    public final String gemItemIdentifierString;

    public static HashMap<Item, Integer> gemColorMap = new HashMap<>();
    public static HashMap<Item, Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean>> gemTickFunctionMap = new HashMap<>();
    public static HashMap<Item, Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean>> gemEquipFunctionMap = new HashMap<>();
    public static HashMap<Item, Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean>> gemUnequipFunctionMap = new HashMap<>();
    public static HashMap<Item, Function6<ItemStack,GemSlot, SlotReference, LivingEntity,UUID,Multimap<EntityAttribute, EntityAttributeModifier>,Multimap<EntityAttribute, EntityAttributeModifier>>> gemModifierFunctionMap = new HashMap<>();

    static{
        registerColor(Items.DIAMOND,0f,1f,1f);
        registerColor(Items.EMERALD,0f,1f,0f);
        registerColor(Items.LAPIS_LAZULI,0f,0f,1f);

        // Diamond gems give 2 Armor per quality
        registerModifierFunction(Items.DIAMOND,((itemStack, gemSlot, slotReference, livingEntity, uuid, modifiers) -> {
            modifiers.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid, "geomancy:jewelry_diamond_gem_armor", 2*gemSlot.getEffectiveQuality(itemStack,livingEntity), EntityAttributeModifier.Operation.ADDITION));
            return modifiers;
        }));
    }

    public static void registerColor(Item item, float red, float green, float blue){
        registerColor(item, Toolbox.colorFromRGBA(red,green,blue,1));
    }

    public static void registerColor(Item item, int red, int green, int blue){
        registerColor(item,0xFF000000 | red << 16 | green << 8 | blue);
    }

    public static void registerColor(Item item, int color){
        gemColorMap.put(item,color);
    }

    public static void registerTickFunction(Item item, Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean> func){
        gemTickFunctionMap.put(item,func);
    }

    public static void registerEquipFunction(Item item, Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean> func){
        gemEquipFunctionMap.put(item,func);
    }

    public static void registerUnequipFunction(Item item, Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean> func){
        gemUnequipFunctionMap.put(item,func);
    }

    public static void registerModifierFunction(Item item, Function6<ItemStack,GemSlot, SlotReference, LivingEntity,UUID,Multimap<EntityAttribute, EntityAttributeModifier>,Multimap<EntityAttribute, EntityAttributeModifier>> func){
        gemModifierFunctionMap.put(item,func);
    }

    public GemSlot(Item item) {
        this(item,1);
    }

    public GemSlot(Item item, float quality){
        this.gemItem=item;
        this.quality = quality;
        this.gemItemIdentifierString = Registries.ITEM.getId(item.asItem()).toString();
    }

    public static GemSlot fromNbt(NbtCompound nbt){
        String identifierString = "minecraft:diamond";
        if(nbt.contains("item", NbtElement.STRING_TYPE)) identifierString = nbt.getString("item");
        else Geomancy.logError("GemSlot fromNbt: item field was missing");
        Item item = Items.DIAMOND;
        if(Identifier.isValid(identifierString)){
            Identifier identifier = Identifier.tryParse(identifierString);
            var op = Registries.ITEM.getOrEmpty(identifier);
            if(op.isPresent()){
                item=op.get();
            }
            else{
                Geomancy.logError("GemSlot fromNbt: item field contained nonexisting item "+identifierString);
            }
        }

        float quality = 1;
        if(nbt.contains("quality",NbtElement.FLOAT_TYPE)){
            quality = nbt.getFloat("quality");
        }

        return new GemSlot(item,quality);
    }

    public static NbtCompound toNbt(GemSlot slot){
        NbtCompound res = new NbtCompound();
        res.putString("item", Registries.ITEM.getId(slot.gemItem.asItem()).toString());
        res.putFloat("quality",slot.quality);
        return res;
    }

    public int getColor(){
        return gemColorMap.get(gemItem);
    }

    public float getEffectiveQuality(ItemStack stack, LivingEntity entity){
        return quality;
    }

    public static boolean tick(ItemStack stack,GemSlot gem, SlotReference slot, LivingEntity entity){

        Item item = stack.getItem();
        if(gemTickFunctionMap.containsKey(item))
            return gemTickFunctionMap.get(item).apply(stack,gem,slot,entity);

        return false;
    }

    public static boolean equip(ItemStack stack,GemSlot gem, SlotReference slot, LivingEntity entity){

        Item item = stack.getItem();
        if(gemEquipFunctionMap.containsKey(item))
            return gemEquipFunctionMap.get(item).apply(stack,gem,slot,entity);

        return false;
    }

    public static boolean unequip(ItemStack stack,GemSlot gem, SlotReference slot, LivingEntity entity){

        Item item = stack.getItem();
        if(gemUnequipFunctionMap.containsKey(item))
            return gemUnequipFunctionMap.get(item).apply(stack,gem,slot,entity);

        return false;
    }

    public static Multimap<EntityAttribute, EntityAttributeModifier> modifyModifiers(ItemStack stack, GemSlot gem, SlotReference slot, LivingEntity entity, UUID uuid, Multimap<EntityAttribute, EntityAttributeModifier> modifiers){
        Item item = stack.getItem();
        if(gemModifierFunctionMap.containsKey(item))
            return gemModifierFunctionMap.get(item).apply(stack,gem,slot,entity,uuid,modifiers);

        return modifiers;
    }
}
