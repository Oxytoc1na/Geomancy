package org.oxytocina.geomancy.items;

import net.minecraft.client.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.stat.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.oxytocina.geomancy.Geomancy;

import java.util.*;

public class LorebookItem extends Item {

    public String guidebookPageToOpen;
    private final int toolTipColor;

    public LorebookItem(Settings settings, String guidebookPageToOpen) {
        this(settings, guidebookPageToOpen, -1);
    }

    public LorebookItem(Settings settings, String guidebookPageToOpen, int toolTipColor) {
        super(settings);
        this.guidebookPageToOpen = guidebookPageToOpen;
        this.toolTipColor = toolTipColor;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            user.incrementStat(Stats.USED.getOrCreateStat(this));

            return TypedActionResult.success(user.getStackInHand(hand));
        } else {
            try {
                openGuidebookPage(Geomancy.locate(guidebookPageToOpen), 0);
            } catch (NullPointerException e) {
                Geomancy.logError(user.getName().getString() + " used a LorebookItem to open the guidebook page " + this.guidebookPageToOpen + " but it does not exist");
            }
        }

        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    private void openGuidebookPage(Identifier entry, int page) {
        ModItems.GUIDE_BOOK.openGuidebook(entry, page);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (toolTipColor == -1) {
            tooltip.add(Text.translatable(this.getTranslationKey() + ".tooltip").formatted(Formatting.GRAY));
            return;
        }

        tooltip.add(Text.translatable(this.getTranslationKey() + ".tooltip").styled(s -> s.withColor(toolTipColor)));
    }

}
