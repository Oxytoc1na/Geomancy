package org.oxytocina.geomancy.items.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.items.ICustomRarityItem;
import org.oxytocina.geomancy.items.IMaddeningItem;
import org.oxytocina.geomancy.items.IManaStoringItem;

public class OctanguliteShovelItem extends ShovelItem implements IMaddeningItem, IManaStoringItem, ICustomRarityItem {
    private final float maddeningSpeed;

    public OctanguliteShovelItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings, float maddeningSpeed) {
        super(material,attackDamage,attackSpeed,settings);
        this.maddeningSpeed=maddeningSpeed;
    }

    @Override
    public float getInInventoryMaddeningSpeed() {
        return maddeningSpeed;
    }

    @Override
    public float getInHandMaddeningSpeed() {
        return maddeningSpeed*3;
    }

    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return 100;
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
