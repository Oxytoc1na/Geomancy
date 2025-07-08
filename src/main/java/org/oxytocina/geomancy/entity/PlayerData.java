package org.oxytocina.geomancy.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    public final static HashMap<UUID,PlayerData> clientData = new HashMap<>();

    public float mana = 0;
    public float maxMana = 0;
    public float baseManaRechargeRate = 1;
    public float leadPoisoning = 0;
    public float octanguliteInfluence = 0;

    //public HashMap<Integer, Integer> fatigue = new HashMap<>();

    //public List<Integer> oldCravings = new ArrayList<>();

    public PlayerData(){

    }

    public PlayerData(UUID uuid){

    }

    public static PlayerData fromNbt(NbtCompound nbt){
        PlayerData res = new PlayerData();
        res.mana = nbt.getFloat("mana");
        res.maxMana = nbt.getFloat("maxMana");
        res.leadPoisoning = nbt.getFloat("leadPoisoning");
        res.octanguliteInfluence = nbt.getFloat("octanguliteInfluence");
        res.baseManaRechargeRate = nbt.getFloat("baseManaRechargeRate");
        return res;
    }

    public static PlayerData fromBuf(PacketByteBuf buf){
        PlayerData res = new PlayerData();
        res.mana = buf.readFloat();
        res.maxMana = buf.readFloat();
        res.leadPoisoning = buf.readFloat();
        res.octanguliteInfluence = buf.readFloat();
        res.baseManaRechargeRate = buf.readFloat();
        return res;
    }

    public void writeNbt(NbtCompound nbt){
        nbt.putFloat("mana",mana);
        nbt.putFloat("maxMana",maxMana);
        nbt.putFloat("leadPoisoning",leadPoisoning);
        nbt.putFloat("octanguliteInfluence",octanguliteInfluence);
        nbt.putFloat("baseManaRechargeRate",baseManaRechargeRate);
    }

    public void writeBuf(PacketByteBuf buf){
        buf.writeFloat(mana);
        buf.writeFloat(maxMana);
        buf.writeFloat(leadPoisoning);
        buf.writeFloat(octanguliteInfluence);
        buf.writeFloat(baseManaRechargeRate);
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