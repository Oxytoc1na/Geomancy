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
import org.oxytocina.geomancy.enchantments.ModEnchantments;
import org.oxytocina.geomancy.util.Toolbox;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GemSlot {

    public final Item gemItem;
    public float quality;
    public final String gemItemIdentifierString;

    public static HashMap<Item, Settings> settingsMap = new HashMap<>();
    public static HashMap<Item, Integer> gemColorMap = new HashMap<>();

    static{

        // Diamond gems give 2 Armor per quality
        register(Settings.create(Items.DIAMOND).setColor(0,1,1).setDifficulty(1).setProgressCost(0).withGenericTooltip(Formatting.AQUA).setModifier((itemStack, gemSlot, slotReference, livingEntity, uuid, modifiers) -> {
            float value = 2*gemSlot.getEffectiveQuality(itemStack,livingEntity);
            String id = "geomancy:jewelry_diamond_gem_armor";
            EntityAttributeModifier.Operation op = EntityAttributeModifier.Operation.ADDITION;
            var newMod = new EntityAttributeModifier(uuid, id, value, op);

            // check for duplicates
            boolean added = false;
            if(modifiers.containsKey(EntityAttributes.GENERIC_ARMOR)){
                for(EntityAttributeModifier mod : modifiers.get(EntityAttributes.GENERIC_ARMOR).stream().toList()){
                    if(mod.equals(newMod))
                    {
                        // matches!
                        // replace with combined
                        modifiers.replaceValues(EntityAttributes.GENERIC_ARMOR,modifiers.get(EntityAttributes.GENERIC_ARMOR).stream().map(entityAttributeModifier -> {
                            if(mod.equals(newMod))
                                return new EntityAttributeModifier(uuid, id, mod.getValue()+value, op);
                            return entityAttributeModifier;
                        }).toList());
                        added=true;
                        break;
                    }
                }
            }
            if(!added)
                modifiers.put(EntityAttributes.GENERIC_ARMOR, newMod);
            return modifiers;
        }));
        register(Settings.create(Items.EMERALD).setColor(0,1,0).setDifficulty(1).setProgressCost(0).withGenericTooltip(Formatting.GREEN));
        register(Settings.create(Items.LAPIS_LAZULI).setColor(0,0,1).setDifficulty(1).setProgressCost(0).withGenericTooltip(Formatting.BLUE));

    }

    public static void register(Settings settings){
        settingsMap.put(settings.item,settings);
        registerColor(settings.item,settings.color);
    }

    public static void registerColor(Item item, int color){
        gemColorMap.put(item,color);
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
        return quality * (1 + ModEnchantments.getLevel(stack,ModEnchantments.BRILLIANCE) * 0.2F);
    }

    public static Settings getSettings(Item item){
        if(!settingsMap.containsKey(item)) return null;
        return settingsMap.get(item);
    }

    public static boolean tick(ItemStack stack,GemSlot gem, SlotReference slot, LivingEntity entity){
        Settings s = getSettings(gem.gemItem);
        if(s!=null&&s.tickFunction!=null)
            return s.tickFunction.apply(stack,gem,slot,entity);
        return false;
    }

    public static boolean equip(ItemStack stack,GemSlot gem, SlotReference slot, LivingEntity entity){
        Settings s = getSettings(gem.gemItem);
        if(s!=null&&s.equipFunction!=null)
            return s.equipFunction.apply(stack,gem,slot,entity);
        return false;
    }

    public static boolean unequip(ItemStack stack,GemSlot gem, SlotReference slot, LivingEntity entity){
        Settings s = getSettings(gem.gemItem);
        if(s!=null&&s.unequipFunction!=null)
            return s.unequipFunction.apply(stack,gem,slot,entity);
        return false;
    }

    public static Multimap<EntityAttribute, EntityAttributeModifier> modifyModifiers(ItemStack stack, GemSlot gem, SlotReference slot, LivingEntity entity, UUID uuid, Multimap<EntityAttribute, EntityAttributeModifier> modifiers){
        Settings s = getSettings(gem.gemItem);
        if(s!=null&&s.modifierFunction!=null)
            return s.modifierFunction.apply(stack,gem,slot,entity,uuid,modifiers);
        return modifiers;
    }

    public static boolean appendTooltip(ItemStack stack, GemSlot gem,LivingEntity wearer, World world, List<Text> list, TooltipContext context) {
        Settings s = getSettings(gem.gemItem);
        if(s!=null&&s.tooltipFunction!=null)
            return s.tooltipFunction.apply(stack,gem,wearer,world,list,context);
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

    public static class Settings{
        public Item item;
        public int difficulty = 0;
        public int progressCost = 0;
        public int color = 0xFFFFFFFF;
        public Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean> tickFunction = null;
        public Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean> equipFunction = null;
        public Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean> unequipFunction = null;
        public Function6<ItemStack,GemSlot, SlotReference, LivingEntity,UUID,Multimap<EntityAttribute, EntityAttributeModifier>,Multimap<EntityAttribute, EntityAttributeModifier>> modifierFunction = null;
        public Function6<ItemStack,GemSlot, LivingEntity, World , List<Text> , TooltipContext ,Boolean> tooltipFunction = null;

        private Settings(){

        }

        public Settings setDifficulty(int difficulty){this.difficulty=difficulty;return this;}
        public Settings setProgressCost(int progressCost){this.progressCost=progressCost;return this;}
        public Settings setColor(int color){this.color=color;return this;}
        public Settings setColor(float r, float g, float b){this.color=Toolbox.colorFromRGB(r,g,b);return this;}
        public Settings setTick(Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean> f){tickFunction=f;return this;}
        public Settings setEquip(Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean> f){equipFunction=f;return this;}
        public Settings setUnequip(Function4<ItemStack,GemSlot, SlotReference, LivingEntity,Boolean> f){unequipFunction=f;return this;}
        public Settings setModifier(Function6<ItemStack,GemSlot, SlotReference, LivingEntity,UUID,Multimap<EntityAttribute, EntityAttributeModifier>,Multimap<EntityAttribute, EntityAttributeModifier>> f){modifierFunction=f;return this;}
        public Settings setTooltip(Function6<ItemStack,GemSlot,LivingEntity, World , List<Text> , TooltipContext ,Boolean> f){tooltipFunction=f;return this;}
        public Settings withGenericTooltip(net.minecraft.util.Formatting formatting){ return setTooltip((itemStack, gemSlot,wearer, world, texts, tooltipContext) -> {
            texts.add(Text.translatable("tooltip.geomancy.jewelry.quality").formatted(Formatting.AQUA)
                    .append(" ")
                    .append(gemSlot.getQualityString(itemStack,wearer)).append(" ")
                    .append(Text.translatable(gemSlot.gemItem.getTranslationKey()).formatted(formatting))); return true;});};
        public Settings setItem(Item item){this.item=item;return this;}

        public static Settings create(Item item){
            return new Settings().setItem(item);
        }
    }

    private Text getQualityString(ItemStack stack, LivingEntity wearer) {
        Formatting formatting = Formatting.DARK_RED;
        int qualityPercent = Math.round(quality*100);
        int effectiveQualityPercent = Math.round(getEffectiveQuality(stack,wearer)*100);

        if(qualityPercent > 100) formatting = Formatting.LIGHT_PURPLE;
        else if (qualityPercent>80) formatting = Formatting.GREEN;
        else if (qualityPercent>60) formatting = Formatting.YELLOW;
        else if (qualityPercent>40) formatting = Formatting.GOLD;
        else if (qualityPercent>20) formatting = Formatting.RED;

        Text effectiveText = Text.empty();
        if(effectiveQualityPercent!=qualityPercent){
            effectiveText = Text.literal(" ("+ effectiveQualityPercent +"%)").formatted(
                    effectiveQualityPercent>qualityPercent? Formatting.LIGHT_PURPLE
                            : Formatting.RED
            );
        }

        return Text.literal(Integer.toString(qualityPercent)).append("%").formatted(formatting).append(effectiveText);
    }

    public float getXPMultiplier(ItemStack parent, LivingEntity wearer){
        float res = 0;

        if(gemItem == Items.LAPIS_LAZULI){
            res += getEffectiveQuality(parent,wearer);
        }

        return res;
    }
}
