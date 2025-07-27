package org.oxytocina.geomancy.items.tools;

import net.minecraft.block.Block;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import org.oxytocina.geomancy.items.ILeadPoisoningItem;

public class LeadMiningToolItem extends MiningToolItem implements ILeadPoisoningItem {
    private final float poisoningSpeed;

    public LeadMiningToolItem(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings, float poisoningSpeed) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
        this.poisoningSpeed = poisoningSpeed;
    }

    @Override
    public float getInInventoryPoisoningSpeed() {
        return poisoningSpeed;
    }
}
