package org.oxytocina.geomancy.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
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
import org.oxytocina.geomancy.entity.ExtraEntitySettings;
import org.oxytocina.geomancy.items.artifacts.ArtifactItem;
import org.oxytocina.geomancy.items.artifacts.ArtifactSettings;
import org.oxytocina.geomancy.items.artifacts.GoldArtifact;
import org.oxytocina.geomancy.items.artifacts.IronArtifact;
import org.oxytocina.geomancy.items.jewelry.*;
import org.oxytocina.geomancy.loottables.ModLootTables;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.spells.SpellBlocks;

import java.util.ArrayList;
import java.util.HashMap;

public class ModItems {

    public static final ArrayList<ArtifactItem> ArtifactItems = new ArrayList<>();
    public static final ArrayList<JewelryItem> JewelryRingItems = new ArrayList<>();
    public static final ArrayList<JewelryItem> JewelryNecklaceItems = new ArrayList<>();
    public static final ArrayList<JewelryItem> JewelryAnySlotItems = new ArrayList<>();
    public static final ArrayList<GeodeItem> geodeItems = new ArrayList<>();
    public static final HashMap<EntityType<? extends MobEntity>, SpawnEggItem> spawnEggs = new HashMap<>();

    public static final FoodComponent SUSPICIOUS_FOOD_COMPONENT = new FoodComponent.Builder()
            .hunger(1).alwaysEdible().snack().statusEffect(new StatusEffectInstance(StatusEffects.POISON, 6 * 20, 1), 1.0f).build();

    public static final Item SUSPICIOUS_SUBSTANCE = register("suspicious_substance",new Item(new FabricItemSettings().food(SUSPICIOUS_FOOD_COMPONENT)));

    public static final Item GUIDITE_SWORD = register("guidite_sword",new SwordItem(ModToolMaterials.GUIDITE, 2, 0.5F, new FabricItemSettings()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));

