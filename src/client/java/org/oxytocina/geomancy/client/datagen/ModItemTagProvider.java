package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.artifacts.ArtifactItem;
import org.oxytocina.geomancy.items.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider<Item> {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    public static final TagKey<Item> SMELLY_ITEMS = TagKey.of(RegistryKeys.ITEM, Identifier.of(Geomancy.MOD_ID, "smelly_items"));
    public static final TagKey<Item> MUSIC_DISCS = TagKey.of(RegistryKeys.ITEM, Identifier.of(Identifier.DEFAULT_NAMESPACE, "music_discs"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(SMELLY_ITEMS)
            .add(Items.SLIME_BALL)
            .add(Items.ROTTEN_FLESH)
            .addOptionalTag(ItemTags.DIRT)
            .add(Identifier.of(Identifier.DEFAULT_NAMESPACE,"oak_planks"))
            .forceAddTag(ItemTags.BANNERS)
            .setReplace(true);

        getOrCreateTagBuilder(MUSIC_DISCS)
                .add(ModItems.MUSIC_DISC_DIGGY)
                .setReplace(true);

        generateAccessoryTags();
    }

    private void generateAccessoryTags() {
        //this.getOrCreateTagBuilder(accessory("chest/cape")).add(
        //);
        //this.getOrCreateTagBuilder(accessory("chest/necklace")).add(
        //);
        Item[] rings = {

        };
        this.getOrCreateTagBuilder(accessory("hand/ring")).add(rings);
        this.getOrCreateTagBuilder(accessory("offhand/ring")).add(rings);
        //this.getOrCreateTagBuilder(accessory("head/face")).add(
        //);
        //this.getOrCreateTagBuilder(accessory("head/hat")).add(
        //);
        //this.getOrCreateTagBuilder(accessory("legs/belt")).add(
        //);
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