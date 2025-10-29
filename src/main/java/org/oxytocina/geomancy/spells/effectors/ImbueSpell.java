package org.oxytocina.geomancy.spells.effectors;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.effects.ModStatusEffects;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellSignal;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;
import java.util.List;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class ImbueSpell {
    private static final HashMap<Identifier, ImbueData> imbueData = new HashMap();

    private static void addImbueData(StatusEffect effect, ImbueData data){
        imbueData.put(Registries.STATUS_EFFECT.getId(effect),data);
    }
    public static SpellBlock get(){
        // imbue data
        {
            addImbueData(StatusEffects.REGENERATION,new ImbueData(10,1));
            addImbueData(StatusEffects.POISON,new ImbueData(10,1,1.5f));
            addImbueData(StatusEffects.WITHER,new ImbueData(10,1,1.5f));
            addImbueData(StatusEffects.STRENGTH,new ImbueData(10,2));
            addImbueData(StatusEffects.WEAKNESS,new ImbueData(10,2));
            addImbueData(StatusEffects.SPEED,new ImbueData(10,2));
            addImbueData(StatusEffects.SLOWNESS,new ImbueData(10,2,1.5f));
            addImbueData(StatusEffects.JUMP_BOOST,new ImbueData(10,2));
            addImbueData(StatusEffects.NIGHT_VISION,new ImbueData(0,1,1));
            addImbueData(StatusEffects.BLINDNESS,new ImbueData(0,1,1));
            addImbueData(StatusEffects.WATER_BREATHING,new ImbueData(0,1,1));
            addImbueData(StatusEffects.DOLPHINS_GRACE,new ImbueData(0,3,1));
            addImbueData(StatusEffects.FIRE_RESISTANCE,new ImbueData(0,1,1));
            addImbueData(StatusEffects.INVISIBILITY,new ImbueData(0,0.5f,1));
            addImbueData(StatusEffects.GLOWING,new ImbueData(0,0.25f,1f));
            addImbueData(StatusEffects.RESISTANCE,new ImbueData(4,2f,2));
            addImbueData(StatusEffects.LUCK,new ImbueData(10,0.5f,1.5f));
            addImbueData(StatusEffects.UNLUCK,new ImbueData(10,0.25f,1.2f));
            addImbueData(StatusEffects.SLOW_FALLING,new ImbueData(0,0.5f,1f));
            addImbueData(StatusEffects.LEVITATION,new ImbueData(10,2,2f));
            addImbueData(StatusEffects.HERO_OF_THE_VILLAGE,new ImbueData(3,3f,2f));
            addImbueData(StatusEffects.BAD_OMEN,new ImbueData(0,0.1f,1f));
            addImbueData(StatusEffects.HUNGER,new ImbueData(10,0.5f,1.5f));
            addImbueData(StatusEffects.SATURATION,new ImbueData(10,10f,2f));
            addImbueData(StatusEffects.HASTE,new ImbueData(10,2));
            addImbueData(StatusEffects.MINING_FATIGUE,new ImbueData(10,2));
            addImbueData(StatusEffects.ABSORPTION,new ImbueData(10,1,1.5f));
            addImbueData(StatusEffects.HEALTH_BOOST,new ImbueData(10,1,1.5f));
            addImbueData(StatusEffects.INSTANT_HEALTH,new ImbueData(10,50,1f,true));
            addImbueData(StatusEffects.INSTANT_DAMAGE,new ImbueData(10,70,1f,true));

            // modded effects
            addImbueData(ModStatusEffects.PARANOIA,new ImbueData(2,2));
            addImbueData(ModStatusEffects.MOURNING,new ImbueData(4,2,1.5f));
            addImbueData(ModStatusEffects.REGRETFUL,new ImbueData(4,2,1.5f));
            addImbueData(ModStatusEffects.ECSTATIC,new ImbueData(1,2));
            addImbueData(ModStatusEffects.RIGHTEOUS,new ImbueData(4,2,2));
            addImbueData(ModStatusEffects.BLISSFUL,new ImbueData(4,2,2));
        }

        return SpellBlock.Builder.create("imbue")
                .inputs(
                        SpellSignal.createUUID().named("entity"),
                        SpellSignal.createNumber(0).named("amp"),
                        SpellSignal.createNumber(10).named("duration")
                )
                .parameters(SpellBlock.Parameter.createText("effect","regeneration"))
                .func((comp,vars) -> {
                    var uuid = vars.getUUID("entity");
                    var effect = vars.getText("effect");
                    var amp = vars.getInt("amp");
                    var duration = vars.getNumber("duration");
                    LivingEntity ent = comp.world() instanceof ServerWorld sw ? (sw.getEntity(uuid) instanceof LivingEntity le ? le : null) : null;
                    if(ent==null) return SpellBlockResult.empty(); // invalid entity
                    if(duration<0||amp<0) return SpellBlockResult.empty(); // invalid amp or duration
                    Identifier id = Identifier.tryParse(effect);
                    if(id==null) { tryLogDebugNoSuchEffect(comp,effect); return SpellBlockResult.empty();  } // invalid status effect
                    if(!imbueData.containsKey(id)) { tryLogDebugUnimbuableEffect(comp, Registries.STATUS_EFFECT.get(id).getName()); return SpellBlockResult.empty();  } // unimbuable status effect;
                    var data = imbueData.get(id);
                    amp = Toolbox.clampI(amp,0,data.maxAmp);
                    if(data.instant) duration = 1f;
                    float manaCost = 0.5f + data.getCost(amp,duration);
                    if(data.instant) duration = 1/20f;


                    if(trySpendSoul(comp,manaCost)){
                        var effectInst = new StatusEffectInstance(Registries.STATUS_EFFECT.get(id),Math.round(duration*20),amp);
                        ent.addStatusEffect(effectInst,comp.context.caster);
                        spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,ent.getPos()));

                        if((ent instanceof VillagerEntity || (ent instanceof PlayerEntity pe&&pe!=comp.caster())) && List.of(
                                StatusEffects.INSTANT_HEALTH,StatusEffects.REGENERATION).contains(effectInst.getEffectType()))
                            tryUnlockSpellAdvancement(comp,"medic");
                    }
                    else{
                        // too broke
                        tryLogDebugBroke(comp,manaCost);
                        spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                    }

                    return SpellBlockResult.empty();
                })
                .sideConfigGetter((comp)->{
                    SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                    for(int i = 0; i <6; i++) res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDirString(i)).named(i%3==0?"amp":i%3==1?"entity":"duration");
                    return res;
                })
                .category(SpellBlock.Category.Effector).build();
    }

    public static class ImbueData{
        public final int maxAmp;
        public final float ampExponent;
        public final float costMult;
        public final boolean instant;

        public ImbueData(int maxAmp, float costMult){
            this(maxAmp,costMult,2,false);
        }

        public ImbueData(int maxAmp, float costMult, float ampExponent){
            this(maxAmp,costMult,ampExponent,false);
        }

        public ImbueData(int maxAmp, float costMult, float ampExponent,boolean instant){
            this.maxAmp=maxAmp;
            this.costMult=costMult;
            this.ampExponent=ampExponent;
            this.instant=instant;
        }

        public float getCost(int amp, float duration) {
            return (float)Math.pow((amp+1),ampExponent) * duration * costMult * 0.2f;
        }
    }

}
