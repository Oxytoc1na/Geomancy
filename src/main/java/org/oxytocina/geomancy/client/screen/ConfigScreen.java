package org.oxytocina.geomancy.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.oxytocina.geomancy.client.screen.widgets.FloatSlider;
import org.oxytocina.geomancy.client.screen.widgets.IntSlider;
import org.oxytocina.geomancy.util.GeomancyConfig;

import java.util.function.Supplier;

public class ConfigScreen extends Screen {

    private static final Text CREDITS_AND_ATTRIBUTION_TEXT = Text.translatable("options.credits_and_attribution");

    private static final int COLUMNS = 2;
    private final Screen parent;
    private CyclingButtonWidget<Boolean> difficultyButton;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("options.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(COLUMNS);
        //adder.add(this.settings.getFov().createWidget(this.client.options, 0, 0, 150));
        //adder.add(this.createTopRightButton());

        // epilepsy
        adder.add(CyclingButtonWidget.<Boolean>builder(b->Text.literal(b.toString())).values(true,false)
                .initially(GeomancyConfig.CONFIG.epilepsyMode.value()).build(0,0, 150, 20,
                        Text.translatable("geomancy.options.epilepsy"),(button, toggle) ->
                                GeomancyConfig.CONFIG.epilepsyMode.setValue(toggle)));

        // spellmaker move
        adder.add(CyclingButtonWidget.<Boolean>builder(b->Text.literal(b.toString())).values(true,false)
                .initially(GeomancyConfig.CONFIG.noSpellmakerMove.value()).build(0,0, 150, 20,
                        Text.translatable("geomancy.options.no_spellmaker_move"),(button, toggle) ->
                                GeomancyConfig.CONFIG.noSpellmakerMove.setValue(toggle)));

        // spell timeout penalty
        adder.add(CyclingButtonWidget.<Boolean>builder(b->Text.literal(b.toString())).values(true,false)
                .initially(GeomancyConfig.CONFIG.penalizeSpellTimeout.value()).build(0,0, 150, 20,
                        Text.translatable("geomancy.options.penalize_spell_timeout"),(button, toggle) ->
                                GeomancyConfig.CONFIG.penalizeSpellTimeout.setValue(toggle)));

        // player variable loading
        adder.add(CyclingButtonWidget.<Boolean>builder(b->Text.literal(b.toString())).values(true,false)
                .initially(GeomancyConfig.CONFIG.playerVariableLoading.value()).build(0,0, 150, 20,
                        Text.translatable("geomancy.options.player_variable_loading"),(button, toggle) ->
                                GeomancyConfig.CONFIG.playerVariableLoading.setValue(toggle)));

        // spellmaker ui speed
        var slider = FloatSlider.create(0,0,150,20,Text.translatable("geomancy.options.spellmakeruispeed"),
                GeomancyConfig.CONFIG.spellmakerUiSpeed.value(),0.1f,1f, GeomancyConfig.CONFIG.spellmakerUiSpeed::setValue);
        adder.add(slider);

        // cam shake
        slider = FloatSlider.create(0,0,150,20,Text.translatable("geomancy.options.shake_intensity"),
                GeomancyConfig.CONFIG.shakeIntensity.value(),0f,1f, GeomancyConfig.CONFIG.shakeIntensity::setValue);
        adder.add(slider);

        // spellcradle tooltip truncation
        var intSlider = IntSlider.create(0,0,150,20,Text.translatable("geomancy.options.spellcradle_tooltip_truncation"),
                GeomancyConfig.CONFIG.spellcradleTooltipTruncation.value(),1,20, GeomancyConfig.CONFIG.spellcradleTooltipTruncation::setValue);
        adder.add(intSlider);

        // credits and attribution
        adder.add(this.createButton(CREDITS_AND_ATTRIBUTION_TEXT, () -> new GeomancyCreditsScreen(this,false,()->{})));

        // done
        adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent)).width(200).build(), 2, adder.copyPositioner().marginTop(6));
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
        gridWidget.forEachChild(this::addDrawableChild);
    }

    //private Widget createTopRightButton() {
    //    if (this.client.world != null && this.client.isIntegratedServerRunning()) {
    //        this.difficultyButton = createDifficultyButtonWidget(0, 0, "options.difficulty", this.client);
    //        if (!this.client.world.getLevelProperties().isHardcore()) {
    //            this.lockDifficultyButton = new LockButtonWidget(
    //                    0,
    //                    0,
    //                    button -> this.client
    //                            .setScreen(
    //                                    new ConfirmScreen(
    //                                            this::lockDifficulty,
    //                                            Text.translatable("difficulty.lock.title"),
    //                                            Text.translatable("difficulty.lock.question", this.client.world.getLevelProperties().getDifficulty().getTranslatableName())
    //                                    )
    //                            )
    //            );
    //            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockDifficultyButton.getWidth());
    //            this.lockDifficultyButton.setLocked(this.client.world.getLevelProperties().isDifficultyLocked());
    //            this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
    //            this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
    //            AxisGridWidget axisGridWidget = new AxisGridWidget(150, 0, AxisGridWidget.DisplayAxis.HORIZONTAL);
    //            axisGridWidget.add(this.difficultyButton);
    //            axisGridWidget.add(this.lockDifficultyButton);
    //            return axisGridWidget;
    //        } else {
    //            this.difficultyButton.active = false;
    //            return this.difficultyButton;
    //        }
    //    } else {
    //        return ButtonWidget.builder(
    //                        Text.translatable("options.online"), button -> this.client.setScreen(OnlineOptionsScreen.create(this.client, this, this.settings))
    //                )
    //                .dimensions(this.width / 2 + 5, this.height / 6 - 12 + 24, 150, 20)
    //                .build();
    //    }
    //}

    public static CyclingButtonWidget<Difficulty> createDifficultyButtonWidget(int x, int y, String translationKey, MinecraftClient client) {
        return CyclingButtonWidget.<Difficulty>builder(Difficulty::getTranslatableName)
                .values(Difficulty.values())
                .initially(client.world.getDifficulty())
                .build(
                        x, y, 150, 20, Text.translatable(translationKey), (button, difficulty) -> client.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket(difficulty))
                );
    }

    @Override
    public void removed() {
        //GeomancyConfig.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(context, mouseX, mouseY, delta);
    }

    private ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier) {
        return ButtonWidget.builder(message, button -> this.client.setScreen((Screen)screenSupplier.get())).build();
    }
}
