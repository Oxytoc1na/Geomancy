package org.oxytocina.geomancy.items.jewelry;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.ModItems;

import java.util.*;

public class JewelryItem extends TrinketItem implements IJewelryItem {

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

    @Override
    public int getGemSlotCount() {
        return gemSlotCount;
    }

    @Override
    public JewelryItemSettings getSettings() {
        return jewelrySettings;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var gems = IJewelryItem.getSlots(stack);
        for(var gem : gems)
            GemSlot.tick(stack,gem,slot,entity);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var gems = IJewelryItem.getSlots(stack);
        for(var gem : gems)
            GemSlot.equip(stack,gem,slot,entity);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var gems = IJewelryItem.getSlots(stack);
        for(var gem : gems)
            GemSlot.unequip(stack,gem,slot,entity);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);

        var gems = IJewelryItem.getSlots(stack);
        for(var gem : gems)
            modifiers = GemSlot.modifyModifiers(stack,gem,slot,entity,uuid,modifiers);

        return modifiers;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> list, TooltipContext context) {

        if(isPendant())
        {
            list.add(Text.translatable("tooltip.geomancy.jewelry.pendant1").formatted(Formatting.DARK_GRAY));
            list.add(Text.translatable("tooltip.geomancy.jewelry.pendant2").formatted(Formatting.DARK_GRAY));
        }

        var gems = IJewelryItem.getSlots(stack);
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

}
