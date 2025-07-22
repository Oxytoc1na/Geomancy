package org.oxytocina.geomancy.compat.emi;


import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.render.*;
import dev.emi.emi.api.stack.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;

public class ModEMIRecipeCategories {
    public static final EmiRecipeCategory SMITHING = new ModCategory(Geomancy.locate("smithing"), EmiStack.of(ModBlocks.SMITHERY));

    private static class ModCategory extends EmiRecipeCategory {
        private final String key;

        public ModCategory(Identifier id, EmiRenderable icon) {
            this(id, icon, "container." + id.getNamespace() + ".rei." + id.getPath() + ".title");
        }

        public ModCategory(Identifier id, EmiRenderable icon, String key) {
            super(id, icon, icon, EmiRecipeSorting.compareOutputThenInput());
            this.key = key;
        }

        @Override
        public Text getName() {
            return Text.translatable(key);
        }
    }
}