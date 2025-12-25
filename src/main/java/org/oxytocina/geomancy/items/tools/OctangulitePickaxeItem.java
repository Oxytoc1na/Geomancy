package org.oxytocina.geomancy.items.tools;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.oxytocina.geomancy.items.ICustomRarityItem;
import org.oxytocina.geomancy.items.IMaddeningItem;
import org.oxytocina.geomancy.items.ISoulStoringItem;

public class OctangulitePickaxeItem extends PickaxeItem implements IMaddeningItem, ISoulStoringItem, ICustomRarityItem {
    private final float maddeningSpeed;

    public OctangulitePickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings, float maddeningSpeed) {
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
    public ICustomRarityItem.Rarity getRarity() {
        return ICustomRarityItem.Rarity.Octangulite;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }
}
