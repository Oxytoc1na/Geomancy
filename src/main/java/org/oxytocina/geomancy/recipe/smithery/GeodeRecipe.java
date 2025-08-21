package org.oxytocina.geomancy.recipe.smithery;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.enchantments.ModEnchantments;
import org.oxytocina.geomancy.items.GeodeItem;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.recipe.GatedModRecipe;
import org.oxytocina.geomancy.registries.ModRecipeTypes;

import java.util.ArrayList;
import java.util.List;

public class GeodeRecipe extends GatedModRecipe<Inventory> implements SmitheryRecipeI{

    protected final SmithingIngredient base;
    protected final int progressRequiredBase;
    protected final int difficulty;
    protected final float difficultyPerMighty;

    public GeodeRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier, @NotNull SmithingIngredient base, int progressRequiredBase, int difficulty, float difficultyPerMighty) {
        super(id,group,secret,requiredAdvancementIdentifier);
        this.base = base;
        this.progressRequiredBase = progressRequiredBase;
        this.difficulty=difficulty;
        this.difficultyPerMighty =difficultyPerMighty;
    }

    @Override
    public boolean matches(@NotNull Inventory inv, World world) {

        return !getOutput(inv,false,false,ItemStack.EMPTY,null).isEmpty();
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        List<ItemStack> res = newCraft(inventory);
        return res.isEmpty()?ItemStack.EMPTY:res.get(0);
    }

    public List<ItemStack> newCraft(Inventory inv) {
        return getOutput(inv, true, false,ItemStack.EMPTY,null);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return null;
    }

    public List<ItemStack> getOutput(Inventory inv, boolean removeIngredients, boolean preview, ItemStack hammer, LivingEntity hammerer) {
        List<ItemStack> res = new ArrayList<>();

        ItemStack baseStack = getRecipeBase(inv);
        if(baseStack.isEmpty()) return res;


        // add loottable drops
        if(!preview){
            if(baseStack.getItem() instanceof GeodeItem geodeItem){
                LootTable lootTable = geodeItem.getLootTable();

                if(hammerer != null && hammerer.getWorld() instanceof ServerWorld serverWorld){
                    var loot = lootTable.generateLoot(new LootContextParameterSet.Builder(serverWorld).build(LootContextType.create().build()));
                    res.addAll(loot);
                }
                else res.add(ModItems.GEODE_PREVIEW.getDefaultStack());
            }
        }
        else{
            res.add(ModItems.GEODE_PREVIEW.getDefaultStack());
        }

        if(removeIngredients) baseStack.decrement(1);

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
    public int getDifficulty(Inventory inv, ItemStack hammer, LivingEntity hammerer) {
        int res = difficulty;

        res+= (int)(difficultyPerMighty*ModEnchantments.getLevel(hammer,ModEnchantments.MIGHTY));

        return res;
    }

    @Override
    public int getProgressRequired(Inventory inv) {
        return progressRequiredBase;
    }

    @Override
    public ItemStack getPreviewOutput(Inventory inv) {
        List<ItemStack> outputs = getOutput(inv,false,true,ItemStack.EMPTY,null);
        return outputs.isEmpty()?ItemStack.EMPTY:outputs.get(0);
    }

    @Override
    public boolean hasBaseStack() {
        return false;
    }

    @Override
    public List<SmithingIngredient> getSmithingIngredients(Inventory inv) {
        List<SmithingIngredient> res = new ArrayList<>();
        ItemStack stack = getRecipeBase(inv);
        res.add(SmithingIngredient.ofItems(stack.getCount(),base.mishapWeight,stack.getItem()));
        return res;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.STONE_GEODE);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.GEODE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.GEODE;
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
        return ModRecipeTypes.GEODE_ID;
    }

    @Override
    public List<ItemStack> getSmithingResult(Inventory inv, boolean removeItems, boolean preview, ItemStack hammer, LivingEntity hammerer) {
        return getOutput(inv,removeItems,preview, hammer, hammerer);
    }
}