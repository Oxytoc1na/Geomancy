package org.oxytocina.geomancy.networking.packet.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.items.IStorageItem;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;

public class StorageItemTryUpdateC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        var slot = buf.readInt();
        var nbt = buf.readNbt();

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;
            if(slot<0 || slot >= player.getInventory().size()) return;

            ItemStack output = player.getInventory().getStack(slot);
            if(output!=null && output.getItem() instanceof IStorageItem storage){
                // TODO: server authoritative checks!!
                storage.setInventory(output,nbt);
            }
        });


    }
}
