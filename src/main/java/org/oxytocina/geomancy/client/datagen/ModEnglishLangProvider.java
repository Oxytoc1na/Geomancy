package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.event.KeyInputHandler;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.GemSlot;

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

        add(KeyInputHandler.KEY_CATEGORY_GEOMANCY,"Geomancy");
        add(KeyInputHandler.KEY_OPEN_SKILLTREE,"Skills");

        // Items
        {
            add("item.MODID.suspicious_substance"   , "Suspicious Substance");
            add("item.MODID.guidite_sword"          , "Guidite Sword");

            add("item.MODID.raw_mithril"            , "Raw Mithril");
            add("item.MODID.mithril_ingot"          , "Mithril Ingot");
            add("item.MODID.mithril_nugget"         , "Mithril Nugget");

            add("item.MODID.raw_molybdenium"            , "Raw Molybdenium");
            add("item.MODID.molybdenium_ingot"          , "Molybdenium Ingot");
            add("item.MODID.molybdenium_nugget"         , "Molybdenium Nugget");

            add("item.MODID.raw_octangulite"        , "Raw Octangulite");
            add("item.MODID.octangulite_ingot"      , "Octangulite Ingot");
            add("item.MODID.octangulite_nugget"     , "Octangulite Nugget");

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
            add("tooltip.geomancy.jewelry.nogems"   ,"No Gems");
            add("tooltip.geomancy.jewelry.unsmith"  ,"Salvages gems");
            add("tooltip.geomancy.jewelry.quality"  ,"Quality");

            add("item.MODID.stone_geode"            , "Stone Geode");
            add("tooltip.MODID.geodes"              , "Can be hammered open...if you're careful.");

        }

        // Blocks
        {
            add("block.MODID.condensed_dirt"        , "Condensed Dirt");

            add("block.MODID.mithril_ore"           , "Mithril Ore");
            add("block.MODID.deepslate_mithril_ore" , "Deepslate Mithril Ore");
            add("block.MODID.raw_mithril_block"     , "Block of Raw Mithril");
            add("block.MODID.mithril_block"         , "Block of Mithril");

            add("block.MODID.molten_gold"           , "Molten Gold");
            add("block.MODID.mithril_anvil"         , "Mithril Anvil");
            add("block.MODID.gilded_deepslate"      , "Gilded Deepslate");
            add("block.MODID.decorated_gilded_deepslate", "Decorated Gilded Deepslate");
            add("block.MODID.smithery_block"            , "Smithery");
            add("container.MODID.smithery_block"        , "Smithery");
            add("message.MODID.smithery.fail.break"     ,"that didn't sound good...");

            add("block.MODID.octangulite_ore"           , "Octangulite Ore");
            add("block.MODID.deepslate_octangulite_ore" , "Deepslate Octangulite Ore");
            add("block.MODID.raw_octangulite_block"     , "Octangulite Scrap");
            add("block.MODID.octangulite_block"         , "Block of Octangulite");

            add("block.MODID.molybdenium_ore"           , "Molybdenium Ore");
            add("block.MODID.deepslate_molybdenium_ore" , "Deepslate Molybdenium Ore");
            add("block.MODID.raw_molybdenium_block"     , "Block of raw Molybdenium");
            add("block.MODID.molybdenium_block"         , "Block of Molybdenium");
        }

        // Enchantments
        add("enchantment.MODID.skillful"    , "Skillful");
        add("enchantment.MODID.mighty"      , "Mighty");

        // damage types
        add("death.attack.geomancy.duplicate_trinkets", "%1$s felt their own hubris");
        add("death.attack.geomancy.molten_gold", "%1$s fell into King Midas' bathtub");

        // Misc
        add("itemGroup.MODID", "Geomancy");

        // Advancements
        {
            add("advancement.MODID.main.name"                               , "Geomancy");
            add("advancement.MODID.main.description"                        , "A world of buried treasures and wonders");

            add("advancement.MODID.main.get_mithril.name"                   , "Precious");
            add("advancement.MODID.main.get_mithril.description"            , "Discover Raw Mithril");

            add("advancement.MODID.main.get_octangulite.name"               , "Otherworldy");
            add("advancement.MODID.main.get_octangulite.description"        , "Discover something strange");

            add("advancement.MODID.main.get_molten_gold.name"               , "Greedy");
            add("advancement.MODID.main.get_molten_gold.description"        , "Discover Molten Gold");

            add("advancement.MODID.main.get_gilded_deepslate.name"          , "Adorned");
            add("advancement.MODID.main.get_gilded_deepslate.description"   , "Discover Gilded Deepslate");

            add("advancement.MODID.main.simple_duplicate_trinkets.name"         , "Hubris");
            add("advancement.MODID.main.simple_duplicate_trinkets.description"  , "Try and fail to equip two artifacts of the same type at once");

            add("advancement.MODID.main.simple_tried_to_take_smithery_result.name"          , "You've gotta hammer it!");
            add("advancement.MODID.main.simple_tried_to_take_smithery_result.description"   , "You need to smack the smithery with a hammer to start crafting");

            add("advancement.MODID:milestones/milestone_smithery.name", "The Craft of the Ancients");
            add("advancement.MODID:milestones/milestone_smithery.description", "Obtain the smithery");

            add("advancement.MODID:location/ancient_hall.name", "Ancient");
            add("advancement.MODID:location/ancient_hall.description", "Discover the once prosperous remains of the ancients");
        }

        // Guidebook
        {
            add("book.MODID.guidebook.name", "Notes on Minerals");
            add("book.MODID.guidebook.tooltip", "we can never dig too deep");


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

                addGemStatsText(getS("gb:sm")+"gems.diamond.text", Items.DIAMOND,"provides two armor points.");
                addGemStatsText(getS("gb:sm")+"gems.emerald.text", Items.EMERALD,"???");
                addGemStatsText(getS("gb:sm")+"gems.lapis_lazuli.text", Items.LAPIS_LAZULI,"???");
            }

            add(getS("gb:oc")+"name", "Octangulite");
            {
                addGBEntryAndInfo(getS("gb:oc")+"octangulite","Octangulite");
                add(getS("gb:oc")+"octangulite.description"  , "");
                add(getS("gb:oc")+"octangulite.intro.text", "This... strange substance seems to shift in color when I don't look. Its hardness also doesn't seem to stay consistent. It feels out of this world. I am not quite sure what to do with it.");
                add(getS("gb:oc")+"octangulite.raw_octangulite.text", "Undulating.");

            }


        }


        tb=null;
    }

    // helper function
    void add(String key, String value){
        key=key.replace("MODID", Geomancy.MOD_ID);
        tb.add(key,value);
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

        String difficultyText = getS("gems.difficulty.negligible");
        float difficulty = GemSlot.getGemDifficulty(gemItem.getDefaultStack());
        if(difficulty > 50) difficultyText = getS("gems.difficulty.legendary");
        else if(difficulty > 20) difficultyText = getS("gems.difficulty.immense");
        else if(difficulty > 5) difficultyText = getS("gems.difficulty.noticeable");

        String progressText = getS("gems.difficulty.negligible");
        float progressCost = GemSlot.getGemProgressCost(gemItem.getDefaultStack());
        if(progressCost > 100) progressText = getS("gems.difficulty.odyssey");
        else if(progressCost > 35) progressText = getS("gems.difficulty.molasses");
        else if(progressCost > 15) progressText = getS("gems.difficulty.prolonging");

        res += "\r\rDifficulty: "+ difficultyText;
        res += "\rComplexity: "+ progressText;

        if(!Objects.equals(suffix, ""))
            res+="\r\r"+suffix;
        add(key,res);
    }
}