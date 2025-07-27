package org.oxytocina.geomancy.networking.packet.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.oxytocina.geomancy.blocks.blockEntities.SpellstorerBlockEntity;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;

public class SpellstorerTryUpdateCasterC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        BlockPos blockEntityPos = buf.readBlockPos();
        var nbt = buf.readNbt();

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;

            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockEntityPos);
            if(blockEntity instanceof SpellstorerBlockEntity spellstorer){
                var output = spellstorer.getSlottedCasterItem();
                if(output!=null && output.getItem() instanceof SoulCastingItem caster){
                    // TODO: server authoritative checks!!
                    caster.setInventory(output,nbt);
                }

            }
        });


    }
}
