package org.oxytocina.geomancy.items.jewelry;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.items.ManaStoringItem;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.util.ManaUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OctanguliteJewelryItem extends JewelryItem implements ManaStoringItem {

    public float baseSoulCapacity;

    public OctanguliteJewelryItem(Settings settings, JewelryItemSettings jewelryItemSettings, float baseSoulCapacity) {
        super(settings, jewelryItemSettings);
        this.baseSoulCapacity = baseSoulCapacity;
    }

    @Override
    public float getCapacity(World world, ItemStack stack) {
        return baseSoulCapacity;
    }

    @Override
    public float getBaseCapacity(ItemStack stack) {
        return baseSoulCapacity;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);

        if(entity instanceof ServerPlayerEntity player)
            ManaUtil.queueRecalculateMana(player);
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);

        if(entity instanceof ServerPlayerEntity player)
            ManaUtil.queueRecalculateMana(player);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ((ManaStoringItem)stack.getItem()).getBarColor(stack);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if(MinecraftClient.getInstance()==null) return 0;

        var world = MinecraftClient.getInstance().world;
        return Math.round(getMana(world,stack) * 13.0F / getCapacity(world,stack));
    }
}
