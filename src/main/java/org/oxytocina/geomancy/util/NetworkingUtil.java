package org.oxytocina.geomancy.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;

public class NetworkingUtil {
    public static BlockState readState(PacketByteBuf buf){
        try{
            var string = buf.readString();
            return NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(),NbtHelper.fromNbtProviderString(string));

        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public static void writeState(PacketByteBuf buf,BlockState state){
        buf.writeString(NbtHelper.fromBlockState(state).asString());
    }
}
