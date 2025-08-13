package org.oxytocina.geomancy.client.screen.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.oxytocina.geomancy.client.screen.SpellmakerScreen;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpellmakerTextInput extends TextFieldWidget {

    protected final SpellmakerScreen parent;
    public boolean validInput = true;

    protected boolean prevFocused = false;

    public Consumer<String> onEditFinished;

    public String prevText = "";

    public SpellmakerTextInput(SpellmakerScreen parent, TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
        this.parent=parent;

        setRenderTextProvider((string, pos) -> OrderedText.styledForwardsVisitedString(string, Style.EMPTY.withColor(
                validInput? Formatting.RESET:Formatting.RED
        )));
    }

    @Override
    public void tick() {
        super.tick();

        if(!isFocused()&&prevFocused){
            onUnfocused();
        }

        prevFocused=isFocused();
    }

    public void onUnfocused(){
        onEditFinished();
    }

    public void onEditFinished(){
        if(onEditFinished!=null)
            onEditFinished.accept(getText());


    }
}