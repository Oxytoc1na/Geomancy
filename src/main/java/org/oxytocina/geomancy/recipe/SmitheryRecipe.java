package org.oxytocina.geomancy.recipe;

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
import org.oxytocina.geomancy.fluids.ModFluids;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SmitheryRecipe extends GatedModRecipe<Inventory> {

    protected final DefaultedList<SmithingIngredient> inputs;
    protected final ItemStack output;
    protected final int progressRequired;
    protected final int difficulty;
    protected final boolean shapeless;

    public SmitheryRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, @NotNull DefaultedList<SmithingIngredient> inputs, ItemStack output, int progressRequired,int difficulty,boolean shapeless) {
        super(id, group, secret, requiredAdvancementIdentifier);
        this.inputs = inputs;
        this.output = output;
        this.progressRequired = progressRequired;
        this.difficulty=difficulty;
        this.shapeless=shapeless;
    }

    @Override
    public boolean matches(@NotNull Inventory inv, World world) {

        if(shapeless){
            // shapeless logic
            for(CountIngredient ing : inputs)
            {
                int countLeft = ing.count;
                boolean ingredientAvailable = false;
                for (int i = 0; i < inv.size(); i++) {
                    var slot = inv.getStack(i);
                    if(!slot.isEmpty() && ing.test(slot)/*&& ing.count <= slot.getCount()*/){
                        int removed = Math.min(countLeft,slot.getCount());
                        countLeft-=removed;
                        if(countLeft<=0){
                            ingredientAvailable = true;
                            break;
                        }

                    }
                }
                if(!ingredientAvailable) return false;
            }
        }
        else{
            // shaped logic

            ArrayList<Integer> shapelessExcludedSlots = new ArrayList<>();

            // partial shaped logic
            for(CountIngredient ing : inputs)
            {
                if(!ing.hasSlot()) continue;

                shapelessExcludedSlots.add(ing.slot);

                boolean ingredientAvailable = false;
                var slot = inv.getStack(ing.slot);
                if(!slot.isEmpty() && ing.test(slot)){
                    if(slot.getCount() >= ing.count){
                        ingredientAvailable = true;
                    }
                }
                if(!ingredientAvailable) return false;
            }

            // partial shapeless logic
            for(CountIngredient ing : inputs)
            {
                if(ing.hasSlot()) continue;

                int countLeft = ing.count;
                boolean ingredientAvailable = false;
                for (int i = 0; i < inv.size(); i++) {
                    if(shapelessExcludedSlots.contains(i)) continue;
                    var slot = inv.getStack(i);
                    if(!slot.isEmpty() && ing.test(slot)/*&& ing.count <= slot.getCount()*/){
                        int removed = Math.min(countLeft,slot.getCount());
                        countLeft-=removed;
                        if(countLeft<=0){
                            ingredientAvailable = true;
                            break;
                        }

                    }
                }
                if(!ingredientAvailable) return false;
            }
        }



        return true;
    }

    @Override
    public ItemStack craft(Inventory inv, DynamicRegistryManager drm) {

        // remove items from inventory
        for(CountIngredient ing : inputs)
        {
            int countLeft = ing.count;
            for (int i = 0; i < inv.size(); i++) {
                var slot = inv.getStack(i);
                if(!slot.isEmpty() && ing.test(slot)  /*&& ing.count <= slot.getCount()*/){
                    int removed = Math.min(slot.getCount(),countLeft);
                    slot.decrement( removed );
                    countLeft-=removed;
                    if(countLeft<=0)
                        break;
                }
            }

            if(countLeft!=0)
            {
                Geomancy.logError("recipe "+getId().toString()+" got crafted but couldnt remove the necessary ingredients");
            }

        }

        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> res = DefaultedList.of();
        res.addAll(inputs.stream()
                .map((a)->a.ingredient)
                .toList());
        return res;
    }

    public DefaultedList<SmithingIngredient> getSmithingIngredients(){
        return inputs;
    }

    public int getProgressRequired() {return progressRequired;}
    public int getDifficulty() {return difficulty;}
    public boolean getShapeless() {return shapeless;}

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.IRON_HAMMER);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SMITHING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.SMITHING;
    }

    @Override
    public Identifier getRecipeTypeUnlockIdentifier() {
        Geomancy.logError("getRecipeTypeUnlockIdentifier returning null");
        return null; //UNLOCK_IDENTIFIER;
    }

    @Override
    public String getRecipeTypeShortID() {
        return ModRecipeTypes.SMITHING_ID;
    }
}