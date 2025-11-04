package org.oxytocina.geomancy.spells.effectors;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.oxytocina.geomancy.enchantments.ModEnchantments;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellSignal;
import org.oxytocina.geomancy.util.ParticleUtil;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class DelegateSpell {
    public static SpellBlock get(){
        return SpellBlock.Builder.create("delegate")
                .inputs(
                        SpellSignal.createVector().named("position"),
                        SpellSignal.createVector().named("direction"),
                        SpellSignal.createText().named("spell"),
                        SpellSignal.createNumber().named("delay")
                )
                .outputs(SpellSignal.createUUID().named("delegate"))
                .func((comp,vars) -> {
                    var spellName = vars.getText("spell");
                    if(comp.context.getSpellSelector()==null) return SpellBlockResult.empty();
                    var spell =comp.context.getSpellSelector().getSpell(comp.context.casterItem,spellName);
                    if(spell==null) return SpellBlockResult.empty();
                    var pos = vars.getVector("position");
                    var dir = vars.getVector("direction");
                    int delay = Math.round(20*vars.getNumber("delay"));

                    // calculate cost
                    float manaCost = Math.max(0.1f,(3f
                            +normalCastOffsetSoulCost(comp,pos))
                            * (1 - 0.2f*(comp.context.hasCasterItem() ? EnchantmentHelper.getLevel(ModEnchantments.PARALLELISM,comp.context.casterItem) : 0)));

                    if(canAfford(comp,manaCost)){
                        // spawn delegate
                        var d = dir.horizontalLength();
                        Vec2f rot = new Vec2f(
                                (float)(MathHelper.atan2(dir.x, dir.z) * (double)(180F / (float)Math.PI)),
                                (float)(MathHelper.atan2(dir.y, d) * (double)(180F / (float)Math.PI))
                        );

                        spell.spawnDelegate(comp.context,pos,rot,delay);

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
