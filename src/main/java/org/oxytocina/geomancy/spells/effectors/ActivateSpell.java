package org.oxytocina.geomancy.spells.effectors;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.VaultLampBlock;
import org.oxytocina.geomancy.blocks.blockEntities.AutocasterBlock;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SoulForgeBlock;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellSignal;
import org.oxytocina.geomancy.util.EntityUtil;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class ActivateSpell {
    public static SpellBlock get(){
        return SpellBlock.Builder.create("activate")
                .inputs(
                        SpellSignal.createVector().named("position")
                )
                .func((comp,vars) -> {
                    var pos = vars.getVector("position");
                    var blockPos = Toolbox.posToBlockPos(pos);
                    var restrictions = RestrictorBlockEntity.getRestrictionsAt(pos,comp.world());
                    if(!restrictions.allowsActivate() && !comp.context.isFromPrecomiled()){
                        // not allowed to place here! punish!
                        punishDisallowedAction(comp.context);
                        return SpellBlockResult.empty();
                    }

                    World world = comp.world();
                    BlockState targetState = world.getBlockState(blockPos);
                    Block targetBlock = targetState.getBlock();
                    BlockEntity targetEntity = world.getBlockEntity(blockPos);

                    float manaCost = 5f
                            +normalCastOffsetSoulCost(comp,pos);

                    if(canAfford(comp,manaCost)){
                        boolean success=true;
                        // open doors, trapdoors, press butons, flip levers
                        if(
                                targetBlock instanceof DoorBlock
                                        || targetBlock instanceof TrapdoorBlock
                                        || targetBlock instanceof ButtonBlock
                                        || targetBlock instanceof LeverBlock
                                        || targetBlock instanceof FenceGateBlock
                        ){
                            try{
                                targetBlock.onUse(targetState,world,blockPos,(PlayerEntity) comp.caster(),null,null);
                            }
                            catch(Exception ignored){
                                // some modded variant wanted to use hand, hit, or the caster and errored because of it
                            }
                        }
                        // trigger pressure plates
                        else if(targetBlock instanceof PressurePlateBlock pp){
                            world.setBlockState(blockPos,targetState.with(PressurePlateBlock.POWERED,true));
                            world.scheduleBlockTick(blockPos, pp, pp.getTickRate());
                        }
                        // trigger tripwire
                        else if(targetBlock instanceof TripwireBlock tw){
                            if(!targetState.get(TripwireBlock.POWERED))
                            {
                                var blockState = targetState.with(TripwireBlock.POWERED, true);
                                tw.update(world, blockPos, blockState);
                                world.setBlockState(blockPos,targetState.with(TripwireBlock.POWERED,true),3);
                                world.scheduleBlockTick(blockPos, tw, 10);
                            }
                        }
                        // trigger tripwire hook
                        else if(targetBlock instanceof TripwireHookBlock tw){
                            if(!targetState.get(TripwireHookBlock.POWERED))
                            {
                                var blockState = targetState.with(TripwireHookBlock.POWERED, true);
                                tw.update(world, blockPos,blockState,false,true,-1,null);
                                world.setBlockState(blockPos,blockState,3);
                                world.scheduleBlockTick(blockPos, tw, 10);
                            }
                        }
                        // trigger tnt
                        else if(targetBlock instanceof TntBlock){
                            TntBlock.primeTnt(world, blockPos);
                            world.removeBlock(blockPos, false);
                        }
                        // trigger detector rail
                        else if(targetBlock instanceof DetectorRailBlock drb){
                            BlockState blockState = (BlockState)targetState.with(DetectorRailBlock.POWERED, true);
                            world.setBlockState(blockPos, blockState, 3);
                            drb.updateNearbyRails(world, blockPos, blockState, true);
                            world.updateNeighborsAlways(blockPos, drb);
                            world.updateNeighborsAlways(blockPos.down(), drb);
                            world.scheduleBlockTick(blockPos, drb, 20);
                        }
                        // trigger dispenser, dropper, observers, autocasters
                        else if(
                                targetBlock instanceof DispenserBlock
                                        || targetBlock instanceof ObserverBlock
                                        || targetBlock instanceof AutocasterBlock
                        ){
                            world.scheduleBlockTick(blockPos, targetBlock, 0);
                        }
                        // lamp
                        else if(targetBlock instanceof RedstoneLampBlock){
                            world.setBlockState(blockPos, (BlockState)targetState.cycle(RedstoneLampBlock.LIT), Block.NOTIFY_ALL);
                        }
                        // vault lamp
                        else if(targetBlock instanceof VaultLampBlock){
                            world.setBlockState(blockPos, (BlockState)targetState.cycle(VaultLampBlock.LIT), Block.NOTIFY_ALL);
                        }
                        // note block
                        else if(targetBlock instanceof NoteBlock nb){
                            nb.playNote(null,targetState,world,blockPos);
                        }
                        // jukebox
                        else if(targetBlock instanceof JukeboxBlock){
                            if ((Boolean)targetState.get(JukeboxBlock.HAS_RECORD)) {
                                if (world.getBlockEntity(blockPos) instanceof JukeboxBlockEntity jbe) {
                                    jbe.dropRecord();
                                }
                            }
                        }
                        // bell
                        else if(targetBlock instanceof BellBlock bb){
                            bb.ring(world,blockPos,null);
                        }
                        // soul forge
                        else if(targetBlock instanceof SoulForgeBlock sfb){
                            sfb.activate(world,blockPos,comp.context);
                        }
                        else success = false;

                        if(success){
                            if(comp.caster()!=null && EntityUtil.distanceTo(comp.caster(),pos) >=1000)
                                tryUnlockSpellAdvancement(comp,"ftl");

                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,pos));
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
}
