package org.oxytocina.geomancy.util;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import java.nio.charset.Charset;

public class ByteUtil {

    public static byte intToByte(int i){
        return (byte)(i);
    }

    public static int byteToInt(byte b){
        return Byte.toUnsignedInt(b);
    }

    public static PacketByteBuf stringToBuf(String s){
        return PacketByteBufs.copy(Unpooled.copiedBuffer(s.getBytes(Charset.defaultCharset())));
    }

    public static String bufToString(PacketByteBuf buf){
        return buf.toString(Charset.defaultCharset());
    }
}
