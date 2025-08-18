package org.oxytocina.geomancy.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.ChunkLightProvider;

import java.util.EnumSet;

public class GenUtil {

    public static void setState(Chunk chunk, BlockPos pos, BlockState state){
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (j >= chunk.getBottomY() && j < chunk.getTopY()) {
            int l = chunk.getSectionIndex(j);
            ChunkSection chunkSection = chunk.getSection(l);
            boolean bl = chunkSection.isEmpty();
            if (bl && state.isOf(Blocks.AIR)) {
                return;
            } else {
                int m = ChunkSectionPos.getLocalCoord(i);
                int n = ChunkSectionPos.getLocalCoord(j);
                int o = ChunkSectionPos.getLocalCoord(k);
                BlockState blockState = chunkSection.setBlockState(m, n, o, state,false);


                EnumSet<Heightmap.Type> enumSet = chunk.getStatus().getHeightmapTypes();
                EnumSet<Heightmap.Type> enumSet2 = null;

                for(Heightmap.Type type : enumSet) {
                    Heightmap heightmap = chunk.getHeightmap(type);
                    if (heightmap == null) {
                        if (enumSet2 == null) {
                            enumSet2 = EnumSet.noneOf(Heightmap.Type.class);
                        }

                        enumSet2.add(type);
                    }
                }

                if (enumSet2 != null) {
                    Heightmap.populateHeightmaps(chunk, enumSet2);
                }

                for(Heightmap.Type type : enumSet) {
                    (chunk.getHeightmap(type)).trackUpdate(m, j, o, state);
                }

                return;
            }
        } else {
            return;
        }
    }

    public static void fillBox(Chunk chunk, BlockState state, int x1, int y1, int z1, int x2, int y2, int z2){

        BlockPos.Mutable pos = new BlockPos.Mutable();

        if(x2<x1){int t = x1;x1 = x2;x2 = t;}
        if(y2<y1){int t = y1;y1 = y2;y2 = t;}
        if(z2<z1){int t = z1;z1 = z2;z2 = t;}

        for (int ix = x1; ix <= x2; ix++) {
            pos.setX(ix);
            for (int iy = y1; iy <= y2; iy++) {
                pos.setY(iy);
                for (int iz = z1; iz <= z2; iz++) {
                    pos.setZ(iz);
                    setState(chunk,pos,state);
                }
            }
        }
    }

    public static void fillKeepWireframe(Chunk chunk,BlockState state, int x1, int y1, int z1, int x2, int y2, int z2){
        // x
        fillBox(chunk,state,x1,y1,z1,x2,y1,z1);
        fillBox(chunk,state,x1,y1,z2,x2,y1,z2);
        fillBox(chunk,state,x1,y2,z1,x2,y2,z1);
        fillBox(chunk,state,x1,y2,z2,x2,y2,z2);
        // y
        fillBox(chunk,state,x1,y1,z1,x1,y2,z1);
        fillBox(chunk,state,x2,y1,z1,x2,y2,z1);
        fillBox(chunk,state,x1,y1,z2,x1,y2,z2);
        fillBox(chunk,state,x2,y1,z2,x2,y2,z2);
        // z
        fillBox(chunk,state,x1,y1,z1,x1,y1,z2);
        fillBox(chunk,state,x2,y1,z1,x2,y1,z2);
        fillBox(chunk,state,x1,y2,z1,x1,y2,z2);
        fillBox(chunk,state,x2,y2,z1,x2,y2,z2);
    }
}
