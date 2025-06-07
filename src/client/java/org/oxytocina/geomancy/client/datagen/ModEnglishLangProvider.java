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
        add("item.MODID.suspicious_substance"   , "Suspicious Substance");
        add("item.MODID.guidite_sword"          , "Guidite Sword");
        add("item.MODID.raw_mithril"            , "Raw Mithril");
        add("item.MODID.mithril_ingot"          , "Mithril Ingot");
        add("item.MODID.mithril_nugget"         , "Mithril Nugget");
        add("item.MODID.gold_bucket"            , "Molten Gold Bucket");

        // Blocks
        add("block.MODID.condensed_dirt"        , "Condensed Dirt");
        add("block.MODID.mithril_ore"           , "Mithril Ore");
        add("block.MODID.deepslate_mithril_ore" , "Deepslate Mithril Ore");
        add("block.MODID.raw_mithril_block"     , "Block of Raw Mithril");
        add("block.MODID.mithril_block"         , "Block of Mithril");
        add("block.MODID.gold"                  , "Molten Gold");

        // Misc
        add("itemGroup.MODID"                   , "Geomancy");

        // Guidebook
        add("book.MODID.guidebook.name"         , "Notes on Minerals");
        add("book.MODID.guidebook.tooltip"      , "we can never dig too deep");

        add("book.MODID.guidebook.main.name"        , "Discovery");
        add("book.MODID.guidebook.main.entry.name"  , "Discovery");
        add("book.MODID.guidebook.main.gold.name"   , "Molten Gold");
        add("book.MODID.guidebook.main.mithril.name", "Mithril");


        tb=null;
    }

    // helper function
    void add(String key, String value){
        key=key.replace("MODID", Geomancy.MOD_ID);
        tb.add(key,value);
    }
}