package org.oxytocina.geomancy.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.oxytocina.geomancy.client.screen.widgets.FloatSlider;
import org.oxytocina.geomancy.util.GeomancyConfig;

import java.util.function.Supplier;

public class SimpleGeomancyCreditsScreen extends Screen {


    private static final int COLUMNS = 2;
    private final Screen parent;

    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

    public SimpleGeomancyCreditsScreen(Screen parent) {
        super(Text.translatable("geomancy.credits.title"));
        this.parent = parent;
    }

    protected void init() {
        this.layout.addHeader(new TextWidget(this.getTitle(), this.textRenderer));
        GridWidget gridWidget = ((GridWidget)this.layout.addBody(new GridWidget())).setSpacing(8);
        gridWidget.getMainPositioner().alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(1);
        String creditsString = """
                Oxytocina
                Herzogin Maxi
                
                Freesound.org:
                https://freesound.org/people/JalynCatbtg/sounds/633950/
                https://freesound.org/people/LiamG_SFX/sounds/334234/
                https://freesound.org/people/Heshl/sounds/269129/
                https://freesound.org/people/RescopicSound/sounds/750429/
                https://freesound.org/people/RescopicSound/sounds/750431/
                https://freesound.org/people/RescopicSound/sounds/750435/
                """;
        var sSplit = creditsString.split("\n");
        for(var s : sSplit)
            adder.add(new TextWidget(Text.literal(s+"\n"),MinecraftClient.getInstance().textRenderer));
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());
        this.layout.refreshPositions();
        this.layout.forEachChild(this::addDrawableChild);
    }

    protected void initTabNavigation() {
        this.layout.refreshPositions();
    }

    private void openCredits() {
        this.client.setScreen(new CreditsScreen(false, () -> this.client.setScreen(this)));
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }
}
