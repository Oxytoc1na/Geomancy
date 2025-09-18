package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.client.screen.SpellmakerScreenHandler;

public class SpellmakerRefreshS2CPacket {

    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        var pos = buf.readBlockPos();
        client.execute(()-> {
            if(client.world == null) return;
            var entity = client.world.getBlockEntity(pos);
            if (entity instanceof SpellmakerBlockEntity) {
                if (SpellmakerScreenHandler.current != null) {
                    SpellmakerScreenHandler.current.refresh();
                }
            }
        });

    }
}
