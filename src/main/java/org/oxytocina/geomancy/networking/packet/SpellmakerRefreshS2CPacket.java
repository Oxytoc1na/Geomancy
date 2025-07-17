package org.oxytocina.geomancy.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.client.screen.SpellmakerScreenHandler;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.ManaUtil;

public class SpellmakerRefreshS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        var pos = buf.readBlockPos();
        var entity = client.world!=null?client.world.getBlockEntity(pos):null;
        if(entity instanceof SpellmakerBlockEntity){
            if(SpellmakerScreenHandler.current!=null){
                SpellmakerScreenHandler.current.updateAvailableComponents();
            }
        }
    }
}
