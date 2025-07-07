package org.oxytocina.geomancy.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    public final static HashMap<UUID,PlayerData> clientData = new HashMap<>();

    public int mana = 0;
    public int maxMana = 100;

    //public HashMap<Integer, Integer> fatigue = new HashMap<>();

    //public List<Integer> oldCravings = new ArrayList<>();

    public PlayerData(){

    }

    public static PlayerData fromNbt(NbtCompound nbt){
        PlayerData res = new PlayerData();
        res.mana = nbt.getInt("mana");
        res.maxMana = nbt.getInt("maxMana");
        return res;
    }

    public static PlayerData fromBuf(PacketByteBuf buf){
        PlayerData res = new PlayerData();
        res.mana = buf.readInt();
        res.maxMana = buf.readInt();
        return res;
    }

    public void writeNbt(NbtCompound nbt){
        nbt.putInt("mana",mana);
        nbt.putInt("maxMana",maxMana);
    }

    public void writeBuf(PacketByteBuf buf){
        buf.writeInt(mana);
        buf.writeInt(maxMana);
    }

    public static PlayerData from(PlayerEntity entity){
        return StateSaverAndLoader.getPlayerState(entity);
    }

    public static PlayerData getOrCreate(UUID uuid){
        if(!clientData.containsKey(uuid))
            clientData.put(uuid,new PlayerData());
        return clientData.get(uuid);
    }

    public static void setClientData(UUID uuid, PlayerData data){
        clientData.put(uuid,data);
    }
}