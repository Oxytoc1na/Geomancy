package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {

        Advancement getDirt = Advancement.Builder.create()
                .display(
                        Items.DIRT, // The display icon
                        Text.literal("Your First Dirt Block"), // The title
                        Text.literal("Now make a house from it"), // The description
                        Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/gui/advancements/backgrounds/adventure.png"), // Background image for the tab in the advancements page, if this is a root advancement (has no parent)
                        AdvancementFrame.TASK, // TASK, CHALLENGE, or GOAL
                        true, // Show the toast when completing it
                        true, // Announce it to chat
                        false // Hide it in the advancement tab until it's achieved
                )
                // "got_dirt" is the name referenced by other advancements when they want to have "requirements."
                .criterion("got_dirt", InventoryChangedCriterion.Conditions.items(Items.DIRT))
                // Give the advancement an id
                .build(consumer, Geomancy.MOD_ID + ":get_dirt");
    }
}
