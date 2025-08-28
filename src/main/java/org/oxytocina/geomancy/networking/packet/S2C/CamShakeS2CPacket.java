package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.oxytocina.geomancy.client.util.CamShakeUtil;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.util.EntityUtil;

public class CamShakeS2CPacket {

    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        CamShakeUtil.shake(buf.readFloat(),buf.readFloat(),buf.readFloat());
    }

    public static void send(ServerPlayerEntity spe, float intensity, float duration, float speed){
        var buf = PacketByteBufs.create();
        buf.writeFloat(intensity);
        buf.writeFloat(duration);
        buf.writeFloat(speed);
        ServerPlayNetworking.send(spe,ModMessages.CAM_SHAKE,buf);
    }

    public static void send(ServerWorld sw, Vec3d pos, float range, float intensity, float duration, float speed){
        for(var spe : sw.getPlayers()){
            float mult = 1-(float)EntityUtil.distanceTo(spe,pos) / range;
            if(mult<=0) continue;
            send(spe,intensity*mult,duration,speed*mult);
        }
    }
}
