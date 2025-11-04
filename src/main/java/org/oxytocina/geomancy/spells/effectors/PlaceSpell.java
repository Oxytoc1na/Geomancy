package org.oxytocina.geomancy.spells.effectors;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import org.oxytocina.geomancy.blocks.blockEntities.RestrictorBlockEntity;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.spells.SpellSignal;
import org.oxytocina.geomancy.util.EntityUtil;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class PlaceSpell {
    public static SpellBlock get(){
        return SpellBlock.Builder.create("place")
                .inputs(
                        SpellSignal.createVector().named("position"),
                        SpellSignal.createNumber().named("slot")
                )
                .func((comp,vars) -> {
                    var pos = vars.getVector("position");
                    var slot = vars.getNumber("slot");
                    int slotInt = Math.round(slot);
                    Inventory inv = comp.context.getInventory();
                    if(inv==null) return SpellBlockResult.empty();
                    if(slotInt <0||slotInt>=inv.size()) { tryLogDebugSlotOOB(comp,slotInt); return SpellBlockResult.empty();} // slot OOB
                    ItemStack stack = inv.getStack(slotInt);
                    if(!(stack.getItem() instanceof BlockItem bi)) { tryLogDebugNotPlaceable(comp,stack); return SpellBlockResult.empty(); } // not a block
                    if(stack.isEmpty()) return SpellBlockResult.empty();
                    var blockPos = Toolbox.posToBlockPos(pos);
                    var restrictions = RestrictorBlockEntity.getRestrictionsAt(pos,comp.world());
                    if(!restrictions.allowsBlockManipulation() && !comp.context.isFromPrecomiled()){
                        // not allowed to place here! punish!
                        punishDisallowedAction(comp.context);
                        return SpellBlockResult.empty();
                    }

                    float manaCost = 1
                            +normalCastOffsetSoulCost(comp,pos);

                    if(canAfford(comp,manaCost)){

                        BlockState targetState = comp.world().getBlockState(blockPos);
                        if(!targetState.isReplaceable())
                        {
                            // couldnt replace
                            tryLogDebugNotReplaceable(comp,targetState);
                            return SpellBlockResult.empty();
                        }

                        // place block in world
                        comp.world().setBlockState(Toolbox.posToBlockPos(pos), bi.getBlock().getDefaultState());

                        // remove block from inventory
                        if(!(comp.context.sourceType== SpellContext.SourceType.Caster && ((PlayerEntity)comp.caster()).isCreative()))
                            stack.decrement(1);

                        Toolbox.playSound(bi.getBlock().getSoundGroup(targetState).getPlaceSound(),comp.world(),blockPos, SoundCategory.BLOCKS,1,1);
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
