package org.oxytocina.geomancy.networking.packet.S2C;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.Toolbox;

public class WhisperS2CPacket {
    @Environment(EnvType.CLIENT)
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        float volume = buf.readFloat();
        client.execute(()->{
            if(client.player==null) return;
            Toolbox.playSoundClient(ModSoundEvents.WHISPERS,client.player.getWorld(),pos, SoundCategory.AMBIENT,volume* Geomancy.CONFIG.whisperVolume.value(),Toolbox.randomPitch());
        });
    }
}
