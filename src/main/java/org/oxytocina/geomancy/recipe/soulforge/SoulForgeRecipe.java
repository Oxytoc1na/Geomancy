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

import java.util.ArrayList;
import java.util.List;

public class SoulForgeRecipe extends GatedModRecipe<Inventory> implements ISoulForgeRecipe {

    protected final List<NbtIngredient> inputs;
    protected final ItemStack output;
    protected final float cost;
    protected final float instability;
    protected final float speed;

    public SoulForgeRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, List<NbtIngredient> inputs, ItemStack output, float cost, float instability, float speed) {
        super(id,group,secret,requiredAdvancementIdentifier);
        this.inputs = inputs;
        this.output=output;
        this.cost = cost;
        this.instability=instability;
        this.speed=speed;
    }

    @Override
    public boolean matches(@NotNull Inventory inv, World world) {
        return inputsPresent(inv);
    }

    /// the removal of items is done very differently for this kind of recipe.
    /// see {@link org.oxytocina.geomancy.blocks.blockEntities.SoulForgeBlockEntity} for details
    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    public boolean inputsPresent(Inventory inv){
        List<Integer> usedUp = new ArrayList<>();
        for (NbtIngredient input : inputs) {
            boolean present = false;
            for (int j = 0; j < inv.size(); j++) {
                if(usedUp.contains(j)) continue;
                if (input.test(inv.getStack(j))) {
                    present = true;
                    usedUp.add(j);
                    break;
                }
            }
            if (!present)
                return false;
        }

        return true;
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
        for(var ing : inputs)
            res.add(ing.ingredient);
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
    public float getSpeed(){return speed;}

    @Override
    public List<ItemStack> getResult(Inventory inv, boolean removeItems, boolean preview, LivingEntity owner) {
        return List.of(output);
    }

    @Override
    public float getSoulCost(Inventory inv) {
        return getCost();
    }

    @Override
    public float getInstability(Inventory inv) {
        return getInstability();
    }

    @Override
    public float getSpeed(Inventory inv) {
        return getSpeed();
    }

    @Override
    public ItemStack getPreviewOutput(Inventory inv) {
        return output;
    }

    @Override
    public boolean hasBaseStack() {
        return false;
    }

    @Override
    public List<NbtIngredient> getNbtIngredients(Inventory inv) {
        return inputs;
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }
}