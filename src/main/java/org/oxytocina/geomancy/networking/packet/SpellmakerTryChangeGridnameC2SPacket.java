package org.oxytocina.geomancy.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;

import java.util.Objects;

public class SpellmakerTryChangeGridnameC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        BlockPos blockEntityPos = buf.readBlockPos();
        String nextName = buf.readString();

        // invalid variable
        if(nextName==null) return;

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;

            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockEntityPos);
            if(blockEntity instanceof SpellmakerBlockEntity spellmaker){
                var output = spellmaker.getOutput();
                if(output.getItem() instanceof SpellStoringItem storage){
                    SpellGrid grid =SpellStoringItem.readGrid(output);
                    if(grid!=null){
                        grid.name = nextName;
                        SpellStoringItem.writeGrid(output,grid);
                    }
                }
            }
        });
    }
}
