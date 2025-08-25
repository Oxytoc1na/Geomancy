package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.util.ManaUtil;

public class CastParticlesS2CPacket {

    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        SpellBlocks.CastParticleData data = SpellBlocks.CastParticleData.from(buf);
        data.run();
    }
}
