package org.oxytocina.geomancy.spells;

import com.mojang.datafixers.util.Function3;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;
import org.oxytocina.geomancy.client.util.CamShakeUtil;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class SpellBlocks2 {

    public static final SpellBlock EXODIA_1;
    public static final SpellBlock EXODIA_2;
    public static final SpellBlock EXODIA_3;
    public static final SpellBlock EXODIA_4;
    public static final SpellBlock EXODIA_5;

    private static final List<Exodia2Data> EXODIA_2_DATA = List.of(
            new Exodia2Data("reverse",
                    (c,in,out)-> out.vars.containsKey("res") && Objects.equals(in.getText("arg"), Toolbox.reverseString(out.vars.get("res").getTextValue())),
                        createTextList(new String[]{
                            "test","helloworld","geomancy","octopus","loremipsum","foobar","onlyfoolsdoreadthis","cowlevel","transrightsarehumanrights","congratulations","you","won!"
                            })
            ),
            new Exodia2Data("factorial",
                    (c,in,out)-> out.vars.containsKey("res") && Toolbox.roughlyEqual(out.vars.get("arg").getNumberValue(), Toolbox.factorial(in.getInt("res")),0.1f),
                    createNumberList(new float[]{1,6,4,9,11,17,20,18,2})
            ),
            new Exodia2Data("mix",
                    (c,in,out)-> {
                        if(c.caster==null) return false;
                        if(out.vars.isEmpty() || !out.vars.containsKey("res")) return false;
                        var sig = out.vars.get("res");
                        if(sig.type != SpellSignal.Type.List) return false;
                        var list = sig.getListValue();
                        if(list.size() != 3) return false;
                        if(!SpellSignal.createUUID(c.caster).softEquals(list.get(0))) return false;
                        if(!SpellSignal.createText(c.caster.getName().getString()).softEquals(list.get(1))) return false;
                        if(!Toolbox.roughlyEqual(list.get(2).getNumberValue(),0,0.1f)) return false;
                        return true;
                    },
                    List.of(SpellSignal.createNone())
            ),
            new Exodia2Data("pythagoras",
                    (c,in,out)-> {
                        if(!out.vars.containsKey("res")) return false;
                        var listIn = in.getList("arg");
                        float a = listIn.get(0).getNumberValue();
                        float b = listIn.get(1).getNumberValue();
                        double cDesired = Math.sqrt(a*a+b*b);
                        float cProvided = out.vars.get("res").getNumberValue();
                        return Toolbox.roughlyEqual((float)cDesired,cProvided,0.1f);
                    },
                    List.of(
                            SpellSignal.createList(List.of(SpellSignal.createNumber(1),SpellSignal.createNumber(1))),
                            SpellSignal.createList(List.of(SpellSignal.createNumber(3),SpellSignal.createNumber(1))),
                            SpellSignal.createList(List.of(SpellSignal.createNumber(10),SpellSignal.createNumber(6))),
                            SpellSignal.createList(List.of(SpellSignal.createNumber(21),SpellSignal.createNumber(75))),
                            SpellSignal.createList(List.of(SpellSignal.createNumber(3),SpellSignal.createNumber(665)))
                    )
            )
            //new Exodia2Data("sort",
            //        (in,out)-> out.vars.containsKey("res") && out.vars.get("res").type == SpellSignal.Type.List && isListSorted(out.vars.get("res").getListValue()),
            //        List.of(
            //                SpellSignal.createList(createNumberList(new float[]{4,8,2,3,76,14,65})),
            //                SpellSignal.createList(createNumberList(new float[]{8,2,9,4,0,23,75,1}))
            //        ))
    );
    private static List<SpellSignal> createNumberList(float[] vals){
        List<SpellSignal> res = new ArrayList<>();
        for (int i = 0; i < vals.length; i++)
            res.add(SpellSignal.createNumber(vals[i]).named(Integer.toString(i)));
        return res;
    }
    private static List<SpellSignal> createTextList(String[] vals){
        List<SpellSignal> res = new ArrayList<>();
        for (int i = 0; i < vals.length; i++)
            res.add(SpellSignal.createText(vals[i]).named(Integer.toString(i)));
        return res;
    }
    private static boolean isListSorted(List<SpellSignal> list){
        Float current = null;
        for (int i = 0; i < list.size(); i++) {
            var sig = list.get(i);
            if(sig.type != SpellSignal.Type.Number) return false;
            Float val = sig.getNumberValue();
            if(current==null) {current=val; continue;}
            if(val < current) return false;
        }
        return true;
    }

    public static class Exodia2Data{
        public String name;
        public Function3<SpellContext,SpellBlockArgs,SpellBlockResult,Boolean> check;
        public List<SpellSignal> testcases;

        public Exodia2Data(String name,Function3<SpellContext,SpellBlockArgs,SpellBlockResult,Boolean> check,List<SpellSignal> testcases){
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
                                    // cause madness
                                    MadnessUtil.addMadness(spe,500); MadnessUtil.syncMadness(spe);
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

        // challenge of calculation
        // based on world seed and dimension, provide a reference spell that solves a certain math problem
        EXODIA_2 = SpellBlocks.register(SpellBlock.Builder.create("exodia_2")
                .inputs(SpellSignal.createAny())
                        .parameters(SpellBlock.Parameter.createText("spell","helloworld"))
                .func((comp,args)->{
                    if(!(comp.context.caster instanceof ServerPlayerEntity spe)) return SpellBlockResult.empty();
                    if(EnlightenmentUtil.getEnlightenmentServer(spe) < 1) return SpellBlockResult.empty();
                    if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                    if(!(comp.world() instanceof ServerWorld sw)) return SpellBlockResult.empty();
                    // pick task to complete
                    var task = EXODIA_2_DATA.get((int) (sw.getSeed() % EXODIA_2_DATA.size()));
                    SpellGrid providedSpell = sps.getSpell(comp.context.casterItem,args.getText("spell"));
                    if(providedSpell==null){
                        // print what needs to be done
                        SpellBlocks.tryLogDebug(comp, Text.translatable("geomancy.spells.debug.exodia2").formatted(Formatting.GOLD).append(Text.translatable("geomancy.spells.debug.exodia2."+task.name).formatted(Formatting.GRAY)));
                        return SpellBlockResult.empty();
                    }
                    boolean passedTests = true;
                    for(var testCase : task.testcases)
                    {
                        var testArgs = SpellBlockArgs.empty();
                        testArgs.vars.put("arg",testCase);
                        var testRes = providedSpell.runReferenced(comp.context,comp,testArgs);
                        if(!task.check.apply(comp.context,testArgs,testRes)) {passedTests=false;break;}
                    }

                    if(passedTests){
                        // enlighten!!
                        float manaCost = 250;
                        if(trySpendSoul(comp,manaCost)){
                            spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                            tryUnlockSpellAdvancement(spe,"enlightenment_2");
                            CamShakeUtil.cause(comp.world(),comp.context.getOriginPos(), 100,0.5f,10);
                            // cause madness
                            MadnessUtil.addMadness(spe,750); MadnessUtil.syncMadness(spe);
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                        }

                        return SpellBlockResult.empty();
                    }

                    // print what needs to be done
                    SpellBlocks.tryLogDebug(comp, Text.translatable("geomancy.spells.debug.exodia2_fail").formatted(Formatting.GOLD).append(Text.translatable("geomancy.spells.debug.exodia2."+task.name).formatted(Formatting.GRAY)));
                    return SpellBlockResult.empty();
                })
                .category(cat).defaultLootWeight(0).build());

        // challenge of presence
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
                        // cause madness
                        MadnessUtil.addMadness(spe,1000); MadnessUtil.syncMadness(spe);
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
                    if(spe.getServerWorld().getRegistryKey().getValue().equals(new Identifier("the_end")) && spe.getPos().y<=-60){
                        // enlighten!!
                        float manaCost = 250;
                        if(trySpendSoul(comp,manaCost)){
                            spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                            tryUnlockSpellAdvancement(spe,"enlightenment_4");
                            CamShakeUtil.cause(comp.world(),comp.context.getOriginPos(), 100,0.5f,10);
                            // cause madness
                            MadnessUtil.addMadness(spe,1500); MadnessUtil.syncMadness(spe);
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
                        // cause madness
                        MadnessUtil.addMadness(spe,5000); MadnessUtil.syncMadness(spe);
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
