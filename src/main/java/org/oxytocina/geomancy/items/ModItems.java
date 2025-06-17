package org.oxytocina.geomancy.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
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
import org.oxytocina.geomancy.items.artifacts.ArtifactItem;
import org.oxytocina.geomancy.items.artifacts.ArtifactSettings;
import org.oxytocina.geomancy.items.artifacts.IronArtifact;
import org.oxytocina.geomancy.sound.ModSoundEvents;

import java.util.ArrayList;

public class ModItems {

    public static final ArrayList<Item> ItemsInGroup = new ArrayList<Item>();
    public static final ArrayList<Item> ItemsWithGeneratedModel = new ArrayList<Item>();
    public static final ArrayList<ArtifactItem> ArtifactItems = new ArrayList<ArtifactItem>();

    public static final FoodComponent SUSPICIOUS_FOOD_COMPONENT = new FoodComponent.Builder()
            .hunger(1).alwaysEdible().snack().statusEffect(new StatusEffectInstance(StatusEffects.POISON, 6 * 20, 1), 1.0f).build();

    public static final Item SUSPICIOUS_SUBSTANCE = register("suspicious_substance",new Item(new FabricItemSettings().food(SUSPICIOUS_FOOD_COMPONENT)));

    public static final Item GUIDITE_SWORD = register("guidite_sword",new SwordItem(ModToolMaterials.GUIDITE, 2, 0.5F, new FabricItemSettings()),true,false);

    // mithril
    public static final Item RAW_MITHRIL = register("raw_mithril",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item MITHRIL_INGOT = register("mithril_ingot",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item MITHRIL_NUGGET = register("mithril_nugget",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));

    public static final Item GUIDE_BOOK = register("guidebook",new Item(new FabricItemSettings()),false,true);

    // music discs
    public static final Item MUSIC_DISC_DIGGY = register("music_disc_diggy",new MusicDiscItem(15, ModSoundEvents.MUSIC_DISC_DIGGY, (new Item.Settings()).maxCount(1).rarity(Rarity.RARE), 235));

    // artifacts
    public static final IronArtifact ARTIFACT_OF_IRON = (IronArtifact) register("artifact_of_iron",new IronArtifact(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof(),new ArtifactSettings()));


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

    public static Item register(String id,Item item) {
        return register(id,item,true,true);
    }
    public static Item register(String id,Item item,  boolean shouldAddItemToGroup, boolean hasGeneratedModel) {
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

