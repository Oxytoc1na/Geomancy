package org.oxytocina.geomancy.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;

import java.util.LinkedList;
import java.util.List;

// Done with the help:
// https://github.com/TeamGalacticraft/Galacticraft/tree/main (MIT License)
public class SoulForgeCategory implements DisplayCategory<BasicDisplay> {
    public static final Identifier TEXTURE =
            Geomancy.locate("textures/gui/soulforge_gui_rei.png");
    public static final CategoryIdentifier<SmitheryDisplay> SOUL_FORGE =
            CategoryIdentifier.of(Geomancy.MOD_ID, "soul_forge");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return SOUL_FORGE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.geomancy.rei.soulforge.title");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.SOUL_FORGE.asItem().getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 87, bounds.getCenterY() - 35);
        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 175, 89)));

        for (int i = 0; i < display.getInputEntries().size(); ++i) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 25 + (i%3) * 18, startPoint.y + 18 + (i/3)*18))
                    .entries(display.getInputEntries().get(i)));
        }

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 134, startPoint.y + 36))
                .markOutput().entries(display.getOutputEntries().get(0)));


        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }
}
