package org.oxytocina.geomancy.entity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public interface IMobWithHome {
    void setHome(BlockPos newHome);
    BlockPos getHome();

    default void WriteHome(NbtCompound nbt, @NotNull BlockPos home){
        NbtIntArray homeNBT = new NbtIntArray(new int[]{home.getX(),home.getY(),home.getZ()});
        nbt.put("home",homeNBT);
    }

    default BlockPos ReadHome(NbtCompound nbt){
        if(nbt==null) return null;
        if(!nbt.contains("home", NbtElement.INT_ARRAY_TYPE)) return null;

        int[] arr = nbt.getIntArray("home");
        if(arr.length!=3) return null;

        return new BlockPos(arr[0],arr[1],arr[2]);
    }
}
