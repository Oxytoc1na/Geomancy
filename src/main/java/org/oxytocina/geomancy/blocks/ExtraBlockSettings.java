package org.oxytocina.geomancy.blocks;


import it.unimi.dsi.fastutil.Hash;
import net.minecraft.block.*;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class ExtraBlockSettings {

    public static final HashMap<Block,ExtraBlockSettings> logged = new HashMap<>();

    public static final ArrayList<Block> ToolableBlock_Pickaxe = new ArrayList<Block>();
    public static final ArrayList<Block> ToolableBlock_Axe = new ArrayList<Block>();
    public static final ArrayList<Block> ToolableBlock_Shovel = new ArrayList<Block>();
    public static final ArrayList<Block> BlocksInGroup = new ArrayList<Block>();

    public static final ArrayList<Block> SimpleCubeBlocks = new ArrayList<Block>();
    public static final HashMap<StairsBlock,Block> StairsBlocks = new HashMap<>();
    public static final HashMap<SlabBlock,Block> SlabBlocks = new HashMap<>();
    public static final HashMap<WallBlock,Block> WallBlocks = new HashMap<>();
    public static final HashMap<PressurePlateBlock,Block> PressurePlateBlocks = new HashMap<>();
    public static final HashMap<ButtonBlock,Block> ButtonBlocks = new HashMap<>();
    public static final HashMap<FenceBlock,Block> FenceBlocks = new HashMap<>();
    public static final HashMap<FenceGateBlock,Block> FenceGateBlocks = new HashMap<>();
    public static final ArrayList<PillarBlock> PillarBlocks = new ArrayList<>();

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
    public boolean shouldGenerateModels = true;
    private boolean shouldAddItemToGroup = true;
    private boolean simpleCubeModel = true;
    private boolean regularDrop = true;

    private int miningLevel = 0;
    private int textureVariants = 0;
    private boolean variantCube;
    private boolean variantCubeColumn;

    public ExtraBlockSettings(){

    }

    public ExtraBlockSettings copy(){
        ExtraBlockSettings res = new ExtraBlockSettings();
        res.block=block;
        res.variantBaseBlock=variantBaseBlock;
        res.pickaxe=pickaxe;
        res.axe=axe;
        res.shovel=shovel;
        res.shouldRegisterItem=shouldRegisterItem;
        res.shouldAddItemToGroup=shouldAddItemToGroup;
        res.simpleCubeModel=simpleCubeModel;
        res.regularDrop=regularDrop;
        res.miningLevel=miningLevel;
        res.textureVariants=textureVariants;
        res.variantCube=variantCube;
        res.variantCubeColumn=variantCubeColumn;
        res.shouldGenerateModels=shouldGenerateModels;
        return res;
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
    public ExtraBlockSettings wall(Block base) { variantBaseBlock = base; return notSimpleCube(); }
    public ExtraBlockSettings fence(Block base) { variantBaseBlock = base; return notSimpleCube(); }
    public ExtraBlockSettings fenceGate(Block base) { variantBaseBlock = base; return notSimpleCube(); }
    public ExtraBlockSettings pressurePlate(Block base) { variantBaseBlock = base; return notSimpleCube(); }
    public ExtraBlockSettings button(Block base) { variantBaseBlock = base; return notSimpleCube(); }
    public ExtraBlockSettings noModels() { shouldGenerateModels=false; return this; }

    public void apply(){
        if(pickaxe) ToolableBlock_Pickaxe.add(block);
        if(axe) ToolableBlock_Axe.add(block);
        if(shovel) ToolableBlock_Shovel.add(block);

        if(block instanceof StairsBlock sb)
        {StairsBlocks.put(sb,variantBaseBlock); simpleCubeModel=false;}
        else if(block instanceof SlabBlock sb)
        {SlabBlocks.put(sb,variantBaseBlock); simpleCubeModel=false;}
        else if(block instanceof WallBlock sb)
        {WallBlocks.put(sb,variantBaseBlock); simpleCubeModel=false;}
        else if(block instanceof PressurePlateBlock sb)
        {PressurePlateBlocks.put(sb,variantBaseBlock); simpleCubeModel=false;}
        else if(block instanceof ButtonBlock sb)
        {ButtonBlocks.put(sb,variantBaseBlock); simpleCubeModel=false;}
        else if(block instanceof FenceBlock sb)
        {FenceBlocks.put(sb,variantBaseBlock); simpleCubeModel=false;}
        else if(block instanceof FenceGateBlock sb)
        {FenceGateBlocks.put(sb,variantBaseBlock); simpleCubeModel=false;}
        else if(block instanceof PillarBlock sb)
        {PillarBlocks.add(sb); simpleCubeModel=false;}

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
        logged.put(block,this);
    }

    public static ExtraBlockSettings copyFrom(Block block){
        return logged.get(block);
    }
}
