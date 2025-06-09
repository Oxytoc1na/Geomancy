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
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.fluids.ModFluids;
import org.oxytocina.geomancy.items.ModItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {

        Advancement main = Advancement.Builder.create()
                .display(
                        ModItems.GUIDE_BOOK, // The display icon
                        Text.translatable("advancement."+Geomancy.MOD_ID+".main.name"), // The title
                        Text.translatable("advancement."+Geomancy.MOD_ID+".main.description"), // The title
                        Identifier.of(Geomancy.MOD_ID,"textures/block/deepslate_mithril_ore.png"), // Background image for the tab in the advancements page, if this is a root advancement (has no parent)
                        AdvancementFrame.TASK, // TASK, CHALLENGE, or GOAL
                        false, // Show the toast when completing it
                        false, // Announce it to chat
                        false // Hide it in the advancement tab until it's achieved
                )
                .criterion("crafting_table", InventoryChangedCriterion.Conditions.items(Items.CRAFTING_TABLE))
                .build(consumer, Geomancy.MOD_ID + ":main/root");

        Advancement get_mithril = Advancement.Builder.create()
                .display(
                        ModItems.RAW_MITHRIL, // The display icon
                        Text.translatable("advancement."+Geomancy.MOD_ID+".main.get_mithril.name"), // The title
                        Text.translatable("advancement."+Geomancy.MOD_ID+".main.get_mithril.description"), // The title
                        null,
                        AdvancementFrame.TASK, // TASK, CHALLENGE, or GOAL
                        true, // Show the toast when completing it
                        true, // Announce it to chat
                        true // Hide it in the advancement tab until it's achieved
                )
                // "got_mithril" is the name referenced by other advancements when they want to have "requirements."
                .criterion("got_mithril", InventoryChangedCriterion.Conditions.items(ModItems.RAW_MITHRIL))
                .parent(main)
                // Give the advancement an id
                .build(consumer, Geomancy.MOD_ID + ":main/get_mithril");

        Advancement get_gold = Advancement.Builder.create()
                .display(
                        ModFluids.GOLD_BUCKET, // The display icon
                        Text.translatable("advancement."+Geomancy.MOD_ID+".main.get_gold.name"), // The title
                        Text.translatable("advancement."+Geomancy.MOD_ID+".main.get_gold.description"), // The title
                        null,
                        AdvancementFrame.CHALLENGE, // TASK, CHALLENGE, or GOAL
                        true, // Show the toast when completing it
                        true, // Announce it to chat
                        true // Hide it in the advancement tab until it's achieved
                )
                .criterion("got_gold", InventoryChangedCriterion.Conditions.items(ModFluids.GOLD_BUCKET))
                .parent(main)
                .build(consumer, Geomancy.MOD_ID + ":main/get_gold");
    }
}
