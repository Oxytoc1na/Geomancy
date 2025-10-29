package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.oxytocina.geomancy.blocks.blockEntities.ShiftBlockEntity;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.util.EntityUtil;
import org.oxytocina.geomancy.util.NetworkingUtil;

public class ShiftBlockS2CPacket {

    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        var pos = buf.readBlockPos();
        var dir = Direction.byId(buf.readByte());
        var replacementState = NetworkingUtil.readState(buf);
        var pushedState = NetworkingUtil.readState(buf);
        client.execute(()->{
            if(client.world==null) return;
            client.world.setBlockState(pos,replacementState,0);
            client.world.addBlockEntity(new ShiftBlockEntity(pos, replacementState, pushedState, dir));
        });
    }

    public static void send(ServerWorld world, BlockPos pos, Direction dir, BlockState replacementState, BlockState pushedState){
        var buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeByte(dir.getId());
        NetworkingUtil.writeState(buf,replacementState);
        NetworkingUtil.writeState(buf,pushedState);
        ModMessages.sendToAllClients(world.getServer(),ModMessages.SHIFT_BLOCK,buf, spe->EntityUtil.isInRange(spe,world,pos.toCenterPos(),100));
    }
}
