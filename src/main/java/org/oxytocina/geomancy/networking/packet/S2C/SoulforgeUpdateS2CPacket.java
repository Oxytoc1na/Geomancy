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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.oxytocina.geomancy.blocks.blockEntities.SoulForgeBlockEntity;
import org.oxytocina.geomancy.client.util.CamShakeUtil;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.util.EntityUtil;

public class SoulforgeUpdateS2CPacket {

    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        boolean active = buf.readBoolean();
        Identifier recipe = active?buf.readIdentifier():null;
        float progress = buf.readFloat();
        float instability = buf.readFloat();
        client.execute(()->{
            if(client==null||client.world==null) return;
            SoulForgeBlockEntity forge = (SoulForgeBlockEntity) client.world.getBlockEntity(pos);
            if(forge==null) return;

            forge.setStatus(recipe,progress,instability);
        });
    }
}
