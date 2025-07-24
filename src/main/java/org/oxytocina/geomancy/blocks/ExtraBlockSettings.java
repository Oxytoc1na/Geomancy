package org.oxytocina.geomancy.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class ExtraBlockSettings {

    public static final ArrayList<Block> ToolableBlock_Pickaxe = new ArrayList<Block>();
    public static final ArrayList<Block> ToolableBlock_Axe = new ArrayList<Block>();
    public static final ArrayList<Block> ToolableBlock_Shovel = new ArrayList<Block>();
    public static final ArrayList<Block> BlocksInGroup = new ArrayList<Block>();

    public static final ArrayList<Block> SimpleCubeBlocks = new ArrayList<Block>();
    public static final HashMap<StairsBlock,Block> StairsBlocks = new HashMap<>();
    public static final HashMap<SlabBlock,Block> SlabBlocks = new HashMap<>();

    public static final ArrayList<Block> RegularDropBlocks = new ArrayList<Block>();
    public static final HashMap<Block,Integer> VariantCubeBlocks = new HashMap<Block,Integer>();
    public static final HashMap<Block,Integer> VariantCubeColumnBlocks = new HashMap<Block,Integer>();

    public static final HashMap<Block,Integer> BlockMiningLevels = new HashMap<Block, Integer>();

    public Block block;
    private Block variantBaseBlock;

    private boolean pickaxe = false;
    private boolean axe = false;
    private boolean shovel = false;

    public boolean shouldRegisterItem = true;
    private boolean shouldAddItemToGroup = true;
    private boolean simpleCubeModel = true;
    private boolean regularDrop = true;

    private int miningLevel = 0;
    private int textureVariants = 0;
    private boolean variantCube;
    private boolean variantCubeColumn;

    public ExtraBlockSettings(){

    }

    public static ExtraBlockSettings create(){
        return new ExtraBlockSettings();
    }

    public ExtraBlockSettings setBlock(Block block){
        this.block=block;
        return this;
    }

    public ExtraBlockSettings mineableByPickaxe(){pickaxe=true; return this;}
    public ExtraBlockSettings mineableByAxe(){axe=true; return this;}
    public ExtraBlockSettings mineableByShovel(){shovel=true; return this;}
    public ExtraBlockSettings dontRegisterItem(){shouldRegisterItem=false; return this;}
    public ExtraBlockSettings dontGroupItem(){shouldAddItemToGroup=false; return this;}
    public ExtraBlockSettings notSimpleCube(){simpleCubeModel=false; return this;}
    public ExtraBlockSettings miningLevel(int level){miningLevel=level; return this;}
    public ExtraBlockSettings hasTextureVariants(int count){textureVariants=count; variantCube = true; notSimpleCube(); return this;}
    public ExtraBlockSettings hasTextureVariantsColumn(int count){textureVariants=count; variantCubeColumn = true; notSimpleCube(); return this;}
    public ExtraBlockSettings fluid() { return notSimpleCube().dontGroupItem(); }
    public ExtraBlockSettings notRegularDrop() { regularDrop = false; return this; }
    public ExtraBlockSettings stairs(Block base) { variantBaseBlock = base; return notSimpleCube(); }
    public ExtraBlockSettings slab(Block base) { variantBaseBlock = base; return notSimpleCube(); }

    public void apply(){
        if(pickaxe) ToolableBlock_Pickaxe.add(block);
        if(axe) ToolableBlock_Axe.add(block);
        if(shovel) ToolableBlock_Shovel.add(block);

        if(block instanceof StairsBlock sb)
        {StairsBlocks.put(sb,variantBaseBlock); simpleCubeModel=false;}
        if(block instanceof SlabBlock sb)
        {SlabBlocks.put(sb,variantBaseBlock); simpleCubeModel=false;}

        if(shouldAddItemToGroup)
            BlocksInGroup.add(block);
        if(simpleCubeModel)
            SimpleCubeBlocks.add(block);

        if(variantCube)
            VariantCubeBlocks.put(block,textureVariants);
        if(variantCubeColumn)
            VariantCubeColumnBlocks.put(block,textureVariants);
        if(regularDrop)
            RegularDropBlocks.add(block);

        BlockMiningLevels.put(block,miningLevel);
    }
}
