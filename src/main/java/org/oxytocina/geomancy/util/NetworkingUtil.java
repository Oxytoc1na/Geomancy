package org.oxytocina.geomancy.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;

public class NetworkingUtil {
    public static BlockState readState(PacketByteBuf buf){
        return parseBlockState(buf.readString());
    }

    public static void writeState(PacketByteBuf buf,BlockState state){
        buf.writeString(serializeBlockState(state));
    }

    public static String serializeBlockState(BlockState state){
        return NbtHelper.fromBlockState(state).asString();
    }

    public static BlockState parseBlockState(String string){
        try{
            return NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(),NbtHelper.fromNbtProviderString(string));

        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
