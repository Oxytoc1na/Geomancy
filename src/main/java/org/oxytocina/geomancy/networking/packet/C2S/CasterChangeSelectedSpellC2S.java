package org.oxytocina.geomancy.networking.packet.C2S;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.networking.packet.S2C.CasterSpellChangedS2CPacket;

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
            CasterSpellChangedS2CPacket.send(player,slot,selected);
        });


    }

    public static void send(ItemStack stack, int slot, int selected){
        var buf = PacketByteBufs.create();
        buf.writeItemStack(stack);
        buf.writeInt(slot);
        buf.writeInt(selected);
        ClientPlayNetworking.send(ModMessages.CASTER_CHANGE_SELECTED_SPELL,buf);
    }
}
