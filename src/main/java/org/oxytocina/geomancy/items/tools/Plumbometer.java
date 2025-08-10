package org.oxytocina.geomancy.items.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.oxytocina.geomancy.registries.ModDamageTypes;
import org.oxytocina.geomancy.util.LeadUtil;

import java.text.Normalizer;
import java.util.UUID;

public class Plumbometer extends Item {
    public Plumbometer(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user instanceof ServerPlayerEntity)
            initiateMeasurement(user.getStackInHand(hand),user);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public void initiateMeasurement(ItemStack stack, PlayerEntity player){

        player.damage(ModDamageTypes.of(player.getWorld(),ModDamageTypes.PLUMBOMETER),2);

        player.sendMessage(Text.translatable("geomancy.plumbometer.start",player.getDisplayName()).formatted(Formatting.DARK_AQUA));
        stack.getOrCreateNbt().putShort("timer", (short) 1);
        stack.getOrCreateNbt().putUuid("user", player.getUuid());
    }

    public void finishMeasurement(ItemStack stack, PlayerEntity player){
        if(player==null){
            //player.sendMessage(Text.translatable("geomancy.plumbometer.nouser").formatted(Formatting.DARK_AQUA));
            stack.setNbt(null);
            return;
        }

        int severity = 0;
        float poisoning = LeadUtil.getPoisoning(player);

        if(poisoning>1000)
            severity=5;
        else if(poisoning > 500)
            severity = 4;
        else if(poisoning > 250)
            severity = 3;
        else if(poisoning > 100)
            severity = 2;
        else if(poisoning > 5)
            severity = 1;

        MutableText severityText = Text.translatable("geomancy.plumbometer."+severity).formatted(switch(severity){
            case 1 -> Formatting.DARK_GREEN;
            case 2 -> Formatting.YELLOW;
            case 3 -> Formatting.GOLD;
            case 4 -> Formatting.RED;
            case 5 -> Formatting.DARK_RED;
            default -> Formatting.GREEN;
        });
        player.sendMessage(Text.translatable("geomancy.plumbometer.readout",severityText).formatted(Formatting.DARK_AQUA));
        stack.setNbt(null);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        var nbt = stack.getNbt();
        if(nbt==null) return;
        if(nbt.get("timer")==null) return;
        short timer = nbt.getShort("timer");

        timer++;

        nbt.putShort("timer",timer);

        if(timer>20*3){
            Entity userEnt = null;
            UUID uuid = nbt.getUuid("user");
            if(world instanceof ServerWorld sw)
            {
                userEnt = sw.getEntity(uuid);
                if(userEnt instanceof PlayerEntity user)
                {
                    finishMeasurement(stack,user);
                    return;
                }
            }
            finishMeasurement(stack,null);
        }

    }
}
