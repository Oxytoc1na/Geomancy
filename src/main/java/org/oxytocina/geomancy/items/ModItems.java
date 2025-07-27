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
import org.oxytocina.geomancy.items.armor.materials.LeadArmorItem;
import org.oxytocina.geomancy.items.armor.materials.ModArmorMaterials;
import org.oxytocina.geomancy.items.armor.materials.OctanguliteArmorItem;
import org.oxytocina.geomancy.items.tools.*;
import org.oxytocina.geomancy.items.tools.materials.ModToolMaterials;
import org.oxytocina.geomancy.entity.ExtraEntitySettings;
import org.oxytocina.geomancy.items.artifacts.*;
import org.oxytocina.geomancy.items.jewelry.*;
import org.oxytocina.geomancy.loottables.ModLootTables;
import org.oxytocina.geomancy.sound.ModSoundEvents;

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

    // lead
    public static final LeadSwordItem LEAD_SWORD = (LeadSwordItem) register("lead_sword", new LeadSwordItem(ModToolMaterials.LEAD, 3, -2.4F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadShovelItem LEAD_SHOVEL = (LeadShovelItem) register("lead_shovel", new LeadShovelItem(ModToolMaterials.LEAD, 1.5F, -3.0F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadPickaxeItem LEAD_PICKAXE = (LeadPickaxeItem) register("lead_pickaxe", new LeadPickaxeItem(ModToolMaterials.LEAD, 1, -2.8F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadAxeItem LEAD_AXE = (LeadAxeItem) register("lead_axe", new LeadAxeItem(ModToolMaterials.LEAD, 7.0F, -3.2F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadHoeItem LEAD_HOE = (LeadHoeItem) register("lead_hoe", new LeadHoeItem(ModToolMaterials.LEAD, -1, -2.0F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadArmorItem LEAD_HELMET = (LeadArmorItem) register("lead_helmet", new LeadArmorItem(ModArmorMaterials.LEAD, net.minecraft.item.ArmorItem.Type.HELMET, new Item.Settings(),1));
    public static final LeadArmorItem LEAD_CHESTPLATE = (LeadArmorItem) register("lead_chestplate", new LeadArmorItem(ModArmorMaterials.LEAD, net.minecraft.item.ArmorItem.Type.CHESTPLATE, new Item.Settings(),1));
    public static final LeadArmorItem LEAD_LEGGINGS = (LeadArmorItem) register("lead_leggings", new LeadArmorItem(ModArmorMaterials.LEAD, net.minecraft.item.ArmorItem.Type.LEGGINGS, new Item.Settings(),1));
    public static final LeadArmorItem LEAD_BOOTS = (LeadArmorItem) register("lead_boots", new LeadArmorItem(ModArmorMaterials.LEAD, net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings(),1));


    // mithril
    public static final SwordItem MITHRIL_SWORD = (SwordItem) register("mithril_sword", new SwordItem(ModToolMaterials.MITHRIL, 3, -2.4F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final ShovelItem MITHRIL_SHOVEL = (ShovelItem) register("mithril_shovel", new ShovelItem(ModToolMaterials.MITHRIL, 1.5F, -3.0F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final PickaxeItem MITHRIL_PICKAXE = (PickaxeItem) register("mithril_pickaxe", new PickaxeItem(ModToolMaterials.MITHRIL, 1, -2.8F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final AxeItem MITHRIL_AXE = (AxeItem) register("mithril_axe", new AxeItem(ModToolMaterials.MITHRIL, 7.0F, -3.2F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HoeItem MITHRIL_HOE = (HoeItem) register("mithril_hoe", new HoeItem(ModToolMaterials.MITHRIL, -1, -2.0F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final ArmorItem MITHRIL_HELMET = (ArmorItem) register("mithril_helmet", new ArmorItem(ModArmorMaterials.MITHRIL, net.minecraft.item.ArmorItem.Type.HELMET, new Item.Settings()));
    public static final ArmorItem MITHRIL_CHESTPLATE = (ArmorItem) register("mithril_chestplate", new ArmorItem(ModArmorMaterials.MITHRIL, net.minecraft.item.ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final ArmorItem MITHRIL_LEGGINGS = (ArmorItem) register("mithril_leggings", new ArmorItem(ModArmorMaterials.MITHRIL, net.minecraft.item.ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final ArmorItem MITHRIL_BOOTS = (ArmorItem) register("mithril_boots", new ArmorItem(ModArmorMaterials.MITHRIL, net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings()));

    // titanium
    public static final SwordItem TITANIUM_SWORD = (SwordItem) register("titanium_sword", new SwordItem(ModToolMaterials.TITANIUM, 3, -2.4F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final ShovelItem TITANIUM_SHOVEL = (ShovelItem) register("titanium_shovel", new ShovelItem(ModToolMaterials.TITANIUM, 1.5F, -3.0F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final PickaxeItem TITANIUM_PICKAXE = (PickaxeItem) register("titanium_pickaxe", new PickaxeItem(ModToolMaterials.TITANIUM, 1, -2.8F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final AxeItem TITANIUM_AXE = (AxeItem) register("titanium_axe", new AxeItem(ModToolMaterials.TITANIUM, 7.0F, -3.2F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HoeItem TITANIUM_HOE = (HoeItem) register("titanium_hoe", new HoeItem(ModToolMaterials.TITANIUM, -1, -2.0F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final ArmorItem TITANIUM_HELMET = (ArmorItem) register("titanium_helmet", new ArmorItem(ModArmorMaterials.TITANIUM, net.minecraft.item.ArmorItem.Type.HELMET, new Item.Settings()));
    public static final ArmorItem TITANIUM_CHESTPLATE = (ArmorItem) register("titanium_chestplate", new ArmorItem(ModArmorMaterials.TITANIUM, net.minecraft.item.ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final ArmorItem TITANIUM_LEGGINGS = (ArmorItem) register("titanium_leggings", new ArmorItem(ModArmorMaterials.TITANIUM, net.minecraft.item.ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final ArmorItem TITANIUM_BOOTS = (ArmorItem) register("titanium_boots", new ArmorItem(ModArmorMaterials.TITANIUM, net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings()));

    // molybdenum
    public static final SwordItem MOLYBDENUM_SWORD = (SwordItem) register("molybdenum_sword", new SwordItem(ModToolMaterials.MOLYBDENUM, 3, -2.4F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final ShovelItem MOLYBDENUM_SHOVEL = (ShovelItem) register("molybdenum_shovel", new ShovelItem(ModToolMaterials.MOLYBDENUM, 1.5F, -3.0F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final PickaxeItem MOLYBDENUM_PICKAXE = (PickaxeItem) register("molybdenum_pickaxe", new PickaxeItem(ModToolMaterials.MOLYBDENUM, 1, -2.8F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final AxeItem MOLYBDENUM_AXE = (AxeItem) register("molybdenum_axe", new AxeItem(ModToolMaterials.MOLYBDENUM, 7.0F, -3.2F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HoeItem MOLYBDENUM_HOE = (HoeItem) register("molybdenum_hoe", new HoeItem(ModToolMaterials.MOLYBDENUM, -1, -2.0F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final ArmorItem MOLYBDENUM_HELMET = (ArmorItem) register("molybdenum_helmet", new ArmorItem(ModArmorMaterials.MOLYBDENUM, net.minecraft.item.ArmorItem.Type.HELMET, new Item.Settings()));
    public static final ArmorItem MOLYBDENUM_CHESTPLATE = (ArmorItem) register("molybdenum_chestplate", new ArmorItem(ModArmorMaterials.MOLYBDENUM, net.minecraft.item.ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final ArmorItem MOLYBDENUM_LEGGINGS = (ArmorItem) register("molybdenum_leggings", new ArmorItem(ModArmorMaterials.MOLYBDENUM, net.minecraft.item.ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final ArmorItem MOLYBDENUM_BOOTS = (ArmorItem) register("molybdenum_boots", new ArmorItem(ModArmorMaterials.MOLYBDENUM, net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings()));

    // octangulite
    public static final OctanguliteSwordItem OCTANGULITE_SWORD = (OctanguliteSwordItem) register("octangulite_sword", new OctanguliteSwordItem(ModToolMaterials.OCTANGULITE, 3, -2.4F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctanguliteShovelItem OCTANGULITE_SHOVEL = (OctanguliteShovelItem) register("octangulite_shovel", new OctanguliteShovelItem(ModToolMaterials.OCTANGULITE, 1.5F, -3.0F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctangulitePickaxeItem OCTANGULITE_PICKAXE = (OctangulitePickaxeItem) register("octangulite_pickaxe", new OctangulitePickaxeItem(ModToolMaterials.OCTANGULITE, 1, -2.8F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctanguliteAxeItem OCTANGULITE_AXE = (OctanguliteAxeItem) register("octangulite_axe", new OctanguliteAxeItem(ModToolMaterials.OCTANGULITE, 7.0F, -3.2F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctanguliteHoeItem OCTANGULITE_HOE = (OctanguliteHoeItem) register("octangulite_hoe", new OctanguliteHoeItem(ModToolMaterials.OCTANGULITE, -1, -2.0F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctanguliteArmorItem OCTANGULITE_HELMET = (OctanguliteArmorItem) register("octangulite_helmet", new OctanguliteArmorItem(ModArmorMaterials.OCTANGULITE, net.minecraft.item.ArmorItem.Type.HELMET, new Item.Settings(),1));
    public static final OctanguliteArmorItem OCTANGULITE_CHESTPLATE = (OctanguliteArmorItem) register("octangulite_chestplate", new OctanguliteArmorItem(ModArmorMaterials.OCTANGULITE, net.minecraft.item.ArmorItem.Type.CHESTPLATE, new Item.Settings(),1));
    public static final OctanguliteArmorItem OCTANGULITE_LEGGINGS = (OctanguliteArmorItem) register("octangulite_leggings", new OctanguliteArmorItem(ModArmorMaterials.OCTANGULITE, net.minecraft.item.ArmorItem.Type.LEGGINGS, new Item.Settings(),1));
    public static final OctanguliteArmorItem OCTANGULITE_BOOTS = (OctanguliteArmorItem) register("octangulite_boots", new OctanguliteArmorItem(ModArmorMaterials.OCTANGULITE, net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings(),1));


    public static final HammerItem IRON_HAMMER = (HammerItem) register("iron_hammer",new HammerItem(5,-3.3f,
            ToolMaterials.IRON, TagKey.of(RegistryKeys.BLOCK,new Identifier(Geomancy.MOD_ID,"hammer_mineable")),
            new FabricItemSettings(),10,1,10,20),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HammerItem MITHRIL_HAMMER = (HammerItem) register("mithril_hammer",new HammerItem(7,-3.3f,
            ModToolMaterials.MITHRIL, TagKey.of(RegistryKeys.BLOCK,new Identifier(Geomancy.MOD_ID,"hammer_mineable")),new FabricItemSettings(),
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

