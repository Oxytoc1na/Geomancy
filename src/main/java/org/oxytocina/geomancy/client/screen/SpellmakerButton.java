package org.oxytocina.geomancy.client.screen;

import com.klikli_dev.modonomicon.client.gui.book.BookContentScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookScreenWithButtons;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;

public class SpellmakerButton extends ButtonWidget {

    protected final SpellmakerScreen parent;
    protected final int u, v;
    protected final Supplier<Boolean> displayCondition;
    protected final List<Text> tooltip;

    public SpellmakerButton(SpellmakerScreen parent, int x, int y, int u, int v, int w, int h, Text pMessage, PressAction onPress, Text... tooltip) {
        this(parent, x, y, u, v, w, h, () -> true, pMessage, onPress, tooltip);
    }

    public SpellmakerButton(SpellmakerScreen parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, Text pMessage, PressAction onPress, Text... tooltip) {
        super(x, y, w, h, pMessage, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.parent = parent;
        this.u = u;
        this.v = v;
        this.displayCondition = displayCondition;
        this.tooltip = List.of(tooltip);
    }

    @Override
    public final void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //this.active = this.visible = this.displayCondition.get();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}