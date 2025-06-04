package org.oxytocina.geomancy.blocks;


import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class ExtraBlockSettings {

    public static final ArrayList<Block> ToolableBlock_Pickaxe = new ArrayList<Block>();
    public static final ArrayList<Block> ToolableBlock_Axe = new ArrayList<Block>();
    public static final ArrayList<Block> ToolableBlock_Shovel = new ArrayList<Block>();
    public static final ArrayList<Block> BlocksInGroup = new ArrayList<Block>();
    public static final ArrayList<Block> SimpleCubeBlocks = new ArrayList<Block>();

    public static final HashMap<Block,Integer> BlockMiningLevels = new HashMap<Block, Integer>();

    public Block block;

    private boolean pickaxe = false;
    private boolean axe = false;
    private boolean shovel = false;

    public boolean shouldRegisterItem = true;
    private boolean shouldAddItemToGroup = true;
    private boolean simpleCubeModel = true;

    private int miningLevel = 0;

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

    public void apply(){
        if(pickaxe) ToolableBlock_Pickaxe.add(block);
        if(axe) ToolableBlock_Axe.add(block);
        if(shovel) ToolableBlock_Shovel.add(block);

        if(shouldAddItemToGroup)
            BlocksInGroup.add(block);
        if(simpleCubeModel)
            SimpleCubeBlocks.add(block);

        BlockMiningLevels.put(block,miningLevel);
    }
}
