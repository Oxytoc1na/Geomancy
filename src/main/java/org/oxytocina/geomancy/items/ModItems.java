package org.oxytocina.geomancy.items;

import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.armor.*;
import org.oxytocina.geomancy.items.armor.materials.*;
import org.oxytocina.geomancy.items.misc.SoulPreviewItem;
import org.oxytocina.geomancy.items.tools.*;
import org.oxytocina.geomancy.items.tools.materials.ModToolMaterials;
import org.oxytocina.geomancy.entity.ExtraEntitySettings;
import org.oxytocina.geomancy.items.artifacts.*;
import org.oxytocina.geomancy.items.jewelry.*;
import org.oxytocina.geomancy.items.trinkets.CastingTrinketItem;
import org.oxytocina.geomancy.registries.ModBlockTags;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.LeadUtil;
import org.oxytocina.geomancy.util.MadnessUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class ModItems {

    public static final ArrayList<ArtifactItem> ArtifactItems = new ArrayList<>();
    public static final ArrayList<JewelryItem> JewelryRingItems = new ArrayList<>();
    public static final ArrayList<JewelryItem> JewelryNecklaceItems = new ArrayList<>();
    public static final ArrayList<JewelryItem> JewelryAnySlotItems = new ArrayList<>();
    public static final ArrayList<GeodeItem> geodeItems = new ArrayList<>();
    public static final HashMap<EntityType<? extends MobEntity>, SpawnEggItem> spawnEggs = new HashMap<>();

    // mithril
    public static final Item RAW_MITHRIL =      register("raw_mithril",     new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item MITHRIL_INGOT =    register("mithril_ingot",   new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item MITHRIL_NUGGET =   register("mithril_nugget",  new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));

    // octangulite
    public static final MaddeningItem RAW_OCTANGULITE =     register("raw_octangulite",     new MaddeningItem(new FabricItemSettings().rarity(Rarity.RARE).fireproof(),1));
    public static final MaddeningItem OCTANGULITE_INGOT =   register("octangulite_ingot",   new MaddeningItem(new FabricItemSettings().rarity(Rarity.RARE).fireproof(),1));
    public static final MaddeningItem OCTANGULITE_NUGGET =  register("octangulite_nugget",  new MaddeningItem(new FabricItemSettings().rarity(Rarity.RARE).fireproof(),1/9f));

    // molybdenum
    public static final MaddeningItem RAW_MOLYBDENUM =      register("raw_molybdenum",      new MaddeningItem(new FabricItemSettings().rarity(Rarity.COMMON).fireproof(),-1));
    public static final MaddeningItem MOLYBDENUM_INGOT =    register("molybdenum_ingot",    new MaddeningItem(new FabricItemSettings().rarity(Rarity.COMMON).fireproof(),-1));
    public static final MaddeningItem MOLYBDENUM_NUGGET =   register("molybdenum_nugget",   new MaddeningItem(new FabricItemSettings().rarity(Rarity.COMMON).fireproof(),-1/9f));

    // titanium
    public static final Item RAW_TITANIUM =     register("raw_titanium",    new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item TITANIUM_INGOT =   register("titanium_ingot",  new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item TITANIUM_NUGGET =  register("titanium_nugget", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));

    // lead
    public static final LeadItem RAW_LEAD =     register("raw_lead",    new LeadItem(new FabricItemSettings().rarity(Rarity.COMMON).fireproof(),1));
    public static final LeadItem LEAD_INGOT =   register("lead_ingot",  new LeadItem(new FabricItemSettings().rarity(Rarity.COMMON).fireproof(),1));
    public static final LeadItem LEAD_NUGGET =  register("lead_nugget", new LeadItem(new FabricItemSettings().rarity(Rarity.COMMON).fireproof(),1/9f));

    // gems
    public static final Item TOURMALINE =   register("tourmaline",  new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item ORTHOCLASE =   register("orthoclase",  new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item PERIDOT =      register("peridot",     new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));
    public static final Item AXINITE =      register("axinite",     new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));


    // guidebook and lore
    public static final GuidebookItem GUIDE_BOOK = register("guidebook",new GuidebookItem(new FabricItemSettings()),new ExtraItemSettings().addGroup(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_BOOK_GOLDSMITH_1 = register("lorebook_goldsmith_1",new LorebookItem(new FabricItemSettings(),"lore/goldsmith_1"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_BOOK_GOLDSMITH_2 = register("lorebook_goldsmith_2",new LorebookItem(new FabricItemSettings(),"lore/goldsmith_2"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_BOOK_GOLDSMITH_3 = register("lorebook_goldsmith_3",new LorebookItem(new FabricItemSettings(),"lore/goldsmith_3"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_BOOK_GOLDSMITH_4 = register("lorebook_goldsmith_4",new LorebookItem(new FabricItemSettings(),"lore/goldsmith_4"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));

    public static final LorebookItem LORE_BOOK_WAR_1 = register("lorebook_war_1",new LorebookItem(new FabricItemSettings(),"lore/war_1"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_BOOK_WAR_2 = register("lorebook_war_2",new LorebookItem(new FabricItemSettings(),"lore/war_2"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_BOOK_WAR_3 = register("lorebook_war_3",new LorebookItem(new FabricItemSettings(),"lore/war_3"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));

    public static final LorebookItem LORE_LOG_EXPEDITION_1 = register("lorelog_expedition_1",new LorebookItem(new FabricItemSettings(),"lore/expedition_1"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_LOG_EXPEDITION_2 = register("lorelog_expedition_2",new LorebookItem(new FabricItemSettings(),"lore/expedition_2"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_LOG_EXPEDITION_3 = register("lorelog_expedition_3",new LorebookItem(new FabricItemSettings(),"lore/expedition_3"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_LOG_EXPEDITION_4 = register("lorelog_expedition_4",new LorebookItem(new FabricItemSettings(),"lore/expedition_4"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_LOG_EXPEDITION_5 = register("lorelog_expedition_5",new LorebookItem(new FabricItemSettings(),"lore/expedition_5"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));

    public static final LorebookItem LORE_BOOK_EXTRAS_CREATION = register("lorebook_extras_creation",new LorebookItem(new FabricItemSettings(),"lore/extras_creation"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_LOG_EXTRAS_RESEARCH_1 = register("lorelog_extras_research_1",new LorebookItem(new FabricItemSettings(),"lore/extras_research_1"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_LOG_EXTRAS_RESEARCH_2 = register("lorelog_extras_research_2",new LorebookItem(new FabricItemSettings(),"lore/extras_research_2"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));

    public static final LorebookItem LORE_LOG_EXODIA_1 = register("lorelog_exodia_1",new LorebookItem(new FabricItemSettings(),"lore/exodia_1"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_LOG_EXODIA_2 = register("lorelog_exodia_2",new LorebookItem(new FabricItemSettings(),"lore/exodia_2"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_LOG_EXODIA_3 = register("lorelog_exodia_3",new LorebookItem(new FabricItemSettings(),"lore/exodia_3"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));
    public static final LorebookItem LORE_LOG_EXODIA_4 = register("lorelog_exodia_4",new LorebookItem(new FabricItemSettings(),"lore/exodia_4"),new ExtraItemSettings().group(ExtraItemSettings.Group.Lore));


    // music discs
    public static final Item MUSIC_DISC_DIGGY = register("music_disc_diggy",new MusicDiscItem(15, ModSoundEvents.MUSIC_DISC_DIGGY, (new Item.Settings()).maxCount(1).rarity(Rarity.RARE), 235));

    // artifacts
    public static final ArtifactItem EMPTY_ARTIFACT = register("empty_artifact",new ArtifactItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).fireproof(),new ArtifactSettings()));
    private static final Item.Settings ARTIFACT_SETTINGS = new Item.Settings().maxCount(1).rarity(Rarity.RARE).fireproof();
    public static final IronArtifact ARTIFACT_OF_IRON = register("artifact_of_iron",new IronArtifact(ARTIFACT_SETTINGS,new ArtifactSettings()));
    public static final GoldArtifact ARTIFACT_OF_GOLD = register("artifact_of_gold",new GoldArtifact(ARTIFACT_SETTINGS,new ArtifactSettings()));
    //public static final ArtifactItem ARTIFACT_OF_COAL =       register("artifact_of_coal",new ArtifactItem(ARTIFACT_SETTINGS,new ArtifactSettings()));
    //public static final ArtifactItem ARTIFACT_OF_COPPER =     register("artifact_of_copper",new ArtifactItem(ARTIFACT_SETTINGS,new ArtifactSettings()));
    //public static final ArtifactItem ARTIFACT_OF_DIAMOND =    register("artifact_of_diamond",new ArtifactItem(ARTIFACT_SETTINGS,new ArtifactSettings()));
    //public static final ArtifactItem ARTIFACT_OF_EMERALD =    register("artifact_of_emerald",new ArtifactItem(ARTIFACT_SETTINGS,new ArtifactSettings()));
    //public static final ArtifactItem ARTIFACT_OF_LEAD =       register("artifact_of_lead",new ArtifactItem(ARTIFACT_SETTINGS,new ArtifactSettings()));
    //public static final ArtifactItem ARTIFACT_OF_MITHRIL =    register("artifact_of_mithril",new ArtifactItem(ARTIFACT_SETTINGS,new ArtifactSettings()));
    //public static final ArtifactItem ARTIFACT_OF_MOLYBDENUM = register("artifact_of_molybdenum",new ArtifactItem(ARTIFACT_SETTINGS,new ArtifactSettings()));
    //public static final ArtifactItem ARTIFACT_OF_TITANIUM =   register("artifact_of_titanium",new ArtifactItem(ARTIFACT_SETTINGS,new ArtifactSettings()));
    //public static final ArtifactItem ARTIFACT_OF_OCTANGULITE = register("artifact_of_octangulite",new ArtifactItem(ARTIFACT_SETTINGS,new ArtifactSettings()));

    // jewelry
    // rings

    public static final JewelryItem IRON_RING =                         register("iron_ring",       new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(1)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem GOLD_RING =                         register("gold_ring",       new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem MITHRIL_RING =                      register("mithril_ring",    new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem COPPER_RING =                       register("copper_ring",     new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(1)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final MolybdenumJewelryItem MOLYBDENUM_RING =         register("molybdenum_ring", new MolybdenumJewelryItem   (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2),-1,-3),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem TITANIUM_RING =                     register("titanium_ring",   new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final LeadJewelryItem LEAD_RING =                     register("lead_ring",       new LeadJewelryItem         (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(2),1,5),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final OctanguliteJewelryItem OCTANGULITE_RING =       register("octangulite_ring",new OctanguliteJewelryItem  (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.RING).withGemCount(3),100,1,3),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    // necklaces
    public static final JewelryItem IRON_NECKLACE =                     register("iron_necklace",       new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(1)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem GOLD_NECKLACE =                     register("gold_necklace",       new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem MITHRIL_NECKLACE =                  register("mithril_necklace",    new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(3)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem COPPER_NECKLACE =                   register("copper_necklace",     new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(1)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final MolybdenumJewelryItem MOLYBDENUM_NECKLACE =     register("molybdenum_necklace", new MolybdenumJewelryItem   (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2),-1,-4),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem TITANIUM_NECKLACE =                 register("titanium_necklace",   new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(3)),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final LeadJewelryItem LEAD_NECKLACE =                 register("lead_necklace",       new LeadJewelryItem         (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2),1,5),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final OctanguliteJewelryItem OCTANGULITE_NECKLACE =   register("octangulite_necklace",new OctanguliteJewelryItem  (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(4),200,1,4),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    // pendants
    public static final JewelryItem IRON_PENDANT =                      register("iron_pendant",        new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(1).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem GOLD_PENDANT =                      register("gold_pendant",        new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem MITHRIL_PENDANT =                   register("mithril_pendant",     new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(3).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem COPPER_PENDANT =                    register("copper_pendant",      new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(1).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final MolybdenumJewelryItem MOLYBDENUM_PENDANT =      register("molybdenum_pendant",  new MolybdenumJewelryItem   (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2).setPendant(),-1,-4),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final JewelryItem TITANIUM_PENDANT =                  register("titanium_pendant",    new JewelryItem             (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(3).setPendant()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final LeadJewelryItem LEAD_PENDANT =                  register("lead_pendant",        new LeadJewelryItem         (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(2).setPendant(),1,5),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));
    public static final OctanguliteJewelryItem OCTANGULITE_PENDANT =    register("octangulite_pendant", new OctanguliteJewelryItem  (new Item.Settings().maxCount(1), JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.NECKLACE).withGemCount(4).setPendant(),100,1,4),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Jewelry).group(ExtraItemSettings.Group.Jewelry));

    // tools
    // NOTE: ModToolMaterials may only be called after all the required repair ingredients have been initialized above!!

    // lead
    public static final LeadSwordItem LEAD_SWORD =      register("lead_sword",      new LeadSwordItem   (ModToolMaterials.LEAD, 3, -2.4F,       new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadShovelItem LEAD_SHOVEL =    register("lead_shovel",     new LeadShovelItem  (ModToolMaterials.LEAD, 1.5F, -3.0F,    new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadPickaxeItem LEAD_PICKAXE =  register("lead_pickaxe",    new LeadPickaxeItem (ModToolMaterials.LEAD, 1, -2.8F,       new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadAxeItem LEAD_AXE =          register("lead_axe",        new LeadAxeItem     (ModToolMaterials.LEAD, 7.0F, -3.2F,    new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadHoeItem LEAD_HOE =          register("lead_hoe",        new LeadHoeItem     (ModToolMaterials.LEAD, -1, -2.0F,      new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final LeadArmorItem LEAD_HELMET =     register("lead_helmet",     new LeadArmorItem   (ModArmorMaterials.LEAD, ArmorItem.Type.HELMET,        new Item.Settings(),1));
    public static final LeadArmorItem LEAD_CHESTPLATE = register("lead_chestplate", new LeadArmorItem   (ModArmorMaterials.LEAD, ArmorItem.Type.CHESTPLATE,    new Item.Settings(),1));
    public static final LeadArmorItem LEAD_LEGGINGS =   register("lead_leggings",   new LeadArmorItem(ModArmorMaterials.LEAD, ArmorItem.Type.LEGGINGS,      new Item.Settings(),1));
    public static final LeadArmorItem LEAD_BOOTS =      register("lead_boots",      new LeadArmorItem   (ModArmorMaterials.LEAD, ArmorItem.Type.BOOTS,         new Item.Settings(),1));
    public static final Plumbometer PLUMBOMETER =       register("plumbometer",     new Plumbometer     (new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON)),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));

    // mithril
    public static final SwordItem MITHRIL_SWORD =               register("mithril_sword",       new SwordItem       (ModToolMaterials.MITHRIL, 3, -2.4F,    new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final ShovelItem MITHRIL_SHOVEL =             register("mithril_shovel",      new ShovelItem      (ModToolMaterials.MITHRIL, 1.5F, -3.0F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final PickaxeItem MITHRIL_PICKAXE =           register("mithril_pickaxe",     new PickaxeItem     (ModToolMaterials.MITHRIL, 1, -2.8F,    new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final AxeItem MITHRIL_AXE =                   register("mithril_axe",         new AxeItem         (ModToolMaterials.MITHRIL, 7.0F, -3.2F, new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HoeItem MITHRIL_HOE =                   register("mithril_hoe",         new HoeItem         (ModToolMaterials.MITHRIL, -1, -2.0F,   new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final MithrilArmorItem MITHRIL_HELMET =       register("mithril_helmet",      new MithrilArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.HELMET,      new Item.Settings(),JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.ARMOR).withGemCount(1)),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final MithrilArmorItem MITHRIL_CHESTPLATE =   register("mithril_chestplate",  new MithrilArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.CHESTPLATE,  new Item.Settings(),JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.ARMOR).withGemCount(2)),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final MithrilArmorItem MITHRIL_LEGGINGS =     register("mithril_leggings",    new MithrilArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.LEGGINGS,    new Item.Settings(),JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.ARMOR).withGemCount(1)),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Jewelry));
    public static final MithrilArmorItem MITHRIL_BOOTS =        register("mithril_boots",       new MithrilArmorItem(ModArmorMaterials.MITHRIL, ArmorItem.Type.BOOTS,       new Item.Settings(),JewelryItemSettings.createOf(JewelryItemSettings.TrinketSlot.ARMOR).withGemCount(1)),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Jewelry));

    // titanium
    public static final SwordItem TITANIUM_SWORD =      register("titanium_sword",      new SwordItem   (ModToolMaterials.TITANIUM, 3, -2.4F,       new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final ShovelItem TITANIUM_SHOVEL =    register("titanium_shovel",     new ShovelItem  (ModToolMaterials.TITANIUM, 1.5F, -3.0F,    new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final PickaxeItem TITANIUM_PICKAXE =  register("titanium_pickaxe",    new PickaxeItem (ModToolMaterials.TITANIUM, 1, -2.8F,       new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final AxeItem TITANIUM_AXE =          register("titanium_axe",        new AxeItem     (ModToolMaterials.TITANIUM, 7.0F, -3.2F,    new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HoeItem TITANIUM_HOE =          register("titanium_hoe",        new HoeItem     (ModToolMaterials.TITANIUM, -1, -2.0F,      new Item.Settings()),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final ArmorItem TITANIUM_HELMET =     register("titanium_helmet",     new ArmorItem   (ModArmorMaterials.TITANIUM, ArmorItem.Type.HELMET,     new Item.Settings()));
    public static final ArmorItem TITANIUM_CHESTPLATE = register("titanium_chestplate", new ArmorItem   (ModArmorMaterials.TITANIUM, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final ArmorItem TITANIUM_LEGGINGS =   register("titanium_leggings",   new ArmorItem   (ModArmorMaterials.TITANIUM, ArmorItem.Type.LEGGINGS,   new Item.Settings()));
    public static final ArmorItem TITANIUM_BOOTS =      register("titanium_boots",      new ArmorItem   (ModArmorMaterials.TITANIUM, ArmorItem.Type.BOOTS,      new Item.Settings()));

    // molybdenum
    // madness-healing properties
    public static final MolybdenumSwordItem MOLYBDENUM_SWORD =      register("molybdenum_sword",        new MolybdenumSwordItem     (ModToolMaterials.MOLYBDENUM, 3, -2.4F,    new Item.Settings(),-1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final MolybdenumShovelItem MOLYBDENUM_SHOVEL =    register("molybdenum_shovel",       new MolybdenumShovelItem    (ModToolMaterials.MOLYBDENUM, 1.5F, -3.0F, new Item.Settings(),-1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final MolybdenumPickaxeItem MOLYBDENUM_PICKAXE =  register("molybdenum_pickaxe",      new MolybdenumPickaxeItem   (ModToolMaterials.MOLYBDENUM, 1, -2.8F,    new Item.Settings(),-1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final MolybdenumAxeItem MOLYBDENUM_AXE =          register("molybdenum_axe",          new MolybdenumAxeItem       (ModToolMaterials.MOLYBDENUM, 7.0F, -3.2F, new Item.Settings(),-1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final MolybdenumHoeItem MOLYBDENUM_HOE =          register("molybdenum_hoe",          new MolybdenumHoeItem       (ModToolMaterials.MOLYBDENUM, -1, -2.0F,   new Item.Settings(),-1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final MolybdenumArmorItem MOLYBDENUM_HELMET =     register("molybdenum_helmet",       new MolybdenumArmorItem     (ModArmorMaterials.MOLYBDENUM, ArmorItem.Type.HELMET,        new Item.Settings(),-1));
    public static final MolybdenumArmorItem MOLYBDENUM_CHESTPLATE = register("molybdenum_chestplate",   new MolybdenumArmorItem     (ModArmorMaterials.MOLYBDENUM, ArmorItem.Type.CHESTPLATE,    new Item.Settings(),-1));
    public static final MolybdenumArmorItem MOLYBDENUM_LEGGINGS =   register("molybdenum_leggings",     new MolybdenumArmorItem     (ModArmorMaterials.MOLYBDENUM, ArmorItem.Type.LEGGINGS,      new Item.Settings(),-1));
    public static final MolybdenumArmorItem MOLYBDENUM_BOOTS =      register("molybdenum_boots",        new MolybdenumArmorItem     (ModArmorMaterials.MOLYBDENUM, ArmorItem.Type.BOOTS,         new Item.Settings(),-1));

    // octangulite
    public static final OctanguliteSwordItem OCTANGULITE_SWORD =        register("octangulite_sword",       new OctanguliteSwordItem    (ModToolMaterials.OCTANGULITE, 3, -2.4F,    new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctanguliteShovelItem OCTANGULITE_SHOVEL =      register("octangulite_shovel",      new OctanguliteShovelItem   (ModToolMaterials.OCTANGULITE, 1.5F, -3.0F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctangulitePickaxeItem OCTANGULITE_PICKAXE =    register("octangulite_pickaxe",     new OctangulitePickaxeItem  (ModToolMaterials.OCTANGULITE, 1, -2.8F,    new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctanguliteAxeItem OCTANGULITE_AXE =            register("octangulite_axe",         new OctanguliteAxeItem      (ModToolMaterials.OCTANGULITE, 7.0F, -3.2F, new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctanguliteHoeItem OCTANGULITE_HOE =            register("octangulite_hoe",         new OctanguliteHoeItem      (ModToolMaterials.OCTANGULITE, -1, -2.0F,   new Item.Settings(),1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom));
    public static final OctanguliteArmorItem OCTANGULITE_HELMET =       register("octangulite_helmet",      new OctanguliteArmorItem    (ModArmorMaterials.OCTANGULITE, ArmorItem.Type.HELMET,      new Item.Settings(),1));
    public static final OctanguliteArmorItem OCTANGULITE_CHESTPLATE =   register("octangulite_chestplate",  new OctanguliteArmorItem(ModArmorMaterials.OCTANGULITE, ArmorItem.Type.CHESTPLATE,  new Item.Settings(),1));
    public static final OctanguliteArmorItem OCTANGULITE_LEGGINGS =     register("octangulite_leggings",    new OctanguliteArmorItem    (ModArmorMaterials.OCTANGULITE, ArmorItem.Type.LEGGINGS,    new Item.Settings(),1));
    public static final OctanguliteArmorItem OCTANGULITE_BOOTS =        register("octangulite_boots",       new OctanguliteArmorItem    (ModArmorMaterials.OCTANGULITE, ArmorItem.Type.BOOTS,       new Item.Settings(),1));

    public static final CastingArmorItem CASTER_HELMET =        register("casting_helmet",      new CastingArmorItem(ModArmorMaterials.OCTANGULITE, ArmorItem.Type.HELMET,      new Item.Settings(),1,5,ModItemTags.FITS_IN_CASTERS,false),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom).addGroup(ExtraItemSettings.Group.Spells));
    public static final CastingArmorItem CASTER_CHESTPLATE =    register("casting_chestplate",  new CastingArmorItem(ModArmorMaterials.OCTANGULITE, ArmorItem.Type.CHESTPLATE,  new Item.Settings(),1,5,ModItemTags.FITS_IN_CASTERS,false),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom).addGroup(ExtraItemSettings.Group.Spells));
    public static final CastingArmorItem CASTER_LEGGINGS =      register("casting_leggings",    new CastingArmorItem(ModArmorMaterials.OCTANGULITE, ArmorItem.Type.LEGGINGS,    new Item.Settings(),1,5,ModItemTags.FITS_IN_CASTERS,false),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom).addGroup(ExtraItemSettings.Group.Spells));
    public static final CastingArmorItem CASTER_BOOTS =         register("casting_boots",       new CastingArmorItem(ModArmorMaterials.OCTANGULITE, ArmorItem.Type.BOOTS,       new Item.Settings(),1,5,ModItemTags.FITS_IN_CASTERS,false),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom).addGroup(ExtraItemSettings.Group.Spells));

    public static final CastingTrinketItem CASTER_CORE =    register("caster_core",new CastingTrinketItem(new Item.Settings().maxCount(1), 9*3,ModItemTags.FITS_IN_CASTERS,false),new ExtraItemSettings().group(ExtraItemSettings.Group.Spells));

    public static final TrinketItem MANIA_MASK =        register("mania_mask",new TrinketItem(new Item.Settings().maxCount(1)));
    public static final TrinketItem SORROW_MASK =       register("sorrow_mask",new TrinketItem(new Item.Settings().maxCount(1)));
    public static final TrinketItem PARANOIA_MASK =     register("paranoia_mask",new TrinketItem(new Item.Settings().maxCount(1)));
    public static final TrinketItem MELANCHOLY_MASK =   register("melancholy_mask",new TrinketItem(new Item.Settings().maxCount(1)));
    public static final TrinketItem ADAPTIVE_MASK =     register("adaptive_mask",new TrinketItem(new Item.Settings().maxCount(1)));

    public static final HammerItem IRON_HAMMER = register("iron_hammer",new HammerItem(5,-3.3f,ToolMaterials.IRON, ModBlockTags.HAMMER_MINEABLES, new FabricItemSettings(),
            10,1,10,20),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HammerItem GOLDEN_HAMMER = register("golden_hammer",new HammerItem(5,-3.3f,ToolMaterials.GOLD, ModBlockTags.HAMMER_MINEABLES,new FabricItemSettings(),
            40,1.2f,20,20),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HammerItem LEAD_HAMMER = register("lead_hammer",new HammerItem(5,-3.5f,ModToolMaterials.LEAD, ModBlockTags.HAMMER_MINEABLES,new FabricItemSettings(),
            10,1,10,30),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HammerItem MOLYBDENUM_HAMMER = register("molybdenum_hammer",new HammerItem(5.5f,-3.3f,ModToolMaterials.MOLYBDENUM, ModBlockTags.HAMMER_MINEABLES,new FabricItemSettings(),
            15,1,10,15),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HammerItem TITANIUM_HAMMER = register("titanium_hammer",new HammerItem(7f,-3f,ModToolMaterials.TITANIUM, ModBlockTags.HAMMER_MINEABLES,new FabricItemSettings(),
            20,1.1f,12,15),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final HammerItem MITHRIL_HAMMER = register("mithril_hammer",new HammerItem(7,-3.3f,ModToolMaterials.MITHRIL, ModBlockTags.HAMMER_MINEABLES,new FabricItemSettings(),
            30,1.1f,15,20),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Handheld));
    public static final OctanguliteHammerItem OCTANGULITE_HAMMER = register("octangulite_hammer",new OctanguliteHammerItem(7,-2.5f,ModToolMaterials.OCTANGULITE, ModBlockTags.HAMMER_MINEABLES,new FabricItemSettings(),
            40,1.2f,20,15,1),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Custom));

    // caster items
    //public static final SoulCastingItem CASTER_TEST = register("caster_test",new SoulCastingItem(new FabricItemSettings(),1));
    public static final SoulCastingItem NOVICE_GLOVE =              register("novice_glove",        new SoulCastingItem(new FabricItemSettings().maxCount(1),5,50,0.5f),new ExtraItemSettings().group(ExtraItemSettings.Group.Spells));
    public static final SoulCastingItem APPRENTICE_GLOVE =          register("apprentice_glove",    new SoulCastingItem(new FabricItemSettings().maxCount(1),9,100,1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom).group(ExtraItemSettings.Group.Spells));
    public static final SoulCastingItem JOURNEY_GLOVE =             register("journey_glove",       new SoulCastingItem(new FabricItemSettings().maxCount(1),9*2,200,1),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom).group(ExtraItemSettings.Group.Spells));
    public static final SoulCastingItem EXPERT_GLOVE =              register("expert_glove",        new SoulCastingItem(new FabricItemSettings().maxCount(1),9*4,500,1.5f),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom).group(ExtraItemSettings.Group.Spells));
    public static final SoulCastingItem MASTER_GLOVE =              register("master_glove",        new SoulCastingItem(new FabricItemSettings().maxCount(1),9*6,1000,2f),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom).group(ExtraItemSettings.Group.Spells));
    public static final SpellStoringItem SPELLSTORAGE_SMALL =       register("spellstorage_small",  new SpellStoringItem(new FabricItemSettings().maxCount(8),3,3),new ExtraItemSettings().group(ExtraItemSettings.Group.Spells));
    public static final SpellStoringItem SPELLSTORAGE_MEDIUM =      register("spellstorage_medium", new SpellStoringItem(new FabricItemSettings().maxCount(8),5,5),new ExtraItemSettings().group(ExtraItemSettings.Group.Spells));
    public static final SpellStoringItem SPELLSTORAGE_LARGE =       register("spellstorage_large",  new SpellStoringItem(new FabricItemSettings().maxCount(8),7,7),new ExtraItemSettings().group(ExtraItemSettings.Group.Spells));
    public static final SpellComponentStoringItem SPELLCOMPONENT =  register("spellcomponent",      new SpellComponentStoringItem(new FabricItemSettings()),new ExtraItemSettings().modelType(ExtraItemSettings.ModelType.Custom).group(ExtraItemSettings.Group.Spells));
    public static final StorageItem COMPONENT_POUCH =               register("component_pouch",     new StorageItem(new FabricItemSettings().maxCount(1),9*6, ModItemTags.COMPONENT_STORING,true),ExtraItemSettings.create().modelType(ExtraItemSettings.ModelType.Custom).group(ExtraItemSettings.Group.Spells));

    public static final PrecompiledSoulCastingItem PRECOMP_CASTER = register("precomp_caster",      new PrecompiledSoulCastingItem(new FabricItemSettings().maxCount(1),4,200,2),ExtraItemSettings.create().group(ExtraItemSettings.Group.Spells));

    public static final VariableStoringItem VARSTORAGE_SMALL =       register("varstorage_small",  new VariableStoringItem(new FabricItemSettings().maxCount(8),1),ExtraItemSettings.create().group(ExtraItemSettings.Group.Spells));
    public static final VariableStoringItem VARSTORAGE_MEDIUM =      register("varstorage_medium", new VariableStoringItem(new FabricItemSettings().maxCount(8),4),ExtraItemSettings.create().group(ExtraItemSettings.Group.Spells));
    public static final VariableStoringItem VARSTORAGE_LARGE =       register("varstorage_large",  new VariableStoringItem(new FabricItemSettings().maxCount(8),16),ExtraItemSettings.create().group(ExtraItemSettings.Group.Spells));

    public static final SoulStorageItem SOULSTORAGE_SMALL =       register("soulstorage_small",  new SoulStorageItem(new FabricItemSettings().maxCount(1),100,0.5f),ExtraItemSettings.create().group(ExtraItemSettings.Group.Spells));
    public static final SoulStorageItem SOULSTORAGE_MEDIUM =      register("soulstorage_medium", new SoulStorageItem(new FabricItemSettings().maxCount(1),400,0.75f),ExtraItemSettings.create().group(ExtraItemSettings.Group.Spells));
    public static final PonderableSoulStorageItem SOULSTORAGE_LARGE = register("soulstorage_large",  new PonderableSoulStorageItem(new FabricItemSettings().maxCount(1),1600,1f),ExtraItemSettings.create().group(ExtraItemSettings.Group.Spells));

    public static final SoulBoreItem SOUL_BORE =      register("soul_bore", new SoulBoreItem(new FabricItemSettings().maxCount(1),9),ExtraItemSettings.create().group(ExtraItemSettings.Group.Spells));


    public static final SoulPreviewItem SOUL_PREVIEW = register("soul_preview",new SoulPreviewItem(new FabricItemSettings()),new ExtraItemSettings().dontGroupItem());
    // geodes
    public static final Item GEODE_PREVIEW =                register("geode_preview",   new Item(new FabricItemSettings()),new ExtraItemSettings().dontGroupItem());
    public static final GeodeItem STONE_GEODE =             register("stone_geode",     new GeodeItem(new FabricItemSettings(),Geomancy.locate("geodes/stone")));
    public static final ComponentBagItem COMPONENT_BAG =    register("component_bag",   new ComponentBagItem(new FabricItemSettings(),Geomancy.locate("items/component_bag")),ExtraItemSettings.create().group(ExtraItemSettings.Group.Spells));

    public static final FoodItem LEAD_APPLE = register("lead_apple",new FoodItem(new FabricItemSettings().food(new FoodComponent.Builder().alwaysEdible().hunger(4).build()),le->{
        LeadUtil.addPoisoning((PlayerEntity) le,100);
        LeadUtil.syncPoisoning((PlayerEntity) le);
        if(le instanceof ServerPlayerEntity spe) LeadUtil.tryLeadEffects(spe);
    }),ExtraItemSettings.create());

    public static final MaddeningFoodItem OCTANGULITE_APPLE = register("octangulite_apple",new MaddeningFoodItem(new FabricItemSettings().food(new FoodComponent.Builder().alwaysEdible().hunger(8).saturationModifier(1.5f).build()),le->{
        if(!(le instanceof ServerPlayerEntity spe)) return;
        MadnessUtil.addMadness(spe,100);
        MadnessUtil.syncMadness(spe);
        MadnessUtil.tryMadnessEffects(spe);
    }),ExtraItemSettings.create());

    public static void register() {
        // initialize static fields
        // calling this method is sufficient to do that, actually
        // Add the suspicious substance to the composting registry with a 30% chance of increasing the composter's level.
        //CompostingChanceRegistry.INSTANCE.add(ModItems.SUSPICIOUS_SUBSTANCE, 0.3f);
        // Add the suspicious substance to the flammable block registry with a burn time of 30 seconds.
        // Remember, Minecraft deals with logical based-time using ticks.
        // 20 ticks = 1 second.
        //FuelRegistry.INSTANCE.add(ModItems.SUSPICIOUS_SUBSTANCE, 30 * 20);


        // Register the groups.
        Registry.register(Registries.ITEM_GROUP, MAIN_ITEM_GROUP_KEY, MAIN_ITEM_GROUP);
        Registry.register(Registries.ITEM_GROUP, JEWELRY_ITEM_GROUP_KEY, JEWELRY_ITEM_GROUP);
        Registry.register(Registries.ITEM_GROUP, SPELLS_ITEM_GROUP_KEY, SPELLS_ITEM_GROUP);
        Registry.register(Registries.ITEM_GROUP, LORE_ITEM_GROUP_KEY, LORE_ITEM_GROUP);

        // Register items to the custom item group.
        ItemGroupEvents.modifyEntriesEvent(MAIN_ITEM_GROUP_KEY).register(itemGroup -> {
            for(Item i : ExtraItemSettings.ITEMS_IN_MAIN_GROUP){
                itemGroup.add(i);
            }
        });

        ItemGroupEvents.modifyEntriesEvent(LORE_ITEM_GROUP_KEY).register(itemGroup -> {
            for(Item i : ExtraItemSettings.ITEMS_IN_LORE_GROUP){
                itemGroup.add(i);
            }
        });

        ItemGroupEvents.modifyEntriesEvent(JEWELRY_ITEM_GROUP_KEY).register(itemGroup -> {
            for(Item i : ExtraItemSettings.ITEMS_IN_JEWELRY_GROUP){
                itemGroup.add(i);
            }
        });

        ItemGroupEvents.modifyEntriesEvent(SPELLS_ITEM_GROUP_KEY).register(itemGroup -> {
            for(Item i : ExtraItemSettings.ITEMS_IN_SPELLS_GROUP){
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
            .icon(() -> IRON_RING.getDefaultStack()/*IRON_RING.addSlot(new ItemStack(ModItems.IRON_RING),new GemSlot(Items.DIAMOND,1))*/)
            .displayName(Text.translatable("itemGroup."+Geomancy.MOD_ID+".jewelry"))
            .build();

    public static final RegistryKey<ItemGroup> SPELLS_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(Geomancy.MOD_ID, "spells_item_group"));
    public static final ItemGroup SPELLS_ITEM_GROUP = FabricItemGroup.builder()
            .icon(JOURNEY_GLOVE::getDefaultStack)
            .displayName(Text.translatable("itemGroup."+Geomancy.MOD_ID+".spells"))
            .build();

    public static final RegistryKey<ItemGroup> LORE_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(Geomancy.MOD_ID, "lore_item_group"));
    public static final ItemGroup LORE_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> LORE_LOG_EXODIA_1.getDefaultStack())
            .displayName(Text.translatable("itemGroup."+Geomancy.MOD_ID+".lore"))
            .build();

    public static <T extends Item> T register(String id,T item) {
        return register(id,item,new ExtraItemSettings());
    }
    public static <T extends Item> T register(String id,T item,  ExtraItemSettings extraSettings) {
        // Create the identifier for the item.
        Identifier itemID = new Identifier(Geomancy.MOD_ID, id);

        // Register the item.
        T registeredItem = Registry.register(Registries.ITEM, itemID, item);

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

