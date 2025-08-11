package org.oxytocina.geomancy.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class OctanguliteBlockItem extends BlockItem implements IMaddeningItem, ICustomRarityItem{

    public final float maddeningSpeed;

    public OctanguliteBlockItem(Block block, Settings settings, float maddeningSpeed) {
        super(block, settings);
        this.maddeningSpeed =maddeningSpeed;
    }

    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
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
