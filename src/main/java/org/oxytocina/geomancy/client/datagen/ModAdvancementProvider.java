package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.progression.advancement.LocationCriterion;
import org.oxytocina.geomancy.progression.advancement.ModAdvancementCriterion;
import org.oxytocina.geomancy.progression.advancement.SimpleCriterion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        Advancement got_molten_gold = AddGetItemAdvancement(ModFluids.MOLTEN_GOLD_BUCKET,"molten_gold",ModFluids.MOLTEN_GOLD_BUCKET,"main",AdvancementFrame.CHALLENGE,true,false,main);
        Advancement got_mithril = AddGetItemAdvancement(ModItems.RAW_MITHRIL,"mithril",ModItems.RAW_MITHRIL,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_octangulite = AddGetItemAdvancement(ModItems.RAW_OCTANGULITE,"octangulite",ModItems.RAW_OCTANGULITE,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_titanium = AddGetItemAdvancement(ModItems.RAW_TITANIUM,"titanium",ModItems.RAW_TITANIUM,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_lead = AddGetItemAdvancement(ModItems.RAW_LEAD,"lead",ModItems.RAW_LEAD,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_molybdenum = AddGetItemAdvancement(ModItems.RAW_MOLYBDENUM,"molybdenum",ModItems.RAW_MOLYBDENUM,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_gilded_deepslate = AddGetItemAdvancement(ModBlocks.DECORATED_GILDED_DEEPSLATE,"gilded_deepslate",new ItemConvertible[]{ModBlocks.GILDED_DEEPSLATE,ModBlocks.DECORATED_GILDED_DEEPSLATE},"main",AdvancementFrame.TASK,true,false,main);
        Advancement simple_duplicate_trinkets = AddSimpleAdvancement(ModItems.ARTIFACT_OF_IRON,"duplicate_trinkets","duplicate_trinkets","main",AdvancementFrame.TASK,true,false,main);
        Advancement simple_tried_to_take_smithery_result = AddSimpleAdvancement(ModItems.IRON_HAMMER,"tried_to_take_smithery_result","tried_to_take_smithery_result","main",AdvancementFrame.TASK,true,true,main);

        // progression milestones
        Advancement milestone_smithery = AddOrMilestoneAdvancement("smithery", Arrays.stream(new String[]{"geomancy:recipes/tools/smithery_block"}).toList(),ModBlocks.SMITHERY,Geomancy.locate("textures/block/gilded_deepslate.png"));

        // structures visited
        Advancement structure_ancient_hall = AddLocationAdvancement("ancient_hall","ancient_hall",Items.GILDED_BLACKSTONE,main);
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

    private Advancement AddSimpleAdvancement(ItemConvertible display, String name, String conditionName, String category, AdvancementFrame frame, boolean announce, boolean hidden, Advancement parent)
    {

        Advancement res = Advancement.Builder.create()
                .display(
                        display, // The display icon
                        Text.translatable("advancement."+Geomancy.MOD_ID+"."+category+".simple_"+name+".name"), // The title
                        Text.translatable("advancement."+Geomancy.MOD_ID+"."+category+".simple_"+name+".description"), // The title
                        null,
                        frame, // TASK, CHALLENGE, or GOAL
                        announce, // Show the toast when completing it
                        announce, // Announce it to chat
                        hidden // Hide it in the advancement tab until it's achieved
                )
                .criterion("simple_"+name, new SimpleCriterion.Conditions(conditionName))
                .parent(parent)
                .build(consumer, Geomancy.MOD_ID + ":"+category+"/simple_"+name);

        return res;
    }

    private Advancement AddOrMilestoneAdvancement(String name, List<String> ORprerequisites,ItemConvertible icon, Identifier bgTexture){
        List<List<String>> AND = new ArrayList<>();
        AND.add(ORprerequisites);
        return AddAndMilestoneAdvancement(name,AND,icon,bgTexture);
    }

    private Advancement AddAndMilestoneAdvancement(String name, List<List<String>> ANDprerequisites,ItemConvertible icon, Identifier bgTexture){
        Advancement.Builder builder = Advancement.Builder.create();
        String advancementName = Geomancy.MOD_ID + ":milestones/milestone_"+name;

        String[][] reqs = new String[ANDprerequisites.size()][];
        for (int i = 0; i < ANDprerequisites.size(); i++) {
            var ORprereqs = ANDprerequisites.get(i);
            reqs[i] = new String[ORprereqs.size()];
            for (int j = 0; j < ORprereqs.size(); j++) {
                reqs[i][j] = ORprereqs.get(j);

                CriterionConditions conditions = ModAdvancementCriterion.conditionsFromAdvancement(
                        new Identifier(reqs[i][j])
                );

                builder.criterion(reqs[i][j],conditions);
            }
        }
        builder.requirements(reqs);
        builder.display(
                new ItemStack(icon.asItem()), // The display icon
                Text.translatable("advancement."+advancementName+".name"), // The title
                Text.translatable("advancement."+advancementName+".description"), // The description
                bgTexture, // Background image for the tab in the advancements page, if this is a root advancement (has no parent)
                AdvancementFrame.GOAL, // TASK, CHALLENGE, or GOAL
                true, // Show the toast when completing it
                true, // Announce it to chat
                true // Hide it in the advancement tab until it's achieved
        );

        return builder.build(consumer, advancementName);
    }

    private Advancement AddLocationAdvancement(String name, String location, ItemConvertible icon, Advancement parent){
        return AddLocationAdvancement(name,Geomancy.locate(location),icon,parent);
    }

    private Advancement AddLocationAdvancement(String name, Identifier location, ItemConvertible icon, Advancement parent){
        Advancement.Builder builder = Advancement.Builder.create();
        String advancementName = Geomancy.MOD_ID + ":location/"+name;

        builder.criterion(location.toString(),new LocationCriterion.Conditions(location.toString()));
        builder.display(
                new ItemStack(icon.asItem()), // The display icon
                Text.translatable("advancement."+advancementName+".name"), // The title
                Text.translatable("advancement."+advancementName+".description"), // The description
                null, // Background image for the tab in the advancements page, if this is a root advancement (has no parent)
                AdvancementFrame.CHALLENGE, // TASK, CHALLENGE, or GOAL
                true, // Show the toast when completing it
                true, // Announce it to chat
                true // Hide it in the advancement tab until it's achieved
        );
        builder.parent(parent);

        return builder.build(consumer, advancementName);
    }
}
