package org.oxytocina.geomancy.items.jewelry;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.item.TooltipContext;
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
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.Util.Toolbox;

import java.util.HashMap;
import java.util.List;
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
    public static HashMap<Item, Function5<ItemStack,GemSlot, World , List<Text> , TooltipContext ,Boolean>> gemTooltipFunctionMap = new HashMap<>();

    static{
        registerColor(Items.DIAMOND,0f,1f,1f);
        registerColor(Items.EMERALD,0f,1f,0f);
        registerColor(Items.LAPIS_LAZULI,0f,0f,1f);

        // Diamond gems give 2 Armor per quality
        registerModifierFunction(Items.DIAMOND,((itemStack, gemSlot, slotReference, livingEntity, uuid, modifiers) -> {
            modifiers.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid, "geomancy:jewelry_diamond_gem_armor", 2*gemSlot.getEffectiveQuality(itemStack,livingEntity), EntityAttributeModifier.Operation.ADDITION));
            return modifiers;
        }));
        registerTooltipFunction(Items.DIAMOND,((itemStack, gemSlot, world, texts, tooltipContext) ->  {
            texts.add(Text.translatable(gemSlot.gemItem.getTranslationKey()).formatted(Formatting.AQUA)); return true;}));

        // Emeralds
        registerTooltipFunction(Items.EMERALD,((itemStack, gemSlot, world, texts, tooltipContext) ->  {
            texts.add(Text.translatable(gemSlot.gemItem.getTranslationKey()).formatted(Formatting.GREEN)); return true;}));

        // Lapis
        registerTooltipFunction(Items.LAPIS_LAZULI,((itemStack, gemSlot, world, texts, tooltipContext) ->  {
            texts.add(Text.translatable(gemSlot.gemItem.getTranslationKey()).formatted(Formatting.DARK_BLUE)); return true;}));
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

    public static void registerTooltipFunction(Item item, Function5<ItemStack,GemSlot, World, List<Text>, TooltipContext, Boolean> func){
        gemTooltipFunctionMap.put(item,func);
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
        if(gemTickFunctionMap.containsKey(gem.gemItem))
            return gemTickFunctionMap.get(gem.gemItem).apply(stack,gem,slot,entity);
        return false;
    }

    public static boolean equip(ItemStack stack,GemSlot gem, SlotReference slot, LivingEntity entity){
        if(gemEquipFunctionMap.containsKey(gem.gemItem))
            return gemEquipFunctionMap.get(gem.gemItem).apply(stack,gem,slot,entity);
        return false;
    }

    public static boolean unequip(ItemStack stack,GemSlot gem, SlotReference slot, LivingEntity entity){
        if(gemUnequipFunctionMap.containsKey(gem.gemItem))
            return gemUnequipFunctionMap.get(gem.gemItem).apply(stack,gem,slot,entity);
        return false;
    }

    public static Multimap<EntityAttribute, EntityAttributeModifier> modifyModifiers(ItemStack stack, GemSlot gem, SlotReference slot, LivingEntity entity, UUID uuid, Multimap<EntityAttribute, EntityAttributeModifier> modifiers){
        if(gemModifierFunctionMap.containsKey(gem.gemItem))
            return gemModifierFunctionMap.get(gem.gemItem).apply(stack,gem,slot,entity,uuid,modifiers);
        return modifiers;
    }

    public static boolean appendTooltip(ItemStack stack, GemSlot gem, World world, List<Text> list, TooltipContext context) {
        if(gemTooltipFunctionMap.containsKey(gem.gemItem))
            return gemTooltipFunctionMap.get(gem.gemItem).apply(stack,gem,world,list,context);
        return false;
    }

    public static boolean itemIsGem(ItemStack item){
        return item.isIn(TagKey.of(RegistryKeys.ITEM, Geomancy.locate("jewelry_gems")));
    }

    public static float getGemDifficulty(ItemStack item){
        return 1;
    }

    public static float getGemProgressCost(ItemStack item){
        return 0;
    }
}
