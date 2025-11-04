package org.oxytocina.geomancy.spells.effectors;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModBlockTags;
import org.oxytocina.geomancy.registries.ModDamageTypes;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellSignal;
import org.oxytocina.geomancy.util.BlockHelper;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;
import static org.oxytocina.geomancy.spells.SpellBlocks.spawnCastParticles;
import static org.oxytocina.geomancy.spells.SpellBlocks.tryLogDebugBroke;

public class ShiftSpell {
    public static SpellBlock get(){
        return SpellBlock.Builder.create("shift")
                .inputs(
                        SpellSignal.createVector().named("position"),
                        SpellSignal.createVector().named("direction")
                )
                .func((comp,vars) -> {
                    var pos = vars.getVector("position");
                    var dir = vars.getVector("direction");
                    var direction = Direction.getFacing(dir.x,dir.y,dir.z);
                    var fromPos = Toolbox.posToBlockPos(pos);
                    var toPos = fromPos.offset(direction);
                    var restrictions = RestrictorBlockEntity.getRestrictionsAt(pos,comp.world());
                    if(!restrictions.allowsBlockManipulation() && !comp.context.isFromPrecomiled()){
                        // not allowed to place here! punish!
                        punishDisallowedAction(comp.context);
                        return SpellBlockResult.empty();
                    }
                    BlockState fromState = comp.world().getBlockState(fromPos);
                    BlockState toState = comp.world().getBlockState(toPos);

                    // check if pushable
                    if(
                            !PistonBlock.isMovable(fromState,comp.world(),fromPos,direction,true,null)
                            || !PistonBlock.isMovable(fromState,comp.world(),toPos,direction.getOpposite(),true,null)
                    ) return SpellBlockResult.empty();

                    // check for mobs caught in the blast
                    var positions = BlockHelper.getMovedBlockPositions(comp.world(),fromPos.offset(direction.getOpposite()),direction);
                    List<Entity> pushedEntities = new ArrayList<>();
                    for(var pushedPos : positions){
                        var pushedState = comp.world().getBlockState(pushedPos);
                        var voxelShape = pushedState.getCollisionShape(comp.world(),pushedPos);
                        Box box = Box.of(pushedPos.toCenterPos(),1,1,1);
                        List<Entity> contenders = comp.world().getOtherEntities(null, Boxes.stretch(box, direction, 1).union(box));
                        if (!contenders.isEmpty()) {
                            //List<Box> list2 = voxelShape.getBoundingBoxes();
                            for(var contender : contenders)
                            {
                                if(contender.getPistonBehavior() == PistonBehavior.IGNORE) continue;
                                //for (Box box2 : list2) {
                                    //Box box3 = Boxes.stretch(box2, direction, 1);
                                    //Box box4 = contender.getBoundingBox();
                                    //if (box3.intersects(box4)) {
                                        pushedEntities.add(contender);
                                        //break;
                                    //}
                                //}
                            }
                        }
                    }

                    // calculate cost
                    float manaCost = 2f
                            +pushedEntities.size()*3
                            +normalCastOffsetSoulCost(comp,pos);

                    if(canAfford(comp,manaCost)){
                        BlockHelper.push(comp.world(),fromPos.offset(direction.getOpposite()),direction);
                        for(var entity : pushedEntities){
                            if(entity != comp.caster())
                                entity.damage(ModDamageTypes.of(comp.world(),ModDamageTypes.SHIFT),4);
                            var vel = entity.getVelocity().withAxis(direction.getAxis(),1*direction.getAxis().choose(direction.getOffsetY(),direction.getOffsetY(),direction.getOffsetZ()));
                            entity.setVelocity(vel);
                            if(entity instanceof ServerPlayerEntity spe){
                                var buf = PacketByteBufs.create();
                                buf.writeVector3f(vel.toVector3f());
                                ServerPlayNetworking.send(spe, ModMessages.SET_VELOCITY,buf);
                            }
                        }
                        if(fromState.isIn(ModBlockTags.TRIGGERS_EARTH_BENDING_ADVANCEMENT))
                            SpellBlocks.tryUnlockSpellAdvancement(comp,"earth_bending");
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
