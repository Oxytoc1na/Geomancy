package org.oxytocina.geomancy.compat.modonomicon.pages;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookAndCondition;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.oxytocina.geomancy.recipe.GatedRecipe;
import org.oxytocina.geomancy.util.StellgeUtil;

import java.util.ArrayList;
import java.util.List;

public class BookGatedRecipeStellgePage<T extends GatedRecipe<?>> extends BookGatedRecipePage<T> {

	public final float requiredKnowledge;
	public final float knowledgeBonus;
	public final float recipeFraction;

	public BookGatedRecipeStellgePage(RecipeType<T> recipeType, Identifier pageType, BookTextHolder title1, Identifier recipeId1, BookTextHolder title2,
									  Identifier recipeId2, BookTextHolder text, String anchor, BookCondition condition, float requiredKnowledge, float knowledgeBonus, float recipeFraction) {
		super(recipeType,pageType, title1, recipeId1, title2, recipeId2, text, anchor, condition);
		this.requiredKnowledge = requiredKnowledge;
		this.knowledgeBonus = knowledgeBonus;
		this.recipeFraction=recipeFraction;
	}
	
	public static BookCondition getConditionWithRecipes(BookCondition condition, Identifier recipeId1, Identifier recipeId2) {
		List<Identifier> list = new ArrayList<>();
		if (recipeId1 != null) {
			list.add(recipeId1);
		}
		if (recipeId2 != null) {
			list.add(recipeId2);
		}
		BookCondition[] conditions = {condition/*, new RecipesLoadedAndUnlockedCondition(null, list)*/};
		return new BookAndCondition(null, conditions);
	}
	
	public static <T extends GatedRecipe<?>> BookGatedRecipeStellgePage<T> fromJson(Identifier pageType, RecipeType<T> recipeType, JsonObject json, boolean supportsTwoRecipesOnOnePage) {
		var anchor = JsonHelper.getString(json, "anchor", "");
		var condition = json.has("condition")
				? BookCondition.fromJson(json.getAsJsonObject("condition"))
				: new BookNoneCondition();
		var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
		var skipRecipeUnlockCheck = JsonHelper.getBoolean(json, "skip_recipe_unlock_check", false);

		float requiredKnowledge = JsonHelper.getFloat(json,"requiredKnowledge");
		float knowledgeBonus = JsonHelper.getFloat(json,"knowledgeBonus");
		float recipeFraction = JsonHelper.getFloat(json,"recipeFraction");

		if (supportsTwoRecipesOnOnePage) {
			var title1 = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY);
			var title2 = BookGsonHelper.getAsBookTextHolder(json, "title2", BookTextHolder.EMPTY);
			Identifier recipeId1 = json.has("recipe_id_1") ? Identifier.tryParse(JsonHelper.getString(json, "recipe_id_1")) : null;
			Identifier recipeId2 = json.has("recipe_id_2") ? Identifier.tryParse(JsonHelper.getString(json, "recipe_id_2")) : null;
			condition = skipRecipeUnlockCheck ? condition : getConditionWithRecipes(condition, recipeId1, recipeId2);
			return new BookGatedRecipeStellgePage<>(recipeType, pageType, title1, recipeId1, title2, recipeId2, text, anchor, condition,requiredKnowledge,knowledgeBonus,recipeFraction);
		} else {
			var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY);
			Identifier recipeId = json.has("recipe_id_1") ? Identifier.tryParse(JsonHelper.getString(json, "recipe_id_1")) : null;
			condition = skipRecipeUnlockCheck ? condition : getConditionWithRecipes(condition, recipeId, null);
			return new BookGatedRecipeStellgePage<>(recipeType, pageType, title, recipeId, BookTextHolder.EMPTY, null, text, anchor, condition,requiredKnowledge,knowledgeBonus,recipeFraction);
		}
	}
	
	public static <T extends GatedRecipe<?>> BookGatedRecipeStellgePage<T> fromNetwork(Identifier pageType, RecipeType<T> recipeType, PacketByteBuf buffer) {
		var common = BookRecipePage.commonFromNetwork(buffer);
		var anchor = buffer.readString();
		var condition = BookCondition.fromNetwork(buffer);
		float requiredKnowledge = buffer.readFloat();
		float knowledgeBonus = buffer.readFloat();
		float recipeFraction = buffer.readFloat();
		return new BookGatedRecipeStellgePage<>(recipeType, pageType, common.title1(), common.recipeId1(), common.title2(), common.recipeId2(), common.text(), anchor, condition,requiredKnowledge,knowledgeBonus,recipeFraction);
	}

	@Override
	public void toNetwork(PacketByteBuf buffer) {
		super.toNetwork(buffer);
		buffer.writeFloat(requiredKnowledge);
		buffer.writeFloat(knowledgeBonus);
		buffer.writeFloat(recipeFraction);
	}
	
	@Override
	protected ItemStack getRecipeOutput(World world, T recipe) {
		if (recipe == null) {
			return ItemStack.EMPTY;
		}
		return recipe.getOutput(world.getRegistryManager());
	}

	@Override
	public BookTextHolder getTitle1() {
		return new BookTextHolder(StellgeUtil.stellgify(Text.literal(super.getTitle1().getString()),requiredKnowledge,knowledgeBonus));
	}

	@Override
	public BookTextHolder getText() {
		return new BookTextHolder(StellgeUtil.stellgify(Text.literal(super.getText().getString()),requiredKnowledge,knowledgeBonus));
	}
	
}