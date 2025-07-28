package org.oxytocina.geomancy.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.oxytocina.geomancy.Geomancy;

public class ModStatusEffects {
    /// increases soul casting cost by 50% per level
    public static final StatusEffect REGRETFUL = register("regretful", new RegretfulStatusEffect(StatusEffectCategory.HARMFUL, 0x2324f8));

    /// decreases soul regeneration speed by 20% per level
    public static final StatusEffect MOURNING = register("mourning", new MourningStatusEffect(StatusEffectCategory.HARMFUL, 0x2324f8));

    /// makes you jump when moving around
    public static final StatusEffect ECSTATIC = register("ecstatic", new EcstaticStatusEffect(StatusEffectCategory.HARMFUL, 0x2324f8));

    /// makes you hear footsteps, ambient mob sounds, chests opening and closing, etc.
    public static final StatusEffect PARANOIA = register("paranoia", new ParanoiaStatusEffect(StatusEffectCategory.HARMFUL, 0x2324f8));


    private static StatusEffect register(String id, StatusEffect entry) {
        return Registry.register(Registries.STATUS_EFFECT, Geomancy.locate(id), entry);
    }

    public static void register(){}
}
