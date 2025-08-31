package org.oxytocina.geomancy.items.tools;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.items.ICustomRarityItem;
import org.oxytocina.geomancy.items.IMaddeningItem;
import org.oxytocina.geomancy.items.ISoulStoringItem;

public class OctanguliteHammerItem extends HammerItem implements IMaddeningItem, ISoulStoringItem, ICustomRarityItem {
    private final float maddeningSpeed;

    public OctanguliteHammerItem(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings, float skillBonus, float skillMultiplier, int progressPerHit, int cooldown, float maddeningSpeed) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings, skillBonus, skillMultiplier, progressPerHit, cooldown);
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
