package org.oxytocina.geomancy.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.util.IEntityDataSaver;
import org.oxytocina.geomancy.util.ManaUtil;

import java.util.Random;

public class PlayerTickHandler implements ServerTickEvents.StartTick {
    @Override
    public void onStartTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if(new Random().nextFloat() <= 0.005f) {
                IEntityDataSaver dataPlayer = ((IEntityDataSaver) player);
                ManaUtil.setMana(dataPlayer,(ManaUtil.getMana(dataPlayer)+1) % 10);
                //player.sendMessage(Text.literal("added mana"));
            }
        }
    }
}
