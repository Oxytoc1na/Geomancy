package org.oxytocina.geomancy.particles;

import com.mojang.serialization.*;
import net.fabricmc.fabric.api.particle.v1.*;
import net.minecraft.particle.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.oxytocina.geomancy.Geomancy;

import java.util.function.*;

public class ModParticleTypes {

    public static DefaultParticleType MOLTEN_GOLD_SPLASH = register("molten_gold_splash", false);
    public static DefaultParticleType DRIPPING_MOLTEN_GOLD = register("dripping_molten_gold", false);
    public static DefaultParticleType FALLING_MOLTEN_GOLD = register("falling_molten_gold", false);
    public static DefaultParticleType LANDING_MOLTEN_GOLD = register("landing_molten_gold", false);

    public static DefaultParticleType CASTER_MUZZLE = register("caster_muzzle", false);

    // Simple particles
    private static DefaultParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, Geomancy.locate(name), FabricParticleTypes.simple(alwaysShow));
    }

    // complex particles
    @SuppressWarnings("deprecation")
    private static <T extends ParticleEffect> ParticleType<T> register(String name, ParticleEffect.Factory<T> factory, final Function<ParticleType<T>, Codec<T>> function, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, Geomancy.locate(name), new ParticleType<T>(alwaysShow, factory) {
            @Override
            public Codec<T> getCodec() {
                return function.apply(this);
            }
        });
    }

    public static void register() {

    }
}
