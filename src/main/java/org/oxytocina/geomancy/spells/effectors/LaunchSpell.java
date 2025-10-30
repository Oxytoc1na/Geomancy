package org.oxytocina.geomancy.spells.effectors;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.client.util.CamShakeUtil;
import org.oxytocina.geomancy.entity.LaunchedBlockEntity;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellComponent;
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
                .parameters(SpellBlock.Parameter.createBoolean("transformative",true))
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
                    boolean transformative = vars.getBoolean("transformative");
                    BlockState fromState = comp.world().getBlockState(fromPos);

                    // check if pushable
                    if(!canLaunch(fromState,fromPos,direction,comp)) return SpellBlockResult.empty();

                    // calculate cost
                    float manaCost = 3f
                            +(float)Math.pow(velocity.length(),2f)*10
                            +normalCastOffsetSoulCost(comp,pos);
                    var res = SpellBlockResult.empty();
                    if(canAfford(comp,manaCost)){
                        LaunchedBlockEntity entity = LaunchedBlockEntity.spawnFromBlock(
                                comp.world(), fromPos,velocity,
                                fromState.contains(Properties.WATERLOGGED) ? fromState.with(Properties.WATERLOGGED, false) : fromState,
                                !transformative
                        );
                        Toolbox.playSound(fromState.getSoundGroup().getBreakSound(),comp.world(),fromPos, SoundCategory.BLOCKS,1);
                        ParticleUtil.ParticleData.createBlock(comp.world(),fromState,fromPos.toCenterPos(),velocity.multiply(5),50,0.5f).send();
                        CamShakeUtil.cause(comp.world(),fromPos.toCenterPos(),15,0.3f);
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

    public static boolean canLaunch(BlockState state, BlockPos pos, Direction dir, SpellComponent comp){
        return PistonBlock.isMovable(state,comp.world(),pos,dir,true,null) || state.getBlock() instanceof AnvilBlock;
    }
}
