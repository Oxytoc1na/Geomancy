package org.oxytocina.geomancy.recipe;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

public abstract class GatedModRecipe<C extends Inventory> implements GatedRecipe<C> {

    public final Identifier id;
    public final String group;
    public final boolean secret;
    public final Identifier requiredAdvancementIdentifier;

    protected GatedModRecipe(Identifier id, String group, boolean secret, Identifier requiredAdvancementIdentifier) {
        this.id = id;
        this.group = group;
        this.secret = secret;
        this.requiredAdvancementIdentifier = requiredAdvancementIdentifier;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public boolean isSecret() {
        return this.secret;
    }

    /**
     * The advancement the player has to have for the recipe to be craftable
     *
     * @return The advancement identifier. A null value means the player is always able to craft this recipe
     */
    @Nullable
    @Override
    public Identifier getRequiredAdvancementIdentifier() {
        return this.requiredAdvancementIdentifier;
    }

    @Override
    public abstract Identifier getRecipeTypeUnlockIdentifier();

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof GatedModRecipe gatedRecipe) {
            return gatedRecipe.getId().equals(this.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }

}