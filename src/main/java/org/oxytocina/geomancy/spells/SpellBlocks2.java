package org.oxytocina.geomancy.spells;

import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;
import org.oxytocina.geomancy.client.util.CamShakeUtil;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.util.EnlightenmentUtil;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.SoulUtil;
import org.oxytocina.geomancy.util.Toolbox;

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
                    (in,out)-> out.vars.containsKey("res") && Objects.equals(in.getText("arg"), Toolbox.reverseString(out.vars.get("res").getTextValue())),
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
                .category(cat).defaultLootWeight(0).build());

        // challenge of math
        // based on world seed and dimension, provide a reference spell that solves a certain math problem
        EXODIA_2 = SpellBlocks.register(SpellBlock.Builder.create("exodia_2")
                .inputs(SpellSignal.createAny())
                        .parameters(SpellBlock.Parameter.createText("spell","helloworld"))
                .func((comp,args)->{
                    if(!(comp.context.caster instanceof ServerPlayerEntity spe)) return SpellBlockResult.empty();
                    if(EnlightenmentUtil.getEnlightenmentServer(spe) < 1) return SpellBlockResult.empty();
                    if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                    SpellGrid providedSpell = sps.getSpell(comp.context.casterItem,args.getText("spell"));
                    if(providedSpell==null) return SpellBlockResult.empty();
                    // pick task to complete
                    var task = EXODIA_2_DATA.get(0);
                    boolean passedTests = true;
                    for(var testCase : task.testcases)
                    {
                        var testArgs = SpellBlockArgs.empty();
                        testArgs.vars.put("arg",testCase);
                        var testRes = providedSpell.runReferenced(comp.context,comp,testArgs);
                        if(!task.check.apply(testArgs,testRes)) {passedTests=false;break;}
                    }

                    if(passedTests){
                        // enlighten!!
                        float manaCost = 250;
                        if(trySpendSoul(comp,manaCost)){
                            spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                            tryUnlockSpellAdvancement(spe,"enlightenment_2");
                            CamShakeUtil.cause(comp.world(),comp.context.getOriginPos(), 100,0.5f,10);
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                        }
                    }
                    return SpellBlockResult.empty();
                })
                .category(cat).defaultLootWeight(0).build());

        // challenge of recklessness
        // activate a loud sound event spell while in an ancient city
        EXODIA_3 = SpellBlocks.register(SpellBlock.Builder.create("exodia_3")
                .post(comp -> {
                    if(!(comp.context.caster instanceof ServerPlayerEntity spe)) return;
                    if(EnlightenmentUtil.getEnlightenmentServer(spe) < 2) return;
                    if(!comp.context.hasFlag("exodia_3")) return;
                    // enlighten!!
                    float manaCost = 250;
                    if(trySpendSoul(comp,manaCost)){
                        spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                        tryUnlockSpellAdvancement(spe,"enlightenment_3");
                        CamShakeUtil.cause(comp.world(),comp.context.getOriginPos(), 100,0.5f,10);
                        // trigger skulk sensors and shriekers
                        spe.getSculkShriekerWarningManager().ifPresent(manager->{manager.setWarningLevel(10);});
                        var sw = spe.getServerWorld();
                        var centerChunkPos = sw.getChunk(comp.context.getOriginBlockPos()).getPos();
                        final int range = 4; // in chunks
                        for (int ix = -range; ix <= range; ix++) {
                            for (int iz = -range; iz <= range; iz++) {
                                var chunk = sw.getChunk(centerChunkPos.x+ix,centerChunkPos.z+iz);
                                for(var be : chunk.getBlockEntities().values()){
                                    if(be instanceof SculkSensorBlockEntity sculkSensor){
                                        sculkSensor.getEventListener().listen(sw, GameEvent.STEP,new GameEvent.Emitter(spe,null),comp.context.getOriginPos());
                                    }
                                    else if(be instanceof SculkShriekerBlockEntity shrieker){
                                        shrieker.getEventListener().listen(sw, GameEvent.STEP,new GameEvent.Emitter(spe,null),comp.context.getOriginPos());
                                    }
                                }
                            }
                        }
                    }
                    else{
                        // too broke
                        tryLogDebugBroke(comp,manaCost);
                        spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                    }

                })
                .category(cat).defaultLootWeight(0).build());

        // challenge of curiosity
        // activate while deep enough in the ends void to take damage
        EXODIA_4 = SpellBlocks.register(SpellBlock.Builder.create("exodia_4")
                .func((comp,args)->{
                    if(!(comp.caster() instanceof ServerPlayerEntity spe)) return SpellBlockResult.empty();
                    if(EnlightenmentUtil.getEnlightenmentServer(spe) < 3) return SpellBlockResult.empty();
                    if(spe.getServerWorld().getRegistryKey().getValue().equals(new Identifier("the_end")) && spe.getPos().y<-20){
                        // enlighten!!
                        float manaCost = 250;
                        if(trySpendSoul(comp,manaCost)){
                            spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                            tryUnlockSpellAdvancement(spe,"enlightenment_4");
                            CamShakeUtil.cause(comp.world(),comp.context.getOriginPos(), 100,0.5f,10);
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                        }
                    }
                    return SpellBlockResult.empty();
                })
                .category(cat).defaultLootWeight(0).build());

        // challenge of absolution
        // cast lightning ontop of the nulls roof, while also standing on it
        EXODIA_5 = SpellBlocks.register(SpellBlock.Builder.create("exodia_5")
                .post((comp)->{
                    if(!(comp.context.caster instanceof ServerPlayerEntity spe)) return;
                    if(EnlightenmentUtil.getEnlightenmentServer(spe) < 4) return;
                    if(!comp.context.hasFlag("exodia_5")) return;
                    // enlighten!!
                    float manaCost = 1000;
                    if(trySpendSoul(comp,manaCost)){
                        spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                        tryUnlockSpellAdvancement(spe,"enlightenment_5");
                        CamShakeUtil.cause(comp.world(),comp.context.getOriginPos(), 100,0.5f,10);
                    }
                    else{
                        // too broke
                        tryLogDebugBroke(comp,manaCost);
                        spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                    }
                })
                .category(cat).defaultLootWeight(0).build());
    }
}
