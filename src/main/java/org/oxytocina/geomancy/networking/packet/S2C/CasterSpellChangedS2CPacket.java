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
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.networking.ModMessages;

public class CasterSpellChangedS2CPacket {
    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        int slot = buf.readInt();
        int spellIndex = buf.readInt();
        client.execute(()->{
            if(client==null || client.player==null) return;
            var stack = client.player.getInventory().getStack(slot);
            if(!(stack.getItem() instanceof ISpellSelectorItem sps)) return;
            sps.onSpellChanged(stack,client.player,spellIndex);
        });
    }

    public static void send(ServerPlayerEntity spe, int slot, int spellIndex){
        var buf = PacketByteBufs.create();
        buf.writeInt(slot);
        buf.writeInt(spellIndex);
        ServerPlayNetworking.send(spe,ModMessages.CASTER_SPELL_CHANGED,buf);
    }
}
