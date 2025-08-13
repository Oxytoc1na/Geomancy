package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.artifacts.ArtifactItem;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.GemSlot;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import static org.oxytocina.geomancy.registries.ModItemTags.*;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider<Item> {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }


    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(SMELLY_ITEMS)
            .add(Items.SLIME_BALL)
            .add(Items.ROTTEN_FLESH)
            .addOptionalTag(ItemTags.DIRT)
            .add(Identifier.of(Identifier.DEFAULT_NAMESPACE,"oak_planks"))
            .forceAddTag(ItemTags.BANNERS)
            .setReplace(true);

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

        // wood
        getOrCreateTagBuilder(ItemTags.PLANKS).add(
                ModBlocks.SOUL_OAK_PLANKS.asItem()
        ).setReplace(false);
        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN).add(
                ModBlocks.SOUL_OAK_LOG.asItem(),
                ModBlocks.STRIPPED_SOUL_OAK_LOG.asItem(),
                ModBlocks.SOUL_OAK_WOOD.asItem(),
                ModBlocks.STRIPPED_SOUL_OAK_WOOD.asItem()
        ).setReplace(false);
        getOrCreateTagBuilder(ItemTags.WOODEN_BUTTONS).add(
                ModBlocks.SOUL_OAK_BUTTON.asItem()
        ).setReplace(false);
        getOrCreateTagBuilder(ItemTags.WOODEN_DOORS).add(
                ModBlocks.SOUL_OAK_DOOR.asItem()
        ).setReplace(false);
        getOrCreateTagBuilder(ItemTags.WOODEN_FENCES).add(
                ModBlocks.SOUL_OAK_FENCE.asItem()
        ).setReplace(false);
        getOrCreateTagBuilder(ItemTags.WOODEN_SLABS).add(
                ModBlocks.SOUL_OAK_SLAB.asItem()
        ).setReplace(false);
        getOrCreateTagBuilder(ItemTags.WOODEN_STAIRS).add(
                ModBlocks.SOUL_OAK_STAIRS.asItem()
        ).setReplace(false);
        getOrCreateTagBuilder(ItemTags.WOODEN_PRESSURE_PLATES).add(
                ModBlocks.SOUL_OAK_PRESSURE_PLATE.asItem()
        ).setReplace(false);
        getOrCreateTagBuilder(ItemTags.WOODEN_TRAPDOORS).add(
                ModBlocks.SOUL_OAK_TRAPDOOR.asItem()
        ).setReplace(false);

        // stellge curious
        getOrCreateTagBuilder(STELLGE_CURIOUS).setReplace(false)
                        .forceAddTag(OCTANGULITE);

        // spell storing
        getOrCreateTagBuilder(SPELL_STORING).setReplace(false)
                .add(
                        ModItems.SPELLSTORAGE_SMALL,
                        ModItems.SPELLSTORAGE_MEDIUM,
                        ModItems.SPELLSTORAGE_LARGE
                );

        // component storing
        getOrCreateTagBuilder(COMPONENT_STORING).setReplace(false)
                .add(
                        ModItems.SPELLCOMPONENT
                );

        // spell casters
        getOrCreateTagBuilder(CASTING_ITEM).setReplace(false)
                .add(
                        //ModItems.CASTER_TEST,
                        ModItems.SPELLGLOVE
                        );


        generateAccessoryTags();
    }

    private void generateAccessoryTags() {
        //this.getOrCreateTagBuilder(accessory("chest/cape")).add(
        //this.getOrCreateTagBuilder(accessory("head/face")).add(
        //this.getOrCreateTagBuilder(accessory("head/hat")).add(
        //this.getOrCreateTagBuilder(accessory("legs/belt")).add(

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

    }

    private static TagKey<Item> accessory(String name) {
        return itemTag(new Identifier("trinkets", name));
    }

    private static TagKey<Item> itemTag(Identifier location) {
        return TagKey.of(Registries.ITEM.getKey(), location);
    }
}