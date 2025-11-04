package org.oxytocina.geomancy.enchantments;

import org.oxytocina.geomancy.items.armor.CastingArmorItem;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.items.trinkets.CastingTrinketItem;

public class CasterEnchantment extends ModEnchantment {

    protected CasterEnchantment() {
        super(Rarity.COMMON, s->
                s.getItem() instanceof SoulCastingItem ||
                s.getItem() instanceof CastingArmorItem ||
                s.getItem() instanceof CastingTrinketItem

        );
    }

    @Override
    public int getMinPower(int level) {
        return 1+level*5;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
