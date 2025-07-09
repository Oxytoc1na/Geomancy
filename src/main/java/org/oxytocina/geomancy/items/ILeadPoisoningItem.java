package org.oxytocina.geomancy.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.entity.ManaStoringItemData;

import java.util.UUID;

public interface ILeadPoisoningItem {
    float getInInventoryPoisoningSpeed();
    default float getWornPoisoningSpeed(){return getInInventoryPoisoningSpeed();}
    default float getInHandPoisoningSpeed(){return getInInventoryPoisoningSpeed();}
}
