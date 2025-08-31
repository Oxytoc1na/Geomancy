package org.oxytocina.geomancy.blocks.blockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface IPedestalListener {
    List<IPedestalListener> LIST = new ArrayList<>();

    static void clear(){
        LIST.clear();
    }

    default void register(){
        LIST.add(this);
    }
    default void destroy(){
        LIST.remove(this);
    }

    static void onPedestalCreated(PedestalBlockEntity pedestal){
        for(var l : LIST)
            l.registerPedestal(pedestal);
    }

    static void onPedestalDestroyed(PedestalBlockEntity pedestal){
        for(var l : LIST)
            l.pedestalRemoved(pedestal);
    }

    void registerPedestal(PedestalBlockEntity pedestal);
    void pedestalRemoved(PedestalBlockEntity pedestal);
    default void registerInArea(World world, BlockPos pos, int range){
        BlockPos.iterateOutwards(pos,range,range,range).forEach(p->{
            if(world.getBlockEntity(p) instanceof PedestalBlockEntity pedestal)
                registerPedestal(pedestal);
        });
    }
}
