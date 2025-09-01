package org.oxytocina.geomancy.items.tools;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.ICustomRarityItem;
import org.oxytocina.geomancy.items.ISoulStoringItem;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.List;

public class PonderableSoulStorageItem extends SoulStorageItem {

    public PonderableSoulStorageItem(Settings settings, float capacity, float rechargeSpeedMultiplier) {
        super(settings,capacity,rechargeSpeedMultiplier);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient){
            if(Toolbox.random.nextFloat()<0.001f)
                user.sendMessage(Text.translatable("geomancy.pondering.prefix").formatted(Formatting.LIGHT_PURPLE).append(
                        Text.translatable("geomancy.pondering.mishap").formatted(Formatting.GRAY)
                ));
            else
                user.sendMessage(Text.translatable("geomancy.pondering.prefix").formatted(Formatting.LIGHT_PURPLE).append(
                        Text.translatable("geomancy.pondering."+ Toolbox.random.nextInt(20)).formatted(Formatting.GRAY)
                ));
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> list, TooltipContext context) {
        list.add(Text.translatable("geomancy.pondering.tooltip").formatted(Formatting.LIGHT_PURPLE).formatted(Formatting.ITALIC));
        super.appendTooltip(stack, world, list, context);
    }
}
