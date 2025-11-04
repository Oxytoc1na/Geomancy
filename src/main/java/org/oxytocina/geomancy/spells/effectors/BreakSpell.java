package org.oxytocina.geomancy.spells.effectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellSignal;
import org.oxytocina.geomancy.util.*;

import java.util.function.Predicate;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class BreakSpell {
    public static SpellBlock get(){
        return SpellBlock.Builder.create("break")
                .inputs(
                        SpellSignal.createVector().named("position"),
                        SpellSignal.createBoolean(true).named("silk touch"),
                        SpellSignal.createBoolean(true).named("autocollect")
                )
                .func((comp,vars) -> {
                    var pos = vars.getVector("position");
                    var silkTouch = vars.getBoolean("silk touch");
                    var autocollect = vars.getBoolean("autocollect");
                    var blockPos = Toolbox.posToBlockPos(pos);
                    var restrictions = RestrictorBlockEntity.getRestrictionsAt(pos,comp.world());
                    if(!restrictions.allowsBlockManipulation() && !comp.context.isFromPrecomiled()){
                        // not allowed to place here! punish!
                        punishDisallowedAction(comp.context);
                        return SpellBlockResult.empty();
                    }

                    // calculate breaking cost
                    BlockState targetState = comp.world().getBlockState(blockPos);

                    float manaCost = 0.2f
                            +targetState.getBlock().getHardness()/5f* (silkTouch?2:1)
                            +(autocollect?0.2f:0f)
                            +normalCastOffsetSoulCost(comp,pos);

                    if(canAfford(comp,manaCost)){

                        ItemStack stack = new ItemStack(Items.DIRT);
                        if(targetState.isToolRequired()){
                            if(targetState.isIn(BlockTags.PICKAXE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_PICKAXE);
                            else if(targetState.isIn(BlockTags.AXE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_AXE);
                            else if(targetState.isIn(BlockTags.SHOVEL_MINEABLE)) stack = new ItemStack(Items.NETHERITE_SHOVEL);
                            else if(targetState.isIn(BlockTags.HOE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_HOE);
                            else if(targetState.isIn(BlockTags.SWORD_EFFICIENT)) stack = new ItemStack(Items.NETHERITE_SWORD);
                        }

                        if(silkTouch)
                            stack.addEnchantment(Enchantments.SILK_TOUCH,1);
                        else if(comp.context.hasCasterItem())
                            stack.addEnchantment(Enchantments.FORTUNE, EnchantmentHelper.getLevel(Enchantments.FORTUNE,comp.context.casterItem));

                        final ItemStack s2 = stack.copy();

                        Predicate<BlockState> minableBlocksPredicate = s -> s.getBlock().getHardness()>=0&&(!s.isToolRequired() || s2.isSuitableFor(s));

                        if (!minableBlocksPredicate.test(targetState)) {
                            // couldnt mine
                            tryLogDebugNotBreakable(comp,targetState);
                            return SpellBlockResult.empty();
                        }

                        PlayerEntity pe = switch(comp.context.sourceType){
                            case Caster -> (PlayerEntity) comp.caster();
                            case Delegate -> (PlayerEntity) comp.caster();
                            default->null;
                        };

                        boolean broke = BlockHelper.breakBlock(pe,stack,comp.world(),blockPos,minableBlocksPredicate,!autocollect);

                        if(!broke){
                            // couldnt mine... again?
                            tryLogDebugNotBreakable(comp,targetState);
                            return SpellBlockResult.empty();
                        }

                        if(autocollect){
                            // give the caster the drops
                            var stacks = Block.getDroppedStacks(targetState,(ServerWorld)comp.world(),blockPos,comp.world().getBlockEntity(blockPos),comp.caster(),stack);
                            var casterPos = comp.context.getOriginPos();
                            var inv = comp.context.getInventory();
                            for(var s : stacks){
                                s = InventoryUtil.tryInsert(inv,s);
                                if(s.isEmpty()) continue;

                                ItemEntity ie = new ItemEntity(comp.world(),casterPos.x,casterPos.y,casterPos.z,s);
                                comp.world().spawnEntity(ie);
                            }
                        }

                        if(comp.caster()!=null && EntityUtil.distanceTo(comp.caster(),pos) >7)
                            tryUnlockSpellAdvancement(comp,"long_arms");
                        trySpendSoul(comp,manaCost);
                        spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,pos));
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
}
