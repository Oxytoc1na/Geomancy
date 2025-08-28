package org.oxytocina.geomancy.world.dimension;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class MazeUtil {

    public static final int SECTION_SIZE = 16;

    public static final Vec3i[] checkOffsets = new Vec3i[]{
            new Vec3i(SECTION_SIZE/2,-1,SECTION_SIZE/2),  //down
            new Vec3i(SECTION_SIZE/2,SECTION_SIZE,SECTION_SIZE/2),  //up
            new Vec3i(SECTION_SIZE/2,SECTION_SIZE/2,-1),  //north
            new Vec3i(SECTION_SIZE/2,SECTION_SIZE/2,SECTION_SIZE),  //south
            new Vec3i(-1,SECTION_SIZE/2,SECTION_SIZE/2),  //west
            new Vec3i(SECTION_SIZE,SECTION_SIZE/2,SECTION_SIZE/2),  //east
    };

    public static void generateSection(World world, BlockPos origin, int recursion){

        BlockPos end = origin.add(SECTION_SIZE-1,SECTION_SIZE-1,SECTION_SIZE-1);

        // fetch existing section data
        int[] sectionsPresent = new int[6];
        for (int i = 0; i < 6; i++) {
            var checkPos = origin.add(checkOffsets[i]);
            sectionsPresent[i] = world.isPosLoaded(checkPos.getX(),checkPos.getZ())
            ? (world.getBlockState(checkPos).isAir() ? 0 : 1) : -1
            ;
        }

        // TODO pick fitting cell to place here based on other existing cells
        fillStates(world,origin,end,Blocks.GLOWSTONE.getDefaultState());

        // fill wireframe
        BlockState frameState = Blocks.STONE.getDefaultState();
        fillWireframe(world,origin,end,frameState);

        if(recursion>0){
            for (int i = 0; i < 6; i++) {
                var newPos = origin.add(Direction.byId(i).getVector().multiply(SECTION_SIZE));
                generateSection(world,newPos,recursion-1);
            }
        }
    }

    public static void setState(World world, BlockPos pos, BlockState state){
        world.setBlockState(pos,state,2,0);
    }

    public static void fillStates(World world, BlockPos pos1, BlockPos pos2, BlockState state){
        BlockPos.Mutable p = new BlockPos.Mutable();
        int w = Math.max(pos2.getX(),pos1.getX());
        int h = Math.max(pos2.getY(),pos1.getY());
        int d = Math.max(pos2.getZ(),pos1.getZ());
        int x = Math.max(pos2.getX(),pos1.getX());
        int y = Math.max(pos2.getY(),pos1.getY());
        int z = Math.max(pos2.getZ(),pos1.getZ());

        for (int ix = x; ix <= w; ix++) {
            p.setX(ix);
            for (int iy = y; iy <= h; iy++) {
                p.setY(iy);
                for (int iz = z; iz <= d; iz++) {
                    p.setZ(iz);
                    setState(world,p,state);
                }
            }
        }
    }

    public static void fillWireframe(World world,BlockPos pos000, BlockPos pos111,BlockState state){
        var pos001 = new BlockPos(pos000.getX(),pos000.getY(),pos111.getZ());
        var pos010 = new BlockPos(pos000.getX(),pos111.getY(),pos000.getZ());
        var pos011 = new BlockPos(pos000.getX(),pos111.getY(),pos111.getZ());
        var pos100 = new BlockPos(pos111.getX(),pos000.getY(),pos000.getZ());
        var pos101 = new BlockPos(pos111.getX(),pos000.getY(),pos111.getZ());
        var pos110 = new BlockPos(pos111.getX(),pos111.getY(),pos000.getZ());

        fillStates(world,pos000,pos100,state);
        fillStates(world,pos001,pos101,state);
        fillStates(world,pos010,pos110,state);
        fillStates(world,pos011,pos111,state);

        fillStates(world,pos000,pos010,state);
        fillStates(world,pos001,pos011,state);
        fillStates(world,pos100,pos110,state);
        fillStates(world,pos101,pos111,state);

        fillStates(world,pos000,pos001,state);
        fillStates(world,pos010,pos011,state);
        fillStates(world,pos100,pos101,state);
        fillStates(world,pos110,pos111,state);
    }
}
