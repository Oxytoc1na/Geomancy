package org.oxytocina.geomancy.client.screen.widgets;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.client.screen.SpellmakerScreen;
import org.oxytocina.geomancy.sound.ModSoundEvents;

public class SpellmakerButton extends ButtonWidget {

    protected final SpellmakerScreen parent;
    protected final int u, v;
    protected final Supplier<Boolean> displayCondition;
    protected final List<Text> tooltip;
    protected final SoundEvent soundEvent;

    public SpellmakerButton(SpellmakerScreen parent, int x, int y, int u, int v, int w, int h, Text pMessage, PressAction onPress,SoundEvent soundEvent, Text... tooltip) {
        this(parent, x, y, u, v, w, h, () -> true, pMessage, onPress,soundEvent, tooltip);
    }

    public SpellmakerButton(SpellmakerScreen parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, Text pMessage, PressAction onPress,SoundEvent soundEvent, Text... tooltip) {
        super(x, y, w, h, pMessage, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.parent = parent;
        this.u = u;
        this.v = v;
        this.displayCondition = displayCondition;
        this.tooltip = List.of(tooltip);
        this.soundEvent=soundEvent;
    }

    @Override
    public final void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //this.active = this.visible = this.displayCondition.get();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(soundEvent, 1.0F));
    }
}