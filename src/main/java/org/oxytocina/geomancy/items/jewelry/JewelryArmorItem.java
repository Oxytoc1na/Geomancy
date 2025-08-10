package org.oxytocina.geomancy.items.jewelry;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.items.ModItems;

import java.util.*;

public class JewelryArmorItem extends ArmorItem implements IJewelryItem {
    private static final EnumMap<Type, UUID> MODIFIERS = (EnumMap) Util.make(new EnumMap(Type.class), (uuidMap) -> {
        uuidMap.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A59B6B"));
        uuidMap.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D744E0D"));
        uuidMap.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846905B48E"));
        uuidMap.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BC150"));
    });

    public final int gemSlotCount;
    private final JewelryItemSettings jewelrySettings;

    public static final HashMap<ItemStack,LivingEntity> wearerMap = new HashMap<>();

    public JewelryArmorItem(ArmorMaterial material, Type type, Settings settings,JewelryItemSettings jewelryItemSettings) {
        super(material, type, settings);
        this.gemSlotCount=jewelryItemSettings.gemSlotCount;
        List.add(this);
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
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!(entity instanceof LivingEntity le)) return;
        wearerMap.put(stack,le);

        // check if equipped
        if(le instanceof PlayerEntity pe && stack.getItem() == this){
            if(pe.getEquippedStack(this.getSlotType()) == stack){
                // equipped! run gem slots
                var gems = IJewelryItem.getSlots(stack);
                for(var gem : gems)
                    GemSlot.tick(stack,gem,null,le);
            }
        }


    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var res = super.use(world, user, hand);
        if(res.getResult().isAccepted()){
            if(res.getValue().getItem() instanceof JewelryArmorItem arI)
            {
                arI.onUnequip(res.getValue(),user);
            }
            if(user.getStackInHand(hand).getItem() instanceof JewelryArmorItem arI)
            {
                arI.onEquip(user.getStackInHand(hand),user);
            }
        }
        return res;
    }

    public void onEquip(ItemStack stack, LivingEntity entity) {
        var gems = IJewelryItem.getSlots(stack);
        for(var gem : gems)
            GemSlot.equip(stack,gem,null,entity);
    }

    public void onUnequip(ItemStack stack, LivingEntity entity) {
        var gems = IJewelryItem.getSlots(stack);
        for(var gem : gems)
            GemSlot.unequip(stack,gem,null,entity);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        return slot == this.type.getEquipmentSlot() ? getAttributeModifiers(stack) : super.getAttributeModifiers(slot);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
        for(var e : this.attributeModifiers.entries()){
            modifiers.put(e.getKey(),e.getValue());
        }

        var gems = IJewelryItem.getSlots(stack);
        for(var gem : gems)
            modifiers = GemSlot.modifyModifiers(stack,gem,null,getWearer(stack),MODIFIERS.get(this.type),modifiers);

        return modifiers;

    }

    public static LivingEntity getWearer(ItemStack stack){
        return wearerMap.get(stack);
    }

    @Override
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
