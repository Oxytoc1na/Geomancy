package org.oxytocina.geomancy.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.util.StellgeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StellgeTooltippedItem extends Item {

    public final String descriptionLangKey;

    public StellgeTooltippedItem(Settings settings,String descriptionLangKey) {
        super(settings);
        this.descriptionLangKey=descriptionLangKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        float knowledge = 1;
        var base = Text.translatable(descriptionLangKey).getString();
        Text res = StellgeUtil.stellgify(Text.literal(base),2,knowledge);
        tooltip.add(res);
    }
}
