package org.oxytocina.geomancy.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.items.IManaStoringItem;
import org.oxytocina.geomancy.util.ManaUtil;

import java.util.HashMap;
import java.util.UUID;

public class ManaStoringItemData {

    // used to identify duplicated stacks, to give them a unique UUID
    public final static HashMap<UUID, ItemStack> stackMap = new HashMap<>();

    // client cache for rendering
    public final static HashMap<UUID, ManaStoringItemData> clientMap = new HashMap<UUID, ManaStoringItemData>();

    public float mana = 0;
    public float maxMana = 0;
    public float speedMultiplier = 1;
    public UUID uuid = null;

    public ManaStoringItemData(){

    }

    public ManaStoringItemData(float mana, float maxMana, float speedMultiplier){
        this.mana = mana;
        this.maxMana=maxMana;
        this.speedMultiplier=speedMultiplier;
    }

    public ManaStoringItemData(UUID uuid) {
        this.uuid = uuid;
    }

    public ManaStoringItemData(UUID uuid, ItemStack base){
        this.uuid=uuid;
        mana=((IManaStoringItem)base.getItem()).getInitialMana(base);
        maxMana = ((IManaStoringItem)base.getItem()).getBaseSoulCapacity(base);
    }

    public static ManaStoringItemData fromNbt(NbtCompound nbt){
        ManaStoringItemData res = new ManaStoringItemData();
        res.uuid=nbt.getUuid("uuid");
        res.mana = nbt.getFloat("mana");
        if(Float.isNaN(res.mana))
        {
            Geomancy.logError("mana item data had NaN as mana!");
            res.mana = 0;
        }
        res.maxMana = nbt.getFloat("maxMana");
        res.speedMultiplier = nbt.getFloat("speedMultiplier");
        return res;
    }

    public static ManaStoringItemData fromBuf(PacketByteBuf buf){
        ManaStoringItemData res = new ManaStoringItemData();
        res.uuid = buf.readUuid();
        res.mana = buf.readFloat();
        res.maxMana = buf.readFloat();
        res.speedMultiplier = buf.readFloat();
        return res;
    }

    public static UUID getNextUUID() {
        return UUID.randomUUID();
    }


    public void writeNbt(NbtCompound nbt){
        nbt.putUuid("uuid",uuid);
        nbt.putFloat("mana",mana);
        nbt.putFloat("maxMana",maxMana);
        nbt.putFloat("speedMultiplier",speedMultiplier);
    }

    public void writeBuf(PacketByteBuf buf){
        buf.writeUuid(uuid);
        buf.writeFloat(mana);
        buf.writeFloat(maxMana);
        buf.writeFloat(speedMultiplier);
    }

    public static ManaStoringItemData from(World world, ItemStack stack, UUID uuid){

        if(world.isClient){
            if(clientMap.containsKey(uuid)) return clientMap.get(uuid);
            return new ManaStoringItemData(0,0,0);
        }

        if(stackMap.containsKey(uuid) && stackMap.get(uuid) != stack){
            //duplicate! split into new UUID
            ManaStoringItemData newData = StateSaverAndLoader.getManaStoringItemData(world,uuid,stack).clone();
            newData.uuid = UUID.randomUUID();
            IManaStoringItem.setUUID(stack,newData.uuid);
            stackMap.put(newData.uuid,stack);
            uuid = newData.uuid;
            StateSaverAndLoader.setManaStoringItemData(world,uuid,newData);
            ManaUtil.syncItemMana(world,stack);
            return newData;
        }
        else if(!stackMap.containsKey(uuid)){
            stackMap.put(uuid,stack);
        }

        return StateSaverAndLoader.getManaStoringItemData(world,uuid,stack);
    }

    public ManaStoringItemData clone(){
        return new ManaStoringItemData(mana,maxMana,speedMultiplier);
    }

    @Environment(EnvType.CLIENT)
    public static void setFromBuffer(PacketByteBuf buf){
        ManaStoringItemData newData = fromBuf(buf);

        if(!clientMap.containsKey(newData.uuid))
        {
            clientMap.put(newData.uuid,newData);
        }

        clientMap.get(newData.uuid).mana = newData.mana;
        clientMap.get(newData.uuid).maxMana = newData.maxMana;
        clientMap.get(newData.uuid).speedMultiplier = newData.speedMultiplier;
    }
}