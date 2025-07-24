package org.oxytocina.geomancy.networking.packet.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.spells.SpellGrid;

public class SpellmakerTryChangeGridLibC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        BlockPos blockEntityPos = buf.readBlockPos();
        boolean nextLib = buf.readBoolean();

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;

            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockEntityPos);
            if(blockEntity instanceof SpellmakerBlockEntity spellmaker){
                var output = spellmaker.getOutput();
                if(output.getItem() instanceof SpellStoringItem storage){
                    SpellGrid grid =SpellStoringItem.readGrid(output);
                    if(grid!=null){
                        grid.library = nextLib;
                        SpellStoringItem.writeGrid(output,grid);
                    }
                }
            }
        });
    }
}