    // mithril
    public static final Item RAW_MITHRIL = register("raw_mithril",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item MITHRIL_INGOT = register("mithril_ingot",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item MITHRIL_NUGGET = register("mithril_nugget",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));

    // octangulite
    public static final OctanguliteItem RAW_OCTANGULITE = (OctanguliteItem)register("raw_octangulite",new OctanguliteItem(new FabricItemSettings().rarity(Rarity.RARE).fireproof(),1));
    public static final OctanguliteItem OCTANGULITE_INGOT = (OctanguliteItem)register("octangulite_ingot",new OctanguliteItem(new FabricItemSettings().rarity(Rarity.RARE).fireproof(),1));
    public static final OctanguliteItem OCTANGULITE_NUGGET = (OctanguliteItem)register("octangulite_nugget",new OctanguliteItem(new FabricItemSettings().rarity(Rarity.RARE).fireproof(),1/9f));

    // molybdenum
    public static final Item RAW_MOLYBDENUM = register("raw_molybdenum",new Item(new FabricItemSettings().rarity(Rarity.COMMON).fireproof()));
    public static final Item MOLYBDENUM_INGOT = register("molybdenum_ingot",new Item(new FabricItemSettings().rarity(Rarity.COMMON).fireproof()));
    public static final Item MOLYBDENUM_NUGGET = register("molybdenum_nugget",new Item(new FabricItemSettings().rarity(Rarity.COMMON).fireproof()));

    // titanium
    public static final Item RAW_TITANIUM = register("raw_titanium",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item TITANIUM_INGOT = register("titanium_ingot",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item TITANIUM_NUGGET = register("titanium_nugget",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));

    // lead
    public static final LeadItem RAW_LEAD = (LeadItem) register("raw_lead",new LeadItem(new FabricItemSettings().rarity(Rarity.COMMON).fireproof(),1));
    public static final LeadItem LEAD_INGOT = (LeadItem) register("lead_ingot",new LeadItem(new FabricItemSettings().rarity(Rarity.COMMON).fireproof(),1));
    public static final LeadItem LEAD_NUGGET = (LeadItem) register("lead_nugget",new LeadItem(new FabricItemSettings().rarity(Rarity.COMMON).fireproof(),1/9f));

    // gems
    public static final Item TOURMALINE = register("tourmaline",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item ORTHOCLASE = register("orthoclase",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item PERIDOT = register("peridot",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item AXINITE = register("axinite",new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));


    // guidebook and lore
    public static final GuidebookItem GUIDE_BOOK = (GuidebookItem)register("guidebook",new GuidebookItem(new FabricItemSettings()),new ExtraItemSettings());
    public static final LorebookItem LORE_BOOK_GOLDSMITH_1 = (LorebookItem)register("lorebook_goldsmith_1",new LorebookItem(new FabricItemSettings(),"lore/goldsmith_1"),new ExtraItemSettings());
    public static final LorebookItem LORE_BOOK_GOLDSMITH_2 = (LorebookItem)register("lorebook_goldsmith_2",new LorebookItem(new FabricItemSettings(),"lore/goldsmith_2"),new ExtraItemSettings());
    public static final LorebookItem LORE_BOOK_GOLDSMITH_3 = (LorebookItem)register("lorebook_goldsmith_3",new LorebookItem(new FabricItemSettings(),"lore/goldsmith_3"),new ExtraItemSettings());
    public static final LorebookItem LORE_BOOK_GOLDSMITH_4 = (LorebookItem)register("lorebook_goldsmith_4",new LorebookItem(new FabricItemSettings(),"lore/goldsmith_4"),new ExtraItemSettings());

    public static final LorebookItem LORE_BOOK_WAR_1 = (LorebookItem)register("lorebook_war_1",new LorebookItem(new FabricItemSettings(),"lore/war_1"),new ExtraItemSettings());
    public static final LorebookItem LORE_BOOK_WAR_2 = (LorebookItem)register("lorebook_war_2",new LorebookItem(new FabricItemSettings(),"lore/war_2"),new ExtraItemSettings());
    public static final LorebookItem LORE_BOOK_WAR_3 = (LorebookItem)register("lorebook_war_3",new LorebookItem(new FabricItemSettings(),"lore/war_3"),new ExtraItemSettings());
    public static final LorebookItem LORE_BOOK_WAR_4 = (LorebookItem)register("lorebook_war_4",new LorebookItem(new FabricItemSettings(),"lore/war_4"),new ExtraItemSettings());

    public static final LorebookItem LORE_LOG_EXPEDITION_1 = (LorebookItem)register("lorelog_expedition_1",new LorebookItem(new FabricItemSettings(),"lore/expedition_1"),new ExtraItemSettings());
    public static final LorebookItem LORE_LOG_EXPEDITION_2 = (LorebookItem)register("lorelog_expedition_2",new LorebookItem(new FabricItemSettings(),"lore/expedition_2"),new ExtraItemSettings());
    public static final LorebookItem LORE_LOG_EXPEDITION_3 = (LorebookItem)register("lorelog_expedition_3",new LorebookItem(new FabricItemSettings(),"lore/expedition_3"),new ExtraItemSettings());
    public static final LorebookItem LORE_LOG_EXPEDITION_4 = (LorebookItem)register("lorelog_expedition_4",new LorebookItem(new FabricItemSettings(),"lore/expedition_4"),new ExtraItemSettings());
    public static final LorebookItem LORE_LOG_EXPEDITION_5 = (LorebookItem)register("lorelog_expedition_5",new LorebookItem(new FabricItemSettings(),"lore/expedition_5"),new ExtraItemSettings());


    // music discs
    public static final Item MUSIC_DISC_DIGGY = register("music_disc_diggy",new MusicDiscItem(15, ModSoundEvents.MUSIC_DISC_DIGGY, (new Item.Settings()).maxCount(1).rarity(Rarity.RARE), 235));

    // artifacts
    public static final ArtifactItem EMPTY_ARTIFACT = (ArtifactItem) register("empty_artifact",new ArtifactItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).fireproof(),new ArtifactSettings()));
    public static final IronArtifact ARTIFACT_OF_IRON = (IronArtifact) register("artifact_of_iron",new IronArtifact(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof(),new ArtifactSettings()));
    public static final GoldArtifact ARTIFACT_OF_GOLD = (GoldArtifact) register("artifact_of_gold",new GoldArtifact(new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof(),new ArtifactSettings()));

    // jewelry
    // rings
    public static final JewelryItem IRON_RING = (JewelryItem) register("iron_ring",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(1)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem GOLD_RING = (JewelryItem) register("gold_ring",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem MITHRIL_RING = (JewelryItem) register("mithril_ring",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem COPPER_RING = (JewelryItem) register("copper_ring",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(1)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem MOLYBDENUM_RING = (JewelryItem) register("molybdenum_ring",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem TITANIUM_RING = (JewelryItem) register("titanium_ring",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final LeadJewelryItem LEAD_RING = (LeadJewelryItem) register("lead_ring",new LeadJewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2),1,5),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final OctanguliteJewelryItem OCTANGULITE_RING = (OctanguliteJewelryItem) register("octangulite_ring",new OctanguliteJewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(3),100,1,3),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    // necklaces
    public static final JewelryItem IRON_NECKLACE = (JewelryItem) register("iron_necklace",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(1)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem GOLD_NECKLACE = (JewelryItem) register("gold_necklace",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem MITHRIL_NECKLACE = (JewelryItem) register("mithril_necklace",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(3)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem COPPER_NECKLACE = (JewelryItem) register("copper_necklace",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(1)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem MOLYBDENUM_NECKLACE = (JewelryItem) register("molybdenum_necklace",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem TITANIUM_NECKLACE = (JewelryItem) register("titanium_necklace",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(3)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final LeadJewelryItem LEAD_NECKLACE = (LeadJewelryItem) register("lead_necklace",new LeadJewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2),1,5),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final OctanguliteJewelryItem OCTANGULITE_NECKLACE = (OctanguliteJewelryItem) register("octangulite_necklace",new OctanguliteJewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(4),200,1,4),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    // pendants
    public static final JewelryItem IRON_PENDANT = (JewelryItem) register("iron_pendant",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(1).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem GOLD_PENDANT = (JewelryItem) register("gold_pendant",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem MITHRIL_PENDANT = (JewelryItem) register("mithril_pendant",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(3).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem COPPER_PENDANT = (JewelryItem) register("copper_pendant",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(1).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem MOLYBDENUM_PENDANT = (JewelryItem) register("molybdenum_pendant",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final JewelryItem TITANIUM_PENDANT = (JewelryItem) register("titanium_pendant",new JewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(3).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final LeadJewelryItem LEAD_PENDANT = (LeadJewelryItem) register("lead_pendant",new LeadJewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2).setPendant(),1,5),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final OctanguliteJewelryItem OCTANGULITE_PENDANT = (OctanguliteJewelryItem) register("octangulite_pendant",new OctanguliteJewelryItem(new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(4).setPendant(),100,1,4),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry));


    // tools
    public static final HammerItem IRON_HAMMER = (HammerItem) register("iron_hammer",new HammerItem(5,-3.3f,
            ToolMaterials.IRON, TagKey.of(RegistryKeys.BLOCK,new Identifier(Geomancy.MOD_ID,"hammer_mineable")),
            new FabricItemSettings(),10,1,10,20),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HammerItem MITHRIL_HAMMER = (HammerItem) register("mithril_hammer",new HammerItem(7,-3.3f,
            ToolMaterials.DIAMOND, TagKey.of(RegistryKeys.BLOCK,new Identifier(Geomancy.MOD_ID,"hammer_mineable")),new FabricItemSettings(),
            1000,100,50,10),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));

    // caster items
    public static final SoulCastingItem CASTER_TEST = (SoulCastingItem) register("caster_test",new SoulCastingItem(new FabricItemSettings(),1));
    public static final SoulCastingItem SPELLGLOVE = (SoulCastingItem) register("spellglove",new SoulCastingItem(new FabricItemSettings(),3),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Custom));
    public static final SpellStoringItem SPELLSTORAGE_SMALL = (SpellStoringItem) register("spellstorage_small",new SpellStoringItem(new FabricItemSettings(),3,3));
    public static final SpellStoringItem SPELLSTORAGE_MEDIUM = (SpellStoringItem) register("spellstorage_medium",new SpellStoringItem(new FabricItemSettings(),5,5));
    public static final SpellStoringItem SPELLSTORAGE_LARGE = (SpellStoringItem) register("spellstorage_large",new SpellStoringItem(new FabricItemSettings(),7,7));
    public static final SpellComponentStoringItem SPELLCOMPONENT = (SpellComponentStoringItem) register("spellcomponent",new SpellComponentStoringItem(new FabricItemSettings()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Custom));

    // geodes
    public static final GeodeItem STONE_GEODE = (GeodeItem) register("stone_geode",new GeodeItem(new FabricItemSettings(),ModLootTables.GEODE_STONE));

    // test alien tooltip
    public static final StellgeTooltippedItem TEST = (StellgeTooltippedItem) register("stellge_test",new StellgeTooltippedItem(new FabricItemSettings(),"lorem ipsum dolor sit amet"));

    public static void register() {
        // initialize static fields
        // calling this method is sufficient to do that, actually

        // Add the suspicious substance to the composting registry with a 30% chance of increasing the composter's level.
        CompostingChanceRegistry.INSTANCE.add(ModItems.SUSPICIOUS_SUBSTANCE, 0.3f);
        // Add the suspicious substance to the flammable block registry with a burn time of 30 seconds.
        // Remember, Minecraft deals with logical based-time using ticks.
        // 20 ticks = 1 second.
        FuelRegistry.INSTANCE.add(ModItems.SUSPICIOUS_SUBSTANCE, 30 * 20);


        // Register the groups.
        Registry.register(Registries.ITEM_GROUP, MAIN_ITEM_GROUP_KEY, MAIN_ITEM_GROUP);
        Registry.register(Registries.ITEM_GROUP, JEWELRY_ITEM_GROUP_KEY, JEWELRY_ITEM_GROUP);
        Registry.register(Registries.ITEM_GROUP, SPELLS_ITEM_GROUP_KEY, SPELLS_ITEM_GROUP);

        // Register items to the custom item group.
        ItemGroupEvents.modifyEntriesEvent(MAIN_ITEM_GROUP_KEY).register(itemGroup -> {
            for(Item i : ExtraItemSettings.ItemsInGroup){
                itemGroup.add(i);
            }
        });

    }

    public static final RegistryKey<ItemGroup> MAIN_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(Geomancy.MOD_ID, "main_item_group"));
    public static final ItemGroup MAIN_ITEM_GROUP = FabricItemGroup.builder()
            .icon(ModItems.GUIDE_BOOK::getDefaultStack)
            .displayName(Text.translatable("itemGroup."+Geomancy.MOD_ID+".main"))
            .build();

    public static final RegistryKey<ItemGroup> JEWELRY_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(Geomancy.MOD_ID, "jewelry_item_group"));
    public static final ItemGroup JEWELRY_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> IRON_RING.addSlot(new ItemStack(ModItems.IRON_RING),new GemSlot(Items.DIAMOND,1)))
            .displayName(Text.translatable("itemGroup."+Geomancy.MOD_ID+".jewelry"))
            .build();

    public static final RegistryKey<ItemGroup> SPELLS_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(Geomancy.MOD_ID, "spells_item_group"));
    public static final ItemGroup SPELLS_ITEM_GROUP = FabricItemGroup.builder()
            .icon(OCTANGULITE_INGOT::getDefaultStack)
            .displayName(Text.translatable("itemGroup."+Geomancy.MOD_ID+".spells"))
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

    public static void registerSpawnEgg(ExtraEntitySettings settings) {
        SpawnEggItem spawnEgg = (SpawnEggItem) register(settings.mobEntity.getUntranslatedName()+"_spawn_egg",new SpawnEggItem(settings.mobEntity, settings.spawnEggColorMain, settings.spawnEggColorSecond, new FabricItemSettings()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Custom));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS)
                .register((itemGroup) -> itemGroup.add(spawnEgg));
        spawnEggs.put(settings.mobEntity,spawnEgg);
    }
}

