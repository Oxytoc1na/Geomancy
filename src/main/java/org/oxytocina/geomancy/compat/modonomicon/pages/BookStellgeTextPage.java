package org.oxytocina.geomancy.compat.modonomicon.pages;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookSpotlightPage;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.oxytocina.geomancy.compat.modonomicon.ModonomiconCompat;
import org.oxytocina.geomancy.helpers.NbtHelper;

import java.util.ArrayList;
import java.util.List;

public class BookStellgeTextPage extends BookTextPage {

    public final float knowledgeRequired;

    public BookStellgeTextPage(BookTextHolder title, BookTextHolder text, boolean useMarkdownInTitle, boolean showTitleSeparator, String anchor, BookCondition condition, float knowledgeRequired) {
        super(title, text, useMarkdownInTitle, showTitleSeparator, anchor, condition);
        this.knowledgeRequired=knowledgeRequired;
    }

    public static BookStellgeTextPage fromJson(JsonObject json) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY);
        var useMarkdownInTitle = JsonHelper.getBoolean(json, "use_markdown_title", false);
        var showTitleSeparator = JsonHelper.getBoolean(json, "show_title_separator", true);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        float knowledgeRequired = JsonHelper.getFloat(json,"knowledgeRequired");
        var anchor = JsonHelper.getString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(json.getAsJsonObject("condition"))
                : new BookNoneCondition();
        return new BookStellgeTextPage(title, text, useMarkdownInTitle, showTitleSeparator, anchor, condition,knowledgeRequired);
    }

    public static BookStellgeTextPage fromNetwork(PacketByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var useMarkdownInTitle = buffer.readBoolean();
        var showTitleSeparator = buffer.readBoolean();
        var text = BookTextHolder.fromNetwork(buffer);
        var anchor = buffer.readString();
        var condition = BookCondition.fromNetwork(buffer);
        float knowledgeRequired = buffer.readFloat();
        return new BookStellgeTextPage(title, text, useMarkdownInTitle, showTitleSeparator, anchor, condition,knowledgeRequired);
    }

    @Override
    public Identifier getType() {
        return ModonomiconCompat.STELLGE_TEXT;
    }

    @Override
    public BookTextHolder getTitle() {
        return super.getTitle();
    }

    @Override
    public BookTextHolder getText() {
        return super.getText();
    }
}