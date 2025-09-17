package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.client.event.KeyInputHandler;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.GemSlot;
import org.oxytocina.geomancy.registries.ModItemTags;
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

    private final HashMap<String,String> shortcuts = new HashMap<>();

    @Override
    public void generateTranslations(TranslationBuilder tb) {
        this.tb =tb;

        addShort("gb:mn","book.MODID.guidebook.main.");
        addShort("gb:sm","book.MODID.guidebook.smithing.");
        addShort("gb:oc","book.MODID.guidebook.octangulite.");
        addShort("gb:lr","book.MODID.guidebook.lore.");
        addShort("gb:sc","book.MODID.guidebook.soulcasting.");

        // Spells
        {
            add("MODID.spellmaker.delete",  "remove");
            add("MODID.spellmaker.rotate",  "rotate");
            add("MODID.spellmaker.grid.name","Spell name");
            add("MODID.spellmaker.grid.lib","Library Spell");
            add("MODID.spellmaker.grid.appearance","Menu Appearance");
            add("MODID.spellmaker.grid.displaysas","Displays as");
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
            add("MODID.spellmaker.types.list",      "List");
            add("MODID.spellmaker.abouttoplace1",   "You are about to place");
            add("MODID.spellmaker.abouttoplace2",   "this component.");
            add("MODID.spellmaker.insertiontip",    "Insert a Spellcradle");
            add("MODID.spellmaker.tip",             "Spellmaking Tip");
            // tips
            {
                addSpellmakerTip("references","References","Components with copper as a background allow you to interact with other spells installed in a casting item!");
                addSpellmakerTip("sum","The versatility of summation","The sum component can sum up numbers and vectors, acts as an OR for booleans, concatenates texts and lists, inserts signals into lists, and appends the text representation of most things to texts.\nA true multitalent!");
                addSpellmakerTip("dimhop","Dimensional hopping","Known dimensional identifiers are \"minecraft:overworld\", \"minecraft:the_nether\" and \"minecraft:the_end\". You'll appear at the same coordinates you're currently at, just in a different dimension.");
                addSpellmakerTip("casterleggings","Leggings and groundedness","If you jump while wearing Caster Leggings, any \"Entity Grounded\" component inside the triggered spell will output true for you if you weren't airborne at the time.");
                addSpellmakerTip("casterboots","Boots and eloquence","If you say something while wearing Caster Leggings, an argument named \"message\" containing what you said is passed into the cast spell.\nAccess it with a Ref. Output component to change the behavior of your spell(s)!");
                addSpellmakerTip("casterhelmet","Helmets and aggression","If you attack something while wearing a Caster Helmet, an argument named \"target\" containing the thing that you hit is passed into the cast spell.\nAccess it with a Ref. Output component to do even more harm!");
                addSpellmakerTip("casterchestplate","Chestplates and stalwartness","If you get attacked by something while wearing a Caster Chestplate, an argument named \"attacker\" containing the thing that just hit you is passed into the cast spell.\nTake revenge on that skeleton without having to turn around!");
                addSpellmakerTip("degrading","Degrading","The degrade block component can turn metal to ore, ore to stone, stone to cobblestone, cobblestone to gravel, gravel to sand, and sand to dirt! It also does other things, like damage anvils! Why would you need this?");
                addSpellmakerTip("nearbyentities","Nearby Entities","The nearby entities component returns a list of entities, sorted by their distance to the center. The closest entity gets listed first!");
                addSpellmakerTip("debugging","Debug your spells!","Putting a debug component somewhere in your grid will cause things that go wrong to tell you about themselves instead of staying silent. Be aware of the fact that this only applies to the grid it's placed in, not other grids referenced from it.");
                addSpellmakerTip("activate","Activation","The activate component can interact with buttons, levers, doors, and can activate things like droppers and dispensers.");
                addSpellmakerTip("varpots","Variable storage","Varpots (short for variable pots) let you save signals and retrieve them at a later time. They allow for reusable recall spells and two-point fill spells, to name a few examples. You can also save references to entities, including players.");
                addSpellmakerTip("varpots2","Variable storage Pt. 2","Components that manipulate variables not only do so with varpots within casters,\nthey can also read and write variables from and to varpots stored within other casters, or plainly just your inventory!");

                addSpellmakerTip("rockandstone","Rock and stone!","What is the difference?\nPerhaps that rocking is more legal than stoning.\nWe may never know.");
            }

            add("MODID.spellcomponent.category.flowcontrol",    "Flow control");
            add("MODID.spellcomponent.category.provider",       "Provider");
            add("MODID.spellcomponent.category.arithmetic",     "Arithmetic");
            add("MODID.spellcomponent.category.effector",       "Effector");
            add("MODID.spellcomponent.category.reference",      "Reference");
            add("MODID.spellcomponent.category.lists",          "Lists");
            add("MODID.spellcomponent.category.ancient",        "Ancient");

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
            addSpell("caster_slot","Caster Slot","returns the slot ID the caster item is in");
            addSpell("blockpos_caster","Caster Block Pos.","returns the block position of the casting block");
            addSpell("pos_caster","Caster Pos.","returns the position of the casting entity or block");
            addSpell("pos_muzzle","Muzzle Pos.","returns the muzzle position of the casting entity or block");
            addSpell("eyepos_caster","Caster Eye Pos.","returns the eye position of the casting entity or block");
            addSpell("dir_caster","Caster Dir.","returns the facing direction of the casting entity or block");
            addSpell("get_weather","Weather","returns 1 if raining, 2 if thundering, and 0 otherwise");
            addSpell("get_time","Time","returns how progressed the current day is");
            addSpell("entity_delegate","Get Delegate","in a delegate context, returns the delegate entity");
            addSpell("consumed_soul","Consumed Soul","returns how much soul was consumed between start and end of the spell");
            addSpell("empty_list","Empty List","returns an empty list");
            // arithmetic
            addSpell("vector_entityspawn","Entity Spawn","returns the respawn point of the entity");
            addSpell("vector_entitypos","Entity Position","returns the position of the entity");
            addSpell("vector_entityeyepos","Entity Eye Position","returns the position of the eyes of the entity");
            addSpell("vector_entitydir","Entity Direction","returns the direction the entity is looking in");
            addSpell("vector_entityvel","Entity Velocity","returns the velocity of an entity");
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
            addSpell("log","Log","returns the logarithm to a base of a number");
            addSpell("mod","Modulo","returns the remainder of a division");
            addSpell("raycast_pos","Raycast Position","returns the block position hit by a ray");
            addSpell("raycast_dir","Raycast Direction","returns the block face hit by a ray");
            addSpell("raycast_entity","Raycast Entity","returns the entity hit by a ray");
            addSpell("bool_entitygrounded","Entity Grounded","returns if an entity is on the ground");
            addSpell("entity_nearest","Nearest Entity","returns the entity closest to the position");
            addSpell("invert","Invert","returns an inversion of the input");
            addSpell("and","And","returns the result of a AND b");
            addSpell("or","Or","returns the result of a OR b");
            addSpell("xor","Xor","returns the result of a XOR b");
            addSpell("equals","Equals","returns true if a equals b");
            addSpell("text_entityid","Entity ID","returns the Identifier of the entity");
            addSpell("text_blockid","Block ID","returns the Identifier of the block at the given position");
            addSpell("entity_has_effect","Has Effect","returns if an entity has a specified status effect");
            addSpell("entity_health","Entity Health","returns the health, max health, air, max air, and absorption of an entity");
            addSpell("random_integer","Random Integer","returns a random integer between 0 and the argument");
            addSpell("parse","Parse","attempt to turn a text into a signal of a different type");
            addSpell("to_text","To Text","returns the text representation of a signal");
            addSpell("translate","Translate","returns the translation of a translation key");
            addSpell("greater","Greater","returns if a > b");
            addSpell("less","Less","returns if a < b");
            // effectors
            addSpell("print","Print","outputs a value to the casters chat");
            addSpell("fireball","Fireball","summons a fireball");
            addSpell("lightning","Lightning","summons a lightning bolt");
            addSpell("debug","Debug","enables debug mode if placed somewhere in the grid");
            addSpell("teleport","Teleport","teleports an entity to a specified position");
            addSpell("dimhop","Dimhop","teleports an entity to a specified dimension");
            addSpell("push","Push","sets the velocity of an entity");
            addSpell("place","Place","attempts to place a block from an inventory slot");
            addSpell("break","Break","attempts to destroy a block at a position");
            addSpell("imbue","Imbue","grants a configurable status effect");
            addSpell("set_spell","Set Spell","sets the selected spell of the caster item");
            addSpell("degrade_block","Degrade Block","degrades a targeted block into something else");
            addSpell("replace","Replace","a combination of the break and place components");
            addSpell("ignite","Ignite","heats a given block position up");
            addSpell("play_sound","Play Sound","plays a sound in the world");
            addSpell("delegate","Delegate","runs a spell with a specified delay");
            addSpell("set_weather","Set Weather","sets weather. 0 clears, 1 rains, 2 thunders");
            addSpell("set_time","Set Time","changes the progress of the current day cycle");
            addSpell("grow","Grow","accelerates growth at a target position");
            addSpell("silent","Mute","makes spell execution silent");
            addSpell("transmute_item","Transmute Item","turns items into other items by infusing them with souls");
            addSpell("store","Store Item","puts an item held by the caster into a storage block");
            addSpell("take","Take Item","gives an item from a storage block to the caster");
            addSpell("transfer","Transfer Item","transfers an item from one storage block to another");
            addSpell("invisible","Invisible","makes spell execution invisible");
            addSpell("particles","Particles","spawns decorative particles in the world");
            addSpell("shout","Shout","outputs a value to the chat of anyone within range");
            addSpell("whisper","Whisper","outputs a value to the chat of a specified player");
            // reference
            addSpell("action","Action","calls another installed spell");
            addSpell("provider","Provider","automatically returns the result of a spell");
            addSpell("ref_output","Ref. Output","gets a variable from the reference arguments");
            addSpell("ref_input","Ref. Input","sets a call result variable");
            addSpell("function","Function","calls a spell and returns a result");
            addSpell("function_two","Function 2","calls a spell with two arguments and returns a result");
            addSpell("var_output","Var. Output","gets a variable from installed variable storage items");
            addSpell("var_input","Var. Input","sets a variable to an installed variable storage item");
            addSpell("var_delete","Delete Var.","removes a variable from an installed variable storage item");
            addSpell("var_exists","Var. Exists","returns if a variable from an installed variable storage item exists");
            addSpell("activate","Activate","interacts with certain blocks like doors or droppers");
            // lists
            addSpell("foreach","Foreach","individually sends out the elements of a list");
            addSpell("split","Split","splits a list into two at the given index");
            addSpell("pop","Pop","returns the last element of a list, and the list without it");
            addSpell("dequeue","Dequeue","returns the first element of a list, and the list without it");
            addSpell("size","Size","returns the count of elements in a list");
            addSpell("get_element","Get Element","returns the element in a list at a given position");
            addSpell("set_element","Set Element","sets the element in a list at a given position");
            addSpell("entities_near","Entities Near","returns a list containing all entities within the specified area");
            addSpell("block_box","Block Box","returns a list containing all block positions in the area");
            addSpell("raycast_march","Raycast March","returns a list containing all block positions intersected by a ray");
            addSpell("to_list","To List","returns a list composed of the elements of the input (vectors -> x, y, z; texts -> letters)");
            // ancient
            addSpell("exodia_1","Challenge of Soul","???");
            addSpell("exodia_2","Challenge of Calculation","???");
            addSpell("exodia_3","Challenge of Presence","???");
            addSpell("exodia_4","Challenge of Curiosity","???");
            addSpell("exodia_5","Challenge of Absolution","???");

            add("MODID.spells.debug.error","Error in spell component %1$s: %2$s");
            add("MODID.spells.debug.broke","Couldn't afford spell %1$s. Cost: %2$f, available: %3$f");
            add("MODID.spells.debug.wrongsignal","Component %1$s received wrong signal type. Got: %2$s, expected: %3$s");
            add("MODID.spells.debug.nosuchfunction","Component %1$s referenced non-existing spell %2$s");
            add("MODID.spells.debug.slotoob","Component %1$s referenced non-existing inventory slot %2$i");
            add("MODID.spells.debug.notplaceable","Component %1$s tried to place non-placeable item %2$t");
            add("MODID.spells.debug.notbreakable","Component %1$s tried to break non-breakable block %2$t");
            add("MODID.spells.debug.notreplaceable","Component %1$s tried to replace non-replaceable block %2$t");
            add("MODID.spells.debug.invalideffect","Component %1$s tried to imbue non-existent status effect %2$t");
            add("MODID.spells.debug.notimbuable","Component %1$s tried to imbue incompatible status effect %2$t");
            add("MODID.spells.debug.toobig","Component %1$s tried to create a list with too many entries: %2$f/10000");
            add("MODID.spells.debug.depthlimit","Component %1$s: Depth limit reached!");
            add("MODID.spells.debug.timeout","Spell %1$s Timed out! Time taken: %2$sms");
            add("MODID.spells.debug.player_variables_disallowed","Component %1$s: Loading player variables is disallowed on this server!");
            add("MODID.spells.debug.restricted","Component %1$s: The desired action is restricted here!");
            add("MODID.spells.debug.exodia2","The ancient component speaks to me...: ");
            add("MODID.spells.debug.exodia2_fail","The ancient component fizzles...but does nothing, except speak to me: ");
            add("MODID.spells.debug.exodia2.reverse","Provide a spell that reverses the input text \"arg\" and outputs it as \"res\".");
            add("MODID.spells.debug.exodia2.sort","Provide a spell that sorts the list containing number signals \"arg\" and outputs it as \"res\".");
            add("MODID.spells.debug.exodia2.factorial","Provide a spell that takes a number as an argument and returns the factorial of the number.");
            add("MODID.spells.debug.exodia2.mix","Provide a spell that returns a list containing a reference to yourself, your name, and the number 0.");
            add("MODID.spells.debug.exodia2.pythagoras","Provide a spell that takes a list of numbers [a,b] and returns c according to the pythagorean theorem.");

            add("MODID.caster.nospells","No spells installed");
            add("MODID.caster.emptyhint1","Shift + use to insert and select spells");
            add("MODID.caster.emptyhint2","Shift + scroll to select spell");
            add("MODID.caster.emptyhint3","Use to cast selected spell");
            add("MODID.caster.emptyhint4","Spells may need souls to cast");

            add("MODID.caster.trigger.gethit","Get hit to cast first spell");
            add("MODID.caster.trigger.hit","Attack to cast first spell");
            add("MODID.caster.trigger.jump","Jump to cast first spell");
            add("MODID.caster.trigger.tick","Casts spell named \"auto\" 20 times a second");
            add("MODID.caster.trigger.hotkey","Casts spells named 1-9 by hotkey");
            add("MODID.caster.trigger.chat","Say something to cast first spell");
            add("MODID.caster.willcast","Will cast: %1$s");
        }

        // Items
        {
            add("item.MODID.raw_mithril"            , "Raw Mithril");
            add("item.MODID.mithril_ingot"          , "Mithril Ingot");
            add("item.MODID.mithril_nugget"         , "Mithril Nugget");
            add("item.MODID.mithril_sword"          , "Mithril Sword");
            add("item.MODID.mithril_shovel"         , "Mithril Shovel");
            add("item.MODID.mithril_pickaxe"        , "Mithril Pickaxe");
            add("item.MODID.mithril_axe"            , "Mithril Axe");
            add("item.MODID.mithril_hoe"            , "Mithril Hoe");
            add("item.MODID.mithril_boots"          , "Mithril Boots");
            add("item.MODID.mithril_leggings"       , "Mithril Leggings");
            add("item.MODID.mithril_chestplate"     , "Mithril Chestplate");
            add("item.MODID.mithril_helmet"         , "Mithril Helmet");

            add("item.MODID.raw_molybdenum"            , "Raw Molybdenum");
            add("item.MODID.molybdenum_ingot"          , "Molybdenum Ingot");
            add("item.MODID.molybdenum_nugget"         , "Molybdenum Nugget");
            add("item.MODID.molybdenum_sword"          , "Molybdenum Sword");
            add("item.MODID.molybdenum_shovel"         , "Molybdenum Shovel");
            add("item.MODID.molybdenum_pickaxe"        , "Molybdenum Pickaxe");
            add("item.MODID.molybdenum_axe"            , "Molybdenum Axe");
            add("item.MODID.molybdenum_hoe"            , "Molybdenum Hoe");
            add("item.MODID.molybdenum_boots"          , "Molybdenum Boots");
            add("item.MODID.molybdenum_leggings"       , "Molybdenum Leggings");
            add("item.MODID.molybdenum_chestplate"     , "Molybdenum Chestplate");
            add("item.MODID.molybdenum_helmet"         , "Molybdenum Helmet");

            add("item.MODID.raw_titanium"            , "Raw Titanium");
            add("item.MODID.titanium_ingot"          , "Titanium Ingot");
            add("item.MODID.titanium_nugget"         , "Titanium Nugget");
            add("item.MODID.titanium_sword"          , "Titanium Sword");
            add("item.MODID.titanium_shovel"         , "Titanium Shovel");
            add("item.MODID.titanium_pickaxe"        , "Titanium Pickaxe");
            add("item.MODID.titanium_axe"            , "Titanium Axe");
            add("item.MODID.titanium_hoe"            , "Titanium Hoe");
            add("item.MODID.titanium_boots"          , "Titanium Boots");
            add("item.MODID.titanium_leggings"       , "Titanium Leggings");
            add("item.MODID.titanium_chestplate"     , "Titanium Chestplate");
            add("item.MODID.titanium_helmet"         , "Titanium Helmet");

            add("item.MODID.raw_lead"            , "Raw Lead");
            add("item.MODID.lead_ingot"          , "Lead Ingot");
            add("item.MODID.lead_nugget"         , "Lead Nugget");
            add("item.MODID.lead_sword"          , "Lead Sword");
            add("item.MODID.lead_shovel"         , "Lead Shovel");
            add("item.MODID.lead_pickaxe"        , "Lead Pickaxe");
            add("item.MODID.lead_axe"            , "Lead Axe");
            add("item.MODID.lead_hoe"            , "Lead Hoe");
            add("item.MODID.lead_boots"          , "Lead Boots");
            add("item.MODID.lead_leggings"       , "Lead Leggings");
            add("item.MODID.lead_chestplate"     , "Lead Chestplate");
            add("item.MODID.lead_helmet"         , "Lead Helmet");
            add("item.MODID.lead_apple"          , "Lead Apple");
            add("item.MODID.plumbometer"         , "Plumbometer");
            add("item.MODID.plumbometer.desc"    , "Painfully measures blood lead levels");
            add("MODID.plumbometer.start"        , "Reading Blood Lead Levels of %1$s...");
            add("MODID.plumbometer.readout"      , "Blood Lead Levels: %1$s");
            add("MODID.plumbometer.nouser"       , "No valid user!");
            add("MODID.plumbometer.0"            , "None");
            add("MODID.plumbometer.1"            , "Negligible");
            add("MODID.plumbometer.2"            , "Notable");
            add("MODID.plumbometer.3"            , "Poisoned");
            add("MODID.plumbometer.4"            , "Heavily Poisoned");
            add("MODID.plumbometer.5"            , "Extremely Poisoned");

            add("item.MODID.raw_octangulite"        , "Raw Octangulite");
            add("item.MODID.octangulite_ingot"      , "Octangulite Ingot");
            add("item.MODID.octangulite_nugget"     , "Octangulite Nugget");
            add("item.MODID.octangulite_sword"          , "Octangulite Sword");
            add("item.MODID.octangulite_shovel"         , "Octangulite Shovel");
            add("item.MODID.octangulite_pickaxe"        , "Octangulite Pickaxe");
            add("item.MODID.octangulite_axe"            , "Octangulite Axe");
            add("item.MODID.octangulite_hoe"            , "Octangulite Hoe");
            add("item.MODID.octangulite_boots"          , "Octangulite Boots");
            add("item.MODID.octangulite_leggings"       , "Octangulite Leggings");
            add("item.MODID.octangulite_chestplate"     , "Octangulite Chestplate");
            add("item.MODID.octangulite_helmet"         , "Octangulite Helmet");
            add("item.MODID.octangulite_apple"          , "Octangulite Apple");

            add("item.MODID.casting_boots"          , "Soulcasting Boots");
            add("item.MODID.casting_leggings"       , "Soulcasting Leggings");
            add("item.MODID.casting_chestplate"     , "Soulcasting Chestplate");
            add("item.MODID.casting_helmet"         , "Soulcasting Helmet");

            add("MODID.soul_storage.tooltip","Soul: %1$s/%2$s (%3$s%%)");

            add("item.MODID.tourmaline" , "Tourmaline");
            add("item.MODID.orthoclase" , "Orthoclase");
            add("item.MODID.peridot"    , "Peridot");
            add("item.MODID.axinite"    , "Axinite");


            add("item.MODID.gold_bucket"            , "Molten Gold Bucket");
            add("item.MODID.music_disc_diggy"       , "Music Disc");
            add("item.MODID.music_disc_diggy.desc"  , "Diggy");

            add("item.MODID.iron_hammer"            , "Iron Hammer");
            add("item.MODID.golden_hammer"          , "Golden Hammer");
            add("item.MODID.lead_hammer"            , "Lead Hammer");
            add("item.MODID.molybdenum_hammer"      , "Molybdenum Hammer");
            add("item.MODID.titanium_hammer"        , "Titanium Hammer");
            add("item.MODID.mithril_hammer"         , "Mithril Hammer");
            add("item.MODID.octangulite_hammer"     , "Octangulite Hammer");
            add("item.MODID.hammer.desc"       , "Can be used to smith items");

            add("item.MODID.empty_artifact"         , "Empty Artifact");
            add("item.MODID.empty_artifact.desc"    , "Makes you feel incomplete");
            addArtifact("iron"          ,"Artifact of Iron","Makes you feel stalwart");
            addArtifact("gold"          ,"Artifact of Gold","Turns held apples and carrots to gold");
            addArtifact("copper"        ,"Artifact of Copper","???");
            addArtifact("emerald"       ,"Artifact of Emerald","???");
            addArtifact("diamond"       ,"Artifact of Diamond","???");
            addArtifact("lead"          ,"Artifact of Lead","Removes negative lead effects");
            addArtifact("molybdenum"    ,"Artifact of Molybdenum","???");
            addArtifact("mithril"       ,"Artifact of Mithril","???");
            addArtifact("octangulite"   ,"Artifact of Octangulite","???");
            addArtifact("titanium"      ,"Artifact of Titanium","???");

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
            add("item.MODID.caster_core","Caster Core");

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

            add("item.MODID.geode_preview",                 "Who knows?");
            add("item.MODID.stone_geode"            ,       "Stone Geode");
            add("item.MODID.deepslate_geode"        ,       "Deepslate Geode");
            add("item.MODID.component_bag"          ,       "Component Bag");
            add("tooltip.MODID.geodes"              ,       "Can be hammered open...if you're careful.");
            add("item.MODID.explorers_map.ancient_hall",    "Directions home");
            add("item.MODID.explorers_map.digsite",         "DIGSITE COORDINATES");

            add("item.MODID.spellstorage_small",        "Minute Spellcradle");
            add("item.MODID.spellstorage_medium",       "Mundane Spellcradle");
            add("item.MODID.spellstorage_large",        "Spacious Spellcradle");
            add("item.MODID.varstorage_small",          "Minute Varpot");
            add("item.MODID.varstorage_medium",         "Mundane Varpot");
            add("item.MODID.varstorage_large",          "Spacious Varpot");
            add("item.MODID.soulstorage_small",         "Regular Attractor");
            add("item.MODID.soulstorage_medium",        "Captivating Attractor");
            add("item.MODID.soulstorage_large",         "Mesmerizing Attractor");
            // pondering
            {
                add("MODID.pondering.tooltip","Ponderable");
                add("MODID.pondering.prefix","The orb says: ");
                add("MODID.pondering.mishap","Fuck you.");
                int i = 0;
                add("MODID.pondering."+(i++),"It is certain.");
                add("MODID.pondering."+(i++),"It is decidedly so.");
                add("MODID.pondering."+(i++),"Without a doubt.");
                add("MODID.pondering."+(i++),"Yes, definitely.");
                add("MODID.pondering."+(i++),"You may rely on it.");
                add("MODID.pondering."+(i++),"Ah, I see it, yes.");
                add("MODID.pondering."+(i++),"Most likely.");
                add("MODID.pondering."+(i++),"Outlook good.");
                add("MODID.pondering."+(i++),"yes.");
                add("MODID.pondering."+(i++),"Signs point to yes.");

                add("MODID.pondering."+(i++),"Reply hazy, try again.");
                add("MODID.pondering."+(i++),"Ask again later.");
                add("MODID.pondering."+(i++),"Better not tell you now.");
                add("MODID.pondering."+(i++),"Cannot predict now.");
                add("MODID.pondering."+(i++),"Concentrate and ask again.");

                add("MODID.pondering."+(i++),"Don't count on it.");
                add("MODID.pondering."+(i++),"My reply is no.");
                add("MODID.pondering."+(i++),"My sources say no.");
                add("MODID.pondering."+(i++),"Outlook not so good.");
                add("MODID.pondering."+(i++),"Very doubtful.");
            }
            add("item.MODID.soul_bore",                 "Soul Bore");
            add("geomancy.soul_bore.tooltip","Turns octangulite into souls");
            add("geomancy.soul_bore.tooltip.1","Current Fuel: %1$s");
            add("geomancy.soul_bore.tooltip.2","Available Soul: %1$s");
            add("item.MODID.spellcomponent",            "Spell Component");

            add("MODID.spellcomponent.empty",               "empty");
            add("MODID.spellstorage.empty",                 "empty");
            add("MODID.spellstorage.unnamed",               "unnamed");
            add("MODID.spellstorage.open_storage",          "Open Storage");

            add("MODID.varstorage.storage", "Stored Variables: ");
            add("MODID.varstorage.prefix",  "Accessor Prefix: ");

            add("item.MODID.novice_glove",          "Novice Spellglove");
            add("item.MODID.apprentice_glove",      "Apprentice Spellglove");
            add("item.MODID.journey_glove",         "Journeyman Spellglove");
            add("item.MODID.expert_glove",          "Expert Spellglove");
            add("item.MODID.master_glove",          "Master Spellglove");
            add("item.MODID.precomp_caster",    "Precompiled Machine");
            add("item.MODID.component_pouch","Component Pouch");
            add("MODID.storage.tooltip","Use to open");

            add("item.MODID.mania_mask","Mania Mask");
            add("item.MODID.sorrow_mask","Sorrow Mask");
            add("item.MODID.paranoia_mask","Paranoia Mask");
            add("item.MODID.melancholy_mask","Melancholy Mask");
            add("item.MODID.adaptive_mask","Adaptive Mask");

            add("geomancy.storage_item.more","...and %1$s more");
        }

        // Blocks
        {
            add("block.MODID.condensed_dirt"        , "Condensed Dirt");

            addOres("mithril_ore", "Mithril Ore");
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
            add("block.MODID.molten_gold_cauldron"  , "Molten Gold Cauldron");
            add("block.MODID.decorated_gilded_deepslate", "Decorated Gilded Deepslate");

            add("block.MODID.smithery_block"            , "Smithery");
            add("container.MODID.smithery_block"        , "Smithery");
            add("message.MODID.smithery_block.json.fail.break"     ,"that didn't sound good...");

            add("block.MODID.spellmaker_block"            , "Spellmaker");
            add("container.MODID.spellmaker_block"        , "Spellmaker");

            add("block.MODID.autocaster"            , "Autocaster");
            add("MODID.autocaster"        , "Autocaster");

            add("block.MODID.pedestal"            , "Pedestal");
            add("block.MODID.soul_forge"          , "Soul Forge");

            addOres("octangulite_ore", "Octangulite Ore");
            add("block.MODID.raw_octangulite_block"     , "Octangulite Scrap");
            add("block.MODID.octangulite_block"         , "Block of Octangulite");
            add("block.MODID.cut_octangulite"           , "Cut Octangulite");
            add("block.MODID.octangulite_bricks"        , "Octangulite Bricks");
            add("block.MODID.octangulite_brick_stairs"  , "Octangulite Brick Stairs");
            add("block.MODID.octangulite_brick_slab"    , "Octangulite Brick Slab");
            add("block.MODID.octangulite_brick_wall"    , "Octangulite Brick Wall");

            addOres("molybdenum_ore", "Molybdenum Ore");
            add("block.MODID.raw_molybdenum_block"     , "Block of raw Molybdenum");
            add("block.MODID.molybdenum_block"         , "Block of Molybdenum");
            add("block.MODID.cut_molybdenum"           , "Cut Molybdenum");
            add("block.MODID.molybdenum_bricks"        , "Molybdenum Bricks");
            add("block.MODID.molybdenum_brick_stairs"  , "Molybdenum Brick Stairs");
            add("block.MODID.molybdenum_brick_slab"    , "Molybdenum Brick Slab");
            add("block.MODID.molybdenum_brick_wall"    , "Molybdenum Brick Wall");

            addOres("titanium_ore", "Titanium Ore");
            add("block.MODID.raw_titanium_block"     , "Block of raw Titanium");
            add("block.MODID.titanium_block"         , "Block of Titanium");
            add("block.MODID.cut_titanium"           , "Cut Titanium");
            add("block.MODID.mossy_cut_titanium"     , "Mossy Cut Titanium");
            add("block.MODID.titanium_bricks"        , "Titanium Bricks");
            add("block.MODID.titanium_brick_stairs"  , "Titanium Brick Stairs");
            add("block.MODID.titanium_brick_slab"    , "Titanium Brick Slab");
            add("block.MODID.titanium_brick_wall"    , "Titanium Brick Wall");

            addOres("lead_ore", "Lead Ore");
            add("block.MODID.raw_lead_block"     , "Block of raw Lead");
            add("block.MODID.lead_block"         , "Block of Lead");
            add("block.MODID.cut_lead"           , "Cut Lead");
            add("block.MODID.lead_bricks"        , "Lead Bricks");
            add("block.MODID.lead_brick_stairs"  , "Lead Brick Stairs");
            add("block.MODID.lead_brick_slab"    , "Lead Brick Slab");
            add("block.MODID.lead_brick_wall"    , "Lead Brick Wall");

            addOres("axinite_ore", "Axinite Ore");
            add("block.MODID.axinite_block", "Block of Axinite");
            addOres("orthoclase_ore", "Orthoclase Ore");
            add("block.MODID.orthoclase_block", "Block of Orthoclase");
            addOres("tourmaline_ore", "Tourmaline Ore");
            add("block.MODID.tourmaline_block", "Block of Tourmaline");
            addOres("peridot_ore", "Peridot Ore");
            add("block.MODID.peridot_block", "Block of Peridot");

            add("block.MODID.soul_oak_log"                  , "Soul Oak Log");
            add("block.MODID.stripped_soul_oak_log"         , "Stripped Soul Oak Log");
            add("block.MODID.soul_oak_wood"                 , "Soul Oak Wood");
            add("block.MODID.stripped_soul_oak_wood"        , "Stripped Soul Oak Wood");
            add("block.MODID.soul_oak_planks"               , "Soul Oak Planks");
            add("block.MODID.soul_oak_leaves"               , "Soul Oak Leaves");
            add("block.MODID.soul_oak_sign"                 , "Soul Oak Sign");
            add("block.MODID.soul_oak_wall_sign"            , "Soul Oak Wall Sign");
            add("block.MODID.soul_oak_hanging_sign"         , "Soul Oak Hanging Sign");
            add("block.MODID.soul_oak_wall_hanging_sign"    , "Soul Oak Wall Hanging Sign");
            add("block.MODID.soul_oak_pressure_plate"       , "Soul Oak Pressure Plate");
            add("block.MODID.soul_oak_sapling"              , "Soul Oak Sapling");
            add("block.MODID.potted_soul_oak_sapling"       , "Potted Soul Oak Sapling");
            add("block.MODID.soul_oak_button"               , "Soul Oak Button");
            add("block.MODID.soul_oak_fence_gate"           , "Soul Oak Fence Gate");
            add("block.MODID.soul_oak_stairs"               , "Soul Oak Stairs");
            add("block.MODID.soul_oak_slab"                 , "Soul Oak Slab");
            add("block.MODID.soul_oak_fence"                , "Soul Oak Fence");
            add("block.MODID.soul_oak_door"                 , "Soul Oak Door");
            add("block.MODID.soul_oak_trapdoor"             , "Soul Oak Trapdoor");

            add("block.MODID.vault_block"                   , "Vault Block");
            add("block.MODID.vault_block_stairs"            , "Vault Stairs");
            add("block.MODID.vault_block_slab"              , "Vault Slab");
            add("block.MODID.vault_glass"                   , "Vault Glass");
            add("block.MODID.vault_gate"                    , "Vault Gate");
            add("block.MODID.vault_gate_control"            , "Vault Gate Control");
        }

        // status effects
        {
            add("effect.MODID.paranoia"    , "Paranoia");
            add("effect.MODID.regretful"   , "Regretful");
            add("effect.MODID.mourning"    , "Mourning");
            add("effect.MODID.ecstatic"    , "Ecstatic");
            add("effect.MODID.blissful"    , "Blissful");
            add("effect.MODID.righteous"   , "Righteous");

        }

        // Enchantments
        add("enchantment.MODID.skillful"    , "Skillful");
        add("enchantment.MODID.mighty"      , "Mighty");
        add("enchantment.MODID.brilliance"  , "Brilliance");
        add("enchantment.MODID.soul_saver"  , "Soul Saver");
        add("enchantment.MODID.mesmerizing" , "Mesmerizing");

        // damage types
        add("death.attack.geomancy.duplicate_trinkets", "%1$s felt their own hubris");
        add("death.attack.geomancy.molten_gold", "%1$s fell into King Midas' bathtub");
        add("death.attack.geomancy.plumbometer", "%1$s died of Hypochondria");
        add("death.attack.geomancy.restricted_action", "%1$s was caught trespassing");
        add("death.attack.geomancy.null_rubble", "%1$s's head was caved in by nothing");

        // Misc
        {
            add("itemGroup.MODID.main",     "Geomancy");
            add("itemGroup.MODID.jewelry",  "Geomancy Jewelry");
            add("itemGroup.MODID.spells",   "Geomancy Spells");
            add("itemGroup.MODID.lore",     "Geomancy Books");
            add("geomancy.message.lead.tingling",   "Your fingers are tingling...");
            add("geomancy.message.lead.nausea",     "You feel like throwing up...");
            add("geomancy.message.lead.poison",     "You feel a sharp pain in your head...");
            add("geomancy.message.lead.joints",     "Your joints hurt...");
            add("geomancy.message.madness.regret",      "Maybe it is time to stop...");
            add("geomancy.message.madness.mourning",    "You feel terrible...");
            add("geomancy.message.madness.ecstasy",     "You feel wonderful!");
            add("geomancy.message.madness.nausea",      "Your head is spinning...");
            add("geomancy.message.madness.paranoia",    "Did you hear something?");

            add(KeyInputHandler.LANG_CATEGORY_GEOMANCY,"Geomancy");
            //add(KeyInputHandler.LANG_OPEN_SKILLTREE,"Skills");
            add(KeyInputHandler.LANG_CAST_1,"Cast Trinket 1");
            add(KeyInputHandler.LANG_CAST_2,"Cast Trinket 2");
            add(KeyInputHandler.LANG_CAST_3,"Cast Trinket 3");
            add(KeyInputHandler.LANG_ACTIVATE_SPELLS,"Trinket Hotbar Casting");
        }

        // Toasts
        add("geomancy.toast.stellgeknowledge.title","Stellgian Knowledge gained");
        add("geomancy.toast.stellgeknowledge.text","Your Comprehension grows...");

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
            addAdvancement(".main.get_spellmaker","Technomagical","Discover the Spellmaker");

            addAdvancement(".main.simple_duplicate_trinkets","Hubris","Try and fail to equip two artifacts of the same type at once");
            addAdvancement(".main.simple_tried_to_take_smithery_result","You've gotta hammer it!","Hit the Smithery with a Hammer to craft!");
            addAdvancement(".main.simple_tried_to_smith_cold_forge","You've gotta activate it!","Cast activate on the forge, then hit with a hammer to craft, but don't take too long!");
            addAdvancement(".main.simple_lead_poisoned","Delicious","Experience symptoms of heavy metal poisoning");
            addAdvancement(".main.simple_maddened","The silence after sanity","Let the whispers get to you");
            addAdvancement(".main.simple_tried_disallowed_action","Naughty","Attempt to do a crime");

            addAdvancement(".spells.simple_ambition","Ambition","Try to cast a spell that is too complex");
            addAdvancement(".spells.simple_bones","There's magic in my bones!","Use the grow spell component");
            addAdvancement(".spells.simple_brazilian","Brazilian","Use a push component on yourself while jumping mid-air");
            addAdvancement(".spells.simple_celeste","Celeste","Use a push component on yourself using a hotkey");
            addAdvancement(".spells.simple_fireball","Fireball!!","Activate a fireball component in a spell named \"fireball\" by saying its name in chat");
            addAdvancement(".spells.simple_ftl","FTL transmission","Use an activate component on a block at least 1000 blocks away");
            addAdvancement(".spells.simple_ignition","Ignition","Use an ignite component");
            addAdvancement(".spells.simple_liftoff","Liftoff","Use a push component with an upwards velocity of at least 1 block per tick");
            addAdvancement(".spells.simple_long_arms","Long arms","Place or break a block outside of your normal reach");
            addAdvancement(".spells.simple_deception","Deception","Use a play sound component from a muted spell");
            addAdvancement(".spells.simple_medic","Medic!","Heal another player by using an imbue component");
            addAdvancement(".spells.simple_ulterior_motives","Ulterior Motives","Save a reference to another player onto a varpot");
            addAdvancement(".spells.simple_build_big","Technical debt","Fill three quarters of a spacious spellcradle with components");
            addAdvancement(".spells.simple_deconsciousness","Deconsciousness","Cast a spell that costs at least 10000 souls");
            addAdvancement(".spells.simple_enlightenment_1","Conqueror of Soul","Activate something strange at a lively place");
            addAdvancement(".spells.simple_enlightenment_2","Conqueror of Calculation","Pass the job interview programming question");
            addAdvancement(".spells.simple_enlightenment_3","Conqueror of Presence","Tell the world how you truly feel. Give it your all. It deserves to be heard.");
            addAdvancement(".spells.simple_enlightenment_4","Conqueror of Curiosity","Taste incomprehensible destruction");
            addAdvancement(".spells.simple_enlightenment_5","Conqueror of Absolution","Reach the summit, and be touched by the gaze of those to whom you are less than nothing.");

            addAdvancement(":milestones/milestone_smithery","The Craft of the Ancients","Obtain the smithery");
            addAdvancement(":milestones/milestone_souls","Soulcraft","Learn of the true nature of octangulite");

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
                add(getS("gb:mn")+"gold.info.text", "Fascinating. I'm not used to finding fluids other than water or lava... yet here's some molten gold.\nWhat is equally as fascinating is that the only places I've found it in seem to be these ruins... How has it not resolidified after all these years?");
                add(getS("gb:mn")+"gold.gold_bucket.text", "I doubt I'll be able to dip my apples or carrots in it...");

                addGBEntryAndInfo(getS("gb:mn")+"deepslate","Gilded Deepslate");
                add(getS("gb:mn")+"deepslate.description", "");
                add(getS("gb:mn")+"deepslate.info.text", "I have discovered these polished deepslate blocks in some of those ruins. Some of them bear ancient symbols and sigils. I wonder what they mean.");
                add(getS("gb:mn")+"deepslate.gilded_deepslate.text", "It's skillfully adorned with glittering gold.");
                add(getS("gb:mn")+"deepslate.decorated_gilded_deepslate.text", "It appears to display various tools, weapons, and treasure.");

                addGBEntryAndInfo(getS("gb:mn")+"mithril","Mithril");
                add(getS("gb:mn")+"mithril.description"  , "");
                add(getS("gb:mn")+"mithril.info.text", "This incredibly rare metal is impressively durable for its light weight. It shines with a bright white color. I can sense that there is more to it than tools and armor.");
                add(getS("gb:mn")+"mithril.mithril_ingot.text", "It is also definitely not edible.");

                addGBEntryAndInfo(getS("gb:mn")+"lead","Lead");
                add(getS("gb:mn")+"lead.description"  , "");
                add(getS("gb:mn")+"lead.info.text", "This common, dark gray metal is pretty soft, and incredibly heavy.\n\\\nIt doesn't seem to have many uses. Maybe there is more to it than meets the eye?");
                add(getS("gb:mn")+"lead.lead_ingot.text", "Maybe it's edible...?");

                addGBEntryAndInfo(getS("gb:mn")+"molybdenum","Molybdenum");
                add(getS("gb:mn")+"molybdenum.description"  , "");
                add(getS("gb:mn")+"molybdenum.info.text", "This pinkish metal shares many qualities with iron.\n\\\nFor some reason, I feel extra focused while I'm around it.");
                add(getS("gb:mn")+"molybdenum.molybdenum_ingot.text", "It's shiny and it makes me feel good!");
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
                add(getS("gb:sm")+"hammers.lead.title",getItemName(ModItems.LEAD_HAMMER));
                add(getS("gb:sm")+"hammers.lead.text","This thing is worse than iron. I'm not sure why I'd use it.");
                add(getS("gb:sm")+"hammers.molybdenum.title",getItemName(ModItems.MOLYBDENUM_HAMMER));
                add(getS("gb:sm")+"hammers.molybdenum.text","A decent upgrade from iron. It also makes me feel strangely focused.");
                add(getS("gb:sm")+"hammers.titanium.title",getItemName(ModItems.TITANIUM_HAMMER));
                add(getS("gb:sm")+"hammers.titanium.text","It'll take me quite some time to wear this puppy down.");
                add(getS("gb:sm")+"hammers.gold.title",getItemName(ModItems.GOLDEN_HAMMER));
                add(getS("gb:sm")+"hammers.gold.text","Fragile, but incredibly effective.");
                add(getS("gb:sm")+"hammers.mithril.title",getItemName(ModItems.MITHRIL_HAMMER));
                add(getS("gb:sm")+"hammers.mithril.text","Mithrils durability and feathery weight makes it ideal at continuously hitting a workpiece without getting tired.");
                add(getS("gb:sm")+"hammers.octangulite.title",getItemName(ModItems.OCTANGULITE_HAMMER));
                add(getS("gb:sm")+"hammers.octangulite.text","This is probably the best there is.");


                addGBEntryAndInfo(getS("gb:sm")+"jewelry_bases","Jewelry");
                add(getS("gb:sm")+"jewelry_bases.description","");
                add(getS("gb:sm")+"jewelry_bases.info.text","Inspired by the ancient scriptures detailing jewelry, I have devised these recipes for empty jewelry items. They should apply to many different metals.");
                add(getS("gb:sm")+"jewelry_bases.iron_ring.text","This form can be worn on the fingers.");
                add(getS("gb:sm")+"jewelry_bases.iron_necklace.text","This form can be worn around the neck.");
                add(getS("gb:sm")+"jewelry_bases.iron_pendant.text","This form can be worn around the neck. It doesn't provide benefits itself, but buffs the same type of gem as slotted in it.");

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
                for(var s : new String[]{"gb:mn","gb:oc"}){
                    addGBEntryAndInfo(getS(s)+"octangulite_intro","Octangulite");
                    add(getS(s)+"octangulite_intro.description"  , "");
                    add(getS(s)+"octangulite_intro.info.text", "This...strange substance seems to shift in color when I don't look. Its hardness also doesn't seem to stay consistent. It feels out of this world. I am not quite sure what to do with it.");
                    add(getS(s)+"octangulite_intro.raw_octangulite.text", "Undulating.");
                }

                addGBEntryAndInfo(getS("gb:oc")+"soul_oak","Soul Oak");
                add(getS("gb:oc")+"soul_oak.description"  , "");
                add(getS("gb:oc")+"soul_oak.info.text", "This type of wood is mesmerizing. Its color is inconsistent. I struggle to look away from it.");
                add(getS("gb:oc")+"soul_oak.soul_oak_planks.text", "Mesmerizing.");

                addGBEntryAndInfo(getS("gb:oc")+"whispers","Whispers");
                add(getS("gb:oc")+"whispers.description"  , "");
                add(getS("gb:oc")+"whispers.info.text", "I can't shake the feeling that some of the more colorful discoveries I've made are...talking to me? Either I'm going mad, or something weird is going on. Or both.");
                add(getS("gb:oc")+"whispers.1.text", "Their language is foreign to me. The more I hear of them the surer I am that it's not just one language, but many.");
                add(getS("gb:oc")+"whispers.octangulite_ore.text", "This mineral whispers the loudest. Maybe if I just listen to it long enough I'll understand...");

                addGBEntryAndInfo(getS("gb:oc")+"madness","Madness");
                add(getS("gb:oc")+"madness.description"  , "");
                add(getS("gb:oc")+"madness.info.text", "It's both! The octangulite, or whatever it's made of is driving me mad! I best stay away from it. But...maybe it can tell me whats going on?");
                add(getS("gb:oc")+"madness.1.text", "I feel fuzzy... This feels dangerous. Whatever happens, I am sorry to those that I'll hurt when I no longer remember them.");

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
                    // initiation
                    int entry = 1;
                    add("item.MODID.lorelog_expedition_"+entry,"SOL III EXPEDITION ORDER");
                    add("item.MODID.lorelog_expedition_"+entry+".tooltip","INITIATION");

                    addGBEntryAndInfo(getS("gb:lr")+"expedition_"+entry,"SOL III EXPEDITION ORDER");
                    add(getS("gb:lr")+"expedition_"+entry+".description"  , "INITIATION");
                    add(getS("gb:lr")+"expedition_"+entry+".1.text","""
                        increased Soul Stone concentrations detected on Sol III.
                        
                        evaluating...
                        
                        local risk factors:
                        
                        wildlife: harmless
                        
                        radiation: type B
                        
                        temperature: warm
                        
                        verdict: suitable
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".2.text", """
                        beginning extraction protocol.
                        
                        sending extraction flotilla 35A.
                        calculated time of arrival: 7254829108647L
                        estimated time of completion: 7254829137574L
                        estimated time of return: 7254829174068L
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".3.text", """
                        estimated yield: 8329.53Ms
                        
                        estimated profit: 6284Ms
                        
                        expected losses: 5%
                        
                        operation name: Sol_III_ex_34
                        """);

                    // planetfall
                    entry++;
                    add("item.MODID.lorelog_expedition_"+entry,"SOL III PLANETFALL LOG");
                    add("item.MODID.lorelog_expedition_"+entry+".tooltip","PLANETFALL");

                    addGBEntryAndInfo(getS("gb:lr")+"expedition_"+entry,"SOL III PLANETFALL LOG");
                    add(getS("gb:lr")+"expedition_"+entry+".description"  , "PLANETFALL");
                    add(getS("gb:lr")+"expedition_"+entry+".1.text","""
                        Sol_III_ex_34 log 1
                        current time: 7254829108647L
                        
                        arrived as planned.
                        since the previous visit, the locals have advanced to a type # civilization.
                        no alteration to the plan is required.
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".2.text", """
                        estimated losses have gone up by 0.03% following the development of the locals.
                        
                        reserving 32Ms for wiper A, just in case.
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".3.text", """
                        flotilla 35A has landed and is setting up for the impending mining operations in several hotspots on Sol III.
                        """);

                    // annoying locals
                    entry++;
                    add("item.MODID.lorelog_expedition_"+entry,"SOL III ADDENDUM I");
                    add("item.MODID.lorelog_expedition_"+entry+".tooltip","LOCALS");

                    addGBEntryAndInfo(getS("gb:lr")+"expedition_"+entry,"SOL III ADDENDUM I");
                    add(getS("gb:lr")+"expedition_"+entry+".description"  , "LOCALS");
                    add(getS("gb:lr")+"expedition_"+entry+".1.text","""
                        Sol_III_ex_34 log 2
                        current time: 7254829115729L
                        
                        mining operations at outposts IZ, JC, JK and JO have been interrupted by organized packs of locals.
                        use of wiper A has been authorized.
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".2.text", """
                        pest control flotillette 35A-qt has been dispatched.
                        target species: Sol_III_DHM_7
                        target tolerance: 5%
                        expected time taken: 5389L
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".3.text", """
                        species hideouts: burrows, class E
                        locations: mountains
                        
                        heightening warrior patrols.
                        continuing with mining operations as planned.
                        """);

                    // eradication complete
                    entry++;
                    add("item.MODID.lorelog_expedition_"+entry,"SOL III ADDENDUM II");
                    add("item.MODID.lorelog_expedition_"+entry+".tooltip","ERADICATION");

                    addGBEntryAndInfo(getS("gb:lr")+"expedition_"+entry,"SOL III ADDENDUM II");
                    add(getS("gb:lr")+"expedition_"+entry+".description"  , "ERADICATION");
                    add(getS("gb:lr")+"expedition_"+entry+".1.text","""
                        Sol_III_ex_34 log 3
                        current time: 7254829120684L
                        
                        mining operations completed: 14293/25930
                        
                        pest control flotillette 35A-qt has finished its mission.
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".2.text", """
                        the remaining outposts are projected to complete operation as planned.
                        
                        current yield: 6215Ms
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".3.text", """
                        """);

                    // departure and surveillance
                    entry++;
                    add("item.MODID.lorelog_expedition_"+entry,"SOL III EXPEDITION FINALIZATION");
                    add("item.MODID.lorelog_expedition_"+entry+".tooltip","DEPARTURE");

                    addGBEntryAndInfo(getS("gb:lr")+"expedition_"+entry,"SOL III EXPEDITION FINALIZATION");
                    add(getS("gb:lr")+"expedition_"+entry+".description"  , "DEPARTURE");
                    add(getS("gb:lr")+"expedition_"+entry+".1.text","""
                        Sol_III_ex_34 log 4
                        current time: 7254829137697L
                        
                        extraction complete.
                        yield and profits within tolerance.
                        slight loss caused by late local species Sol_III_DHM_7.
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".2.text", """
                        to prevent losses on future expeditions, control force 35A-cs has been stationed.
                        they will surveil Sol III for suspicious intelligent activity.
                        Warrior Core 35A-wc is on standby for fast response.
                        """);
                    add(getS("gb:lr")+"expedition_"+entry+".3.text", """
                        extraction flotilla 35A is departing from Sol III.
                        calculated time of arrival: 7254829174191L
                        
                        Sol_III_ex_34 is deemed a success.
                        
                        estimated followup extraction: 7254847295745L
                        """);
                }

                // dwarf extras
                {
                    // creation
                    add("item.MODID.lorebook_extras_creation","Dwarven History: Creation and beyond");
                    add("item.MODID.lorebook_extras_creation.tooltip","The Dwarven creation mythos");

                    addGBEntryAndInfo(getS("gb:lr")+"extras_creation","Dwarven History: Creation and beyond");
                    add(getS("gb:lr")+"extras_creation.description"  , "The dwarven creation mythos");
                    add(getS("gb:lr")+"extras_creation.1.text","""
                        Not many have felt it, but their tales line up.
                        Not many have glimpsed beyond our light, but all tell the same.
                        I am now one of those that know that they are nothing, and yet so much more than that.
                        Allow me to write down, nay, attempt to write down what I have seen.
                        Hammerscale. The sparks sent flying when hitting hot metal with a hammer.
                        """);
                    add(getS("gb:lr")+"extras_creation.2.text","""
                        Glowing so brightly, burning up in the air that surrounds it.
                        What we call reality: The sun, the moon, the stars, the earth and the depths. All of it, as one.
                        It is nought but a singular speck of hammerscale.
                        Hammerscale from a project so grand, so incomprehensibly significant, that we ought to be proud to be there for its creation.
                        """);
                    add(getS("gb:lr")+"extras_creation.3.text","""
                        We will not see it through. Our reality will fizzle out long before it is completed.
                        And yet, what oh so few of us have managed to do is see through our own glow and witness the workpiece of all realities.
                        Glowing, malleable, and full of potential. To us, frozen in time, but in progress.
                        The fact that we exist is proof of its progress.
                        """);
                    add(getS("gb:lr")+"extras_creation.4.text","""
                        I bet you'll wonder who the blacksmith is.
                        The light from our sun hides the stars. You may only see them at night.
                        I could not see, nor could the others. We were blinded by beauty, and ourselves.
                        I believe that we may only witness the blacksmith when we have fizzled out, and when their work is done.
                        """);
                }

                // stellge extras
                {
                    // research 1
                    add("item.MODID.lorelog_extras_research_1","Research diary Pt. 1");
                    add("item.MODID.lorelog_extras_research_1.tooltip","Signed: Head Researcher Pentangula");

                    addGBEntryAndInfo(getS("gb:lr")+"extras_research_1","Research diary Pt. 1");
                    add(getS("gb:lr")+"extras_research_1.description"  , "Signed: Head Researcher Pentangula");
                    add(getS("gb:lr")+"extras_research_1.1.text","""
                        Dear diary,
                        I can't figure out how to remove that stupid "dear diary" text.
                        If L0G-Pro 3.i had better UI I would've asked for IT instead of Diarizer Vtera.
                        Oh well. Too late now.
                        Allow me to introduce myself.
                        I am Pentangula of the Stellge, head of research in section DIM-5.
                        """);
                    add(getS("gb:lr")+"extras_research_1.2.text","""
                        My area of expertise is metadimensional physics.
                        I have been granted the individual status around 1095000L ago.
                        My interests are copper, sidestepping the fabric of reality, and organics, especially species 8UN-E of Sol III.
                        Their long auditory sensory organs and soft secondary external ventilator are just so adorable.
                        """);
                    add(getS("gb:lr")+"extras_research_1.3.text","""
                        My current task is the creation of an experimental soulcasting machine that allows for personal interdimensional travel.
                        I would like to thank us for personally entrusting me with this.
                        I guess mindless drones don't make for good enough innovators,
                        """);
                    add(getS("gb:lr")+"extras_research_1.4.text","""
                        otherwise we wouldn't individualize for it.
                        In any case, expedition "Queens Pawn" is ongoing. Last cycle, I sent another drone through to site 0 using experimental machine D4.
                        As expected, communications with the drone have been lost as soon as it left.
                        """);
                    add(getS("gb:lr")+"extras_research_1.5.text","""
                        My current theories as to why this happens with this destination only are as follows:
                        
                        - the destination is vaporisingly hot
                        - the destination is lead-fryingly irradiated
                        - the destination is pressurized akin to a black hole
                        """);
                    add(getS("gb:lr")+"extras_research_1.6.text","""
                        - there is a hungry allovorial predator waiting on the other side
                        - time works differently there
                        - only soul-bearers are allowed there
                        """);
                    add(getS("gb:lr")+"extras_research_1.7.text","""
                        I will continue to send out drones to find out which of these it is.
                        In the meantime, the side effects of dimensional travel seem to be manageable in the drones that returned from Sol III.
                        A promising sign.
                        """);

                    // research 2
                    add("item.MODID.lorelog_extras_research_2","Research diary Pt. 2");
                    add("item.MODID.lorelog_extras_research_2.tooltip","Signed: Head Researcher Pentangula");

                    addGBEntryAndInfo(getS("gb:lr")+"extras_research_2","Research diary Pt. 2");
                    add(getS("gb:lr")+"extras_research_2.description"  , "Signed: Head Researcher Pentangula");
                    add(getS("gb:lr")+"extras_research_2.1.text","""
                        Dear diary,
                        I wish I was an organic.
                        Specially bred Vivocasters with imprinted communication protocols *did* manage to ping us after having entered site 0.
                        This tells me that organics survive there, but mechanicals don't.
                        """);
                    add(getS("gb:lr")+"extras_research_2.2.text","""
                        In order for me to further study site 0, I will have to spend incredible amounts of power and time to breed the necessary specialized observation and transmission Vivocasters.
                        If I was an organic, I could just waltz in there and explore it myself.
                        """);
                    add(getS("gb:lr")+"extras_research_2.3.text","""
                        P.S.:
                        I asked us for an instance of L0G-Pro 4.a, and am currently writing in there.
                        The "Dear diary" at the start of my logs kind of grew on me, so now I'm putting it there myself.
                        """);
                }

                // exodia
                {
                    // 1 : soul
                    add("item.MODID.lorelog_exodia_1","Field Research Log");
                    add("item.MODID.lorelog_exodia_1.tooltip","Signed: Prospector Septangula");

                    addGBEntryAndInfo(getS("gb:lr")+"exodia_1","Field Research Log");
                    add(getS("gb:lr")+"exodia_1.description"  , "Signed: Prospector Septangula");
                    add(getS("gb:lr")+"exodia_1.1.text","""
                        I am Septangula of the Stellge, Prospector at Site IZ. Ambient soul-slurry density exceeds previous records. In collaboration with Head Engineer Tritangula, we have designed a new soulcasting component.
                        """);
                    add(getS("gb:lr")+"exodia_1.2.text","""
                        Component E1, when cast in an area with exceptional slurry density, will potentially allow us to gain insights into project 0.
                        """);
                    add(getS("gb:lr")+"exodia_1.3.text","""
                        Attaching recipe for Component E1.
                        Requesting observation squad and recipient Vivocasters.
                        """);

                    // 2 : calculation
                    add("item.MODID.lorelog_exodia_2","Dimensional Exploration Log Pt.1");
                    add("item.MODID.lorelog_exodia_2.tooltip","Signed: Head Researcher Pentangula");

                    addGBEntryAndInfo(getS("gb:lr")+"exodia_2","Dimensional Exploration Log Pt.1");
                    add(getS("gb:lr")+"exodia_2.description"  , "Signed: Head Researcher Pentangula");
                    add(getS("gb:lr")+"exodia_2.1.text","""
                        Dear diary,
                        this is Pentangula of the Stellge, reporting from dimension Sol III, sub 1.
                        After the scout drones deemed the immediate area safe, I have decided to visit personally!
                        I can confirm temperature and slurry density readings. Getting back home will not be a concern.
                        """);
                    add(getS("gb:lr")+"exodia_2.2.text","""
                        Unfortunately, though, soul stone expectancy here is still below the threshold for extraction.
                        For some reason, souls like to gather into loose granulate here.
                        The SPV of mentioned "soul sand" (I came up with that alliteration myself!) sadly isn't high enough for it to be of use for us.
                        """);
                    add(getS("gb:lr")+"exodia_2.3.text","""
                        It would make for good soil to grow Vivocaster food in, though.
                        Anyways, I'm happy to report that I've figure out something that may help us make some progress with project 0.
                        If I can supply a machine with the correct algorithm...
                        """);
                    add(getS("gb:lr")+"exodia_2.4.text","""
                        ...it may be able to tell me whats so special about this place.
                        I have attached a schematic.
                        """);

                    // 3 : presence
                    add("item.MODID.lorelog_exodia_3","Dimensional Exploration Log Pt.2");
                    add("item.MODID.lorelog_exodia_3.tooltip","Signed: Head Researcher Pentangula");
                    addGBEntryAndInfo(getS("gb:lr")+"exodia_3","Dimensional Exploration Log Pt.2");
                    add(getS("gb:lr")+"exodia_3.description"  , "Signed: Head Researcher Pentangula");
                    add(getS("gb:lr")+"exodia_3.1.text","""
                        Dear diary,
                        this is Pentangula of the Stellge, reporting from deep inside my favourite planet!
                        The prospectors have transmitted discovery of a peculiar point of interest, and I'm there right now!
                        It appears to be some kind of abandoned settlement.
                        """);
                    add(getS("gb:lr")+"exodia_3.2.text","""
                        According to the report, this place has unusually dense slurry for being underground.
                        Theres patches of a mushy, dark-blue substance here.
                        It seems to be another naturally occurring form of soul storage on Sol III.
                        """);
                    add(getS("gb:lr")+"exodia_3.3.text","""
                        This gunk seems to spread much, much faster than soul stone does.
                        The density of souls per block suffers greatly because of it.
                        I still think that it could give us important insights into project 0.
                        Some of the local biomass reacts strangely to noise...
                        """);
                    add(getS("gb:lr")+"exodia_3.4.text","""
                        Together with the digsite engineers, we've come up with a new component that will greatly amplify the volume of a sufficiently loud conjured sound.
                        """);

                    // 4 : curiosity
                    add("item.MODID.lorelog_exodia_4","Dimensional Exploration Log Pt.3");
                    add("item.MODID.lorelog_exodia_4.tooltip","Signed: Head Researcher Pentangula");
                    addGBEntryAndInfo(getS("gb:lr")+"exodia_4","Dimensional Exploration Log Pt.3");
                    add(getS("gb:lr")+"exodia_4.description"  , "Signed: Head Researcher Pentangula");
                    add(getS("gb:lr")+"exodia_4.1.text","""
                        Dear diary,
                        this is Pentangula of the Stellge, reporting from dimension Sol III, sub 2.
                        Readings for this one were curious, so I decided to visit personally.
                        """);
                    add(getS("gb:lr")+"exodia_4.2.text","""
                        I can confirm that local slurry density is, for an inhabited dimension, at a record low.
                        A long expedition is unfeasible due to limited portable soul storage.
                        """);
                    add(getS("gb:lr")+"exodia_4.3.text","""
                        I wouldn't want to get stuck here...
                        There's a certain curious happenstance I would like to mention.
                        There is no ground.
                        It's sort of like space, except that theres gravity. Whatever gets pulled down there gets spaghettified into malfunction.
                        """);
                    add(getS("gb:lr")+"exodia_4.4.text","""
                        I have devised a component that can measure this spaghettification. Perhaps learning more about it will help us with project 0.
                        """);
                }

                addGBEntryAndInfo(getS("gb:lr")+"stellge","The Stellge");
                add(getS("gb:lr")+"stellge.description"  , "");
                add(getS("gb:lr")+"stellge.info.text","""
                        I have found something indubitably alien.
                        Ruins of highly advanced and weirdly geometric spacecraft are littered all over this worlds liveliest areas.
                        Judging by their mostly intact interior, they were not designed to accommodate living beings.
                        """);
                add(getS("gb:lr")+"stellge.2.text","""
                        As of writing this, it is unclear to me why they were here - and why they no longer are.
                        \\
                        Their written language is made of unknown glyphs and symbols.
                        \\
                        If I learn more about them, maybe I can decipher their meaning.
                        """);
                add(getS("gb:lr")+"stellge.3.text","""
                        From the marks in their structures, I did manage to at least learn of their name.
                        \\
                        \\
                        They call themselves the "Stellge".
                        """);

                addGBEntryAndInfo(getS("gb:lr")+"dwarves","The Dwarves");
                add(getS("gb:lr")+"dwarves.description"  , "");
                add(getS("gb:lr")+"dwarves.info.text","""
                        The ruins I have uncovered seem to have been built by a society of miners and blacksmiths.
                        Their architecture makes heavy use of golden engravings and decoration.
                        """);
                add(getS("gb:lr")+"dwarves.2.text","""
                        The dust and rot present in their ruins suggests that they are long gone.
                        \\
                        I wonder what happened to them. There is no visible damage from a potential siege, yet the armories are full of unused weapons.
                        \\
                        \\
                        All the riches are still there, too...
                        """);
            }

            add(getS("gb:sc")+"name", "Soulcasting");
            {
                for(var cat : new String[]{"oc","sc"})
                {
                    addGBEntryAndInfo(getS("gb:"+cat)+"revelation","Revelation");
                    add(getS("gb:"+cat)+"revelation.description"  , "");
                    add(getS("gb:"+cat)+"revelation.info.text", """
                            I...understand.
                            \\
                            The stellge came here because of the octangulite.
                            \\
                            They use it as fuel for machines that can do all sorts of things.
                            """);
                    add(getS("gb:"+cat)+"revelation.2.text", """
                            Octangulite is most abundant in areas with a lot of life in them.
                            \\
                            Swamps, jungles, forests...
                            \\
                            The answer is souls. Octangulite is made of hundreds of thousands of souls, from all kinds of animals.
                            \\
                            Maybe even from microbes or bacteria.
                            They all come together and manifest physically as octangulite.
                            """);
                    add(getS("gb:"+cat)+"revelation.3.text", """
                            THAT'S why it has been whispering to me, and THAT'S why i feel so drawn to it.
                            Its collective consciousness wants me to become a part of it, so that it can grow.
                            """);
                    add(getS("gb:"+cat)+"revelation.4.text", """
                            I don't know which is worse: That *this* is what happens to things that die, 
                            or that the Stellge destroy unthinkable quantities of souls to power their toasters.
                            """);

                    addGBEntryAndInfo(getS("gb:"+cat)+"spellcomponents","Spell Components");
                    add(getS("gb:"+cat)+"spellcomponents.description"  , "");
                    add(getS("gb:"+cat)+"spellcomponents.0.title"  , "Spell Components");
                    add(getS("gb:"+cat)+"spellcomponents.0.text"  , "These weird hexagonal doodads seem to be able to perform mathematical operations and affect the world, if used correctly.");
                    add(getS("gb:"+cat)+"spellcomponents.spellcomponent.text"  , "This seems to be the template for all of them.");

                    addGBEntryAndInfo(getS("gb:"+cat)+"spellmaker","Spellmaker");
                    add(getS("gb:"+cat)+"spellmaker.description"  , "");
                    add(getS("gb:"+cat)+"spellmaker.info.text", """
                            This strange Stellgian machine allows for the creation of "spells".
                            \\
                            If I insert a so-called "spell cradle", it opens up and lets me manipulate its component slots. Interesting.
                            """);
                    add(getS("gb:"+cat)+"spellmaker.spellmaker_block.text", """
                            I feel like I'm building something out of fuse beads while using this.
                            """);
                }

                addGBEntryAndInfo(getS("gb:sc")+"soulstorage","Soul Storage");
                add(getS("gb:sc")+"soulstorage.description"  , "");
                add(getS("gb:sc")+"soulstorage.info.text", """
                            If I want to cast more complex spells, I will want to be able to afford it.
                            \\
                            These mesmerizing doodads will passively draw souls from the environment and keep them until I need them.
                            """);
                add(getS("gb:sc")+"soulstorage.soulstorage_small.text", """
                            Even souls will want to look at a pyramid this colorful.
                            """);
                add(getS("gb:sc")+"soulstorage.soulstorage_medium.text", """
                            Cubes seem to be even more enticing to them than pyramids are.
                            \\
                            I wonder if there is an even better shape for this.
                            """);
                add(getS("gb:sc")+"soulstorage.soulstorage_large.text", """
                            This is it.
                            \\
                            Spheres are by far the best shape for the purpose of capturing souls.
                            \\
                            ...and my gaze.
                            """);

                addGBEntryAndInfo(getS("gb:sc")+"spellstorage","Spell Storage");
                add(getS("gb:sc")+"spellstorage.description"  , "");
                add(getS("gb:sc")+"spellstorage.info.text", """
                            These unorthodox storage devices can hold components in a hexagonal grid.
                            \\
                            Putting them into a glove will let me "run" the components inside and manipulate the world based on how they are arranged.
                            """);
                add(getS("gb:sc")+"spellstorage.spellstorage_small.text", """
                            It's not much, but it will let me do the basics.
                            """);
                add(getS("gb:sc")+"spellstorage.spellstorage_medium.text", """
                            Now we're talking. The possibilities just expanded 10-fold!
                            """);
                add(getS("gb:sc")+"spellstorage.spellstorage_large.text", """
                            These will be solely for convenience most of the time. If I really need that big a grid, I should reconsider.
                            """);

                addGBEntryAndInfo(getS("gb:sc")+"varstorage","Variable Storage");
                add(getS("gb:sc")+"varstorage.description"  , "");
                add(getS("gb:sc")+"varstorage.info.text", """
                            I have come up with a solution to storing signals for later use, or safekeeping.
                            \\
                            These "Varpots" will allow me to save and load signals to it using special spell components.
                            \\
                            I may put them into a caster, or keep them on my person. Both work!
                            """);
                add(getS("gb:sc")+"varstorage.varstorage_small.text", """
                            It really doesn't get much smaller than this, but 1 is still a whole lot more than 0.
                            """);
                add(getS("gb:sc")+"varstorage.varstorage_medium.text", """
                            This one can hold four times as many signals as its predecessor!
                            """);
                add(getS("gb:sc")+"varstorage.varstorage_large.text", """
                            If I ever end up needing more than this, I should dive a little deeper into list signals instead.
                            """);

                addGBEntryAndInfo(getS("gb:sc")+"gloves","Spellgloves");
                add(getS("gb:sc")+"gloves.description"  , "The tool of a Soulcaster");
                add(getS("gb:sc")+"gloves.info.text", """
                            With one of these and a well-built spellcradle, I will be able to cast spells!
                            \\
                            I can access the internal storage by shift-right clicking.
                            \\
                            Scrolling while sneaking will also change the selected spell.
                            """);
                add(getS("gb:sc")+"gloves.novice_glove.text", """
                            This beauty will let me cast up to 5 different spells!
                            """);
                add(getS("gb:sc")+"gloves.apprentice_glove.text", """
                            A little pricier, but the capacity is now almost twice as big!
                            """);
                add(getS("gb:sc")+"gloves.journey_glove.text", """
                            Double the capacity at two full rows!
                            """);
                add(getS("gb:sc")+"gloves.expert_glove.text", """
                            It doesn't get much better than this.
                            """);
            }

        }

        // Entities
        {
            add("entity.MODID.stellge_engineer"         , "Stellge Engineer");
            add("item.MODID.stellge_engineer_spawn_egg" , "Stellge Engineer Spawn Egg");
            add("entity.MODID.stellge_caster"           , "Stellge Caster");
            add("item.MODID.stellge_caster_spawn_egg"   , "Stellge Caster Spawn Egg");
        }

        // REI, EMI
        {
            add("container.MODID.rei.smithing.title","Smithing");
            add("container.MODID.rei.soul_forge.title","Soul Forging");

            add("geomancy.rei.locked_recipe","Locked Recipe");
            add("geomancy.rei.locked_recipe.2","Progress to unlock");

            add("geomancy.rei.locked_recipe_type","Locked Recipe Type");
            add("geomancy.rei.locked_recipe_type.2","Progress to unlock");

            add("geomancy.soulpreview.tooltip.cost","Souls required: %1$f");

            // tags
            add("tag.MODID.casting_item","Casting item");
            add("tag.MODID.component_storing","Stores components");
            add("tag.MODID.fits_in_casters","Fits in casters");
            add("tag.MODID.fits_in_soul_bore","Fits in soul bore");
            add("tag.MODID.jewelry_gems","Jewelry gems");
            add("tag.MODID.octangulite","Octangulite");
            add("tag.MODID.spell_storing","Stores spells");
            add("tag.MODID.stellge_curious","Stellge curious");
            add("tag.MODID.variable_storing","Stores variables");
            add("tag.MODID.molten_gold","Molten gold");
            add("tag.MODID.viscous_fluid","Viscous fluid");
        }

        // config
        {
            add("MODID.options.epilepsy","Epilepsy Mode");
            add("MODID.options.spellmakeruispeed","Spellmaker UI speed");
            add("MODID.options.no_spellmaker_move","No Spellmaker UI movement");
            add("MODID.options.shake_intensity","Camera shake intensity");
            add("MODID.options.penalize_spell_timeout","Penalize spell timeout");
            add("MODID.options.spellcradle_tooltip_truncation","Spellcradle tooltip truncation");
            add("MODID.options.player_variable_loading","Player reference loading");
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
        add(getS("gb:sc")+"spellcomponents."+spell+".text"  , modonomiconDescription);
    }

    void addAdvancement(String advancement, String name, String description){
        add("advancement.MODID"+advancement+".name"       , name);
        add("advancement.MODID"+advancement+".description", description);
    }
    void addOres(String key, String name){
        add("block.MODID."+key, name);
        add("block.MODID.deepslate_"+key, "Deepslate "+name);
    }

    void addArtifact(String of, String name, String desc){
        add("item.MODID.artifact_of_"+of, name);
        add("item.MODID.artifact_of_"+of+".desc", desc);
    }

    void addSpellmakerTip(String name, String text,String description){
        add("MODID.spellmaker.tip."+name,text);
        add("MODID.spellmaker.tip."+name+".desc",description);
    }
}