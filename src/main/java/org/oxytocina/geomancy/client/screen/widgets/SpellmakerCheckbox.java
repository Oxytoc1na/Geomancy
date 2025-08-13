package org.oxytocina.geomancy.client.screen.widgets;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.client.screen.SpellmakerScreen;
import org.oxytocina.geomancy.sound.ModSoundEvents;

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

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(isChecked()? ModSoundEvents.SPELLMAKER_TYPE:ModSoundEvents.SPELLMAKER_TYPE_BACK, 1.0F));
    }
}