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
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.*;
import org.oxytocina.geomancy.client.datagen.recipes.GeodeRecipeJsonBuilder;
import org.oxytocina.geomancy.client.datagen.recipes.JewelryRecipeJsonBuilder;
import org.oxytocina.geomancy.client.datagen.recipes.SmitheryRecipeJsonBuilder;
import org.oxytocina.geomancy.client.datagen.recipes.TransmuteRecipeJsonBuilder;
import org.oxytocina.geomancy.items.GeodeItem;
import static org.oxytocina.geomancy.items.ModItems.*;
import static org.oxytocina.geomancy.blocks.ModBlocks.*;
import static org.oxytocina.geomancy.items.ModItems.SOULSTORAGE_LARGE;

import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.items.tools.SoulBoreItem;
import org.oxytocina.geomancy.progression.advancement.ModAdvancementCriterion;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;
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

            // spell, soul and variable storage
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

                // small
                AddShapedSmitheryRecipe(new String[]{
                                " t ",
                                " m ",
                                " g "}
                        ,new SPatKey[]{
                                new SPatKey("t",SmithingIngredient.ofItems(1,1,TITANIUM_INGOT)),
                                new SPatKey("m",SmithingIngredient.ofItems(1,1,MITHRIL_INGOT)),
                                new SPatKey("g",SmithingIngredient.ofItems(1,1,Items.DIAMOND)),
                        },
                        VARSTORAGE_SMALL,1,100,12,conditionsFromItem(MITHRIL_INGOT),null);

                // medium
                AddShapedSmitheryRecipe(new String[]{
                                " m ",
                                " o ",
                                " g "}
                        ,new SPatKey[]{
                                new SPatKey("m",SmithingIngredient.ofItems(1,1,MITHRIL_INGOT)),
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,OCTANGULITE_INGOT)),
                                new SPatKey("g",SmithingIngredient.ofItems(1,1,Items.DIAMOND)),
                        },
                        VARSTORAGE_MEDIUM,1,200,15,conditionsFromItem(OCTANGULITE_INGOT),null);

                // large
                AddShapedSmitheryRecipe(new String[]{
                                " o ",
                                " o ",
                                " g "}
                        ,new SPatKey[]{
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,OCTANGULITE_INGOT)),
                                new SPatKey("g",SmithingIngredient.ofItems(1,1,Items.DIAMOND)),
                        },
                        VARSTORAGE_LARGE,1,300,20,conditionsFromItem(OCTANGULITE_INGOT),null);

                // small
                AddShapedSmitheryRecipe(new String[]{
                                "ini",
                                "non",
                                "ini"}
                        ,new SPatKey[]{
                                new SPatKey("t",SmithingIngredient.ofItems(1,1,TITANIUM_INGOT)),
                                new SPatKey("i",SmithingIngredient.ofItems(1,1,Items.IRON_INGOT)),
                                new SPatKey("n",SmithingIngredient.ofItems(1,1,LEAD_INGOT)),
                        },
                        SOULSTORAGE_SMALL,1,100,12,conditionsFromItem(MITHRIL_INGOT),null);

                // medium
                AddShapedSmitheryRecipe(new String[]{
                                "tnt",
                                "non",
                                "tnt"}
                        ,new SPatKey[]{
                                new SPatKey("n",SmithingIngredient.ofItems(1,1,OCTANGULITE_NUGGET)),
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,OCTANGULITE_INGOT)),
                                new SPatKey("t",SmithingIngredient.ofItems(1,1,TITANIUM_INGOT)),
                        },
                        SOULSTORAGE_MEDIUM,1,200,15,conditionsFromItem(OCTANGULITE_INGOT),null);

                // large
                AddShapedSmitheryRecipe(new String[]{
                                "mom",
                                "obo",
                                "mom"}
                        ,new SPatKey[]{
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,OCTANGULITE_INGOT)),
                                new SPatKey("b",SmithingIngredient.ofItems(1,1,OCTANGULITE_BLOCK)),
                                new SPatKey("m",SmithingIngredient.ofItems(1,1,MITHRIL_INGOT)),
                        },
                        SOULSTORAGE_LARGE,1,300,20,conditionsFromItem(OCTANGULITE_INGOT),null);
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
                    AddGenericSpellcomponentRecipe(SpellBlocks.FOR          ,Items.LEVER            ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.GATE         ,Items.OAK_DOOR         ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.NOT          ,Items.REDSTONE_TORCH   ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.CONVEYOR     ,Items.HOPPER           ,baseIngot);
                }

                // providers
                {
                    Item baseIngot = MOLYBDENUM_INGOT;
                    AddGenericSpellcomponentRecipe(SpellBlocks.CONST_BOOLEAN        ,Items.LEVER                ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.CONST_NUM            ,Items.COMPARATOR           ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.CONST_TEXT           ,Items.OAK_SIGN             ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.ENTITY_CASTER        ,Items.DIRT                 ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.ENTITY_DELEGATE      ,Items.SNOW_BLOCK           ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.CASTER_SLOT          ,Items.CHEST                ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.BLOCKPOS_CASTER      ,Items.STONE                ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.POS_CASTER           ,Items.OAK_PRESSURE_PLATE   ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.EYEPOS_CASTER        ,Items.SPIDER_EYE           ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.DIR_CASTER           ,Items.ARROW                ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.GET_WEATHER          ,Items.LIGHTNING_ROD        ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.GET_TIME             ,Items.CLOCK                ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.ACTIVATE             ,Items.OAK_BUTTON           ,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.SILENT               ,Items.WHITE_WOOL           ,baseIngot);
                }

                // arithmetic
                {
                    Item baseIngot = LEAD_INGOT;
                    AddGenericSpellcomponentRecipe(SpellBlocks.SUM,Items.REDSTONE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.SUBTRACT,Items.COMPARATOR,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.MULTIPLY,Items.REPEATER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.DIVIDE,Items.WOODEN_AXE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.EXP,Items.REDSTONE_BLOCK,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.LOG,Items.OAK_LOG,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.MOD,Items.WOODEN_SWORD,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.SIN,Items.OAK_STAIRS,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.COS,Items.COBBLESTONE_STAIRS,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.TAN,Items.STONE_STAIRS,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VECTOR_BUILD,Items.CRAFTING_TABLE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VECTOR_ENTITYPOS,Items.STONE_PRESSURE_PLATE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VECTOR_ENTITYVEL,Items.SUGAR,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VECTOR_ENTITYEYEPOS,Items.SPIDER_EYE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VECTOR_ENTITYDIR,Items.ARROW,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VECTOR_SPLIT,Items.STONE_AXE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VECTOR_ENTITYSPAWN,Items.RED_BED,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.RAYCAST_POS,Items.ENDER_EYE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.RAYCAST_DIR,Items.SLIME_BALL,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.RAYCAST_ENTITY,Items.SNOWBALL,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.BOOL_ENTITYGROUNDED,Items.STONE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.ENTITY_NEAREST,Items.LEAD,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.INVERT,Items.REDSTONE_TORCH,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.AND,Items.LIGHT_WEIGHTED_PRESSURE_PLATE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.OR,Items.OAK_PRESSURE_PLATE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.XOR,Items.HEAVY_WEIGHTED_PRESSURE_PLATE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.EQUALS,Items.EMERALD,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.TEXT_ENTITY_ID,Items.NAME_TAG,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.TEXT_BLOCK_ID,Items.OAK_SIGN,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.ENTITY_HAS_EFFECT,Items.BLAZE_POWDER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.ENTITY_HEALTH,Items.GHAST_TEAR,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.RANDOM_INTEGER,Items.DROPPER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.PARSE,Items.FEATHER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.TO_TEXT,Items.BIRCH_SIGN,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.GREATER,Items.OAK_LEAVES,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.LESS,Items.OAK_SAPLING,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.TRANSLATE,Items.SPRUCE_SIGN,baseIngot);

                }

                // effectors
                {
                    Item baseIngot = OCTANGULITE_INGOT;
                    AddGenericSpellcomponentRecipe(SpellBlocks.DEBUG,Items.TRIPWIRE_HOOK,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.PRINT,Items.OAK_SIGN,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.LIGHTNING,Items.LIGHTNING_ROD,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.FIREBALL,Items.FIRE_CHARGE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.TELEPORT,Items.ENDER_PEARL,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.DIMHOP,Items.CHORUS_FLOWER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.PUSH,Items.FIREWORK_ROCKET,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.IMBUE,Items.BREWING_STAND,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.PLACE,Items.CRAFTING_TABLE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.BREAK,Items.IRON_PICKAXE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.SET_SPELL,OCTANGULITE_NUGGET,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.DEGRADE_BLOCK,Items.COBBLESTONE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.REPLACE,Items.CRAFTING_TABLE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.IGNITE,Items.FLINT_AND_STEEL,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.PLAY_SOUND,Items.NOTE_BLOCK,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.DELEGATE,Items.ARMOR_STAND,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.SET_TIME,Items.CLOCK,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.SET_WEATHER,Items.CAULDRON,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.GROW,Items.BONE_BLOCK,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.TRANSMUTE_ITEM,Items.EXPERIENCE_BOTTLE,baseIngot);
                }

                // reference
                {
                    Item baseIngot = Items.COPPER_INGOT;
                    AddGenericSpellcomponentRecipe(SpellBlocks.REF_OUTPUT,Items.STICKY_PISTON,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.REF_INPUT,Items.PISTON,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.ACTION,Items.REDSTONE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.FUNCTION,Items.REDSTONE_BLOCK,baseIngot);

                    AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                            SmithingIngredient.ofItems(1,1,baseIngot),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE_BLOCK),
                            SmithingIngredient.ofItems(1,1,Items.REDSTONE_BLOCK),
                    }).toList(),SpellBlocks.FUNCTION2,true);

                    AddGenericSpellcomponentRecipe(SpellBlocks.PROVIDER,Items.DROPPER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VAR_OUTPUT,Items.BOOK,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VAR_INPUT,Items.FEATHER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VAR_DELETE,Items.TNT,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.VAR_EXISTS,Items.BARREL,baseIngot);
                }

                // lists
                {
                    Item baseIngot = Items.GOLD_INGOT;
                    AddGenericSpellcomponentRecipe(SpellBlocks.FOREACH,Items.DROPPER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.SPLIT,Items.WOODEN_AXE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.POP,Items.POPPY,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.SIZE,Items.LIGHT_WEIGHTED_PRESSURE_PLATE,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.GET_ELEMENT,Items.DISPENSER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.SET_ELEMENT,Items.HOPPER,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.ENTITIES_NEAR,Items.LEAD,baseIngot);
                    AddGenericSpellcomponentRecipe(SpellBlocks.BLOCK_BOX,Items.WOODEN_PICKAXE,baseIngot);
                }

                String builtSpellComps = "";
                for(var block : SpellBlocks.functions.values()){
                    if(!spellComponentRecipesBuilt.contains(block))
                        Geomancy.logWarning("no recipe for spell component "+block.identifier.getPath());

                    builtSpellComps += "\""+block.identifier.getPath()+"\",\n";
                }
                Geomancy.logInfo("Spellblocks:\n"+builtSpellComps);

                // casting armor
                AddShapedSmitheryRecipe(new String[]{
                                " r ",
                                "obo",
                                " o "}
                        ,new SPatKey[]{
                                new SPatKey("b",SmithingIngredient.ofItems(1,1,1,OCTANGULITE_HELMET)),
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,1, OCTANGULITE_BLOCK.asItem())),
                                new SPatKey("r",SmithingIngredient.ofItems(1,1,1,Blocks.REDSTONE_BLOCK)),
                        },
                        CASTER_HELMET,1,100,50,conditionsFromItem(OCTANGULITE_INGOT),Geomancy.locate("milestones/milestone_souls"));
                AddShapedSmitheryRecipe(new String[]{
                                " r ",
                                "obo",
                                " o "}
                        ,new SPatKey[]{
                                new SPatKey("b",SmithingIngredient.ofItems(1,1,1,OCTANGULITE_CHESTPLATE)),
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,1, OCTANGULITE_BLOCK.asItem())),
                                new SPatKey("r",SmithingIngredient.ofItems(1,1,1,Blocks.REDSTONE_BLOCK)),
                        },
                        CASTER_CHESTPLATE,1,100,50,conditionsFromItem(OCTANGULITE_INGOT),Geomancy.locate("milestones/milestone_souls"));
                AddShapedSmitheryRecipe(new String[]{
                                " r ",
                                "obo",
                                " o "}
                        ,new SPatKey[]{
                                new SPatKey("b",SmithingIngredient.ofItems(1,1,1,OCTANGULITE_LEGGINGS)),
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,1, OCTANGULITE_BLOCK.asItem())),
                                new SPatKey("r",SmithingIngredient.ofItems(1,1,1,Blocks.REDSTONE_BLOCK)),
                        },
                        CASTER_LEGGINGS,1,100,50,conditionsFromItem(OCTANGULITE_INGOT),Geomancy.locate("milestones/milestone_souls"));
                AddShapedSmitheryRecipe(new String[]{
                                " r ",
                                "obo",
                                " o "}
                        ,new SPatKey[]{
                                new SPatKey("b",SmithingIngredient.ofItems(1,1,1,OCTANGULITE_BOOTS)),
                                new SPatKey("o",SmithingIngredient.ofItems(1,1,1, OCTANGULITE_BLOCK.asItem())),
                                new SPatKey("r",SmithingIngredient.ofItems(1,1,1,Blocks.REDSTONE_BLOCK)),
                        },
                        CASTER_BOOTS,1,100,50,conditionsFromItem(OCTANGULITE_INGOT),Geomancy.locate("milestones/milestone_souls"));

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

            // soul bore
            AddShapedSmitheryRecipe(new String[]{
                            "bab",
                            "ini",
                            " t "}
                    ,new SPatKey[]{
                            new SPatKey("t",SmithingIngredient.ofItems(1,1,TITANIUM_SWORD)),
                            new SPatKey("n",SmithingIngredient.ofItems(1,1,Items.TNT)),
                            new SPatKey("a",SmithingIngredient.ofItems(1,1,Items.ANVIL)),
                            new SPatKey("b",SmithingIngredient.ofItems(1,1,TITANIUM_BLOCK)),
                            new SPatKey("i",SmithingIngredient.ofItems(1,1,OCTANGULITE_INGOT)),
                    },
                    SOUL_BORE,1,200,15,conditionsFromItem(OCTANGULITE_INGOT),null);

            // caster core
            AddShapedSmitheryRecipe(new String[]{
                            " t ",
                            "tbt",
                            " t "}
                    ,new SPatKey[]{
                            new SPatKey("t",SmithingIngredient.ofItems(1,1,TITANIUM_INGOT)),
                            new SPatKey("b",SmithingIngredient.ofItems(1,1,OCTANGULITE_BLOCK)),
                    },
                    CASTER_CORE,1,200,15,conditionsFromItem(OCTANGULITE_INGOT),null);
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

        // transmuting
        {
            AddTransmutationRecipe(OCTANGULITE_NUGGET,OCTANGULITE_INGOT, SoulBoreItem.INGOT_FUEL_VALUE,SPELLCOMPONENT,null);
            AddTransmutationRecipe(OCTANGULITE_INGOT,OCTANGULITE_BLOCK, SoulBoreItem.INGOT_FUEL_VALUE*9,SPELLCOMPONENT,null);
            AddTransmutationRecipe(Items.OAK_SAPLING,SOUL_OAK_SAPLING, 200,SOUL_OAK_SAPLING,null);
            AddTransmutationRecipe(Items.AMETHYST_SHARD,Items.ECHO_SHARD, 100,Items.ECHO_SHARD,null);
            AddTransmutationRecipe(Items.GOLDEN_APPLE,Items.ENCHANTED_GOLDEN_APPLE, 100000,Items.GOLDEN_APPLE,null);
            AddTransmutationRecipe(Items.GLASS_BOTTLE,Items.EXPERIENCE_BOTTLE, 1000,Items.GLASS_BOTTLE,null);
            AddTransmutationRecipe(Items.ENDER_PEARL,Items.ENDER_EYE, 1000,Items.ENDER_PEARL,null);
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
    private void AddTransmutationRecipe(ItemConvertible input, ItemConvertible output, float cost, ItemConvertible unlock, Identifier requiredAdvancement){
        AddTransmutationRecipe(SmithingIngredient.ofItems(input),output,1,cost,"unlock",conditionsFromItem(unlock),requiredAdvancement);
    }

    private void AddTransmutationRecipe(SmithingIngredient input, ItemConvertible output, int outputCount, float cost, String criterionName, CriterionConditions conditions, Identifier requiredAdvancement){
        TransmuteRecipeJsonBuilder.create(input,output.asItem(),outputCount, cost,RecipeCategory.MISC, requiredAdvancement).criterion(criterionName,conditions).offerTo(exporter,new Identifier(Geomancy.MOD_ID,"transmute_"+getItemName(output)));
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
    private void AddGenericSpellcomponentRecipe(SpellBlock comp, ItemConvertible ingredient,ItemConvertible baseIngot){
        AddSpellcomponentRecipe(Arrays.stream(new SmithingIngredient[] {
                SmithingIngredient.ofItems(1,1,SPELLCOMPONENT),
                SmithingIngredient.ofItems(1,1,baseIngot),
                SmithingIngredient.ofItems(1,1,ingredient),
        }).toList(),comp,true);
    }
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