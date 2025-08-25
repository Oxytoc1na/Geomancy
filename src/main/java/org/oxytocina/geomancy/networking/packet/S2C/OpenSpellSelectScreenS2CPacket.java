package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.client.screen.SpellSelectScreen;
import org.oxytocina.geomancy.networking.ModMessages;

public class OpenSpellSelectScreenS2CPacket {

    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        var player = client.player;
        if(player==null) return;
        var stack = buf.readItemStack();
        var slot = buf.readInt();
        client.execute(()->{
            client.setScreen(new SpellSelectScreen(client.player,stack,slot, Text.empty()));
        });
    }
}
