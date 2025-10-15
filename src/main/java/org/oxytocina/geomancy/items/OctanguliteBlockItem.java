package org.oxytocina.geomancy.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class OctanguliteBlockItem extends MaddeningBlockItem implements ICustomRarityItem{


    public OctanguliteBlockItem(Block block, Settings settings, float maddeningSpeed) {
        super(block, settings,maddeningSpeed);
    }

    @Override
    public Text getName(ItemStack stack) {
        return colorizeName(stack,super.getName(stack));
    }

    @Override
    public Rarity getRarity() {
        return Rarity.Octangulite;
    }
}
