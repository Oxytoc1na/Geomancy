package org.oxytocina.geomancy.recipe.smithery;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.recipe.GatedModRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.oxytocina.geomancy.items.jewelry.GemSlot;

public class JewelryRecipe extends GatedModRecipe<Inventory> implements SmitheryRecipeI{

    protected final SmithingIngredient base;
    protected final int progressRequiredBase;
    protected final int difficulty;
    protected final float gemProgressCostMultiplier;
    protected final float gemDifficultyMultiplier;

    public JewelryRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, @NotNull SmithingIngredient base, int progressRequiredBase, float gemProgressCostMultiplier, int difficulty, float gemDifficultyMultiplier) {
        super(id,group,secret,requiredAdvancementIdentifier);
        this.base = base;
        this.progressRequiredBase = progressRequiredBase;
        this.difficulty=difficulty;
        this.gemProgressCostMultiplier=gemProgressCostMultiplier;
        this.gemDifficultyMultiplier=gemDifficultyMultiplier;
    }

    @Override
    public boolean matches(@NotNull Inventory inv, World world) {

        return !getOutput(inv,false,false).isEmpty();
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        List<ItemStack> res = newCraft(inventory);
        return res.isEmpty()?ItemStack.EMPTY:res.get(0);
    }

    public List<ItemStack> newCraft(Inventory inv) {
        return getOutput(inv, true, false);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return null;
    }

    public List<ItemStack> getOutput(Inventory inv, boolean removeIngredients, boolean preview) {

        // check if at least one new gem is there
        // check for a free gem slot
        // if none, set output to empty slots
        List<ItemStack> res = new ArrayList<>();

        ItemStack baseStack = getRecipeBase(inv);
        if(baseStack.isEmpty()) return res;
        if(!(baseStack.getItem() instanceof JewelryItem jewelryItem)) return res;
        var presentGems = JewelryItem.getSlots(baseStack);

        // fetch free slots
        int freeSlots = jewelryItem.gemSlotCount-presentGems.size();

        // fetch ingredient gems
        HashMap<Integer,ItemStack> gemSlotsToAdd = getAddedGems(inv,freeSlots);

        if(freeSlots<=0 || (gemSlotsToAdd.isEmpty() && !presentGems.isEmpty())){
            // unsmith
            if(presentGems.isEmpty()) return res;
            res.addAll(jewelryItem.UnSmith(baseStack,preview));
            if(removeIngredients){
                baseStack.decrement(1);
            }
            return res;
        }
        else{
            // add gems

            // no gems in crafting grid
            if(gemSlotsToAdd.isEmpty()) return res;

            // add gems to output
            ItemStack output = baseStack.copy();
            for(Integer i : gemSlotsToAdd.keySet()){
                GemSlot newSlot = new GemSlot(gemSlotsToAdd.get(i).getItem(),1);
                jewelryItem.addSlot(output,newSlot);

                if(removeIngredients){
                    gemSlotsToAdd.get(i).decrement(1);
                }
            }

            if(removeIngredients)
            {
                baseStack.decrement(1);
            }

            res.add(output);
            return res;
        }

        //return res;
    }

    public HashMap<Integer,ItemStack> getAddedGems(Inventory inv, int freeSlots){
        HashMap<Integer,ItemStack> res = new HashMap<>();

        // fetch ingredient gems
        HashMap<Integer,ItemStack> slotsWithGems = new HashMap<>();
        for (int i = 0; i < inv.size(); i++) {
            var slot = inv.getStack(i);
            if(!slot.isEmpty() && GemSlot.itemIsGem(slot)){
                slotsWithGems.put(i,slot);
            }
        }

        // no new gems
        if(slotsWithGems.isEmpty()) return res;

        // fetch which gems to add
        for(Integer i : slotsWithGems.keySet())
        {
            if(res.size() >= freeSlots) break;
            res.put(i,slotsWithGems.get(i));
        }

        return res;
    }

    public ItemStack getRecipeBase(Inventory inv){
        // check for base
        for (int baseSlot = 0; baseSlot < inv.size(); baseSlot++) {
            if(base.test(inv.getStack(baseSlot))){
                return inv.getStack(baseSlot);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> res = DefaultedList.of();
        res.add(base.ingredient);
        return res;
    }

    @Override
    public int getDifficulty(Inventory inv,ItemStack hammer, LivingEntity hammerer) {
        int res = difficulty;

        // fetch already slotted gems
        ItemStack baseStack = getRecipeBase(inv);
        if(baseStack.isEmpty() || !(baseStack.getItem() instanceof JewelryItem jewelryItem)) return difficulty;
        var presentGems = JewelryItem.getSlots(baseStack);

        // fetch free slots
        int freeSlots = jewelryItem.gemSlotCount-presentGems.size();

        HashMap<Integer,ItemStack> gemsToAdd = getAddedGems(inv,freeSlots);

        for(ItemStack s : gemsToAdd.values()){
            res += Math.round(GemSlot.getGemDifficulty(s)*gemDifficultyMultiplier);
        }

        return res;
    }

    @Override
    public int getProgressRequired(Inventory inv) {
        int res = progressRequiredBase;

        // fetch already slotted gems
        ItemStack baseStack = getRecipeBase(inv);

        if(!(baseStack.getItem() instanceof JewelryItem jewelryItem)) return 1000000;

        var presentGems = JewelryItem.getSlots(baseStack);

        // fetch free slots
        int freeSlots = jewelryItem.gemSlotCount-presentGems.size();

        HashMap<Integer,ItemStack> gemsToAdd = getAddedGems(inv,freeSlots);

        for(ItemStack s : gemsToAdd.values()){
            res += Math.round(GemSlot.getGemProgressCost(s)*gemProgressCostMultiplier);
        }

        return res;
    }

    @Override
    public ItemStack getPreviewOutput(Inventory inv) {
        List<ItemStack> outputs = getOutput(inv,false,true);
        return outputs.isEmpty()?ItemStack.EMPTY:outputs.get(0);
    }

    @Override
    public boolean hasBaseStack() {
        return false;
    }

    @Override
    public List<SmithingIngredient> getSmithingIngredients(Inventory inv) {
        List<SmithingIngredient> res = new ArrayList<>();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if(stack.isEmpty()) continue;

            if(base.test(stack)){
                SmithingIngredient ing = SmithingIngredient.ofItems(stack.getCount(),base.mishapWeight,stack.getItem());
                res.add(ing);
            } else if (GemSlot.itemIsGem(stack)){
                SmithingIngredient ing = SmithingIngredient.ofItems(stack.getCount(),1,stack.getItem());
                res.add(ing);
            }
        }
        return res;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.IRON_RING);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.JEWELRY_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.JEWELRY;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    @Override
    public Identifier getRecipeTypeUnlockIdentifier() {
        Geomancy.logError("getRecipeTypeUnlockIdentifier returning null");
        return null; //UNLOCK_IDENTIFIER;
    }

    @Override
    public String getRecipeTypeShortID() {
        return ModRecipeTypes.JEWELRY_ID;
    }

    @Override
    public List<ItemStack> getSmithingResult(Inventory inv, boolean removeItems, boolean preview, ItemStack hammer, LivingEntity hammerer) {
        return getOutput(inv,removeItems,preview);
    }
}