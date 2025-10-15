package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.ExtraItemSettings;
import org.oxytocina.geomancy.items.artifacts.ArtifactItem;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.GemSlot;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import static org.oxytocina.geomancy.registries.ModItemTags.*;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider<Item> {
    public static ModItemTagProvider INSTANCE = null;

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
        INSTANCE=this;
    }


    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        //getOrCreateTagBuilder(SMELLY_ITEMS)
        //    .add(Items.SLIME_BALL)
        //    .add(Items.ROTTEN_FLESH)
        //    .addOptionalTag(ItemTags.DIRT)
        //    .add(Identifier.of(Identifier.DEFAULT_NAMESPACE,"oak_planks"))
        //    .forceAddTag(ItemTags.BANNERS)
        //    .setReplace(true);

        getOrCreateTagBuilder(ItemTags.MUSIC_DISCS)
                .add(ModItems.MUSIC_DISC_DIGGY)
                .setReplace(false);

        // jewelry gems
        for(ItemConvertible item : GemSlot.gemColorMap.keySet())
            getOrCreateTagBuilder(JEWELRY_GEMS).add(item.asItem());

        // octangulite
        var octangulite = getOrCreateTagBuilder(OCTANGULITE).setReplace(false);
        octangulite.add(ModItems.OCTANGULITE_INGOT);
        octangulite.add(ModItems.OCTANGULITE_NUGGET);
        octangulite.add(ModItems.RAW_OCTANGULITE);
        octangulite.add(ModItems.OCTANGULITE_RING);
        octangulite.add(ModItems.OCTANGULITE_NECKLACE);
        octangulite.add(ModItems.OCTANGULITE_PENDANT);
        octangulite.add(ModBlocks.OCTANGULITE_BLOCK.asItem());
        octangulite.add(ModBlocks.OCTANGULITE_ORE.asItem());
        octangulite.add(ModBlocks.DEEPSLATE_OCTANGULITE_ORE.asItem());
        octangulite.add(ModBlocks.RAW_OCTANGULITE_BLOCK.asItem());

        // stellge curious
        getOrCreateTagBuilder(STELLGE_CURIOUS).setReplace(false)
                .forceAddTag(OCTANGULITE)
                .forceAddTag(FITS_IN_CASTERS)
                .forceAddTag(FITS_IN_SOUL_BORE)
                .forceAddTag(SPELL_STORING)
                .forceAddTag(CASTING_ITEM)
                .add(ModItems.SPELLCOMPONENT)
        ;

        // spell storing
        getOrCreateTagBuilder(SPELL_STORING).setReplace(false)
                .add(
                        ModItems.SPELLSTORAGE_SMALL,
                        ModItems.SPELLSTORAGE_MEDIUM,
                        ModItems.SPELLSTORAGE_LARGE
                );

        // variable storing
        getOrCreateTagBuilder(VARIABLE_STORING).setReplace(false)
                .add(
                        ModItems.VARSTORAGE_SMALL,
                        ModItems.VARSTORAGE_MEDIUM,
                        ModItems.VARSTORAGE_LARGE
                );

        // fits in casters
        getOrCreateTagBuilder(FITS_IN_CASTERS)
                .forceAddTag(SPELL_STORING)
                .forceAddTag(VARIABLE_STORING)
                        ;

        // fits in soul bore
        getOrCreateTagBuilder(FITS_IN_SOUL_BORE)
                .add(
                        ModItems.OCTANGULITE_NUGGET,
                        ModItems.OCTANGULITE_INGOT,
                        ModItems.RAW_OCTANGULITE,
                        ModBlocks.OCTANGULITE_BLOCK.asItem(),
                        ModBlocks.RAW_OCTANGULITE_BLOCK.asItem()
                )
        ;

        // component storing
        getOrCreateTagBuilder(COMPONENT_STORING).setReplace(false)
                .add(
                        ModItems.SPELLCOMPONENT
                );

        // spell casters
        getOrCreateTagBuilder(CASTING_ITEM).setReplace(false)
                .add(
                        ModItems.NOVICE_GLOVE,
                        ModItems.APPRENTICE_GLOVE,
                        ModItems.JOURNEY_GLOVE,
                        ModItems.EXPERT_GLOVE,
                        ModItems.MASTER_GLOVE,
                        ModItems.PRECOMP_CASTER,
                        ModItems.STELLGE_CASTER
                        );

        // chiseled bookshelf
        var builder = getOrCreateTagBuilder(ItemTags.BOOKSHELF_BOOKS);
        for(var item : ExtraItemSettings.ITEMS_IN_LORE_GROUP)
            builder.add(item);

        // tools
        {
            getOrCreateTagBuilder(ItemTags.SWORDS).add(
                    ModItems.LEAD_SWORD,
                    ModItems.MITHRIL_SWORD,
                    ModItems.MOLYBDENUM_SWORD,
                    ModItems.TITANIUM_SWORD,
                    ModItems.OCTANGULITE_SWORD
            );

            getOrCreateTagBuilder(ItemTags.SHOVELS).add(
                    ModItems.LEAD_SHOVEL,
                    ModItems.MITHRIL_SHOVEL,
                    ModItems.MOLYBDENUM_SHOVEL,
                    ModItems.TITANIUM_SHOVEL,
                    ModItems.OCTANGULITE_SHOVEL
            );

            getOrCreateTagBuilder(ItemTags.PICKAXES).add(
                    ModItems.LEAD_PICKAXE,
                    ModItems.MITHRIL_PICKAXE,
                    ModItems.MOLYBDENUM_PICKAXE,
                    ModItems.TITANIUM_PICKAXE,
                    ModItems.OCTANGULITE_PICKAXE
            );

            getOrCreateTagBuilder(ItemTags.HOES).add(
                    ModItems.LEAD_HOE,
                    ModItems.MITHRIL_HOE,
                    ModItems.MOLYBDENUM_HOE,
                    ModItems.TITANIUM_HOE,
                    ModItems.OCTANGULITE_HOE
            );

            getOrCreateTagBuilder(ItemTags.AXES).add(
                    ModItems.LEAD_AXE,
                    ModItems.MITHRIL_AXE,
                    ModItems.MOLYBDENUM_AXE,
                    ModItems.TITANIUM_AXE,
                    ModItems.OCTANGULITE_AXE
            );
        }


        // stone tool materials
        getOrCreateTagBuilder(ItemTags.STONE_TOOL_MATERIALS).add(
                ModBlocks.NULL_ROCK.asItem()
        );

        // stone crafting materials
        getOrCreateTagBuilder(ItemTags.STONE_CRAFTING_MATERIALS).add(
                ModBlocks.NULL_ROCK.asItem()
        );

        // beacon payments
        getOrCreateTagBuilder(ItemTags.BEACON_PAYMENT_ITEMS).add(
                ModItems.MITHRIL_INGOT,
                ModItems.MOLYBDENUM_INGOT,
                ModItems.LEAD_INGOT,
                ModItems.TITANIUM_INGOT,
                ModItems.OCTANGULITE_INGOT
        );

        // raw ores
        getOrCreateTagBuilder(C_RAW_ORES).add(
                ModItems.RAW_MITHRIL,
                ModItems.RAW_MOLYBDENUM,
                ModItems.RAW_LEAD,
                ModItems.RAW_TITANIUM,
                ModItems.RAW_OCTANGULITE
        );

        // ingots
        getOrCreateTagBuilder(C_INGOTS).add(
                ModItems.MITHRIL_INGOT,
                ModItems.MOLYBDENUM_INGOT,
                ModItems.LEAD_INGOT,
                ModItems.TITANIUM_INGOT,
                ModItems.OCTANGULITE_INGOT
        );

        // nuggets
        getOrCreateTagBuilder(C_NUGGETS).add(
                ModItems.MITHRIL_NUGGET,
                ModItems.MOLYBDENUM_NUGGET,
                ModItems.LEAD_NUGGET,
                ModItems.TITANIUM_NUGGET,
                ModItems.OCTANGULITE_NUGGET
        );

        // foods
        getOrCreateTagBuilder(C_FOODS).add(
                ModItems.LEAD_APPLE,
                ModItems.OCTANGULITE_APPLE
        );

        // gems
        getOrCreateTagBuilder(C_GEMS).add(
                ModItems.PERIDOT,
                ModItems.AXINITE,
                ModItems.TOURMALINE,
                ModItems.ORTHOCLASE
        );

        // geodes
        getOrCreateTagBuilder(GEODES).add(
                ModItems.STONE_GEODE,
                ModItems.DEEPSLATE_GEODE
        );

        generateAccessoryTags();
        actualizeHybrids();
    }

    private void generateAccessoryTags() {
        //this.getOrCreateTagBuilder(accessory("chest/cape")).add(
        //this.getOrCreateTagBuilder(accessory("head/hat")).add(
        //this.getOrCreateTagBuilder(accessory("legs/belt")).add(

        this.getOrCreateTagBuilder(accessory("all")).add(
                ModItems.CASTER_CORE
        );

        for(JewelryItem any : ModItems.JewelryAnySlotItems)
        {
            this.getOrCreateTagBuilder(accessory("all")).add(any);
        }

        for(JewelryItem necklace : ModItems.JewelryNecklaceItems)
        {
            this.getOrCreateTagBuilder(accessory("chest/necklace")).add(necklace);
        }

        for(JewelryItem ring : ModItems.JewelryRingItems)
        {
            this.getOrCreateTagBuilder(accessory("hand/ring")).add(ring);
            this.getOrCreateTagBuilder(accessory("offhand/ring")).add(ring);
        }

        for(ArtifactItem artifact : ModItems.ArtifactItems)
        {
            this.getOrCreateTagBuilder(accessory("all")).add(
                    artifact
            );
        }

        this.getOrCreateTagBuilder(accessory("head/face")).add(
                ModItems.MANIA_MASK,
                ModItems.SORROW_MASK,
                ModItems.PARANOIA_MASK,
                ModItems.MELANCHOLY_MASK,
                ModItems.ADAPTIVE_MASK
        );



    }

    private static TagKey<Item> accessory(String name) {
        return itemTag(new Identifier("trinkets", name));
    }

    private static TagKey<Item> itemTag(Identifier location) {
        return TagKey.of(Registries.ITEM.getKey(), location);
    }

    public void actualizeHybrids(){
        for(var entry : ModBlockTagProvider.HYBRID_ITEM_BLOCKS.entrySet()){
            var builder = getOrCreateTagBuilder(entry.getKey());
            for(var block : entry.getValue())
                builder.add(block.asItem());
        }

        for(var entry : ModBlockTagProvider.HYBRID_ITEM_TAGS.entrySet()){
            var builder = getOrCreateTagBuilder(entry.getKey());
            for(var tag : entry.getValue())
                builder.forceAddTag(tag);
        }
    }
}