package org.oxytocina.geomancy.items.jewelry;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.oxytocina.geomancy.items.ILeadPoisoningItem;
import org.oxytocina.geomancy.items.IManaStoringItem;
import org.oxytocina.geomancy.util.LeadUtil;
import org.oxytocina.geomancy.util.MadnessUtil;

public class LeadJewelryItem extends JewelryItem implements ILeadPoisoningItem {

    public float inventoryPoisoningSpeed;
    public float wornPoisoningSpeed;

    public LeadJewelryItem(Settings settings, JewelryItemSettings jewelryItemSettings, float inventoryPoisoningSpeed, float wornPoisoningSpeed) {
        super(settings, jewelryItemSettings);
        this.inventoryPoisoningSpeed = inventoryPoisoningSpeed;
        this.wornPoisoningSpeed = wornPoisoningSpeed;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);

        if(entity instanceof ServerPlayerEntity player)
        {
            LeadUtil.queueRecalculatePoisoningSpeed(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);

        if(entity instanceof ServerPlayerEntity player)
        {
            LeadUtil.queueRecalculatePoisoningSpeed(player);
        }
    }

    @Override
    public float getInInventoryPoisoningSpeed() {
        return inventoryPoisoningSpeed;
    }

    @Override
    public float getWornPoisoningSpeed() {
        return wornPoisoningSpeed;
    }
}
