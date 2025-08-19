package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.block.*;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.*;
import org.oxytocina.geomancy.client.datagen.recipes.GeodeRecipeJsonBuilder;
import org.oxytocina.geomancy.client.datagen.recipes.JewelryRecipeJsonBuilder;
import org.oxytocina.geomancy.client.datagen.recipes.SmitheryRecipeJsonBuilder;
import org.oxytocina.geomancy.items.GeodeItem;
import static org.oxytocina.geomancy.items.ModItems.*;
import static org.oxytocina.geomancy.blocks.ModBlocks.*;

import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.progression.advancement.ModAdvancementCriterion;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;
import org.oxytocina.geomancy.registries.ModBlockTags;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    private Consumer<RecipeJsonProvider> exporter;

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        this.exporter=exporter;

        // shapeless recipes
        offerShapelessRecipe(exporter, SUSPICIOUS_SUBSTANCE,SUSPICIOUS_SUBSTANCE,null,1);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, GUIDE_BOOK, 1)
                .input(Items.PAPER).input(Items.COBBLESTONE).group("misc").criterion(
                        hasItem(Items.COBBLESTONE), conditionsFromItem(Items.COBBLESTONE))
                .offerTo(exporter, "guidebook");

        // shaped recipes
        // iron hammer
        ShapedRecipeJsonBuilder.create(
                        RecipeCategory.TOOLS, IRON_HAMMER, 1)
                .input('#', Items.IRON_INGOT)
                .input('s', Items.STICK)
                .pattern("###")
                .pattern("#s#")
                .pattern(" s ")
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter);
        // component pouch
        AddSurrounded4(SPELLCOMPONENT,Items.LEATHER,COMPONENT_POUCH,1,RecipeCategory.TOOLS);


        // smelting recipes
        {
            AddSmeltAndBlastRecipe(List.of(
                            RAW_MITHRIL, ModBlocks.MITHRIL_ORE, ModBlocks.DEEPSLATE_MITHRIL_ORE),
                    MITHRIL_INGOT,5f,200,50);
            AddSmeltAndBlastRecipe(List.of(
                            RAW_MOLYBDENUM, ModBlocks.MOLYBDENUM_ORE, ModBlocks.DEEPSLATE_MOLYBDENUM_ORE),
                    MOLYBDENUM_INGOT,5f,100,50);
            AddSmeltAndBlastRecipe(List.of(
                            RAW_LEAD, ModBlocks.LEAD_ORE, ModBlocks.DEEPSLATE_LEAD_ORE),
                    LEAD_INGOT,5f,100,50);
            AddSmeltAndBlastRecipe(List.of(
                            RAW_TITANIUM, ModBlocks.TITANIUM_ORE, ModBlocks.DEEPSLATE_TITANIUM_ORE),
                    TITANIUM_INGOT,5f,200,50);
            AddSmeltAndBlastRecipe(List.of(
                            RAW_OCTANGULITE, ModBlocks.OCTANGULITE_ORE, ModBlocks.DEEPSLATE_OCTANGULITE_ORE),
                    OCTANGULITE_INGOT,5f,400,100);
        }

        // compacting recipes
        {
            AddOreCompactingRecipes(MITHRIL_NUGGET,MITHRIL_INGOT,ModBlocks.MITHRIL_BLOCK,RAW_MITHRIL,ModBlocks.RAW_MITHRIL_BLOCK);
            AddOreCompactingRecipes(OCTANGULITE_NUGGET,OCTANGULITE_INGOT,ModBlocks.OCTANGULITE_BLOCK,RAW_OCTANGULITE,ModBlocks.RAW_OCTANGULITE_BLOCK);
            AddOreCompactingRecipes(MOLYBDENUM_NUGGET,MOLYBDENUM_INGOT,ModBlocks.MOLYBDENUM_BLOCK,RAW_MOLYBDENUM,ModBlocks.RAW_MOLYBDENUM_BLOCK);
            AddOreCompactingRecipes(TITANIUM_NUGGET,TITANIUM_INGOT,ModBlocks.TITANIUM_BLOCK,RAW_TITANIUM,ModBlocks.RAW_TITANIUM_BLOCK);
            AddOreCompactingRecipes(LEAD_NUGGET,LEAD_INGOT,ModBlocks.LEAD_BLOCK,RAW_LEAD,ModBlocks.RAW_LEAD_BLOCK);

            AddReversibleCompressionRecipe(AXINITE_BLOCK,AXINITE);
            AddReversibleCompressionRecipe(ORTHOCLASE_BLOCK,ORTHOCLASE);
            AddReversibleCompressionRecipe(PERIDOT_BLOCK,PERIDOT);
            AddReversibleCompressionRecipe(TOURMALINE_BLOCK,TOURMALINE);
        }

        // smithing recipes
        {


            // generic jewelry recipes
            GenJewelryRecData[] mats = new GenJewelryRecData[]{
                    new GenJewelryRecData("iron",Items.IRON_INGOT,30,15),
                    new GenJewelryRecData("gold",Items.GOLD_INGOT,40,30),
                    new GenJewelryRecData("mithril",MITHRIL_INGOT,50,30),
                    new GenJewelryRecData("copper",Items.COPPER_INGOT,30,20),
                    new GenJewelryRecData("molybdenum",MOLYBDENUM_INGOT,40,20),
                    new GenJewelryRecData("titanium",TITANIUM_INGOT,70,40),
                    new GenJewelryRecData("lead",LEAD_INGOT,20,17),
                    new GenJewelryRecData("octangulite",OCTANGULITE_INGOT,80,100),
            };

            for(var dat : mats){
                // rings
                Identifier tempID = Geomancy.locate(dat.resultPrefix+"_ring");
                Item ringResult = Registries.ITEM.containsId(tempID) ? Registries.ITEM.get(tempID) : null;
                if(ringResult!=null) AddRingSmitheryRecipe(dat.ingredient,ringResult,dat.progress,dat.difficulty);

                // necklaces
                tempID = Geomancy.locate(dat.resultPrefix+"_necklace");
                Item necklaceResult = Registries.ITEM.containsId(tempID) ? Registries.ITEM.get(tempID) : null;
                if(necklaceResult!=null) AddNecklaceSmitheryRecipe(dat.ingredient,necklaceResult,dat.progress,dat.difficulty);

                // pendants
                tempID = Geomancy.locate(dat.resultPrefix+"_pendant");
                Item pendantResult = Registries.ITEM.containsId(tempID) ? Registries.ITEM.get(tempID) : null;
                if(pendantResult!=null) AddPendantSmitheryRecipe(dat.ingredient,pendantResult,dat.progress,dat.difficulty);
            }

            // jewelry gem slotting recipes
            for(Item item : IJewelryItem.List){
                AddSmitheryJewelryRecipe((IJewelryItem)item);
            }

            // mithril hammer
            AddShapedSmitheryRecipe(new String[]{
                            "ooo",
                            "oso",
                            " s "}
                    ,new SPatKey[]{
                            new SPatKey("o",SmithingIngredient.ofItems(1,1,1,MITHRIL_INGOT)),
                            new SPatKey("s",SmithingIngredient.ofItems(1,1,1,Items.STICK)),
                    },
                    MITHRIL_HAMMER,1,100,12,conditionsFromItem(MITHRIL_INGOT),null);


            // geode recipes
            for(GeodeItem item : geodeItems){
                AddGeodeRecipe(item);
            }

            // artifacts
            {
                // Empty Artifact
                AddSmitheryRecipe(Arrays.stream(new SmithingIngredient[] {
                                SmithingIngredient.ofItems(MITHRIL_INGOT),
                                SmithingIngredient.ofItems(Items.LEATHER)
                        }).toList(),EMPTY_ARTIFACT,1, 40,5, true,
                        conditionsFromItem(MITHRIL_INGOT),null);

                // Artifact of Iron
                AddSmitheryRecipe(Arrays.stream(new SmithingIngredient[] {
                                SmithingIngredient.ofItems(1,1,4,EMPTY_ARTIFACT),
                                SmithingIngredient.ofItems(1,1,1,Items.IRON_BLOCK),
                                SmithingIngredient.ofItems(1,1,3,Items.SHIELD),
                                SmithingIngredient.ofItems(1,1,5,Items.IRON_CHESTPLATE),
                                SmithingIngredient.ofItems(1,1,7,Items.IRON_BARS),
                        }).toList(),ARTIFACT_OF_IRON,1, 100,20,false,
                        conditionsFromItem(EMPTY_ARTIFACT),null);

                // Artifact of Gold
                AddSmitheryRecipe(Arrays.stream(new SmithingIngredient[] {
                                SmithingIngredient.ofItems(1,1,4,EMPTY_ARTIFACT),
                                SmithingIngredient.ofItems(1,1,1,Items.GOLD_BLOCK),
                                SmithingIngredient.ofItems(1,1,3,Items.GOLDEN_APPLE),
                                SmithingIngredient.ofItems(1,1,5,Items.GOLDEN_HELMET),
                                SmithingIngredient.ofItems(1,1,7,Items.GOLDEN_CARROT),
                        }).toList(),ARTIFACT_OF_GOLD,1, 100,20,false,
                        conditionsFromItem(EMPTY_ARTIFACT),null);
            }

            // spell storage
            {
                // small
                AddShapedSmitheryRecipe(new String[]{
                                " t ",
                                " m ",
                                " g "}
                        ,new SPatKey[]{
                                new SPatKey("t",SmithingIngredient.ofItems(1,1,TITANIUM_INGOT)),
                                new SPatKey("m",SmithingIngredient.ofItems(1,1,MITHRIL_INGOT)),
                                new SPatKey("g",SmithingIngredient.ofItems(1,1,Items.GOLD_INGOT)),
                        },
                        SPELLSTORAGE_SMALL,1,100,12,conditionsFromItem(MITHRIL_INGOT),null);

                // medium
                AddShapedSmitheryRecipe(new String[]{
                                " m ",
                                " o ",
                                " g "}
                        ,new SPatKey[]{
                                new SPatKey("m",SmithingIngredient.ofItems(1,1,MITHRIL_INGOT)),
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,OCTANGULITE_INGOT)),
                                new SPatKey("g",SmithingIngredient.ofItems(1,1,Items.GOLD_INGOT)),
                        },
                        SPELLSTORAGE_MEDIUM,1,200,15,conditionsFromItem(OCTANGULITE_INGOT),null);

                // large
                AddShapedSmitheryRecipe(new String[]{
                                " o ",
                                " o ",
                                " g "}
                        ,new SPatKey[]{
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,OCTANGULITE_INGOT)),
                                new SPatKey("g",SmithingIngredient.ofItems(1,1,Items.GOLD_INGOT)),
                        },
                        SPELLSTORAGE_LARGE,1,300,20,conditionsFromItem(OCTANGULITE_INGOT),null);

            }

            // spellcomponents
            {
                // base
                AddShapedSmitheryRecipe(new String[]{
                                " t ",
                                "tit",
                                " t "}
                        ,new SPatKey[]{
                                new SPatKey("i",SmithingIngredient.ofItems(1,1,Items.IRON_INGOT)),
                                new SPatKey("t",SmithingIngredient.ofItems(1,1,TITANIUM_NUGGET)),
                        },
                        SPELLCOMPONENT,1,20,10,ModAdvancementCriterion.conditionsFromAdvancement(Geomancy.locate("octangulite/get_spellcomponent")),null);

                // flow control
                {
                    Item baseIngot = MITHRIL_INGOT;
                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.LEVER),
                    }).toList(),SpellBlocks.FOR,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.OAK_DOOR),
                    }).toList(),SpellBlocks.GATE,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE_TORCH),
                    }).toList(),SpellBlocks.NOT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.HOPPER),
                    }).toList(),SpellBlocks.CONVEYOR,true);
                }

                // providers
                {
                    Item baseIngot = MOLYBDENUM_INGOT;
                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.LEVER),
                    }).toList(),SpellBlocks.CONST_BOOLEAN,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.COMPARATOR),
                    }).toList(),SpellBlocks.CONST_NUM,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.OAK_SIGN),
                    }).toList(),SpellBlocks.CONST_TEXT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.DIRT),
                    }).toList(),SpellBlocks.ENTITY_CASTER,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.CHEST),
                    }).toList(),SpellBlocks.CASTER_SLOT,true);
                }

                // arithmetic
                {
                    Item baseIngot = LEAD_INGOT;
                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE),
                    }).toList(),SpellBlocks.SUM,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.COMPARATOR),
                    }).toList(),SpellBlocks.SUBTRACT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.REPEATER),
                    }).toList(),SpellBlocks.MULTIPLY,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.WOODEN_AXE),
                    }).toList(),SpellBlocks.DIVIDE,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE_BLOCK),
                    }).toList(),SpellBlocks.EXP,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.OAK_LOG),
                    }).toList(),SpellBlocks.LOG,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.OAK_STAIRS),
                    }).toList(),SpellBlocks.SIN,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.COBBLESTONE_STAIRS),
                    }).toList(),SpellBlocks.COS,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.STONE_STAIRS),
                    }).toList(),SpellBlocks.TAN,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.CRAFTING_TABLE),
                    }).toList(),SpellBlocks.VECTOR_BUILD,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.STONE_PRESSURE_PLATE),
                    }).toList(),SpellBlocks.VECTOR_ENTITYPOS,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.SPIDER_EYE),
                    }).toList(),SpellBlocks.VECTOR_ENTITYEYEPOS,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.ARROW),
                    }).toList(),SpellBlocks.VECTOR_ENTITYDIR,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.WOODEN_AXE),
                    }).toList(),SpellBlocks.VECTOR_SPLIT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.RED_BED),
                    }).toList(),SpellBlocks.VECTOR_ENTITYSPAWN,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.ENDER_EYE),
                    }).toList(),SpellBlocks.RAYCAST_POS,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.SLIME_BALL),
                    }).toList(),SpellBlocks.RAYCAST_DIR,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.STONE),
                    }).toList(),SpellBlocks.BOOL_ENTITYGROUNDED,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.LEAD),
                    }).toList(),SpellBlocks.ENTITY_NEAREST,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE_TORCH),
                    }).toList(),SpellBlocks.INVERT,true);
                }

                // effectors
                {
                    Item baseIngot = OCTANGULITE_INGOT;
                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.TRIPWIRE_HOOK),
                    }).toList(),SpellBlocks.DEBUG,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.OAK_SIGN),
                    }).toList(),SpellBlocks.PRINT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.LIGHTNING_ROD),
                    }).toList(),SpellBlocks.LIGHTNING,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.FIRE_CHARGE),
                    }).toList(),SpellBlocks.FIREBALL,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.ENDER_PEARL),
                    }).toList(),SpellBlocks.TELEPORT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.CHORUS_FLOWER),
                    }).toList(),SpellBlocks.DIMHOP,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.FIREWORK_ROCKET),
                    }).toList(),SpellBlocks.PUSH,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.EXPERIENCE_BOTTLE),
                    }).toList(),SpellBlocks.IMBUE,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.CRAFTING_TABLE),
                    }).toList(),SpellBlocks.PLACE,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.IRON_PICKAXE),
                    }).toList(),SpellBlocks.BREAK,true);
                }

                // reference
                {
                    Item baseIngot = Items.COPPER_INGOT;
                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.STICKY_PISTON),
                    }).toList(),SpellBlocks.REF_OUTPUT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.PISTON),
                    }).toList(),SpellBlocks.REF_INPUT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE),
                    }).toList(),SpellBlocks.ACTION,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE_BLOCK),
                    }).toList(),SpellBlocks.FUNCTION,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE_BLOCK),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE_BLOCK),
                    }).toList(),SpellBlocks.FUNCTION2,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.DROPPER),
                    }).toList(),SpellBlocks.PROVIDER,true);
                }

                // lists
                {
                    Item baseIngot = Items.GOLD_INGOT;
                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.DROPPER),
                    }).toList(),SpellBlocks.FOREACH,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.WOODEN_AXE),
                    }).toList(),SpellBlocks.SPLIT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.POPPY),
                    }).toList(),SpellBlocks.POP,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.LIGHT_WEIGHTED_PRESSURE_PLATE),
                    }).toList(),SpellBlocks.SIZE,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.DISPENSER),
                    }).toList(),SpellBlocks.GET_ELEMENT,true);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.HOPPER),
                    }).toList(),SpellBlocks.SET_ELEMENT,true);
                }

                for(var block : SpellBlocks.functions.values()){
                    if(!spellComponentRecipesBuilt.contains(block))
                        Geomancy.logWarning("no recipe for spell component "+block.identifier.getPath());
                }
            }

            // spellmaker
            AddShapedSmitheryRecipe(new String[]{
                            "ooo",
                            "trt",
                            "ttt"}
                    ,new SPatKey[]{
                            new SPatKey("o",SmithingIngredient.ofItems(1,1,1,OCTANGULITE_INGOT)),
                            new SPatKey("r",SmithingIngredient.ofItems(1,1,1,Blocks.REDSTONE_BLOCK)),
                            new SPatKey("t",SmithingIngredient.ofItems(1,1,1,TITANIUM_INGOT)),
                    },
                    SPELLMAKER,1,100,50,conditionsFromItem(SPELLMAKER),Geomancy.locate("main/get_spellmaker"));

            // spellglove
            AddShapedSmitheryRecipe(new String[]{
                            "ooo",
                            "lcl",
                            " l "}
                    ,new SPatKey[]{
                            new SPatKey("o",SmithingIngredient.ofItems(1,1,1,OCTANGULITE_INGOT)),
                            new SPatKey("l",SmithingIngredient.ofItems(1,1,1,Items.LEATHER)),
                            new SPatKey("c",SmithingIngredient.ofItems(1,1,1,Items.CHEST)),
                    },
                    SPELLGLOVE,1,100,50,conditionsFromItem(OCTANGULITE_INGOT),Geomancy.locate("milestones/milestone_souls"));

        }

        // tools and armors
        {
            AddToolBatch(LEAD_INGOT,LEAD_SWORD,LEAD_SHOVEL,LEAD_PICKAXE,LEAD_AXE,LEAD_HOE);
            ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, PLUMBOMETER, 1).input('#', LEAD_INGOT)
                    .pattern("#")
                    .pattern("#").criterion("poisoned", ModAdvancementCriterion.conditionsFromAdvancement(Geomancy.locate("main/simple_lead_poisoned"))).offerTo(exporter);
            AddSurrounded(Items.APPLE,LEAD_INGOT,LEAD_APPLE,1,RecipeCategory.TOOLS);

            AddToolBatch(TITANIUM_INGOT,TITANIUM_SWORD,TITANIUM_SHOVEL,TITANIUM_PICKAXE,TITANIUM_AXE,TITANIUM_HOE);
            AddToolBatch(MITHRIL_INGOT,MITHRIL_SWORD,MITHRIL_SHOVEL,MITHRIL_PICKAXE,MITHRIL_AXE,MITHRIL_HOE);
            AddToolBatch(MOLYBDENUM_INGOT,MOLYBDENUM_SWORD,MOLYBDENUM_SHOVEL,MOLYBDENUM_PICKAXE,MOLYBDENUM_AXE,MOLYBDENUM_HOE);
            AddToolBatch(OCTANGULITE_INGOT,OCTANGULITE_SWORD,OCTANGULITE_SHOVEL,OCTANGULITE_PICKAXE,OCTANGULITE_AXE,OCTANGULITE_HOE);

            // armors
            AddArmors(LEAD_INGOT,LEAD_BOOTS,LEAD_LEGGINGS,LEAD_CHESTPLATE,LEAD_HELMET);
            AddArmors(TITANIUM_INGOT,TITANIUM_BOOTS,TITANIUM_LEGGINGS,TITANIUM_CHESTPLATE,TITANIUM_HELMET);
            AddArmors(MITHRIL_INGOT,MITHRIL_BOOTS,MITHRIL_LEGGINGS,MITHRIL_CHESTPLATE,MITHRIL_HELMET);
            AddArmors(MOLYBDENUM_INGOT,MOLYBDENUM_BOOTS,MOLYBDENUM_LEGGINGS,MOLYBDENUM_CHESTPLATE,MOLYBDENUM_HELMET);
            AddArmors(OCTANGULITE_INGOT,OCTANGULITE_BOOTS,OCTANGULITE_LEGGINGS,OCTANGULITE_CHESTPLATE,OCTANGULITE_HELMET);
        }

        // decorative blocks
        {
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOSSY_CUT_TITANIUM, 1).input(ModBlocks.CUT_TITANIUM).input(Items.VINE).group("mossify").criterion(hasItem(ModBlocks.CUT_TITANIUM), conditionsFromItem(ModBlocks.CUT_TITANIUM)).offerTo(exporter, convertBetween(ModBlocks.MOSSY_CUT_TITANIUM, ModBlocks.CUT_TITANIUM));

            AddDecorativeBlockBatch("lead");
            AddDecorativeBlockBatch("titanium");
            AddDecorativeBlockBatch("mithril");
            AddDecorativeBlockBatch("molybdenum");
            AddDecorativeBlockBatch("octangulite");

            // wood
            offerBarkBlockRecipe(exporter,SOUL_OAK_WOOD,SOUL_OAK_LOG);
            //offerHangingSignRecipe(exporter,SOUL_OAK_HANGING_SIGN,STRIPPED_SOUL_OAK_WOOD);
            offerPlanksRecipe(exporter,SOUL_OAK_PLANKS, ModItemTags.SOUL_OAK_LOGS,4);
            offerPressurePlateRecipe(exporter,SOUL_OAK_PRESSURE_PLATE,SOUL_OAK_PLANKS);
            offerSlabRecipe(exporter,RecipeCategory.BUILDING_BLOCKS,SOUL_OAK_SLAB,SOUL_OAK_PLANKS);
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, SOUL_OAK_DOOR, 3).input('#', SOUL_OAK_PLANKS)
                    .pattern("##")
                    .pattern("##")
                    .pattern("##").criterion(hasItem(SOUL_OAK_PLANKS), conditionsFromItem(SOUL_OAK_PLANKS)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, SOUL_OAK_FENCE, 3).input('#', SOUL_OAK_PLANKS).input('x', Items.STICK)
                    .pattern("#x#")
                    .pattern("#x#").criterion(hasItem(SOUL_OAK_PLANKS), conditionsFromItem(SOUL_OAK_PLANKS)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, SOUL_OAK_FENCE_GATE, 1).input('#', SOUL_OAK_PLANKS).input('x', Items.STICK)
                    .pattern("x#x")
                    .pattern("x#x").criterion(hasItem(SOUL_OAK_PLANKS), conditionsFromItem(SOUL_OAK_PLANKS)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, SOUL_OAK_TRAPDOOR, 2).input('#', SOUL_OAK_PLANKS)
                    .pattern("###")
                    .pattern("###").criterion(hasItem(SOUL_OAK_PLANKS), conditionsFromItem(SOUL_OAK_PLANKS)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, SOUL_OAK_STAIRS, 4).input('#', SOUL_OAK_PLANKS)
                    .pattern("#  ")
                    .pattern("## ")
                    .pattern("###").criterion(hasItem(SOUL_OAK_PLANKS), conditionsFromItem(SOUL_OAK_PLANKS)).offerTo(exporter);
            //ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, SOUL_OAK_SIGN, 3).input('#', SOUL_OAK_PLANKS).input('x', Items.STICK)
            //        .pattern("###")
            //        .pattern("###")
            //        .pattern(" x ").criterion(hasItem(SOUL_OAK_PLANKS), conditionsFromItem(SOUL_OAK_PLANKS)).offerTo(exporter);
            offerShapelessRecipe(exporter,SOUL_OAK_BUTTON,SOUL_OAK_PLANKS,"misc",1);
            offerShapelessRecipe(exporter,SOUL_OAK_PLANKS,SOUL_OAK_LOG,"misc",4);
            offerShapelessRecipe(exporter,SOUL_OAK_PLANKS,STRIPPED_SOUL_OAK_LOG,"misc",4);
            offerShapelessRecipe(exporter,SOUL_OAK_PLANKS,SOUL_OAK_WOOD,"misc",4);
            offerShapelessRecipe(exporter,SOUL_OAK_PLANKS,STRIPPED_SOUL_OAK_WOOD,"misc",4);
        }

        this.exporter=null;
    }

    private void AddDecorativeBlockBatch(String mat) {
        AddDecorativeBlockBatch(
                Registries.BLOCK.get(Geomancy.locate(mat+"_block")),
                Registries.BLOCK.get(Geomancy.locate("cut_"+mat)),
                Registries.BLOCK.get(Geomancy.locate(mat+"_bricks")),
                (StairsBlock) Registries.BLOCK.get(Geomancy.locate(mat+"_brick_stairs")),
                (SlabBlock) Registries.BLOCK.get(Geomancy.locate(mat+"_brick_slab")),
                (WallBlock) Registries.BLOCK.get(Geomancy.locate(mat+"_brick_wall"))
        );
    }

    private void AddDecorativeBlockBatch(Block base, Block cut, Block bricks, StairsBlock stairs, SlabBlock slab, WallBlock wall) {
        offerStonecuttingRecipe(exporter,RecipeCategory.BUILDING_BLOCKS,cut,base,8);
        offerStonecuttingRecipe(exporter,RecipeCategory.BUILDING_BLOCKS,bricks,base,8);
        offerStonecuttingRecipe(exporter,RecipeCategory.BUILDING_BLOCKS,stairs,bricks,1);
        offerStonecuttingRecipe(exporter,RecipeCategory.BUILDING_BLOCKS,slab,bricks,2);
        offerStonecuttingRecipe(exporter,RecipeCategory.BUILDING_BLOCKS,wall,bricks,1);
    }

    private void AddToolBatch(Item base, Item sword, Item shovel, Item pickaxe, Item axe, Item hoe) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, sword, 1)
                .input('#', base).input('s', Items.STICK)
                .pattern("#")
                .pattern("#")
                .pattern("s").criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, shovel, 1)
                .input('#', base).input('s', Items.STICK)
                .pattern("#")
                .pattern("s")
                .pattern("s").criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, pickaxe, 1)
                .input('#', base).input('s', Items.STICK)
                .pattern("###")
                .pattern(" s ")
                .pattern(" s ").criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, axe, 1)
                .input('#', base).input('s', Items.STICK)
                .pattern("##")
                .pattern("#s")
                .pattern(" s").criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, hoe, 1)
                .input('#', base).input('s', Items.STICK)
                .pattern("##")
                .pattern(" s")
                .pattern(" s").criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter);
    }

    private void AddArmors(Item base, Item boots, Item leggings, Item chestplate, Item helmet) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, boots, 1)
                .input('#', base)
                .pattern("# #")
                .pattern("# #").criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, leggings, 1)
                .input('#', base)
                .pattern("###")
                .pattern("# #")
                .pattern("# #").criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, chestplate, 1)
                .input('#', base)
                .pattern("# #")
                .pattern("###")
                .pattern("###").criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, helmet, 1)
                .input('#', base)
                .pattern("###")
                .pattern("# #").criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter);

    }

    private void AddSurrounded(Item base, Item surrounded, Item output, int outputCount, RecipeCategory cat) {
        ShapedRecipeJsonBuilder.create(cat, output, outputCount)
                .input('s', surrounded)
                .input('#', base)
                .pattern("sss")
                .pattern("s#s")
                .pattern("sss").criterion(hasItem(surrounded), conditionsFromItem(surrounded)).offerTo(exporter);
    }
    private void AddSurrounded4(Item base, Item surrounded, Item output, int outputCount, RecipeCategory cat) {
        ShapedRecipeJsonBuilder.create(cat, output, outputCount)
                .input('s', surrounded)
                .input('#', base)
                .pattern(" s ")
                .pattern("s#s")
                .pattern(" s ").criterion(hasItem(surrounded), conditionsFromItem(surrounded)).offerTo(exporter);
    }

    private void AddSmeltAndBlastRecipe(List<ItemConvertible> input, ItemConvertible output, float xp, int smeltTime, int blastTime){
        AddSmeltRecipe(input,output,xp,smeltTime);
        AddBlastingRecipe(input,output,xp,blastTime);
    }
    private void AddSmeltRecipe(List<ItemConvertible> input, ItemConvertible output, float xp, int cookTime){
        offerSmelting(exporter, input, RecipeCategory.MISC,output,xp,cookTime,null);
    }
    private void AddBlastingRecipe(List<ItemConvertible> input, ItemConvertible output, float xp, int cookTime){
        offerBlasting(exporter, input, RecipeCategory.MISC,output,xp,cookTime,null);
    }
    private void AddReversibleCompressionRecipe(ItemConvertible dense, ItemConvertible base){

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, dense).input(base, 9).criterion(hasItem(base), conditionsFromItem(base)).offerTo(exporter,getItemName(base)+"_compress_to_"+getItemName(dense));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, base,9).input(dense).criterion(hasItem(dense), conditionsFromItem(dense)).offerTo(exporter,getItemName(dense)+"_decompress_to_"+getItemName(base));
    }
    private void AddOreCompactingRecipes(ItemConvertible nugget, ItemConvertible ingot, ItemConvertible block, ItemConvertible raw, ItemConvertible rawBlock){
        AddReversibleCompressionRecipe(ingot,nugget);
        AddReversibleCompressionRecipe(block,ingot);
        AddReversibleCompressionRecipe(rawBlock,raw);
    }

    private void AddSmitheryRecipe(List<SmithingIngredient> input, ItemConvertible output, int outputCount, int requiredProgress, int difficulty, boolean shapeless, CriterionConditions conditions, Identifier requiredAdvancement){
        AddSmitheryRecipe(input,output,outputCount,requiredProgress,difficulty, shapeless,"default_conditions",conditions,requiredAdvancement);
    }

    private void AddShapedSmitheryRecipe(String[] map, SPatKey[] entries,
                                         ItemConvertible output, int outputCount, int requiredProgress, int difficulty,
                                         CriterionConditions conditions, Identifier requiredAdvancement)
    {
        List<SmithingIngredient> ingredients = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            String partial = map[i];
            for (int j = 0; j < partial.length(); j++) {
                String c = partial.substring(j,j+1);
                for(SPatKey entry : entries){
                    if(Objects.equals(entry.key, c)){
                        ingredients.add(entry.toIngredient(i*partial.length()+j));
                        break;
                    }
                }
            }
        }
        AddSmitheryRecipe(ingredients,output,outputCount,requiredProgress,difficulty,false,conditions,requiredAdvancement);
    }

    private static class SPatKey {
        public String key;
        public SmithingIngredient ingredient;

        public SPatKey(String key, SmithingIngredient ingredient){
            this.key=key;
            this.ingredient=ingredient;
        }

        public SmithingIngredient toIngredient(int slot){
            SmithingIngredient res = new SmithingIngredient(ingredient.ingredient,ingredient.count,slot,ingredient.mishapWeight);
            return res;
        }
    }

    private static class GenJewelryRecData {
        public int difficulty;
        public int progress;
        public ItemConvertible ingredient;
        public String resultPrefix;

        public GenJewelryRecData(String resultPrefix, ItemConvertible ingredient, int progress, int difficulty ){
            this.resultPrefix=resultPrefix;
            this.ingredient=ingredient;
            this.progress=progress;
            this.difficulty=difficulty;
        }
    }

    private void AddSmitheryRecipe(List<SmithingIngredient> input, ItemConvertible output, int outputCount, int requiredProgress, int difficulty, boolean shapeless, String criterionName, CriterionConditions conditions, Identifier requiredAdvancement){
        DefaultedList<SmithingIngredient> ingredients = DefaultedList.of();
        ingredients.addAll(input);
        SmitheryRecipeJsonBuilder.create(ingredients,output.asItem(),outputCount, requiredProgress,difficulty,shapeless,RecipeCategory.MISC, requiredAdvancement).criterion(criterionName,conditions).offerTo(exporter,new Identifier(Geomancy.MOD_ID,"smithing_"+getItemName(output)));

    }

    private void AddSmitheryJewelryRecipe(IJewelryItem base){

        int progressRequiredBase = 10;
        int difficulty = 15;
        float gemProgressCostMultiplier = 1;
        float gemDifficultyMultiplier = 1;
        int baseMishapWeight = base.getMishapWeight();

        JewelryRecipeJsonBuilder.create(SmithingIngredient.ofItems(1,baseMishapWeight,4,(Item)base), progressRequiredBase,
                gemProgressCostMultiplier,difficulty,gemDifficultyMultiplier,RecipeCategory.MISC)
                .criterion("has_ingredient",conditionsFromItem((Item)base)).offerTo(exporter,new Identifier(Geomancy.MOD_ID,"jewelry_"+getItemName((Item)base)));

    }

    private void AddGeodeRecipe(GeodeItem geodeItem){
        int progressRequiredBase = geodeItem.getProgressRequired();
        int difficulty = geodeItem.getBaseDifficulty();
        float difficultyPerMighty = geodeItem.getDifficultyPerMighty();

        GeodeRecipeJsonBuilder.create(SmithingIngredient.ofItems(1,1,geodeItem), progressRequiredBase,
                        difficulty,difficultyPerMighty,RecipeCategory.MISC)
                .criterion("has_ingredient",conditionsFromItem(geodeItem)).offerTo(exporter,new Identifier(Geomancy.MOD_ID,"geode_"+getItemName(geodeItem)));

    }

    private void AddRingSmitheryRecipe(ItemConvertible ingredient, ItemConvertible result, int requiredProgress, int difficulty) {
        AddDefaultedSmitheryRecipe(new String[]{
                " o ",
                "o o",
                " o "},ingredient,result,requiredProgress,difficulty,null);
    }
    private void AddNecklaceSmitheryRecipe(ItemConvertible ingredient, ItemConvertible result, int requiredProgress, int difficulty) {
        AddDefaultedSmitheryRecipe(new String[]{
                "o o",
                "o o",
                " o "},ingredient,result,requiredProgress,difficulty,null);
    }
    private void AddPendantSmitheryRecipe(ItemConvertible ingredient, ItemConvertible result, int requiredProgress, int difficulty) {
        AddDefaultedSmitheryRecipe(new String[]{
                "o o",
                "ooo",
                "   "},ingredient,result,requiredProgress,difficulty,null);
    }
    private void AddDefaultedSmitheryRecipe(String[] pattern, ItemConvertible ingredient, ItemConvertible result, int requiredProgress, int difficulty, Identifier requiredAdvancement) {
        AddShapedSmitheryRecipe(pattern
                ,new SPatKey[]{new SPatKey("o",SmithingIngredient.ofItems(1,1,1,ingredient))},
                result,1,requiredProgress,difficulty,conditionsFromItem(ingredient), requiredAdvancement);
    }

    private final ArrayList<SpellBlock> spellComponentRecipesBuilt = new ArrayList<>();
    private void AddSpellcomponentRecipe(List<SmithingIngredient> input, SpellBlock outputComponent, boolean shapeless){

        var conditions = conditionsFromItem(SPELLCOMPONENT);

        DefaultedList<SmithingIngredient> ingredients = DefaultedList.of();
        ingredients.addAll(input);
        ItemStack output = new ItemStack(SPELLCOMPONENT);
        var nbt = SpellComponentStoringItem.getNbtFor(outputComponent);
        output.setSubNbt("component",nbt);
        SmitheryRecipeJsonBuilder.create(
                ingredients,
                output,
                1,
                outputComponent.recipeRequiredProgress,
                outputComponent.recipeDifficulty,
                shapeless,
                RecipeCategory.MISC,
                ModAdvancementProvider.getComponentID(outputComponent.identifier.getPath()))
                .criterion("gotten_base",conditions)
                .offerTo(exporter,new Identifier(Geomancy.MOD_ID,"spellcomponent_"+outputComponent.identifier.getPath()));

        spellComponentRecipesBuilt.add(outputComponent);

    }

    static String getItemName(ItemConvertible item) {
        return Registries.ITEM.getId(item.asItem()).getPath();
    }

    @Override
    public String getName() {
        return Geomancy.MOD_ID + " Recipe Provider";
    }
}