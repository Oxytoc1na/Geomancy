package org.oxytocina.geomancy.compat.modonomicon.pages;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookTextPage;
import com.klikli_dev.modonomicon.util.BookGsonHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.oxytocina.geomancy.compat.modonomicon.ModonomiconCompat;
import org.oxytocina.geomancy.util.StellgeUtil;

public class BookStellgeTextPage extends BookTextPage {

    public final float requiredKnowledge;
    public final float knowledgeBonus;

    public BookStellgeTextPage(BookTextHolder title, BookTextHolder text, boolean useMarkdownInTitle, boolean showTitleSeparator, String anchor, BookCondition condition, float requiredKnowledge, float knowledgeBonus) {
        super(title, text, useMarkdownInTitle, showTitleSeparator, anchor, condition);
        this.requiredKnowledge = requiredKnowledge;
        this.knowledgeBonus = knowledgeBonus;
    }


    public static BookStellgeTextPage fromJson(JsonObject json) {
        var title = BookGsonHelper.getAsBookTextHolder(json, "title", BookTextHolder.EMPTY);
        var useMarkdownInTitle = JsonHelper.getBoolean(json, "use_markdown_title", false);
        var showTitleSeparator = JsonHelper.getBoolean(json, "show_title_separator", true);
        var text = BookGsonHelper.getAsBookTextHolder(json, "text", BookTextHolder.EMPTY);
        float requiredKnowledge = JsonHelper.getFloat(json,"requiredKnowledge");
        float knowledgeBonus = JsonHelper.getFloat(json,"knowledgeBonus");
        var anchor = JsonHelper.getString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(json.getAsJsonObject("condition"))
                : new BookNoneCondition();
        return new BookStellgeTextPage(title, text, useMarkdownInTitle, showTitleSeparator, anchor, condition, requiredKnowledge, knowledgeBonus);
    }

    public static BookStellgeTextPage fromNetwork(PacketByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var useMarkdownInTitle = buffer.readBoolean();
        var showTitleSeparator = buffer.readBoolean();
        var text = BookTextHolder.fromNetwork(buffer);
        var anchor = buffer.readString();
        var condition = BookCondition.fromNetwork(buffer);
        float requiredKnowledge = buffer.readFloat();
        float knowledgeBonus = buffer.readFloat();
        return new BookStellgeTextPage(title, text, useMarkdownInTitle, showTitleSeparator, anchor, condition,requiredKnowledge,knowledgeBonus);
    }

    @Override
    public void toNetwork(PacketByteBuf buffer) {
        super.toNetwork(buffer);
        buffer.writeFloat(requiredKnowledge);
        buffer.writeFloat(knowledgeBonus);
    }

    @Override
    public Identifier getType() {
        return ModonomiconCompat.STELLGE_TEXT;
    }

    @Override
    public BookTextHolder getTitle() {
        return new BookTextHolder(StellgeUtil.stellgify(Text.literal(super.getTitle().getString()),requiredKnowledge,knowledgeBonus));
    }

    @Override
    public BookTextHolder getText() {
        return new BookTextHolder(StellgeUtil.stellgify(Text.literal(super.getText().getString()),requiredKnowledge,knowledgeBonus));
    }
}