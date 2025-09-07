package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
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
import org.oxytocina.geomancy.spells.SpellBlocks;

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

        // progression milestones
        Advancement milestone_smithery = AddOrMilestoneAdvancement("smithery", List.of("geomancy:interaction/simple_smithery"),ModBlocks.SMITHERY,Geomancy.locate("textures/block/gilded_deepslate.png"));
        Advancement milestone_souls = AddAndMilestoneAdvancement("souls", List.of("geomancy:main/simple_maddened","geomancy:main/get_spellmaker"),ModBlocks.SMITHERY,Geomancy.locate("textures/block/octangulite_block.png"));
        Advancement milestone_soulforge = AddOrMilestoneAdvancement("soulforge", List.of("geomancy:interaction/simple_soulforge"),ModBlocks.SOUL_FORGE,Geomancy.locate("textures/block/orthoclase_block.png"));
        var enlightenmentList = List.of(
                "geomancy:main/simple_enlightenment_1","geomancy:main/simple_enlightenment_2","geomancy:main/simple_enlightenment_3",
                "geomancy:main/simple_enlightenment_4","geomancy:main/simple_enlightenment_5"
        );
        Advancement milestone_enlightenment = AddAndMilestoneAdvancement("enlightenment",enlightenmentList ,ModItems.SOUL_PREVIEW,Geomancy.locate("textures/block/orthoclase_block.png"));
        Advancement milestone_any_enlightenment = AddOrMilestoneAdvancement("enlightenment",enlightenmentList,ModItems.SOUL_PREVIEW,Geomancy.locate("textures/block/orthoclase_block.png"));

        Advancement got_molten_gold = AddGetItemAdvancement(ModFluids.MOLTEN_GOLD_BUCKET,"molten_gold",ModFluids.MOLTEN_GOLD_BUCKET,"main",AdvancementFrame.CHALLENGE,true,false,main);
        Advancement got_mithril = AddGetItemAdvancement(ModItems.RAW_MITHRIL,"mithril",ModItems.RAW_MITHRIL,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_octangulite = AddGetItemAdvancement(ModItems.RAW_OCTANGULITE,"octangulite",ModItems.RAW_OCTANGULITE,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_titanium = AddGetItemAdvancement(ModItems.RAW_TITANIUM,"titanium",ModItems.RAW_TITANIUM,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_lead = AddGetItemAdvancement(ModItems.RAW_LEAD,"lead",ModItems.RAW_LEAD,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_molybdenum = AddGetItemAdvancement(ModItems.RAW_MOLYBDENUM,"molybdenum",ModItems.RAW_MOLYBDENUM,"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_gilded_deepslate = AddGetItemAdvancement(ModBlocks.DECORATED_GILDED_DEEPSLATE,"gilded_deepslate",new ItemConvertible[]{ModBlocks.GILDED_DEEPSLATE,ModBlocks.DECORATED_GILDED_DEEPSLATE},"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_soul_oak = AddGetItemAdvancement(ModBlocks.SOUL_OAK_PLANKS,"soul_oak",new ItemConvertible[]{ModBlocks.SOUL_OAK_LOG,ModBlocks.SOUL_OAK_PLANKS},"main",AdvancementFrame.TASK,true,false,main);
        Advancement got_spellmaker = AddGetItemAdvancement(ModBlocks.SPELLMAKER,"spellmaker",new ItemConvertible[]{ModBlocks.SPELLMAKER},"main",AdvancementFrame.TASK,true,false,main);
        Advancement simple_duplicate_trinkets = AddSimpleAdvancement(ModItems.ARTIFACT_OF_IRON,"duplicate_trinkets","duplicate_trinkets","main",AdvancementFrame.TASK,true,true,main);
        Advancement simple_tried_to_take_smithery_result = AddSimpleAdvancement(ModItems.IRON_HAMMER,"tried_to_take_smithery_result","tried_to_take_smithery_result","main",AdvancementFrame.TASK,true,true,milestone_smithery);
        Advancement simple_tried_to_smith_cold_forge = AddSimpleAdvancement(ModItems.GOLDEN_HAMMER,"tried_to_smith_cold_forge","tried_to_smith_cold_forge","main",AdvancementFrame.TASK,true,true,simple_tried_to_take_smithery_result);
        Advancement simple_lead_poisoned = AddSimpleAdvancement(ModItems.RAW_LEAD,"lead_poisoned","lead_poisoned","main",AdvancementFrame.TASK,true,true,got_lead);
        Advancement simple_maddened = AddSimpleAdvancement(ModItems.RAW_OCTANGULITE,"maddened","maddened","main",AdvancementFrame.TASK,true,true,got_octangulite);

        // lore
        var loreItems = new Item[]{
                ModItems.LORE_BOOK_GOLDSMITH_1,
                ModItems.LORE_BOOK_GOLDSMITH_2,
                ModItems.LORE_BOOK_GOLDSMITH_3,
                ModItems.LORE_BOOK_GOLDSMITH_4,
                ModItems.LORE_BOOK_WAR_1,
                ModItems.LORE_BOOK_WAR_2,
                ModItems.LORE_BOOK_WAR_3,
                ModItems.LORE_LOG_EXPEDITION_1,
                ModItems.LORE_LOG_EXPEDITION_2,
                ModItems.LORE_LOG_EXPEDITION_3,
                ModItems.LORE_LOG_EXPEDITION_4,
                ModItems.LORE_LOG_EXPEDITION_5,
                ModItems.LORE_BOOK_EXTRAS_CREATION,
                ModItems.LORE_LOG_EXTRAS_RESEARCH_1,
                ModItems.LORE_LOG_EXTRAS_RESEARCH_2
        };
        for (var item : loreItems) {
            AddGetItemAdvancement(null, Registries.ITEM.getId(item).getPath(),item,"lore",AdvancementFrame.TASK,false,true,null);
        }

        // interaction (hidden)
        Advancement interaction_smithery = AddSimpleAdvancement(null,"smithery","interact","interaction",AdvancementFrame.TASK,false,true,null);
        Advancement interaction_soulforge = AddSimpleAdvancement(null,"soulforge","interact","interaction",AdvancementFrame.TASK,false,true,null);

        // structures visited
        Advancement structure_ancient_hall = AddLocationAdvancement("ancient_hall","ancient_hall",Items.GILDED_BLACKSTONE,main);
        Advancement structure_octangula = AddLocationAdvancement("octangula","octangula",ModBlocks.CUT_TITANIUM,main);
        Advancement structure_digsite = AddLocationAdvancement("digsite","digsite",ModBlocks.CUT_LEAD,main);

        // obtained spell component advancements
        Advancement got_spellcomponent = AddGetItemAdvancement(ModItems.SPELLCOMPONENT,"spellcomponent",ModItems.SPELLCOMPONENT,"octangulite",AdvancementFrame.TASK,true,false,main);
        for (var id : SpellBlocks.functions.keySet()){
            NbtCompound nbt = new NbtCompound();
            NbtCompound component = new NbtCompound();
            component.putString("func",id.toString());
            nbt.put("component",component);
            AddGetItemWithNbtAdvancement(ModItems.SPELLCOMPONENT,nbt,id.getPath(),"spellcomponents");
        }

        // tasks
        var spell_unlocker = milestone_souls;
        Advancement spell_ignition =    AddSimpleAdvancement(Items.FLINT_AND_STEEL,"ignition","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_liftoff =     AddSimpleAdvancement(Items.FIREWORK_ROCKET,"liftoff","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_ambition =    AddSimpleAdvancement(Items.COMMAND_BLOCK,"ambition","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_brazilian =   AddSimpleAdvancement(Items.LEATHER_BOOTS,"brazilian","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_celeste =     AddSimpleAdvancement(Items.FEATHER,"celeste","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_long_arms =   AddSimpleAdvancement(Items.SPYGLASS,"long_arms","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_ftl =         AddSimpleAdvancement(Items.SPYGLASS,"ftl","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_bones =       AddSimpleAdvancement(Items.BONE,"bones","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_fireball =    AddSimpleAdvancement(Items.FIRE_CHARGE,"fireball","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_deception =   AddSimpleAdvancement(Items.NOTE_BLOCK,"deception","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_medic =       AddSimpleAdvancement(Items.ENCHANTED_GOLDEN_APPLE,"medic","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_ulterior_motives = AddSimpleAdvancement(Items.WRITABLE_BOOK,"ulterior_motives","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_build_big =   AddSimpleAdvancement(Items.CHEST,"build_big","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement spell_deconsciousness =   AddSimpleAdvancement(Items.SOUL_CAMPFIRE,"deconsciousness","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement enlightenment_1 = AddSimpleAdvancement(ModItems.RAW_OCTANGULITE,"enlightenment_1","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement enlightenment_2 = AddSimpleAdvancement(ModItems.RAW_OCTANGULITE,"enlightenment_2","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement enlightenment_3 = AddSimpleAdvancement(ModItems.RAW_OCTANGULITE,"enlightenment_3","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement enlightenment_4 = AddSimpleAdvancement(ModItems.RAW_OCTANGULITE,"enlightenment_4","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);
        Advancement enlightenment_5 = AddSimpleAdvancement(ModItems.RAW_OCTANGULITE,"enlightenment_5","do","spells",AdvancementFrame.CHALLENGE,true,true,spell_unlocker);

    }
    private Advancement AddGetItemAdvancement(ItemConvertible item,String name, ItemConvertible conditionItem, String category, AdvancementFrame frame, boolean announce, boolean hidden, Advancement parent){
        return AddGetItemAdvancement(item,name,new Item[]{conditionItem.asItem()},category,frame,announce,hidden,parent);
    }

    private Advancement AddGetItemAdvancement(ItemConvertible item,String name, ItemConvertible[] conditionItems, String category, AdvancementFrame frame, boolean announce, boolean hidden, Advancement parent)
    {

        var res = Advancement.Builder.create()
                .criterion("got_"+name, InventoryChangedCriterion.Conditions.items(conditionItems))
                .parent(parent);

        if(item!=null) res = res.display(
            item, // The display icon
            Text.translatable("advancement."+Geomancy.MOD_ID+"."+category+".get_"+name+".name"), // The title
            Text.translatable("advancement."+Geomancy.MOD_ID+"."+category+".get_"+name+".description"), // The title
            null,
            frame, // TASK, CHALLENGE, or GOAL
            announce, // Show the toast when completing it
            announce, // Announce it to chat
            hidden // Hide it in the advancement tab until it's achieved
            );

        return res.build(consumer, Geomancy.MOD_ID + ":"+category+"/get_"+name);
    }

    private Advancement AddGetItemWithNbtAdvancement(ItemConvertible item, NbtCompound nbt, String name, String category)
    {

        Advancement res = Advancement.Builder.create()
                .criterion("got_"+name, InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create()
                                .items(item)
                                .nbt(nbt)
                        .build()
                ))
                .build(consumer, Geomancy.MOD_ID + ":"+category+"/get_"+name);

        return res;
    }

    private Advancement AddSimpleAdvancement(ItemConvertible display, String name, String conditionName, String category, AdvancementFrame frame, boolean announce, boolean hidden, Advancement parent)
    {

        Advancement.Builder res = Advancement.Builder.create()
                .criterion("simple_"+name, new SimpleCriterion.Conditions(conditionName))
                .parent(parent);

        if(display!=null)
            res.display(
            display, // The display icon
            Text.translatable("advancement."+Geomancy.MOD_ID+"."+category+".simple_"+name+".name"), // The title
            Text.translatable("advancement."+Geomancy.MOD_ID+"."+category+".simple_"+name+".description"), // The title
            null,
            frame, // TASK, CHALLENGE, or GOAL
            announce, // Show the toast when completing it
            announce, // Announce it to chat
            hidden // Hide it in the advancement tab until it's achieved
            );

        return res.build(consumer, Geomancy.MOD_ID + ":"+category+"/simple_"+name);
    }

    private Advancement AddOrMilestoneAdvancement(String name, List<String> ORprerequisites,ItemConvertible icon, Identifier bgTexture){
        List<List<String>> AND = new ArrayList<>();
        AND.add(ORprerequisites);
        return AddFullMilestoneAdvancement(name,AND,icon,bgTexture);
    }

    private Advancement AddAndMilestoneAdvancement(String name, List<String> ANDprerequisites,ItemConvertible icon, Identifier bgTexture){
        List<List<String>> AND = new ArrayList<>();
        for(var req : ANDprerequisites)
            AND.add(List.of(req));
        return AddFullMilestoneAdvancement(name,AND,icon,bgTexture);
    }

    private Advancement AddFullMilestoneAdvancement(String name, List<List<String>> ANDprerequisites, ItemConvertible icon, Identifier bgTexture){
        Advancement.Builder builder = Advancement.Builder.create();
        String advancementName = Geomancy.MOD_ID + ":milestones/milestone_"+name;

        String[][] reqs = new String[ANDprerequisites.size()][];
        for (int i = 0; i < ANDprerequisites.size(); i++) {
            var ORprereqs = ANDprerequisites.get(i);
            reqs[i] = new String[ORprereqs.size()];
            for (int j = 0; j < ORprereqs.size(); j++) {
                reqs[i][j] = ORprereqs.get(j);

                CriterionConditions conditions = ModAdvancementCriterion.conditionsFromAdvancement(
                        Identifier.tryParse(reqs[i][j])
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

    public static Identifier getComponentID(String compName){
        return Geomancy.locate("spellcomponents/get_"+compName);
    }
}
