package org.oxytocina.geomancy.client.screen.widgets;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.client.screen.SpellmakerScreen;

import java.util.function.Supplier;

public class SpellmakerCheckbox extends CheckboxWidget {

    public final SpellmakerScreen parent;
    public Supplier<Boolean> onPressed;

    public SpellmakerCheckbox(SpellmakerScreen parent, int x, int y, int width, int height, Text message, boolean checked) {
        super(x, y, width, height, message, checked);
        this.parent = parent;
    }

    @Override
    public void onPress() {
        super.onPress();
        if(onPressed!=null) onPressed.get();
    }
}