package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.util.EntityUtil;
import org.oxytocina.geomancy.util.ParticleUtil;

public class ParticlesS2CPacket {

    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        ParticleUtil.ParticleData data = ParticleUtil.ParticleData.from(buf);
        data.run();
    }

    public static void send(World world, ParticleUtil.ParticleData data){
        if(!(world instanceof ServerWorld sw)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        data.write(buf);
        ModMessages.sendToAllClients(sw.getServer(),ModMessages.PARTICLES,buf, spe->
                EntityUtil.isInRange(spe,sw,data.pos,50));
    }
}
