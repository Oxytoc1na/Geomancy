package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
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

    private Consumer<Advancement> consumer;
    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        this.consumer=consumer;
        Advancement main = Advancement.Builder.create()
                .display(
                        ModItems.GUIDE_BOOK, // The display icon
                        Text.translatable("advancement."+Geomancy.MOD_ID+".main.name"), // The title
                        Text.translatable("advancement."+Geomancy.MOD_ID+".main.description"), // The title
                        Identifier.of(Geomancy.MOD_ID,"textures/block/raw_mithril_block.png"), // Background image for the tab in the advancements page, if this is a root advancement (has no parent)
                        AdvancementFrame.TASK, // TASK, CHALLENGE, or GOAL
                        false, // Show the toast when completing it
                        false, // Announce it to chat
                        false // Hide it in the advancement tab until it's achieved
                )
                .criterion("crafting_table", InventoryChangedCriterion.Conditions.items(Items.CRAFTING_TABLE))
                .build(consumer, Geomancy.MOD_ID + ":main/root");

        Advancement got_molten_gold = AddGetItemAdvancement(ModFluids.GOLD_BUCKET,"molten_gold",ModFluids.GOLD_BUCKET,"main",AdvancementFrame.CHALLENGE,true,false,main);
        Advancement got_mithril = AddGetItemAdvancement(ModItems.RAW_MITHRIL,"mithril",ModItems.RAW_MITHRIL,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_gilded_deepslate = AddGetItemAdvancement(ModBlocks.DECORATED_GILDED_DEEPSLATE,"gilded_deepslate",new ItemConvertible[]{ModBlocks.GILDED_DEEPSLATE,ModBlocks.DECORATED_GILDED_DEEPSLATE},"main",AdvancementFrame.TASK,true,false,main);

    }
    private Advancement AddGetItemAdvancement(ItemConvertible item,String name, ItemConvertible conditionItem, String category, AdvancementFrame frame, boolean announce, boolean hidden, Advancement parent){
        return AddGetItemAdvancement(item,name,new Item[]{conditionItem.asItem()},category,frame,announce,hidden,parent);
    }

    private Advancement AddGetItemAdvancement(ItemConvertible item,String name, ItemConvertible[] conditionItems, String category, AdvancementFrame frame, boolean announce, boolean hidden, Advancement parent)
    {

        Advancement res = Advancement.Builder.create()
                .display(
                        item, // The display icon
                        Text.translatable("advancement."+Geomancy.MOD_ID+"."+category+".get_"+name+".name"), // The title
                        Text.translatable("advancement."+Geomancy.MOD_ID+"."+category+".get_"+name+".description"), // The title
                        null,
                        frame, // TASK, CHALLENGE, or GOAL
                        announce, // Show the toast when completing it
                        announce, // Announce it to chat
                        hidden // Hide it in the advancement tab until it's achieved
                )
                .criterion("got_"+name, InventoryChangedCriterion.Conditions.items(conditionItems))
                .parent(parent)
                .build(consumer, Geomancy.MOD_ID + ":"+category+"/get_"+name);

        return res;
    }
}
