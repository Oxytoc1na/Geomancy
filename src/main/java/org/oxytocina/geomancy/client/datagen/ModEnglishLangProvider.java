package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.event.KeyInputHandler;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.GemSlot;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModEnglishLangProvider extends FabricLanguageProvider {
    public ModEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us");
    }

    private TranslationBuilder tb;

    private HashMap<String,String> shortcuts = new HashMap<>();

    @Override
    public void generateTranslations(TranslationBuilder tb) {
        this.tb =tb;

        addShort("gb:mn","book.MODID.guidebook.main.");
        addShort("gb:sm","book.MODID.guidebook.smithing.");
        addShort("gb:oc","book.MODID.guidebook.octangulite.");
        addShort("gb:lr","book.MODID.guidebook.lore.");

        add(KeyInputHandler.LANG_CATEGORY_GEOMANCY,"Geomancy");
        add(KeyInputHandler.LANG_OPEN_SKILLTREE,"Skills");

        // Spells
        {
            add("MODID.spellmaker.delete",  "remove");
            add("MODID.spellmaker.rotate",  "rotate");
            add("MODID.spellmaker.grid.name","Spell name");
            add("MODID.spellmaker.grid.lib","Library Spell");
            add("MODID.spellmaker.dir.ne",  "Northeast");
            add("MODID.spellmaker.dir.e",   "East");
            add("MODID.spellmaker.dir.se",  "Southeast");
            add("MODID.spellmaker.dir.sw",  "Southwest");
            add("MODID.spellmaker.dir.w",   "West");
            add("MODID.spellmaker.dir.nw",  "Northwest");
            add("MODID.spellmaker.mode",    "Mode");
            add("MODID.spellmaker.parameter","Parameter");
            add("MODID.spellmaker.parameters","Parameters");
            add("MODID.spellmaker.modes.blocked",   "Blocked");
            add("MODID.spellmaker.modes.input",     "Input");
            add("MODID.spellmaker.modes.output",    "Output");
            add("MODID.spellmaker.type",            "Type");
            add("MODID.spellmaker.types.number",    "Number");
            add("MODID.spellmaker.types.any",       "Any");
            add("MODID.spellmaker.types.none",      "None");
            add("MODID.spellmaker.types.boolean",   "Boolean");
            add("MODID.spellmaker.types.text",      "Text");
            add("MODID.spellmaker.types.uuid",      "Entity");
            add("MODID.spellmaker.types.vector",    "Vector");

            add("MODID.spellcomponent.category.flowcontrol",    "Flow control");
            add("MODID.spellcomponent.category.provider",       "Provider");
            add("MODID.spellcomponent.category.arithmetic",     "Arithmetic");
            add("MODID.spellcomponent.category.effector",       "Effector");
            add("MODID.spellcomponent.category.reference",      "Reference");

            // flow control
            addSpell("conveyor","Conveyor","outputs its input");
            addSpell("gate","Gate","outputs its input if gate is true");
            addSpell("for","For","repeats signals");
            addSpell("not","Not","outputs true if it didn't receive a signal");
            // getters
            addSpell("entity_caster","Caster","returns the caster entity");
            addSpell("constant_boolean","Constant Truth","returns a configurable boolean");
            addSpell("constant_text","Constant Text","returns a configurable text");
            addSpell("constant_number","Constant Number","returns a configurable number");
            addSpell("constant_vector","Constant Vector","returns a configurable vector");
            // arithmetic
            addSpell("vector_entityspawn","Entity Spawn","returns the respawn point of the entity");
            addSpell("vector_entitypos","Entity Pos.","returns the position of the entity");
            addSpell("vector_entityeyepos","Entity Eye Pos.","returns the position of the eyes of the entity");
            addSpell("vector_entitydir","Entity Dir.","returns the direction the entity is looking in");
            addSpell("vector_split","Split Vec.","returns a vectors components");
            addSpell("vector_build","Build Vec.","constructs a vector from three numbers");
            addSpell("sum","Sum","adds two numbers or vectors");
            addSpell("subtract","Subtract","subtracts two numbers or vectors");
            addSpell("multiply","Multiply","multiplies two numbers or vectors");
            addSpell("divide","Divide","divides two numbers or vectors");
            addSpell("sin","Sine","returns the sine of the input");
            addSpell("cos","Cosine","returns the cosine of the input");
            addSpell("tan","Tangent","returns the tangent of the input");
            addSpell("exp","Exponent","returns a number to the power of another number");
            addSpell("raycast_pos","Raycast Pos.","returns the block position hit by a ray");
            addSpell("raycast_dir","Raycast Dir.","returns the block face hit by a ray");
            // effectors
            addSpell("print","Print","outputs a value to the casters chat");
            addSpell("fireball","Fireball","summons a fireball");
            addSpell("lightning","Lightning","summons a lightning bolt");
            addSpell("debug","Debug","enables debug mode if placed somewhere in the grid");
            addSpell("teleport","Teleport","teleports an entity to a specified position");
            addSpell("dimhop","Dimhop","teleports an entity to a specified dimension");
            addSpell("break","Break","attempts to destroy a block at a position");
            addSpell("push","Push","sets the velocity of an entity");
            // reference
            addSpell("action","Action","calls another installed spell");
            addSpell("provider","Provider","automatically returns the result of a spell");
            addSpell("ref_output","Ref. Output","gets a variable from the reference arguments");
            addSpell("ref_input","Ref. Input","sets a call result variable");
            addSpell("function","Function","calls a spell and returns a result");


            add("MODID.spells.debug.error","Error in spell component %1$s: %2$s");
            add("MODID.spells.debug.broke","Couldn't afford spell %1$s. Cost: %2$f, available: %3$f");
            add("MODID.spells.debug.wrongsignal","Component %1$s received wrong signal type. Got: %2$s, expected: %3$s");
            add("MODID.spells.debug.nosuchfunction","Component %1$s referenced non-existing spell %2$s");
            add("MODID.spells.debug.depthlimit","Component %1$s: Depth limit reached!");
            add("MODID.caster.nospells","No spells installed");
        }

        // Items
        {
            add("item.MODID.suspicious_substance"   , "Suspicious Substance");
            add("item.MODID.guidite_sword"          , "Guidite Sword");

            add("item.MODID.raw_mithril"            , "Raw Mithril");
            add("item.MODID.mithril_ingot"          , "Mithril Ingot");
            add("item.MODID.mithril_nugget"         , "Mithril Nugget");

            add("item.MODID.raw_molybdenum"            , "Raw Molybdenum");
            add("item.MODID.molybdenum_ingot"          , "Molybdenum Ingot");
            add("item.MODID.molybdenum_nugget"         , "Molybdenum Nugget");

            add("item.MODID.raw_titanium"            , "Raw Titanium");
            add("item.MODID.titanium_ingot"          , "Titanium Ingot");
            add("item.MODID.titanium_nugget"         , "Titanium Nugget");

            add("item.MODID.raw_lead"            , "Raw Lead");
            add("item.MODID.lead_ingot"          , "Lead Ingot");
            add("item.MODID.lead_nugget"         , "Lead Nugget");

            add("item.MODID.raw_octangulite"        , "Raw Octangulite");
            add("item.MODID.octangulite_ingot"      , "Octangulite Ingot");
            add("item.MODID.octangulite_nugget"     , "Octangulite Nugget");

            add("item.MODID.tourmaline" , "Tourmaline");
            add("item.MODID.orthoclase" , "Orthoclase");
            add("item.MODID.peridot"    , "Peridot");
            add("item.MODID.axinite"    , "Axinite");


            add("item.MODID.gold_bucket"            , "Molten Gold Bucket");
            add("item.MODID.music_disc_diggy"       , "Music Disc");
            add("item.MODID.music_disc_diggy.desc"  , "Diggy");

            add("item.MODID.iron_hammer"            , "Iron Hammer");
            add("item.MODID.iron_hammer.desc"       , "Can be used to smith items");
            add("item.MODID.mithril_hammer"         , "Mithril Hammer");
            add("item.MODID.mithril_hammer.desc"    , "Can be used to smith items");

            add("item.MODID.empty_artifact"         , "Empty Artifact");
            add("item.MODID.empty_artifact.desc"    , "Makes you feel incomplete");
            add("item.MODID.artifact_of_iron"       , "Artifact of Iron");
            add("item.MODID.artifact_of_iron.desc"  , "Makes you feel stalwart");
            add("item.MODID.artifact_of_gold"       , "Artifact of Gold");
            add("item.MODID.artifact_of_gold.desc"  , "Does something idk");

            add("item.MODID.iron_ring"              , "Iron Ring");
            add("item.MODID.iron_necklace"          , "Iron Necklace");
            add("item.MODID.iron_pendant"           , "Iron Pendant");
            add("item.MODID.gold_ring"              , "Gold Ring");
            add("item.MODID.gold_necklace"          , "Gold Necklace");
            add("item.MODID.gold_pendant"           , "Gold Pendant");
            add("item.MODID.copper_ring"            , "Copper Ring");
            add("item.MODID.copper_necklace"        , "Copper Necklace");
            add("item.MODID.copper_pendant"         , "Copper Pendant");
            add("item.MODID.mithril_ring"           , "Mithril Ring");
            add("item.MODID.mithril_necklace"       , "Mithril Necklace");
            add("item.MODID.mithril_pendant"        , "Mithril Pendant");
            add("item.MODID.molybdenum_ring"        , "Molybdenum Ring");
            add("item.MODID.molybdenum_necklace"    , "Molybdenum Necklace");
            add("item.MODID.molybdenum_pendant"     , "Molybdenum Pendant");
            add("item.MODID.titanium_ring"          , "Titanium Ring");
            add("item.MODID.titanium_necklace"      , "Titanium Necklace");
            add("item.MODID.titanium_pendant"       , "Titanium Pendant");
            add("item.MODID.lead_ring"              , "Lead Ring");
            add("item.MODID.lead_necklace"          , "Lead Necklace");
            add("item.MODID.lead_pendant"           , "Lead Pendant");
            add("item.MODID.octangulite_ring"       , "Octangulite Ring");
            add("item.MODID.octangulite_necklace"   , "Octangulite Necklace");
            add("item.MODID.octangulite_pendant"    , "Octangulite Pendant");
            add("tooltip.geomancy.jewelry.nogems"   ,"No Gems");
            add("tooltip.geomancy.jewelry.pendant1" ,"Empowers other worn gems of the");
            add("tooltip.geomancy.jewelry.pendant2" ,"same type as slotted in this item");

            add("tooltip.geomancy.jewelry.gemeffect.diamond"        ,"provides %1$s armor");
            add("tooltip.geomancy.jewelry.gemeffect.lapis_lazuli"   ,"increases XP drops by %1$s%%");
            add("tooltip.geomancy.jewelry.gemeffect.emerald"        ,"increases your fortune by %1$s levels");
            add("tooltip.geomancy.jewelry.gemeffect.amethyst_shard" ,"increases soul regeneration speed of this item");
            add("tooltip.geomancy.jewelry.gemeffect.heart_of_the_sea","provides water breathing");
            add("tooltip.geomancy.jewelry.gemeffect.ender_pearl"    ,"Teleport up to %1$s blocks with a hotkey");
            add("tooltip.geomancy.jewelry.gemeffect.end_crystal"    ,"provides level %1$s regeneration");
            add("tooltip.geomancy.jewelry.gemeffect.tourmaline"     ,"makes you %1$s%% faster on land");
            add("tooltip.geomancy.jewelry.gemeffect.axinite"        ,"makes you mine %1$s%% faster");
            add("tooltip.geomancy.jewelry.gemeffect.orthoclase"     ,"makes you %1$s%% resistant to debuffs");
            add("tooltip.geomancy.jewelry.gemeffect.peridot"        ,"makes your harvests more bountiful");
            add("tooltip.geomancy.jewelry.gemeffect.prismarine_crystals","makes you %1$s%% faster in water");
            add("tooltip.geomancy.jewelry.gemeffect.nether_star"    ,"provides effects depending on the base material");
            add("tooltip.geomancy.jewelry.gemeffect.ender_eye"      ,"highlights mobs in a %1$s block radius");
            add("tooltip.geomancy.jewelry.gemeffect.echo_shard"     ,"increases soul storage size of this item");

            add("tooltip.geomancy.jewelry.unsmith"  ,"Salvages gems");
            add("tooltip.geomancy.jewelry.quality"  ,"Quality");

            add("item.MODID.stone_geode"            , "Stone Geode");
            add("tooltip.MODID.geodes"              , "Can be hammered open...if you're careful.");
            add("item.MODID.explorers_map.ancient_hall", "Directions home");

            add("item.MODID.spellstorage_small",        "Minute Spellcradle");
            add("item.MODID.spellstorage_medium",       "Mundane Spellcradle");
            add("item.MODID.spellstorage_large",        "Spacious Spellcradle");
            add("item.MODID.spellcomponent",            "Spell Component");

            add("MODID.spellcomponent.empty",               "empty");
            add("MODID.spellstorage.empty",                 "empty");
            add("MODID.spellstorage.unnamed",               "unnamed");

            add("item.MODID.spellglove",        "Spellglove");


        }

        // Blocks
        {
            add("block.MODID.condensed_dirt"        , "Condensed Dirt");

            add("block.MODID.mithril_ore"           , "Mithril Ore");
            add("block.MODID.deepslate_mithril_ore" , "Deepslate Mithril Ore");
            add("block.MODID.raw_mithril_block"     , "Block of Raw Mithril");
            add("block.MODID.mithril_block"         , "Block of Mithril");
            add("block.MODID.cut_mithril"           , "Cut Mithril");
            add("block.MODID.mithril_bricks"        , "Mithril Bricks");
            add("block.MODID.mithril_brick_stairs"  , "Mithril Brick Stairs");
            add("block.MODID.mithril_brick_slab"    , "Mithril Brick Slab");
            add("block.MODID.mithril_brick_wall"    , "Mithril Brick Wall");

            add("block.MODID.molten_gold"           , "Molten Gold");
            add("block.MODID.mithril_anvil"         , "Mithril Anvil");
            add("block.MODID.gilded_deepslate"      , "Gilded Deepslate");
            add("block.MODID.decorated_gilded_deepslate", "Decorated Gilded Deepslate");

            add("block.MODID.smithery_block"            , "Smithery");
            add("container.MODID.smithery_block"        , "Smithery");
            add("message.MODID.smithery.fail.break"     ,"that didn't sound good...");

            add("block.MODID.spellmaker_block"            , "Spellmaker");
            add("container.MODID.spellmaker_block"        , "Spellmaker");

            add("block.MODID.octangulite_ore"           , "Octangulite Ore");
            add("block.MODID.deepslate_octangulite_ore" , "Deepslate Octangulite Ore");
            add("block.MODID.raw_octangulite_block"     , "Octangulite Scrap");
            add("block.MODID.octangulite_block"         , "Block of Octangulite");
            add("block.MODID.cut_octangulite"           , "Cut Octangulite");
            add("block.MODID.octangulite_bricks"        , "Octangulite Bricks");
            add("block.MODID.octangulite_brick_stairs"  , "Octangulite Brick Stairs");
            add("block.MODID.octangulite_brick_slab"    , "Octangulite Brick Slab");
            add("block.MODID.octangulite_brick_wall"    , "Octangulite Brick Wall");

            add("block.MODID.molybdenum_ore"           , "Molybdenum Ore");
            add("block.MODID.deepslate_molybdenum_ore" , "Deepslate Molybdenum Ore");
            add("block.MODID.raw_molybdenum_block"     , "Block of raw Molybdenum");
            add("block.MODID.molybdenum_block"         , "Block of Molybdenum");
            add("block.MODID.cut_molybdenum"           , "Cut Molybdenum");
            add("block.MODID.molybdenum_bricks"        , "Molybdenum Bricks");
            add("block.MODID.molybdenum_brick_stairs"  , "Molybdenum Brick Stairs");
            add("block.MODID.molybdenum_brick_slab"    , "Molybdenum Brick Slab");
            add("block.MODID.molybdenum_brick_wall"    , "Molybdenum Brick Wall");

            add("block.MODID.titanium_ore"           , "Titanium Ore");
            add("block.MODID.deepslate_titanium_ore" , "Deepslate Titanium Ore");
            add("block.MODID.raw_titanium_block"     , "Block of raw Titanium");
            add("block.MODID.titanium_block"         , "Block of Titanium");
            add("block.MODID.cut_titanium"           , "Cut Titanium");
            add("block.MODID.mossy_cut_titanium"     , "Mossy Cut Titanium");
            add("block.MODID.titanium_bricks"        , "Titanium Bricks");
            add("block.MODID.titanium_brick_stairs"  , "Titanium Brick Stairs");
            add("block.MODID.titanium_brick_slab"    , "Titanium Brick Slab");
            add("block.MODID.titanium_brick_wall"    , "Titanium Brick Wall");

            add("block.MODID.lead_ore"           , "Lead Ore");
            add("block.MODID.deepslate_lead_ore" , "Deepslate Lead Ore");
            add("block.MODID.raw_lead_block"     , "Block of raw Lead");
            add("block.MODID.lead_block"         , "Block of Lead");
            add("block.MODID.cut_lead"           , "Cut Lead");
            add("block.MODID.lead_bricks"        , "Lead Bricks");
            add("block.MODID.lead_brick_stairs"  , "Lead Brick Stairs");
            add("block.MODID.lead_brick_slab"    , "Lead Brick Slab");
            add("block.MODID.lead_brick_wall"    , "Lead Brick Wall");
        }

        // Enchantments
        add("enchantment.MODID.skillful"    , "Skillful");
        add("enchantment.MODID.mighty"      , "Mighty");
        add("enchantment.MODID.brilliance"  , "Brilliance");
        add("enchantment.MODID.soul_saver"  , "Soul Saver");

        // damage types
        add("death.attack.geomancy.duplicate_trinkets", "%1$s felt their own hubris");
        add("death.attack.geomancy.molten_gold", "%1$s fell into King Midas' bathtub");

        // Misc
        add("itemGroup.MODID.main",     "Geomancy");
        add("itemGroup.MODID.jewelry",  "Geomancy Jewelry");
        add("itemGroup.MODID.spells",   "Geomancy Spells");
        add("geomancy.message.lead.tingling", "Your fingers are tingling...");
        add("geomancy.message.lead.nausea", "You feel like throwing up...");
        add("geomancy.message.lead.poison", "You feel a sharp pain in your head...");
        add("geomancy.message.lead.joints", "Your joints hurt...");

        // Advancements
        {
            addAdvancement(".main","Geomancy","A world of buried treasures and wonders");
            addAdvancement(".main.get_mithril","Precious","Discover Raw Mithril");
            addAdvancement(".main.get_octangulite","Otherworldy","Discover something strange");
            addAdvancement(".main.get_titanium","Indestructible","Discover something really tough");
            addAdvancement(".main.get_lead","Heavy","Discover something heavy and soft");
            addAdvancement(".main.get_molybdenum","Eccentric","Discover something quirky");
            addAdvancement(".main.get_molten_gold","Greedy","Discover Molten Gold");
            addAdvancement(".main.get_gilded_deepslate","Adorned","Discover Gilded Deepslate");
            addAdvancement(".main.simple_duplicate_trinkets","Hubris","Try and fail to equip two artifacts of the same type at once");
            addAdvancement(".main.simple_tried_to_take_smithery_result","You've gotta hammer it!","You need to smack the smithery with a hammer to start crafting");

            addAdvancement(":milestones/milestone_smithery","The Craft of the Ancients","Obtain the smithery");

            addAdvancement(":location/ancient_hall","Ancient","Discover the once prosperous remains of the ancients");
            addAdvancement(":location/octangula","Geometric","Discover the vessel of something else");
            addAdvancement(":location/digsite","Imposing","Discover the discarded remains of something else");

            addAdvancement(".octangulite.get_spellcomponent","Building Blocks","Discover the foundation of soulcasting");
        }

        // Guidebook
        {
            add("book.MODID.guidebook.name", "Notes on Minerals");
            add("book.MODID.guidebook.tooltip", "we can never dig too deep");

            add("item.MODID.guidebook","Notes on Minerals");


            add(getS("gb:mn")+"name", "Discovery");
            {
                addGBEntryAndInfo(getS("gb:mn")+"intro","Discovery");
                add(getS("gb:mn")+"intro.description", "");
                add(getS("gb:mn")+"intro.info.text", "There exist ruins in this world, ruins of ancient deepslate and inscriptions of rare metals.\nWhat do they mean? I will write down my findings in this book.");

                addGBEntryAndInfo(getS("gb:mn")+"gold","Molten Gold");
                add(getS("gb:mn")+"gold.description", "");
                add(getS("gb:mn")+"gold.intro.text", "Fascinating. I'm not used to finding fluids other than water or lava... yet here's some molten gold.\nWhat is equally as fascinating is that the only places I've found it in seem to be these ruins... How has it not resolidified after all these years?");
                add(getS("gb:mn")+"gold.gold_bucket.text", "I doubt I'll be able to dip my apples or carrots in it...");

                addGBEntryAndInfo(getS("gb:mn")+"deepslate","Gilded Deepslate");
                add(getS("gb:mn")+"deepslate.description", "");
                add(getS("gb:mn")+"deepslate.intro.text", "I have discovered these polished deepslate blocks in some of those ruins. Some of them bear ancient symbols and sigils. I wonder what they mean.");
                add(getS("gb:mn")+"deepslate.gilded_deepslate.text", "It's skillfully adorned with glittering gold.");
                add(getS("gb:mn")+"deepslate.decoaretd_gilded_deepslate.text", "It appears to display various tools, weapons, and treasure.");

                addGBEntryAndInfo(getS("gb:mn")+"mithril","Mithril");
                add(getS("gb:mn")+"mithril.description"  , "");
                add(getS("gb:mn")+"mithril.intro.text", "This incredibly rare metal is impressively durable for its light weight. It shines with a bright white color. I can sense that there is more to it than tools and armor.");
                add(getS("gb:mn")+"mithril.mithril_ingot.text", "It is also definitely not edible.");
            }

            add(getS("gb:sm")+"name", "Smithing");
            {
                addGBEntryAndInfo(getS("gb:sm")+"intro","Smithing");
                add(getS("gb:sm")+"intro.description","");
                add(getS("gb:sm")+"intro.info.text","This station will allow me to put my elbow grease to work and form all kinds of metals into new shapes!");
                add(getS("gb:sm")+"intro.smithery.title",getItemName(ModBlocks.SMITHERY));
                add(getS("gb:sm")+"intro.smithery.text","It is time for me to swing the hammer like the ancients did!");

                addGBEntryAndInfo(getS("gb:sm")+"hammers","Hammers");
                add(getS("gb:sm")+"hammers.description","");
                add(getS("gb:sm")+"hammers.info.text","The hammer that one uses to whack the workpiece determines how successful they shall be in doing so.");
                add(getS("gb:sm")+"hammers.iron.title",getItemName(ModItems.IRON_HAMMER));
                add(getS("gb:sm")+"hammers.iron.text","Probably the most basic hammer for smithing there is.");
                add(getS("gb:sm")+"hammers.mithril.title",getItemName(ModItems.MITHRIL_HAMMER));
                add(getS("gb:sm")+"hammers.mithril.text","Mithrils durability and feathery weight makes it ideal at continuously hitting a workpiece without getting tired.");

                addGBEntryAndInfo(getS("gb:sm")+"jewelry_bases","Jewelry");
                add(getS("gb:sm")+"jewelry_bases.description","");
                add(getS("gb:sm")+"jewelry_bases.info.text","Inspired by the ancient scriptures detailing jewelry, I have devised these recipes for empty jewelry items.");
                add(getS("gb:sm")+"jewelry_bases.iron_ring.text","This vessel holds up to one gem and can be worn on ones fingers.");
                add(getS("gb:sm")+"jewelry_bases.iron_necklace.text","This vessel holds up to one gem and can be worn around ones neck.");

                addGBEntryAndInfo(getS("gb:sm")+"artifacts","Artifacts");
                add(getS("gb:sm")+"artifacts.description","");
                add(getS("gb:sm")+"artifacts.info.text","I am coming the magic of the ancients one step closer with these.");
                add(getS("gb:sm")+"artifacts.empty.text","Not useful on its own, this empty vessel will serve as a base to imbue with elemental magic.");
                add(getS("gb:sm")+"artifacts.iron.text","This artifact bestows its wearer with armor and knockback resistance.");
                add(getS("gb:sm")+"artifacts.gold.text","???");


                addGBEntryAndInfo(getS("gb:sm")+"gems","Gemstones");
                add(getS("gb:sm")+"gems.description","");
                add(getS("gb:sm")+"gems.info.text","This page holds all the different kinds of gemstones I can put into my jewelry.");

                addShort("gems.difficulty.negligible","negligible");
                addShort("gems.difficulty.noticeable","noticeable");
                addShort("gems.difficulty.immense","immense");
                addShort("gems.difficulty.legendary","legendary");
                addShort("gems.progress.negligible","negligible");
                addShort("gems.progress.prolonging","prolonging");
                addShort("gems.progress.molasses","molasses");
                addShort("gems.progress.odyssey","odyssey");

                addGemStatsText(getS("gb:sm")+"gems.diamond.text", Items.DIAMOND,"Provides two armor points.");
                addGemStatsText(getS("gb:sm")+"gems.emerald.text", Items.EMERALD,"Increases fortune.");
                addGemStatsText(getS("gb:sm")+"gems.lapis_lazuli.text", Items.LAPIS_LAZULI,"Increases XP drops.");
                addGemStatsText(getS("gb:sm")+"gems.amethyst_shard.text", Items.AMETHYST_SHARD,"Increases the items aura regeneration speed");
                addGemStatsText(getS("gb:sm")+"gems.heart_of_the_sea.text", Items.HEART_OF_THE_SEA,"Provides water breathing.");
                addGemStatsText(getS("gb:sm")+"gems.ender_pearl.text", Items.ENDER_PEARL,"Provides water breathing.");
                addGemStatsText(getS("gb:sm")+"gems.end_crystal.text", Items.END_CRYSTAL,"Lets you teleport.");
                addGemStatsText(getS("gb:sm")+"gems.tourmaline.text", Items.HEART_OF_THE_SEA,"Makes you move faster.");
                addGemStatsText(getS("gb:sm")+"gems.axinite.text", Items.HEART_OF_THE_SEA,"Makes you mine faster.");
                addGemStatsText(getS("gb:sm")+"gems.orthoclase.text", Items.HEART_OF_THE_SEA,"Provides debuff resistance.");
                addGemStatsText(getS("gb:sm")+"gems.peridot.text", Items.HEART_OF_THE_SEA,"Increases harvest yield.");
                addGemStatsText(getS("gb:sm")+"gems.prismarine_crystals.text", Items.PRISMARINE_CRYSTALS,"Makes you swim faster.");
                addGemStatsText(getS("gb:sm")+"gems.nether_star.text", Items.NETHER_STAR,"Effects resemble those of a beacon, but vary depending on the base material of the jewelry.");
                addGemStatsText(getS("gb:sm")+"gems.ender_eye.text", Items.ENDER_EYE,"Lets you see entities through walls.");
                addGemStatsText(getS("gb:sm")+"gems.echo_shard.text", Items.ECHO_SHARD,"Increases the size of the items aura.");
            }

            add(getS("gb:oc")+"name", "Octangulite");
            {
                addGBEntryAndInfo(getS("gb:oc")+"intro","Octangulite");
                add(getS("gb:oc")+"intro.description"  , "");
                add(getS("gb:oc")+"intro.info.text", "This... strange substance seems to shift in color when I don't look. Its hardness also doesn't seem to stay consistent. It feels out of this world. I am not quite sure what to do with it.");
                add(getS("gb:oc")+"intro.raw_octangulite.text", "Undulating.");

                addGBEntryAndInfo(getS("gb:oc")+"spellcomponents","Spell Components");
                add(getS("gb:oc")+"spellcomponents.0.text"  , "");
                add(getS("gb:oc")+"spellcomponents.1.text"  , "");
            }

            add(getS("gb:lr")+"name", "History");
            {
                // goldsmith
                {
                    addShort("goldsmith_lover","SEXY DWARF");

                    add("item.MODID.lorebook_goldsmith_1","Chronicles of the Goldsmith Pt. 1");
                    add("item.MODID.lorebook_goldsmith_1.tooltip","Notes from a bygone era");

                    addGBEntryAndInfo(getS("gb:lr")+"goldsmith_1","Chronicles of the Goldsmith Pt. 1");
                    add(getS("gb:lr")+"goldsmith_1.description"  , "Notes from a bygone era");
                    add(getS("gb:lr")+"goldsmith_1.1.text",
                            """
                              I will be writing down notes on the different kinds of metals I have been using to make jewelry here.
                              \\
                              []()
                              - [Gold](item://minecraft:gold_ingot): A classic. Can hold a good amount of gems. Relatively easy to work with. Abundant.
                              \\
                              []()
                              - [Iron](item://minecraft:iron_ingot): A little odd. Pretty terrible capacity. Common.
                              """);
                    add(getS("gb:lr")+"goldsmith_1.2.text", """
                        - [Copper](item://minecraft:copper_ingot): Makes me itchy. Pretty terrible capacity. Abundant.
                        \\
                        []()
                        - [Titanium](item://geomancy:titanium_ingot): The tough one. Great capacity. Hard to work with. Rare.
                        \\
                        []()
                        - [Mithril](item://geomancy:mithril_ingot): Legendary. Great capacity. Hard to work with. Incredibly rare.
                        """);
                    add(getS("gb:lr")+"goldsmith_1.3.text", """
                        - [Lead](item://geomancy:lead_ingot): DO NOT USE. Workable and common, but will slowly poison anyone who touches it. Only use for people I don't like.
                        \\
                        []()
                        - [Octangulite](item://geomancy:octangulite_ingot): Never used or seen it. Folklore describes it as cursed. Would like to see what the fuzz is all about some day.
                        """);

                    add("item.MODID.lorebook_goldsmith_2","Chronicles of the Goldsmith Pt. 2");
                    add("item.MODID.lorebook_goldsmith_2.tooltip","On loss");

                    addGBEntryAndInfo(getS("gb:lr")+"goldsmith_2","Chronicles of the Goldsmith Pt. 2");
                    add(getS("gb:lr")+"goldsmith_2.description"  , "On loss");
                    add(getS("gb:lr")+"goldsmith_2.1.text", """
                        There was an accident in the deep today. A support broke and a section got filled with molten rock.
                        \\
                        {lover} didn't make it out in time. The charred remains were retrieved after the flow was stopped.
                        \\
                        I've been mourning for days now, but perhaps there is a way to get them back.
                        """.replace("{lover}",getS("goldsmith_lover")));
                    add(getS("gb:lr")+"goldsmith_2.2.text", """
                         There exist legends, myths, stories and cautionary tales of a certain mineral that allows those thought lost to speak with the living.
                         \\
                         Maybe, just maybe, I can get them to talk to me again.
                         \\
                         That is all I ask.
                         """);
                    add(getS("gb:lr")+"goldsmith_2.3.text", """
                         """);


                    add("item.MODID.lorebook_goldsmith_3","Chronicles of the Goldsmith Pt. 3");
                    add("item.MODID.lorebook_goldsmith_3.tooltip","Channeling");

                    addGBEntryAndInfo(getS("gb:lr")+"goldsmith_3","Chronicles of the Goldsmith Pt. 3");
                    add(getS("gb:lr")+"goldsmith_3.description"  , "Channeling");
                    add(getS("gb:lr")+"goldsmith_3.1.text", """
                        I've done it! {lover} came back to me, and they're in this crystal I picked for them.
                        \\
                        It looks like an ordinary amethyst, but they can speak to me through it.
                        \\
                        It's a miracle!
                        """.replace("{lover}",getS("goldsmith_lover")));
                    add(getS("gb:lr")+"goldsmith_3.2.text", """
                         It is difficult to make out their words though, but I believe I know the solution.
                         \\
                         I will slot their gem into a ring made of octangulite.
                         \\
                         The legends say it makes the dead talk. I am certain that it will allow {lover} to hold conversations with me!
                         """.replace("{lover}",getS("goldsmith_lover")));
                    add(getS("gb:lr")+"goldsmith_3.3.text", """
                         """);

                    add("item.MODID.lorebook_goldsmith_4","Chronicles of the Goldsmith Pt. 4");
                    add("item.MODID.lorebook_goldsmith_4.tooltip","Madness");

                    addGBEntryAndInfo(getS("gb:lr")+"goldsmith_4","Chronicles of the Goldsmith Pt. 4");
                    add(getS("gb:lr")+"goldsmith_4.description"  , "Madness");
                    add(getS("gb:lr")+"goldsmith_4.1.text", """
                        My ring is a success.
                        \\
                        They don't stop talking.
                        \\
                        The whispers are unending.
                        \\
                        It is beautiful.
                        """.replace("{lover}",getS("goldsmith_lover")));
                    add(getS("gb:lr")+"goldsmith_4.2.text", """
                         {lover} often speaks of their perspective.
                         \\
                         They make it sound wonderful.
                         \\
                         They have so many friends now.
                         \\
                         I envy them.
                         \\
                         I want to be with them.
                         """.replace("{lover}",getS("goldsmith_lover")));
                    add(getS("gb:lr")+"goldsmith_4.3.text", """
                         """);
                }

                // war
                {
                    add("item.MODID.lorebook_war_1","War Plans Pt. 1");
                    add("item.MODID.lorebook_war_1.tooltip","Report 78S-1");

                    addGBEntryAndInfo(getS("gb:lr")+"war_1","War Plans Pt. 1");
                    add(getS("gb:lr")+"war_1.description"  , "Report 78S-1");
                    add(getS("gb:lr")+"war_1.1.text","""
                        Dwarven High Regal Defense Department (DHRDD)
                        \\
                        Report 78S-1
                        \\
                        \\
                        Several large floating vessels of metal of unknown origin have landed near the Swamp of Shorg.
                        \\
                        Their captains are rigid yet in motion. They levitate akin to their vessels. Their forms are foreign to all other living beings.
                        \\
                        Their motives are as of yet unknown.
                        """);
                    add(getS("gb:lr")+"war_1.2.text", """
                        """);
                    add(getS("gb:lr")+"war_1.3.text", """
                        """);

                    add("item.MODID.lorebook_war_2","War Plans Pt. 2");
                    add("item.MODID.lorebook_war_2.tooltip","Report 78S-5");

                    addGBEntryAndInfo(getS("gb:lr")+"war_2","War Plans Pt. 2");
                    add(getS("gb:lr")+"war_2.description"  , "Report 78S-5");
                    add(getS("gb:lr")+"war_2.1.text","""
                        Dwarven High Regal Defense Department (DHRDD)
                        \\
                        Report 78S-5
                        \\
                        \\
                        The Invaders have begun what appears to be a mining operation in the Swamp of Shorg and the Jungle of Yharin.
                        \\
                        According to our scouts, they are solely extracting octangulite, while discarding precious metals and gemstones.
                        """);
                    add(getS("gb:lr")+"war_2.2.text", """
                        """);
                    add(getS("gb:lr")+"war_2.3.text", """
                        """);

                    add("item.MODID.lorebook_war_3","War Plans Pt. 3");
                    add("item.MODID.lorebook_war_3.tooltip","Order 78S-W1");

                    addGBEntryAndInfo(getS("gb:lr")+"war_3","War Plans Pt. 3");
                    add(getS("gb:lr")+"war_3.description"  , "Order 78S-W1");
                    add(getS("gb:lr")+"war_3.1.text","""
                        Dwarven High Regal Defense Department (DHRDD)
                        \\
                        Order 78S-W1
                        \\
                        \\
                        The Invaders' hostile actions and sacrilegious disturbance of the dead warrants vengeance.
                        \\
                        By next sunrise, we shall launch operation Octangula.
                        \\
                        Gather the armies. We strike at dawn.
                        """);
                    add(getS("gb:lr")+"war_3.2.text", """
                        """);
                    add(getS("gb:lr")+"war_3.3.text", """
                        """);
                }

                // expedition
                {
                    add("item.MODID.lorelog_expedition_1","SOL III EXPEDITION ORDER");
                    add("item.MODID.lorelog_expedition_1.tooltip","INITIATION");

                    addGBEntryAndInfo(getS("gb:lr")+"expedition_1","SOL III EXPEDITION ORDER");
                    add(getS("gb:lr")+"expedition_1.description"  , "INITIATION");
                    add(getS("gb:lr")+"expedition_1.1.text","""
                        Dwarven High Regal Defense Department (DHRDD)
                        \\
                        Report 78S-1
                        \\
                        \\
                        Several large floating vessels of metal of unknown origin have landed near the Swamp of Shorg.
                        \\
                        Their captains are rigid yet in motion. They levitate akin to their vessels. Their forms are foreign to all other living beings.
                        \\
                        Their motives are as of yet unknown.
                        """);
                    add(getS("gb:lr")+"expedition_1.2.text", """
                        """);
                    add(getS("gb:lr")+"expedition_1.3.text", """
                        """);
                }

            }


        }

        // Entities
        {
            add("entity.MODID.stellge_engineer"         , "Stellge Engineer");
            add("item.MODID.stellge_engineer_spawn_egg" , "Stellge Engineer Spawn Egg");
        }

        // REI, EMI
        {
            add("container.MODID.rei.smithing.title","Smithing");
        }

        tb=null;
    }

    // helper function
    void add(String key, String value){
        key=key.replace("MODID", Geomancy.MOD_ID);
        tb.add(key,value
                /*.replace("\n","     \n")
                .replace("\r","     \n")
                .replace(System.lineSeparator(),"     \n")*/
        );
    }

    void add(String[] keys, String value){
        add("",keys,value);
    }

    void add(String prefix,String[] keys, String value){
        for (int i = 0; i < keys.length; i++) {
            add(prefix + keys[i],value);
        }
    }

    void addShort(String key, String value){
        shortcuts.put(key,value);
    }

    void addGBEntryAndInfo(String prefix, String value){
        add(prefix+".",new String[]{"name","info.title"}, value);
    }

    String getS(String key){
        if(shortcuts.containsKey(key)) return shortcuts.get(key);
        return key;
    }

    String getItemName(ItemConvertible item){
        return item.asItem().getName().getString();
    }

    void addGemStatsText(String key, Item gemItem, String prefix){
        addGemStatsText(key,gemItem,prefix,"");
    }

    void addGemStatsText(String key, Item gemItem, String prefix, String suffix){
        String res = prefix;


        float difficulty = GemSlot.getGemDifficulty(gemItem.getDefaultStack());
        int difficultyColor = Toolbox.gradient()
                .add(0,0x00C917)
                .add(5,0xDAE500)
                .add(20,0xFF1500)
                .add(50,0xB200FF)
                .get(difficulty) & 0x00FFFFFF;
        String difficultyText = getS("gems.difficulty.negligible");
        if(difficulty > 50) difficultyText = getS("gems.difficulty.legendary");
        else if(difficulty > 20) difficultyText = getS("gems.difficulty.immense");
        else if(difficulty > 5) difficultyText = getS("gems.difficulty.noticeable");
        difficultyText = "[#]("+Integer.toHexString(difficultyColor)+")"+difficultyText+"[#]()";

        float progressCost = GemSlot.getGemProgressCost(gemItem.getDefaultStack());
        int progressColor = Toolbox.gradient()
                .add(0,0x00C917)
                .add(15,0xDAE500)
                .add(35,0xFF1500)
                .add(100,0xB200FF)
                .get(progressCost) & 0x00FFFFFF;
        String progressText = getS("gems.progress.negligible");
        if(progressCost > 100) progressText = getS("gems.progress.odyssey");
        else if(progressCost > 35) progressText = getS("gems.progress.molasses");
        else if(progressCost > 15) progressText = getS("gems.progress.prolonging");
        progressText = "[#]("+Integer.toHexString(progressColor)+")"+progressText+"[#]()";

        res += "\\\n\\\nDifficulty: "+ difficultyText;
        res += "\\\nComplexity: "+ progressText;

        if(!Objects.equals(suffix, ""))
            res+="\\\n\\\n"+suffix;
        add(key,res);
    }

    void addSpell(String spell, String name, String description){
        addSpell(spell,name,description,description);
    }

    void addSpell(String spell, String name, String description,String modonomiconDescription){
        add("MODID.spellcomponent."+spell,name);
        add("MODID.tooltip.spellcomponent."+spell,description);

        add(getS("gb:oc")+"spellcomponents."+spell+".text"  , modonomiconDescription);
    }

    void addAdvancement(String advancement, String name, String description){
        add("advancement.MODID"+advancement+".name"       , name);
        add("advancement.MODID"+advancement+".description", description);
    }
}