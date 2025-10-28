package org.oxytocina.geomancy.spells.individual;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.spells.*;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class IgniteSpell{

    private static final HashMap<Function<BlockState,Boolean> , BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult>> igniteBehavior = new LinkedHashMap<>();

    public static SpellBlock get() {
        // ignite behaviors
        {
            BiConsumer<World, BlockPos> playUseSound = (World world, BlockPos pos) -> Toolbox.playSound(SoundEvents.ITEM_FIRECHARGE_USE,world,pos, SoundCategory.BLOCKS,0.2f,0.8f+Toolbox.random.nextFloat()*0.4f);

            addIgniteBehavior(b->b.isOf(Blocks.FURNACE),(comp, vars)->{
                var be = ((FurnaceBlockEntity)(comp.world().getBlockEntity(vars.getBlockPos("position"))));
                if(be.burnTime<800) {be.burnTime=800;be.fuelTime=800;be.markDirty();}
                playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                return SpellBlockResult.empty();
            });
            addIgniteBehavior(b->b.isOf(Blocks.BLAST_FURNACE),(comp,vars)->{
                var be = ((BlastFurnaceBlockEntity)(comp.world().getBlockEntity(vars.getBlockPos("position"))));
                if(be.burnTime<800) {be.burnTime=800;be.fuelTime=800;be.markDirty();}
                playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                return SpellBlockResult.empty();
            });
            addIgniteBehavior(b->b.isIn(BlockTags.STONE_ORE_REPLACEABLES)||b.isIn(BlockTags.DEEPSLATE_ORE_REPLACEABLES),(comp, vars)->{
                comp.world().setBlockState(vars.getBlockPos("position"),Blocks.MAGMA_BLOCK.getDefaultState());
                playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                return SpellBlockResult.empty();
            });
            addIgniteBehavior(b->b.isOf(Blocks.MAGMA_BLOCK),(comp,vars)->{
                comp.world().setBlockState(vars.getBlockPos("position"),Blocks.LAVA.getDefaultState());
                playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                return SpellBlockResult.empty();
            });
            addIgniteBehavior(b->b.getBlock() instanceof CandleBlock,(comp, vars)->{
                comp.world().setBlockState(vars.getBlockPos("position"),comp.world().getBlockState(vars.getBlockPos("position")).with(CandleBlock.LIT, true), 11);
                playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                return SpellBlockResult.empty();
            });
            addIgniteBehavior(b->b.getBlock() instanceof CampfireBlock,(comp, vars)->{
                comp.world().setBlockState(vars.getBlockPos("position"),comp.world().getBlockState(vars.getBlockPos("position")).with(CampfireBlock.LIT, true), 11);
                playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                return SpellBlockResult.empty();
            });
            addIgniteBehavior(b->b.isReplaceable(),(comp,vars)->{
                var pos = vars.getBlockPos("position");
                var state = comp.world().getBlockState(pos);
                if(!state.isReplaceable()) return SpellBlockResult.empty();
                comp.world().setBlockState(pos,Blocks.FIRE.getDefaultState());
                playUseSound.accept(comp.world(),pos);
                return SpellBlockResult.empty();
            });
            addIgniteBehavior(b->true,(comp,vars)->{
                var pos = vars.getBlockPos("position");
                for(var dir : Direction.values())
                {
                    var pos2 = pos.add(dir.getOffsetX(),dir.getOffsetY(),dir.getOffsetZ());
                    if(!comp.world().getBlockState(pos2).isReplaceable()) continue;
                    comp.world().setBlockState(pos2,Blocks.FIRE.getDefaultState());
                }
                playUseSound.accept(comp.world(),pos);
                return SpellBlockResult.empty();
            });
        }
        return SpellBlock.Builder.create("ignite")
                .inputs(
                        SpellSignal.createVector().named("position"),
                        SpellSignal.createUUID().named("entity")
                )
                .fireCondition(SpellBlocks2::allAndOneOfPositionOrEntity)
                .func((comp,vars) -> {
                    var entity = vars.has("entity") ? vars.get("entity").getEntity(comp.world()) : null;
                    var pos = vars.has("position") ? vars.getVector("position") : entity!=null?entity.getPos() :null;
                    if(pos==null) return SpellBlockResult.empty();
                    boolean entityMode = entity!=null;
                    if(!(entity instanceof LivingEntity) && entityMode) return SpellBlockResult.empty();
                    LivingEntity targetEntity = (LivingEntity) entity;
                    var blockPos = Toolbox.posToBlockPos(pos);
                    var restrictions = RestrictorBlockEntity.getRestrictionsAt(pos,comp.world());
                    if(!entityMode && !restrictions.allowsBlockManipulation() && !comp.context.isFromPrecomiled()){
                        // not allowed to place here! punish!
                        punishDisallowedAction(comp.context);
                        return SpellBlockResult.empty();
                    }
                    BlockState targetState = comp.world().getBlockState(blockPos);
                    // calculate breaking cost
                    float manaCost = 30f
                            +normalCastOffsetSoulCost(comp,pos);

                    if(canAfford(comp,manaCost)){
                        if(entityMode){
                            targetEntity.setOnFireFromLava();
                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,pos));
                            tryUnlockSpellAdvancement(comp,"ignition");
                        }
                        else
                            for(var pred : igniteBehavior.keySet()){
                                if(!pred.apply(targetState)) continue;
                                igniteBehavior.get(pred).apply(comp,vars);
                                trySpendSoul(comp,manaCost);
                                spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastSuccess(comp,pos));
                                tryUnlockSpellAdvancement(comp,"ignition");
                                break;
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

    public static void addIgniteBehavior(Function<BlockState, Boolean> predicate, java.util.function.BiFunction<SpellComponent, SpellBlockArgs, SpellBlockResult> func){
        igniteBehavior.put(predicate,func);
    }
}
