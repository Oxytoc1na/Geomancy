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

public class SmitheryRecipe extends GatedModRecipe<Inventory> {

    protected final DefaultedList<Ingredient> inputs;
    protected final ItemStack output;

    public SmitheryRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, @NotNull DefaultedList<Ingredient> inputs, ItemStack output) {
        super(id, group, secret, requiredAdvancementIdentifier);
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public boolean matches(@NotNull Inventory inv, World world) {


        for(Ingredient ing : inputs)
        {
            boolean ingredientAvailable = false;
            for (int i = 0; i < inv.size(); i++) {
                var slot = inv.getStack(i);
                if(!slot.isEmpty() && ing.test(slot)/*&& ing.count <= slot.getCount()*/){
                    ingredientAvailable = true;
                    break;
                }
            }
            if(!ingredientAvailable) return false;
        }

        return true;
    }

    @Override
    public ItemStack craft(Inventory inv, DynamicRegistryManager drm) {

        // remove items from inventory
        for(Ingredient ing : inputs)
        {
            int countLeft = 1;
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
        return inputs;
    }

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