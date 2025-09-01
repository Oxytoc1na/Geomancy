package org.oxytocina.geomancy.items.tools;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.*;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.oxytocina.geomancy.spells.SpellBlockArgs;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.List;

public class PrecompiledSoulCastingItem extends SoulCastingItem {

    public PrecompiledSoulCastingItem(Settings settings, int storageSize, float internalSoulStorage, float rechargeSpeedMultiplier) {
        super(settings, storageSize,internalSoulStorage,rechargeSpeedMultiplier);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if(user.isSneaking() && user.isCreative())
            {
                // open spell selection
                if(user instanceof ServerPlayerEntity sp){
                    var stack = user.getStackInHand(hand);
                    sp.openHandledScreen((PrecompiledSoulCastingItem) stack.getItem());
                }
            }
            else{
                cast(user.getStackInHand(hand),user);
            }
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("geomancy.caster.emptyhint3").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.translatable("geomancy.caster.emptyhint4").formatted(Formatting.DARK_GRAY));
    }

    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return 100;
    }

    @Override
    public Text getName(ItemStack stack) {
        var spells = getCastableSpellItems(stack);
        MutableText spellText = null;
        if(spells.isEmpty()){
            spellText = Text.translatable("geomancy.caster.nospells").formatted(Formatting.RED);
        }
        else{
            var nextIndex=0;
            var spellItem = spells.get(nextIndex);
            var grid = SpellStoringItem.readGrid(spellItem);
            MutableText indexText = Text.literal("").formatted(Formatting.GRAY);
            if(grid==null)
            {
                spellText = indexText.append(Text.translatable("geomancy.spellstorage.empty").formatted(Formatting.GRAY));
            }
            else{
                spellText = indexText.append(grid.getName().formatted(Formatting.DARK_AQUA));
            }
        }

        return ((MutableText)colorizeName(stack,Text.translatable(this.getTranslationKey(stack)))).append(Text.literal(" [").append(
                spellText
                ).append("]").formatted(Formatting.GRAY));
    }


    @Override
    @Environment(EnvType.CLIENT)
    public boolean onScrolled(ItemStack stack, float delta,PlayerEntity player) {
        return false;
    }

    // display selected spell if sneaking
    @Override
    @Environment(EnvType.CLIENT)
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldBlockScrolling(ItemStack stack, PlayerEntity player) {
        return false;
    }

    public static void setSpell(ItemStack stack, ItemStack spellStorer){
        if(!(stack.getItem() instanceof PrecompiledSoulCastingItem compiled)) return;
        if(!(spellStorer.getItem() instanceof SpellStoringItem storer)) return;
        compiled.setStack(stack,0,spellStorer);
    }
}
