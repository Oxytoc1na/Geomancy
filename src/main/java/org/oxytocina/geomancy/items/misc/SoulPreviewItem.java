package org.oxytocina.geomancy.items.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class SoulPreviewItem extends Item {

    public SoulPreviewItem(Settings settings) {
        super(settings);
    }

    public ItemStack getRequirementStack(float cost){
        ItemStack res = getDefaultStack();
        res.getOrCreateNbt().putFloat("cost",cost);
        return res;
    }

    public float getCost(ItemStack stack){
        return stack.getOrCreateNbt().getFloat("cost");
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable("geomancy.soulpreview.tooltip.cost",getCost(stack));
    }

}
