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
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.ModToolMaterials;
import org.oxytocina.geomancy.items.artifacts.ArtifactItem;
import org.oxytocina.geomancy.items.artifacts.ArtifactSettings;
import org.oxytocina.geomancy.items.artifacts.GoldArtifact;
import org.oxytocina.geomancy.items.artifacts.IronArtifact;
import org.oxytocina.geomancy.sound.ModSoundEvents;

import java.util.ArrayList;

public class ModItems {

    public static final ArrayList<ArtifactItem> ArtifactItems = new ArrayList<ArtifactItem>();

    public static final FoodComponent SUSPICIOUS_FOOD_COMPONENT = new FoodComponent.Builder()
            .hunger(1).alwaysEdible().snack().statusEffect(new StatusEffectInstance(StatusEffects.POISON, 6 * 20, 1), 1.0f).build();

    public static final Item SUSPICIOUS_SUBSTANCE = register("suspicious_substance",new Item(new FabricItemSettings().food(SUSPICIOUS_FOOD_COMPONENT)));

    public static final Item GUIDITE_SWORD = register("guidite_sword",new SwordItem(ModToolMaterials.GUIDITE, 2, 0.5F, new FabricItemSettings()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));

    // mithril
    public static final Item RAW_MITHRIL = register("raw_mithril",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item MITHRIL_INGOT = register("mithril_ingot",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item MITHRIL_NUGGET = register("mithril_nugget",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));

    public static final Item GUIDE_BOOK = register("guidebook",new Item(new FabricItemSettings()),new ExtraItemSettings().dontGroupItem());

    // music discs
    public static final Item MUSIC_DISC_DIGGY = register("music_disc_diggy",new MusicDiscItem(15, ModSoundEvents.MUSIC_DISC_DIGGY, (new Item.Settings()).maxCount(1).rarity(Rarity.RARE), 235));

    // artifacts
    public static final ArtifactItem EMPTY_ARTIFACT = (ArtifactItem) register("empty_artifact",new ArtifactItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).fireproof(),new ArtifactSettings()));
    public static final IronArtifact ARTIFACT_OF_IRON = (IronArtifact) register("artifact_of_iron",new IronArtifact(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof(),new ArtifactSettings()));
    public static final GoldArtifact ARTIFACT_OF_GOLD = (GoldArtifact) register("artifact_of_gold",new GoldArtifact(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof(),new ArtifactSettings()));

    // tools
    public static final HammerItem IRON_HAMMER = (HammerItem) register("iron_hammer",new HammerItem(5,-3.3f,ToolMaterials.IRON, TagKey.of(RegistryKeys.BLOCK,new Identifier(Geomancy.MOD_ID,"hammer_mineable")),new FabricItemSettings(),10,1,10,20),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HammerItem MITHRIL_HAMMER = (HammerItem) register("mithril_hammer",new HammerItem(7,-3.3f,ToolMaterials.DIAMOND, TagKey.of(RegistryKeys.BLOCK,new Identifier(Geomancy.MOD_ID,"hammer_mineable")),new FabricItemSettings(),1000,100,50,10),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));

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
            for(Item i : ExtraItemSettings.ItemsInGroup){
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
        return register(id,item,new ExtraItemSettings());
    }
    public static Item register(String id,Item item,  ExtraItemSettings extraSettings) {
        // Create the identifier for the item.
        Identifier itemID = new Identifier(Geomancy.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        extraSettings.setItem(registeredItem);
        extraSettings.apply();

        // Return the registered item!
        return registeredItem;

    }
}

