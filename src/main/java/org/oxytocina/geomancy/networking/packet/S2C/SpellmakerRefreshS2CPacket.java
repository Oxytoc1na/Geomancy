package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.client.screen.SpellmakerScreenHandler;

public class SpellmakerRefreshS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        var pos = buf.readBlockPos();
        var entity = client.world!=null?client.world.getBlockEntity(pos):null;
        if(entity instanceof SpellmakerBlockEntity){
            if(SpellmakerScreenHandler.current!=null){
                SpellmakerScreenHandler.current.refresh();
            }
        }
    }
}
