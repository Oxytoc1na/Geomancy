package org.oxytocina.geomancy.recipe.soulforge;

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
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.recipe.GatedModRecipe;
import org.oxytocina.geomancy.recipe.NbtIngredient;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.spells.SpellBlocks;

import java.util.List;

public class SoulForgeRecipe extends GatedModRecipe<Inventory> implements ISoulForgeRecipe {

    protected final SmithingIngredient base;
    protected final ItemStack output;
    protected final float cost;
    protected final float instability;

    public SoulForgeRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, @NotNull SmithingIngredient base, ItemStack output, float cost, float instability) {
        super(id,group,secret,requiredAdvancementIdentifier);
        this.base = base;
        this.output=output;
        this.cost = cost;
        this.instability=instability;
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
        return ModBlocks.SOUL_FORGE.asItem().getDefaultStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SOULFORGE_SIMPLE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.SOULFORGE_SIMPLE;
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
        return ModRecipeTypes.SOULFORGE_SIMPLE_ID;
    }

    public float getCost(){return cost;}
    public float getInstability(){return instability;}

    @Override
    public List<ItemStack> getResult(Inventory inv, boolean removeItems, boolean preview, LivingEntity owner) {
        return List.of();
    }

    @Override
    public int getProgressRequired(Inventory inv) {
        return 0;
    }

    @Override
    public ItemStack getPreviewOutput(Inventory inv) {
        return null;
    }

    @Override
    public boolean hasBaseStack() {
        return false;
    }

    @Override
    public List<NbtIngredient> getNbtIngredients(Inventory inv) {
        return List.of();
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }
}