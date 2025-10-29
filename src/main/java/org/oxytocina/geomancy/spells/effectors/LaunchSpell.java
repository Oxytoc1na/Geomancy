package org.oxytocina.geomancy.spells.effectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellSignal;
import org.oxytocina.geomancy.util.BlockHelper;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class LaunchSpell {
    public static SpellBlock get(){
        return SpellBlock.Builder.create("launch")
                .inputs(
                        SpellSignal.createVector().named("position"),
                        SpellSignal.createVector().named("velocity")
                )
                .outputs(SpellSignal.createUUID().named("entity"))
                .func((comp,vars) -> {
                    var pos = vars.getVector("position");
                    var velocity = vars.getVector("velocity");
                    var direction = Direction.getFacing(velocity.x,velocity.y,velocity.z);
                    var fromPos = Toolbox.posToBlockPos(pos);
                    var restrictions = RestrictorBlockEntity.getRestrictionsAt(pos,comp.world());
                    if(!restrictions.allowsBlockManipulation() && !comp.context.isFromPrecomiled()){
                        // not allowed to place here! punish!
                        punishDisallowedAction(comp.context);
                        return SpellBlockResult.empty();
                    }
                    BlockState fromState = comp.world().getBlockState(fromPos);

                    // check if pushable
                    if(!PistonBlock.isMovable(fromState,comp.world(),fromPos,direction,true,null)) return SpellBlockResult.empty();

                    // calculate cost
                    float manaCost = 3f
                            +(float)Math.pow(velocity.length(),2f)*10
                            +normalCastOffsetSoulCost(comp,pos);
                    var res = SpellBlockResult.empty();
                    if(canAfford(comp,manaCost)){
                        FallingBlockEntity entity = new FallingBlockEntity(
                                comp.world(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, fromState.contains(Properties.WATERLOGGED) ? fromState.with(Properties.WATERLOGGED, false) : fromState
                        );
                        entity.setVelocity(velocity);
                        comp.world().setBlockState(fromPos, fromState.getFluidState().getBlockState(), Block.NOTIFY_ALL);
                        comp.world().spawnEntity(entity);
                        trySpendSoul(comp,manaCost);
                        spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,pos));
                        res.add("entity",entity.getUuid());
                    }
                    else{
                        // too broke
                        tryLogDebugBroke(comp,manaCost);
                        spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,pos));
                    }

                    return res;
                })
                .category(SpellBlock.Category.Effector).build();
    }
}
