package org.oxytocina.geomancy.networking.packet.C2S;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.items.IStorageItem;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.util.EntityUtil;

public class OpenStorageItemScreenC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int slot = buf.readInt();
        server.execute(()->{
            if(player==null||player.getWorld()==null) return;
            var stack = player.getInventory().getStack(slot);
            if(stack.isEmpty()) return;
            if(!((stack.getItem()) instanceof ExtendedScreenHandlerFactory factory)) return;
            var sci = (SoulCastingItem) stack.getItem();
            if(sci!=null) sci.tempOpenStorageScreenOverride=true;
            player.openHandledScreen(factory);
            if(sci!=null) sci.tempOpenStorageScreenOverride=false;
        });
    }

    public static void send(int slot){
        var buf = PacketByteBufs.create();
        buf.writeInt(slot);
        ClientPlayNetworking.send(ModMessages.OPEN_STORAGE_ITEM_SCREEN,buf);
    }
}
