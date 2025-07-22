package org.oxytocina.geomancy.compat.modonomicon;

import com.klikli_dev.modonomicon.api.multiblock.*;
import com.klikli_dev.modonomicon.client.gui.book.*;
import com.klikli_dev.modonomicon.client.render.*;
import net.minecraft.client.gui.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;
import org.oxytocina.geomancy.recipe.smithery.SmithingIngredient;

import java.util.*;

public class ModonomiconHelper {
	
	public static void renderSmithingIngredient(DrawContext drawContext, BookContentScreen parentScreen, int x, int y, int mouseX, int mouseY, SmithingIngredient ingredientStack) {
		var stacks = ingredientStack.ingredient.getMatchingStacks();
		for (var stack : stacks)
		{
			stack.setCount(ingredientStack.count);
		}
		if (stacks.length>0) {
			parentScreen.renderItemStack(drawContext, x, y, mouseX, mouseY, stacks[parentScreen.ticksInBook / 20 % stacks.length]);
		}
	}
	
	public static void renderMultiblock(Multiblock multiblock, Text text, BlockPos pos, BlockRotation rotation) {
		MultiblockPreviewRenderer.setMultiblock(multiblock, text, false);
		MultiblockPreviewRenderer.anchorTo(pos, rotation);
	}
	
	/**
	 * Clears multiblock if the currently rendered one matches the one in the argument
	 * If null is passed, any multiblock will get cleared
	 */
	public static void clearRenderedMultiblock(@Nullable Multiblock multiblock) {
		Multiblock currentlyRenderedMultiblock = MultiblockPreviewRenderer.getMultiblock();
		if (currentlyRenderedMultiblock == null || currentlyRenderedMultiblock != multiblock) {
			return;
		}
		MultiblockPreviewRenderer.setMultiblock(null, Text.empty(), false);
	}
	
}