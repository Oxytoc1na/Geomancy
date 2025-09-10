package org.oxytocina.geomancy.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.Geomancy;

public class EnlightenmentUtil {
    public static int getEnlightenmentServer(ServerPlayerEntity spe){
        int res = 0;
        for (int i = 1; i <= 5; i++)
            if(AdvancementHelper.hasAdvancementServer(spe, Geomancy.locate("spells/simple_enlightenment_"+i)))
                res++;
        return res;
    }

    public static boolean isFullyEnlightenedServer(ServerPlayerEntity spe){
        return AdvancementHelper.hasAdvancementServer(spe, Geomancy.locate("spells/simple_enlightenment_5"));
    }

    @Environment(EnvType.CLIENT)
    public static int getEnlightenmentClient(){
        int res = 0;
        for (int i = 1; i <= 5; i++)
            if(AdvancementHelper.hasAdvancementClient(MinecraftClient.getInstance().player, Geomancy.locate("spells/simple_enlightenment_"+i)))
                res++;
        return res;
    }

    @Environment(EnvType.CLIENT)
    public static boolean isFullyEnlightenedClient(){
        return AdvancementHelper.hasAdvancementClient(MinecraftClient.getInstance().player, Geomancy.locate("spells/simple_enlightenment_5"));
    }
}
