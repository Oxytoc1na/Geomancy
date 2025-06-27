package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.oxytocina.geomancy.Geomancy;

import java.util.concurrent.CompletableFuture;

public class ModEnglishLangProvider extends FabricLanguageProvider {
    public ModEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us");
    }

    private TranslationBuilder tb;

    @Override
    public void generateTranslations(TranslationBuilder tb) {
        this.tb =tb;

        // Items
        {
            add("item.MODID.suspicious_substance"   , "Suspicious Substance");
            add("item.MODID.guidite_sword"          , "Guidite Sword");
            add("item.MODID.raw_mithril"            , "Raw Mithril");
            add("item.MODID.mithril_ingot"          , "Mithril Ingot");
            add("item.MODID.mithril_nugget"         , "Mithril Nugget");
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
        }

        // Blocks
        {
            add("block.MODID.condensed_dirt"        , "Condensed Dirt");
            add("block.MODID.mithril_ore"           , "Mithril Ore");
            add("block.MODID.deepslate_mithril_ore" , "Deepslate Mithril Ore");
            add("block.MODID.raw_mithril_block"     , "Block of Raw Mithril");
            add("block.MODID.mithril_block"         , "Block of Mithril");
            add("block.MODID.gold"                  , "Molten Gold");
            add("block.MODID.mithril_anvil"         , "Mithril Anvil");
            add("block.MODID.gilded_deepslate"      , "Gilded Deepslate");
            add("block.MODID.decorated_gilded_deepslate", "Decorated Gilded Deepslate");
            add("block.MODID.smithery_block"            , "Smithery");
            add("container.MODID.smithery_block"        , "Smithery");
            add("message.MODID.smithery.fail.break"     ,"that didn't sound good...");
            add("block.MODID.octangulite_ore"           , "Octangulite Ore");
            add("block.MODID.deepslate_octangulite_ore" , "Deepslate Octangulite Ore");
        }

        // Enchantments
        add("enchantment.MODID.skillful"    , "Skillful");

        // damage types
        add("death.attack.geomancy.duplicate_trinkets", "%1$s felt their own hubris");

        // Misc
        add("itemGroup.MODID", "Geomancy");

        // Advancements
        {
            add("advancement.MODID.main.name"                               , "Geomancy");
            add("advancement.MODID.main.description"                        , "A world of buried treasures and wonders");
            add("advancement.MODID.main.get_mithril.name"                   , "Precious");
            add("advancement.MODID.main.get_mithril.description"            , "Discover Raw Mithril");
            add("advancement.MODID.main.get_molten_gold.name"               , "Greedy");
            add("advancement.MODID.main.get_molten_gold.description"        , "Discover Molten Gold");
            add("advancement.MODID.main.get_gilded_deepslate.name"          , "Adorned");
            add("advancement.MODID.main.get_gilded_deepslate.description"   , "Discover Gilded Deepslate");
            add("advancement.MODID.main.simple_duplicate_trinkets.name"         , "Hubris");
            add("advancement.MODID.main.simple_duplicate_trinkets.description"  , "Try and fail to equip two artifacts of the same type at once");
            add("advancement.MODID.main.simple_tried_to_take_smithery_result.name"          , "You've gotta hammer it!");
            add("advancement.MODID.main.simple_tried_to_take_smithery_result.description"   , "You need to smack the smithery with a hammer to start crafting");
        }

        // Guidebook
        {
            add("book.MODID.guidebook.name", "Notes on Minerals");
            add("book.MODID.guidebook.tooltip", "we can never dig too deep");

            add("book.MODID.guidebook.main.name", "Discovery");

            add("book.MODID.guidebook.main.entry.name", "Discovery");
            add("book.MODID.guidebook.main.entry.description", "");
            add("book.MODID.guidebook.main.entry.intro.title", "Discovery");
            add("book.MODID.guidebook.main.entry.intro.text", "There exist ruins in this world, ruins of ancient deepslate and inscriptions of rare metals.\nWhat do they mean? I will write down my findings in this book.");

            add("book.MODID.guidebook.main.gold.name", "Molten Gold");
            add("book.MODID.guidebook.main.gold.description", "");
            add("book.MODID.guidebook.main.gold.intro.title", "Molten Gold");
            add("book.MODID.guidebook.main.gold.intro.text", "Fascinating. I'm not used to finding fluids other than water or lava... yet here's some molten gold.\nWhat is equally as fascinating is that the only places I've found it in seem to be these ruins... How has it not resolidified after all these years?");
            add("book.MODID.guidebook.main.gold.spotlight1.text", "I doubt I'll be able to dip my apples or carrots in it...");

            add("book.MODID.guidebook.main.deepslate.name", "Gilded Deepslate");
            add("book.MODID.guidebook.main.deepslate.description", "");
            add("book.MODID.guidebook.main.deepslate.intro.title", "Gilded Deepslate");
            add("book.MODID.guidebook.main.deepslate.intro.text", "I have discovered these polished deepslate blocks in some of those ruins. Some of them bear ancient symbols and sigils. I wonder what they mean.");
            add("book.MODID.guidebook.main.deepslate.spotlight1.text", "It's skillfully adorned with glittering gold.");
            add("book.MODID.guidebook.main.deepslate.spotlight2.text", "It appears to display various tools, weapons, and treasure.");


            add("book.MODID.guidebook.main.mithril.name", "Mithril");
            add("book.MODID.guidebook.main.mithril.description"  , "");
            add("book.MODID.guidebook.main.mithril.intro.title", "Mithril");
            add("book.MODID.guidebook.main.mithril.intro.text", "This incredibly rare metal is impressively durable for its light weight. It shines with a bright white color. I can sense that there is more to it than tools and armor.");
            add("book.MODID.guidebook.main.mithril.spotlight1.text", "It is also definitely not edible.");

        }


        tb=null;
    }

    // helper function
    void add(String key, String value){
        key=key.replace("MODID", Geomancy.MOD_ID);
        tb.add(key,value);
    }
}