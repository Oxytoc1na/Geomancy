package org.oxytocina.geomancy.spells.individual;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.spells.*;
import org.oxytocina.geomancy.util.BlockHelper;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class DegradeSpell {

    private static final HashMap<Function<BlockState,Boolean>, BlockState> degradeBlockData = new LinkedHashMap<>();

    public static SpellBlock get() {
        // degrade block data
        {
            addDegradeBlockData(Blocks.COBWEB,Blocks.TRIPWIRE);
            addDegradeBlockData(Blocks.TNT,Blocks.SAND);
            addDegradeBlockData(Blocks.COBBLED_DEEPSLATE,Blocks.STONE);
            addDegradeBlockData(Blocks.COBBLESTONE,Blocks.GRAVEL);
            addDegradeBlockData(Blocks.GRAVEL,Blocks.SAND);
            addDegradeBlockData(Blocks.SAND,Blocks.DIRT);
            addDegradeBlockData(Blocks.ANVIL,Blocks.CHIPPED_ANVIL);
            addDegradeBlockData(Blocks.CHIPPED_ANVIL,Blocks.DAMAGED_ANVIL);
            addDegradeBlockData(Blocks.DAMAGED_ANVIL,Blocks.AIR);
            addDegradeBlockData(Blocks.BOOKSHELF,Blocks.CHISELED_BOOKSHELF);
            // ore blocks to ores
            addDegradeBlockData(Blocks.COAL_BLOCK,Blocks.COAL_ORE);
            addDegradeBlockData(Blocks.IRON_BLOCK,Blocks.IRON_ORE);
            addDegradeBlockData(Blocks.GOLD_BLOCK,Blocks.GILDED_BLACKSTONE);
            addDegradeBlockData(Blocks.REDSTONE_BLOCK,Blocks.REDSTONE_ORE);
            addDegradeBlockData(Blocks.LAPIS_BLOCK,Blocks.LAPIS_ORE);
            addDegradeBlockData(Blocks.COPPER_BLOCK,Blocks.COPPER_ORE);
            addDegradeBlockData(Blocks.DIAMOND_BLOCK,Blocks.DIAMOND_ORE);
            addDegradeBlockData(Blocks.DIAMOND_ORE,Blocks.COAL_ORE);
            addDegradeBlockData(Blocks.DEEPSLATE_DIAMOND_ORE,Blocks.DEEPSLATE_COAL_ORE);
            addDegradeBlockData(Blocks.EMERALD_BLOCK,Blocks.EMERALD_ORE);
            addDegradeBlockData(Blocks.QUARTZ_BLOCK,Blocks.NETHER_QUARTZ_ORE);
            addDegradeBlockData(ModBlocks.LEAD_BLOCK,ModBlocks.LEAD_ORE);
            addDegradeBlockData(ModBlocks.TITANIUM_BLOCK,ModBlocks.TITANIUM_ORE);
            addDegradeBlockData(ModBlocks.MOLYBDENUM_BLOCK,ModBlocks.MOLYBDENUM_ORE);
            addDegradeBlockData(ModBlocks.MITHRIL_BLOCK,ModBlocks.MITHRIL_ORE);
            addDegradeBlockData(ModBlocks.OCTANGULITE_BLOCK,ModBlocks.OCTANGULITE_ORE);
            addDegradeBlockData(ModBlocks.TOURMALINE_BLOCK,ModBlocks.TOURMALINE_ORE);
            addDegradeBlockData(ModBlocks.ORTHOCLASE_BLOCK,ModBlocks.ORTHOCLASE_ORE);
            addDegradeBlockData(ModBlocks.AXINITE_BLOCK,ModBlocks.AXINITE_ORE);
            addDegradeBlockData(ModBlocks.PERIDOT_BLOCK,ModBlocks.PERIDOT_ORE);

            addDegradeBlockData(Blocks.STONE,Blocks.COBBLESTONE);
            addDegradeBlockData(b->b.isIn(BlockTags.STONE_ORE_REPLACEABLES),Blocks.STONE.getDefaultState());
            addDegradeBlockData(Blocks.DEEPSLATE,Blocks.COBBLED_DEEPSLATE);
            addDegradeBlockData(b->b.isIn(BlockTags.DEEPSLATE_ORE_REPLACEABLES),Blocks.DEEPSLATE.getDefaultState());

            // ores to stone
            addDegradeBlockData(b-> Registries.BLOCK.getId(b.getBlock()).getPath().contains("_ore") && Registries.BLOCK.getId(b.getBlock()).getPath().contains("deepslate"),Blocks.DEEPSLATE.getDefaultState());
            addDegradeBlockData(b->Registries.BLOCK.getId(b.getBlock()).getPath().contains("_ore"),Blocks.STONE.getDefaultState());
        }
        return SpellBlock.Builder.create("degrade_block")
                .inputs(
                        SpellSignal.createVector().named("position")
                )
                .func((comp,vars) -> {
                    if(!(comp.world() instanceof ServerWorld sw)) return SpellBlockResult.empty(); // not in a server world
                    var pos = vars.getVector("position");
                    var blockPos = Toolbox.posToBlockPos(pos);
                    var restrictions = RestrictorBlockEntity.getRestrictionsAt(pos,comp.world());
                    if(!restrictions.allowsBlockManipulation() && !comp.context.isFromPrecomiled()){
                        // not allowed to place here! punish!
                        punishDisallowedAction(comp.context);
                        return SpellBlockResult.empty();
                    }

                    // calculate breaking cost
                    BlockState targetState = comp.world().getBlockState(blockPos);

                    float manaCost = 1f
                            +targetState.getBlock().getHardness()/10f
                            +normalCastOffsetSoulCost(comp,pos);

                    if(canAfford(comp,manaCost)){

                        // special interactions
                        for(var predicate : degradeBlockData.keySet())
                            if(predicate.apply(targetState))
                            {
                                if (!BlockHelper.replaceBlock(comp.world(),blockPos,degradeBlockData.get(predicate))) {
                                    // couldnt replace
                                    tryLogDebugNotBreakable(comp,targetState);
                                    return SpellBlockResult.empty();
                                }

                                trySpendSoul(comp,manaCost);
                                spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastSuccess(comp,pos));
                                return SpellBlockResult.empty();
                            }

                        // replace with mined variant
                        {
                            ItemStack stack = new ItemStack(Items.DIRT);
                            if(targetState.isToolRequired()){
                                if(targetState.isIn(BlockTags.PICKAXE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_PICKAXE);
                                else if(targetState.isIn(BlockTags.AXE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_AXE);
                                else if(targetState.isIn(BlockTags.SHOVEL_MINEABLE)) stack = new ItemStack(Items.NETHERITE_SHOVEL);
                                else if(targetState.isIn(BlockTags.HOE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_HOE);
                                else if(targetState.isIn(BlockTags.SWORD_EFFICIENT)) stack = new ItemStack(Items.NETHERITE_SWORD);
                            }
                            final ItemStack s2 = stack.copy();
                            Predicate<BlockState> minableBlocksPredicate = s -> !s.isToolRequired() || s2.isSuitableFor(s);
                            if (!minableBlocksPredicate.test(targetState)) {
                                // couldnt mine
                                tryLogDebugNotBreakable(comp,targetState);
                                return SpellBlockResult.empty();
                            }

                            // fetch replacement state
                            var droppedStacks = Block.getDroppedStacks(targetState,sw,blockPos,comp.context.casterBlock,comp.caster(),stack);
                            BlockState replacementState = Blocks.AIR.getDefaultState();
                            for(var droppedStack:droppedStacks){
                                if(!(droppedStack.getItem() instanceof BlockItem bi)) continue;
                                replacementState = bi.getBlock().getDefaultState();
                                break;
                            }

                            // replacing a block with itself, unnecessary
                            if(targetState.isOf(replacementState.getBlock()))
                            {
                                return SpellBlockResult.empty();
                            }

                            if (!BlockHelper.replaceBlock(comp.world(),blockPos,replacementState)) {
                                // couldnt replace
                                tryLogDebugNotBreakable(comp,targetState);
                                return SpellBlockResult.empty();
                            }

                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastSuccess(comp,pos));
                        }
                    }
                    else{
                        // too broke
                        tryLogDebugBroke(comp,manaCost);
                        spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,pos));
                    }

                    return SpellBlockResult.empty();
                })
                .category(SpellBlock.Category.Effector).build();
    }

    public static void addDegradeBlockData(Block b, Block replacement){
        addDegradeBlockData(b.getDefaultState(),replacement);
    }
    public static void addDegradeBlockData(BlockState b, Block replacement){
        addDegradeBlockData(b1->b1.isOf(b.getBlock()),replacement.getDefaultState());
    }
    public static void addDegradeBlockData(Function<BlockState,Boolean> predicate, BlockState replacement){
        degradeBlockData.put(predicate,replacement);
    }
}
