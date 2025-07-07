package org.oxytocina.geomancy.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.networking.ModMessages;

public class ManaUtil {
    public static void addMana(PlayerEntity player, int amount){
        setMana(player,getMana(player)+amount);
    }

    public static boolean setMana(PlayerEntity player, int amount){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        int old = data.mana;
        int mana = Toolbox.clampI(amount,0,data.maxMana);
        if(old==mana) return false;
        data.mana = mana;
        syncMana(player);
        return true;
    }

    //public static boolean setMana(IEntityDataSaver player, int amount){
    //    if(player==null) return false;
    //    NbtCompound nbt = player.getPersistentData();
    //    int old = getMana(player);
    //    int mana = Toolbox.clampI(amount,0,getMaxMana(player));
    //    if(old==mana) return false;
    //    nbt.putInt("mana",mana);
    //    syncMana(player);
    //    return true;
    //}

    public static int getMana(PlayerEntity player){
        return PlayerData.from(player).mana;
    }

    //public static int getMana(IEntityDataSaver player){
    //    return player.getPersistentData().getInt("mana");
    //}

    public static int getMaxMana(IEntityDataSaver player){
        var nbt = player.getPersistentData();
        return nbt.contains("maxMana", NbtElement.INT_TYPE) ? nbt.getInt("maxMana") : 100;
    }

    public static void syncMana(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity spe)) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(getMana(player));
        ServerPlayNetworking.send(spe,ModMessages.MANA_SYNC,buf);
    }
}
