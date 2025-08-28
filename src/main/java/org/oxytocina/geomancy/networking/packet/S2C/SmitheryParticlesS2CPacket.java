package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.util.EntityUtil;

public class SmitheryParticlesS2CPacket {

    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        SmitheryBlockEntity.ParticleData.from(buf).run();
    }

    public static void send(SmitheryBlockEntity smithery, SmitheryBlockEntity.ParticleData data){
        if(!(smithery.getWorld() instanceof ServerWorld sw)) return;
        var buf = PacketByteBufs.create();
        data.write(buf);
        ModMessages.sendToAllClients(sw.getServer(),ModMessages.SMITHERY_PARTICLES,buf,
                serverPlayerEntity -> EntityUtil.isInRange(serverPlayerEntity,sw,data.pos,50));
    }
}
