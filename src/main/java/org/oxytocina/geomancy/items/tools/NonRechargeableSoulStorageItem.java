package org.oxytocina.geomancy.items.tools;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.ICustomRarityItem;
import org.oxytocina.geomancy.items.IManaStoringItem;

import java.util.List;

public class NonRechargeableSoulStorageItem extends SoulStorageItem {

    public NonRechargeableSoulStorageItem(Settings settings, float capacity) {
        super(settings,capacity,0);
    }

    @Override
    public float getInitialMana(ItemStack base) {
        return getBaseSoulCapacity(base);
    }

    @Override
    public float getRechargeSpeedMultiplier(World world, ItemStack stack, LivingEntity entity) {
        return 0;
    }

    @Override
    public void onDepleted(ItemStack stack) {
        stack.decrement(1);
    }

    @Override
    public int depletionPriority(ItemStack stack) {
        return -1;
    }
}
