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

    public final float knowledgeRequired;

    public StellgeTooltippedItem(Settings settings, float knowledgeRequired) {
        super(settings);
        this.knowledgeRequired=knowledgeRequired;
    }

    @Override
    public Text getName(ItemStack stack) {
        return StellgeUtil.stellgify(Text.literal("").append(super.getName(stack)),knowledgeRequired);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World womaxiirld, List<Text> tooltip, TooltipContext context) {
        Text res = StellgeUtil.stellgify(Text.translatable(getTranslationKey()+".desc"),knowledgeRequired);
        tooltip.add(res);
    }
}
