package org.oxytocina.geomancy.spells;

import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.items.armor.CastingArmorItem;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.SoulUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class SpellBlocks2 {

    public static final SpellBlock EXODIA_1;
    public static final SpellBlock EXODIA_2;
    public static final SpellBlock EXODIA_3;
    public static final SpellBlock EXODIA_4;
    public static final SpellBlock EXODIA_5;

    private static final List<Exodia2Data> EXODIA_2_DATA = List.of(
            new Exodia2Data("reverse",
                    (in,out)-> Objects.equals(in.getText("arg"), Toolbox.reverseString(out.vars.get("res").getTextValue())),
                    List.of(
                            SpellSignal.createText("test"),
                            SpellSignal.createText("helloworld"),
                            SpellSignal.createText("geomancy"),
                            SpellSignal.createText("octopus")
                    ))
    );

    public static class Exodia2Data{
        public String name;
        public BiFunction<SpellBlockArgs,SpellBlockResult,Boolean> check;
        public List<SpellSignal> testcases;

        public Exodia2Data(String name,BiFunction<SpellBlockArgs,SpellBlockResult,Boolean> check,List<SpellSignal> testcases){
            this.name=name;
            this.check=check;
            this.testcases=testcases;
        }
    }

    private static SpellBlock.Category cat;
    static{
        cat = SpellBlock.Category.Ancient;

        // challenge of soul
        // activate while in >=1000 ambient souls
        EXODIA_1 = SpellBlocks.register(SpellBlock.Builder.create("exodia_1")
                        .inputs(SpellSignal.createAny())
                        .func((comp,args)->{
                            if(!(comp.context.caster instanceof ServerPlayerEntity spe)) return SpellBlockResult.empty();
                            float ambientSouls = SoulUtil.getAmbientSoulsPerBlock(comp.world(),comp.context.getOriginBlockPos());
                            if(ambientSouls>=1000){
                                // enlighten!!
                                float manaCost = 500;
                                if(trySpendSoul(comp,manaCost)){
                                    spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                                    tryUnlockSpellAdvancement(spe,"enlightenment_1");
                                }
                                else{
                                    // too broke
                                    tryLogDebugBroke(comp,manaCost);
                                    spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                                }
                            }
                            return SpellBlockResult.empty();
                        })
                .category(cat).build());

        // challenge of math
        // based on world seed and dimension, provide a reference spell that solves a certain math problem
        EXODIA_2 = SpellBlocks.register(SpellBlock.Builder.create("exodia_2")
                .inputs(SpellSignal.createAny())
                        .parameters(SpellBlock.Parameter.createText("spell","helloworld"))
                .func((comp,args)->{
                    if(!(comp.context.caster instanceof ServerPlayerEntity spe)) return SpellBlockResult.empty();
                    if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                    // pick task to complete
                    var task = EXODIA_2_DATA.get(0);

                    SpellGrid providedSpell = sps.getSpell(comp.context.casterItem,args.getText("spell"));
                    if(providedSpell==null) return SpellBlockResult.empty();

                    if(ambientSouls>=1000){
                        // enlighten!!
                        float manaCost = 500;
                        if(trySpendSoul(comp,manaCost)){
                            spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                            tryUnlockSpellAdvancement(spe,"enlightenment_1");
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                        }
                    }
                    return SpellBlockResult.empty();
                })
                .category(cat).build());
    }
}
