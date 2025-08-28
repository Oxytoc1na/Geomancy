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
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.spells.SpellBlocks;

import java.util.ArrayList;
import java.util.List;

public class TransmuteRecipe extends GatedModRecipe<Inventory>{

    protected final SmithingIngredient base;
    protected final ItemStack output;
    protected final float cost;

    public TransmuteRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, @NotNull SmithingIngredient base, ItemStack output, float cost) {
        super(id,group,secret,requiredAdvancementIdentifier);
        this.base = base;
        this.output=output;
        this.cost = cost;
    }

    @Override
    public boolean matches(@NotNull Inventory inv, World world) {
        return craft(inv,null).isEmpty();
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        for (int i = 0; i < inventory.size(); i++) {
            if(base.test(inventory.getStack(i))) return output;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return null;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> res = DefaultedList.of();
        res.add(base.ingredient);
        return res;
    }

    @Override
    public ItemStack createIcon() {
        return SpellBlocks.TRANSMUTE_ITEM.getItemStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.TRANSMUTE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.TRANSMUTE;
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
        return ModRecipeTypes.TRANSMUTE_ID;
    }

    public float getCost(){return cost;}
}