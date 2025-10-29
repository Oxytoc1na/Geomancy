package org.oxytocina.geomancy.spells.effectors;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.Direction;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellSignal;
import org.oxytocina.geomancy.util.BlockHelper;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

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
                    /*
                    Box box = Box.of(toPos.toCenterPos(),1,1,1);
                    List<Entity> contenders = comp.world().getOtherEntities(null, Boxes.stretch(box, direction, 1).union(box));
                    if (!contenders.isEmpty()) {
                        List<Box> list2 = voxelShape.getBoundingBoxes();
                        boolean isSLime = blockEntity.pushedBlock.isOf(Blocks.SLIME_BLOCK);
                        Iterator var12 = contenders.iterator();

                        while (true) {
                            Entity entity;
                            while (true) {
                                if (!var12.hasNext()) {
                                    return;
                                }

                                entity = (Entity)var12.next();
                                if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
                                    if (!isSLime) {
                                        break;
                                    }

                                    if (!(entity instanceof ServerPlayerEntity)) {
                                        Vec3d vec3d = entity.getVelocity();
                                        double e = vec3d.x;
                                        double g = vec3d.y;
                                        double h = vec3d.z;
                                        switch (direction.getAxis()) {
                                            case X:
                                                e = direction.getOffsetX();
                                                break;
                                            case Y:
                                                g = direction.getOffsetY();
                                                break;
                                            case Z:
                                                h = direction.getOffsetZ();
                                        }

                                        entity.setVelocity(e, g, h);
                                        break;
                                    }
                                }
                            }

                            double i = 0.0;

                            for (Box box2 : list2) {
                                Box box3 = Boxes.stretch(offsetHeadBox(pos, box2, blockEntity), direction, d);
                                Box box4 = entity.getBoundingBox();
                                if (box3.intersects(box4)) {
                                    i = Math.max(i, getIntersectionSize(box3, direction, box4));
                                    if (i >= d) {
                                        break;
                                    }
                                }
                            }

                            if (!(i <= 0.0)) {
                                i = Math.min(i, d) + 0.01;
                                moveEntity(direction, entity, i, direction);
                                if (!blockEntity.extending && blockEntity.source) {
                                    push(pos, entity, direction, d);
                                }
                            }
                        }
                    }
*/
                    // calculate cost
                    float manaCost = 2f
                            +normalCastOffsetSoulCost(comp,pos);

                    if(canAfford(comp,manaCost)){
                        BlockHelper.push(comp.world(),fromPos.offset(direction.getOpposite()),direction);
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
