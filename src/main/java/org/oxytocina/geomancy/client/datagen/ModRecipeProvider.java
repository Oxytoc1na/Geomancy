package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.client.datagen.recipes.GeodeRecipeJsonBuilder;
import org.oxytocina.geomancy.client.datagen.recipes.JewelryRecipeJsonBuilder;
import org.oxytocina.geomancy.client.datagen.recipes.SmitheryRecipeJsonBuilder;
import org.oxytocina.geomancy.items.GeodeItem;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;

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
        offerShapelessRecipe(exporter, ModItems.SUSPICIOUS_SUBSTANCE,ModItems.SUSPICIOUS_SUBSTANCE,null,1);

        // shaped recipes
        ShapedRecipeJsonBuilder.create(
                        RecipeCategory.TOOLS, ModItems.IRON_HAMMER, 1)
                .input('#', Items.IRON_INGOT)
                .input('s', Items.STICK)
                .pattern("###")
                .pattern("#s#")
                .pattern(" s ")
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(
                        RecipeCategory.TOOLS, ModBlocks.SMITHERY, 1)
                .input('i', Blocks.IRON_BLOCK)
                .input('g', Items.GOLD_INGOT)
                .pattern("gg")
                .pattern("ii")
                .criterion("has_ingredient", conditionsFromItemPredicates(ItemPredicate.Builder.create().items(new ItemConvertible[]{
                        Items.IRON_INGOT,Blocks.IRON_BLOCK,Items.GOLD_INGOT}).build()))
                .offerTo(exporter);

        // smelting recipes
        AddSmeltAndBlastRecipe(List.of(
            ModItems.RAW_MITHRIL, ModBlocks.MITHRIL_ORE, ModBlocks.DEEPSLATE_MITHRIL_ORE),
            ModItems.MITHRIL_INGOT,5f,400,100);

        // compacting recipes
        AddOreCompactingRecipes(ModItems.MITHRIL_NUGGET,ModItems.MITHRIL_INGOT,ModBlocks.MITHRIL_BLOCK,ModItems.RAW_MITHRIL,ModBlocks.RAW_MITHRIL_BLOCK);
        AddOreCompactingRecipes(ModItems.OCTANGULITE_NUGGET,ModItems.OCTANGULITE_INGOT,ModBlocks.OCTANGULITE_BLOCK,ModItems.RAW_OCTANGULITE,ModBlocks.RAW_OCTANGULITE_BLOCK);
        AddOreCompactingRecipes(ModItems.MOLYBDENUM_NUGGET,ModItems.MOLYBDENUM_INGOT,ModBlocks.MOLYBDENUM_BLOCK,ModItems.RAW_MOLYBDENUM,ModBlocks.RAW_MOLYBDENUM_BLOCK);
        AddOreCompactingRecipes(ModItems.TITANIUM_NUGGET,ModItems.TITANIUM_INGOT,ModBlocks.TITANIUM_BLOCK,ModItems.RAW_TITANIUM,ModBlocks.RAW_TITANIUM_BLOCK);
        AddOreCompactingRecipes(ModItems.LEAD_NUGGET,ModItems.LEAD_INGOT,ModBlocks.LEAD_BLOCK,ModItems.RAW_LEAD,ModBlocks.RAW_LEAD_BLOCK);


        // smithing recipes
        {


            // generic jewelry recipes
            GenJewelryRecData[] mats = new GenJewelryRecData[]{
                    new GenJewelryRecData("iron",Items.IRON_INGOT,30,15),
                    new GenJewelryRecData("gold",Items.GOLD_INGOT,40,30),
                    new GenJewelryRecData("mithril",ModItems.MITHRIL_INGOT,50,30),
                    new GenJewelryRecData("copper",Items.COPPER_INGOT,30,20),
                    new GenJewelryRecData("molybdenum",ModItems.MOLYBDENUM_INGOT,40,20),
                    new GenJewelryRecData("titanium",ModItems.TITANIUM_INGOT,70,40),
                    new GenJewelryRecData("lead",ModItems.LEAD_INGOT,20,17),
                    new GenJewelryRecData("octangulite",ModItems.OCTANGULITE_INGOT,80,100),
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
            for(JewelryItem item : JewelryItem.List){
                AddSmitheryJewelryRecipe(item);
            }



            // mithril hammer
            AddShapedSmitheryRecipe(new String[]{
                            "ooo",
                            "oso",
                            " s "}
                    ,new SPatKey[]{
                            new SPatKey("o",SmithingIngredient.ofItems(1,1,1,ModItems.MITHRIL_INGOT)),
                            new SPatKey("s",SmithingIngredient.ofItems(1,1,1,Items.STICK)),
                    },
                    ModItems.MITHRIL_HAMMER,1,100,12,conditionsFromItem(ModItems.MITHRIL_INGOT));


            // geode recipes
            for(GeodeItem item : ModItems.geodeItems){
                AddGeodeRecipe(item);
            }

            // Empty Artifact
            AddSmitheryRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(ModItems.MITHRIL_INGOT),
                            SmithingIngredient.ofItems(Items.LEATHER)
                    }).toList(),ModItems.EMPTY_ARTIFACT,1, 40,5, true,
                    conditionsFromItem(ModItems.MITHRIL_INGOT));

            // Artifact of Iron
            AddSmitheryRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,4,ModItems.EMPTY_ARTIFACT),
                            SmithingIngredient.ofItems(1,1,1,Items.IRON_BLOCK),
                            SmithingIngredient.ofItems(1,1,3,Items.SHIELD),
                            SmithingIngredient.ofItems(1,1,5,Items.IRON_CHESTPLATE),
                            SmithingIngredient.ofItems(1,1,7,Items.IRON_BARS),
                    }).toList(),ModItems.ARTIFACT_OF_IRON,1, 100,20,false,
                    conditionsFromItem(ModItems.EMPTY_ARTIFACT));

            // Artifact of Gold
            AddSmitheryRecipe(Arrays.stream(new SmithingIngredient[] {
                            SmithingIngredient.ofItems(1,1,4,ModItems.EMPTY_ARTIFACT),
                            SmithingIngredient.ofItems(1,1,1,Items.GOLD_BLOCK),
                            SmithingIngredient.ofItems(1,1,3,Items.GOLDEN_APPLE),
                            SmithingIngredient.ofItems(1,1,5,Items.GOLDEN_HELMET),
                            SmithingIngredient.ofItems(1,1,7,Items.GOLDEN_CARROT),
                    }).toList(),ModItems.ARTIFACT_OF_GOLD,1, 100,20,false,
                    conditionsFromItem(ModItems.EMPTY_ARTIFACT));
        }

        // decorative blocks
        offerStonecuttingRecipe(exporter,RecipeCategory.BUILDING_BLOCKS,ModBlocks.CUT_TITANIUM,ModBlocks.TITANIUM_BLOCK,8);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MOSSY_CUT_TITANIUM, 1).input(ModBlocks.CUT_TITANIUM).input(Items.VINE).group("mossify").criterion(hasItem(ModBlocks.CUT_TITANIUM), conditionsFromItem(ModBlocks.CUT_TITANIUM)).offerTo(exporter, convertBetween(ModBlocks.MOSSY_CUT_TITANIUM, ModBlocks.CUT_TITANIUM));


        this.exporter=null;
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

    private void AddSmitheryRecipe(List<SmithingIngredient> input, ItemConvertible output, int outputCount, int requiredProgress, int difficulty, boolean shapeless, CriterionConditions conditions){
        AddSmitheryRecipe(input,output,outputCount,requiredProgress,difficulty, shapeless,"default_conditions",conditions);
    }

    private void AddShapedSmitheryRecipe(String[] map, SPatKey[] entries,
                                         ItemConvertible output, int outputCount, int requiredProgress, int difficulty,
                                         CriterionConditions conditions)
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
        AddSmitheryRecipe(ingredients,output,outputCount,requiredProgress,difficulty,false,conditions);
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

    private void AddSmitheryRecipe(List<SmithingIngredient> input, ItemConvertible output, int outputCount, int requiredProgress, int difficulty, boolean shapeless, String criterionName, CriterionConditions conditions){
        DefaultedList<SmithingIngredient> ingredients = DefaultedList.of();
        ingredients.addAll(input);
        SmitheryRecipeJsonBuilder.create(ingredients,output.asItem(),outputCount, requiredProgress,difficulty,shapeless,RecipeCategory.MISC).criterion(criterionName,conditions).offerTo(exporter,new Identifier(Geomancy.MOD_ID,"smithing_"+getItemName(output)));

    }

    private void AddSmitheryJewelryRecipe(JewelryItem base){

        int progressRequiredBase = 10;
        int difficulty = 15;
        float gemProgressCostMultiplier = 1;
        float gemDifficultyMultiplier = 1;
        int baseMishapWeight = base.getMishapWeight();

        JewelryRecipeJsonBuilder.create(SmithingIngredient.ofItems(1,baseMishapWeight,4,base), progressRequiredBase,
                gemProgressCostMultiplier,difficulty,gemDifficultyMultiplier,RecipeCategory.MISC)
                .criterion("has_ingredient",conditionsFromItem(base)).offerTo(exporter,new Identifier(Geomancy.MOD_ID,"jewelry_"+getItemName(base)));

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
                " o "},ingredient,result,requiredProgress,difficulty);
    }
    private void AddNecklaceSmitheryRecipe(ItemConvertible ingredient, ItemConvertible result, int requiredProgress, int difficulty) {
        AddDefaultedSmitheryRecipe(new String[]{
                "o o",
                "o o",
                " o "},ingredient,result,requiredProgress,difficulty);
    }
    private void AddPendantSmitheryRecipe(ItemConvertible ingredient, ItemConvertible result, int requiredProgress, int difficulty) {
        AddDefaultedSmitheryRecipe(new String[]{
                "o o",
                "ooo",
                "   "},ingredient,result,requiredProgress,difficulty);
    }
    private void AddDefaultedSmitheryRecipe(String[] pattern, ItemConvertible ingredient, ItemConvertible result, int requiredProgress, int difficulty) {
        AddShapedSmitheryRecipe(pattern
                ,new SPatKey[]{new SPatKey("o",SmithingIngredient.ofItems(1,1,1,ingredient))},
                result,1,requiredProgress,difficulty,conditionsFromItem(ingredient));
    }

    static String getItemName(ItemConvertible item) {
        return Registries.ITEM.getId(item.asItem()).getPath();
    }

    @Override
    public String getName() {
        return Geomancy.MOD_ID + " Recipe Provider";
    }
}