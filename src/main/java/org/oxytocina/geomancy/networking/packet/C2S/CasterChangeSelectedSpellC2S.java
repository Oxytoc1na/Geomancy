package org.oxytocina.geomancy.networking.packet.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;

public class CasterChangeSelectedSpellC2S {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){

        ItemStack stack = buf.readItemStack();
        int slot = buf.readInt();
        int selected = buf.readInt();

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;

            var serverStack = player.getInventory().getStack(slot);
            if(serverStack.getItem()!=stack.getItem()) return; // mismatching stacks
            if(!(serverStack.getItem() instanceof SoulCastingItem caster)) return; // not a caster

            caster.setSelectedSpellIndex(serverStack,selected);
        });


    }
}
