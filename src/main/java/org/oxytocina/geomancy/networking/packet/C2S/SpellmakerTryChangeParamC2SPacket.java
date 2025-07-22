package org.oxytocina.geomancy.networking.packet.C2S;

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

public class SpellmakerTryChangeParamC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        NbtCompound nbt = buf.readNbt();
        BlockPos blockEntityPos = buf.readBlockPos();
        String paramName = buf.readString();
        String nextVal = buf.readString();

        // invalid variable
        if(nextVal==null||Objects.equals(nextVal, "")) return;

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;

            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockEntityPos);
            if(blockEntity instanceof SpellmakerBlockEntity spellmaker){
                var output = spellmaker.getOutput();
                if(output.getItem() instanceof SpellStoringItem storage){
                    SpellGrid grid =SpellStoringItem.readGrid(output);
                    if(grid!=null){
                        SpellComponent component = new SpellComponent(null,nbt);
                        var presentComponent = grid.getComponent(component.position);
                        if(presentComponent!=null){
                            // does a param with this name exist?
                            if(presentComponent.function.parameters.containsKey(paramName)){
                                // is the value parseable?
                                if(presentComponent.canAcceptParam(paramName,nextVal)){
                                    // success!!
                                    presentComponent.setAndParseParam(paramName,nextVal);
                                    SpellStoringItem.writeGrid(output,grid);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
