package org.oxytocina.geomancy.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.oxytocina.geomancy.items.ManaStoringItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.UUID;

public class ManaStoringItemData {

    // used to identify duplicated stacks, to give them a unique UUID
    public final static HashMap<UUID, ItemStack> stackMap = new HashMap<UUID, ItemStack>();

    // client cache for rendering
    public final static HashMap<UUID, ManaStoringItemData> clientMap = new HashMap<UUID, ManaStoringItemData>();

    public float mana = 0;
    public float maxMana = 0;
    public UUID uuid = null;

    public ManaStoringItemData(){

    }

    public ManaStoringItemData(float mana, float maxMana){
        this.mana = mana;
        this.maxMana=maxMana;
    }

    public ManaStoringItemData(UUID uuid) {
        this.uuid = uuid;
    }

    public ManaStoringItemData(UUID uuid, ItemStack base){
        this.uuid=uuid;
        mana=0;
        maxMana = ((ManaStoringItem)base.getItem()).getBaseCapacity(base);
    }

    public static ManaStoringItemData fromNbt(NbtCompound nbt){
        ManaStoringItemData res = new ManaStoringItemData();
        res.uuid=nbt.getUuid("uuid");
        res.mana = nbt.getFloat("mana");
        res.maxMana = nbt.getFloat("maxMana");
        return res;
    }

    public static ManaStoringItemData fromBuf(PacketByteBuf buf){
        ManaStoringItemData res = new ManaStoringItemData();
        res.uuid = buf.readUuid();
        res.mana = buf.readFloat();
        res.maxMana = buf.readFloat();
        return res;
    }


    public void writeNbt(NbtCompound nbt){
        nbt.putUuid("uuid",uuid);
        nbt.putFloat("mana",mana);
        nbt.putFloat("maxMana",maxMana);
    }

    public void writeBuf(PacketByteBuf buf){
        buf.writeUuid(uuid);
        buf.writeFloat(mana);
        buf.writeFloat(maxMana);
    }

    public static ManaStoringItemData from(World world, ItemStack stack, UUID uuid){

        if(world.isClient){
            if(clientMap.containsKey(uuid)) return clientMap.get(uuid);
        }

        if(stackMap.containsKey(uuid) && stackMap.get(uuid) != stack){
            //duplicate! split into new UUID
            ManaStoringItemData newData = StateSaverAndLoader.getManaStoringItemData(world,uuid,stack).clone();
            newData.uuid = UUID.randomUUID();
            ManaStoringItem.setUUID(stack,newData.uuid);
            stackMap.put(newData.uuid,stack);
            uuid = newData.uuid;
            StateSaverAndLoader.setManaStoringItemData(world,uuid,newData);
            return newData;
        }
        else if(!stackMap.containsKey(uuid)){
            stackMap.put(uuid,stack);
        }

        return StateSaverAndLoader.getManaStoringItemData(world,uuid,stack);
    }

    public ManaStoringItemData clone(){
        return new ManaStoringItemData(mana,maxMana);
    }

    public static void setFromBuffer(PacketByteBuf buf){
        ManaStoringItemData newData = fromBuf(buf);

        if(!clientMap.containsKey(newData.uuid))
        {
            clientMap.put(newData.uuid,newData);
        }

        clientMap.get(newData.uuid).mana = newData.mana;
    }
}