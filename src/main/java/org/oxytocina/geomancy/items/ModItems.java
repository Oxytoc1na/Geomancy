package org.oxytocina.geomancy.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.ModToolMaterials;

import java.util.ArrayList;

public class ModItems {

    public static final ArrayList<Item> ItemsInGroup = new ArrayList<Item>();
    public static final ArrayList<Item> ItemsWithGeneratedModel = new ArrayList<Item>();

    public static final FoodComponent SUSPICIOUS_FOOD_COMPONENT = new FoodComponent.Builder()
            .hunger(1).alwaysEdible().snack().statusEffect(new StatusEffectInstance(StatusEffects.POISON, 6 * 20, 1), 1.0f).build();

    public static final Item SUSPICIOUS_SUBSTANCE = register(new Item(new FabricItemSettings().food(SUSPICIOUS_FOOD_COMPONENT)), "suspicious_substance");

    public static final Item GUIDITE_SWORD = register(new SwordItem(ModToolMaterials.GUIDITE, 2, 0.5F, new FabricItemSettings()), "guidite_sword",true,false);

    // mithril
    public static final Item RAW_MITHRIL = register(new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()), "raw_mithril");
    public static final Item MITHRIL_INGOT = register(new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()), "mithril_ingot");
    public static final Item MITHRIL_NUGGET = register(new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()), "mithril_nugget");

    public static final Item GUIDE_BOOK = register(new Item(new FabricItemSettings()), "guidebook",false,true);


    public static void initialize() {
        // initialize static fields
        // calling this method is sufficient to do that, actually

        // Add the suspicious substance to the composting registry with a 30% chance of increasing the composter's level.
        CompostingChanceRegistry.INSTANCE.add(ModItems.SUSPICIOUS_SUBSTANCE, 0.3f);
        // Add the suspicious substance to the flammable block registry with a burn time of 30 seconds.
        // Remember, Minecraft deals with logical based-time using ticks.
        // 20 ticks = 1 second.
        FuelRegistry.INSTANCE.add(ModItems.SUSPICIOUS_SUBSTANCE, 30 * 20);

        // Register the group.
        Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);

        // Register items to the custom item group.
        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register(itemGroup -> {
            for(Item i : ItemsInGroup){
                itemGroup.add(i);
            }
        });
    }

    public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(Geomancy.MOD_ID, "item_group"));
    public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.GUIDITE_SWORD))
            .displayName(Text.translatable("itemGroup."+Geomancy.MOD_ID))
            .build();

    public static Item register(Item item, String id) {
        return register(item,id,true,true);
    }
    public static Item register(Item item, String id, boolean shouldAddItemToGroup, boolean hasGeneratedModel) {
        // Create the identifier for the item.
        Identifier itemID = new Identifier(Geomancy.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);
        if(shouldAddItemToGroup)
            ItemsInGroup.add(registeredItem);
        if(hasGeneratedModel)
            ItemsWithGeneratedModel.add(registeredItem);
        // Return the registered item!
        return registeredItem;
    }
}

