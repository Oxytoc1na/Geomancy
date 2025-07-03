package org.oxytocina.geomancy.particles;

import org.oxytocina.geomancy.particles.client.ModBlockLeakParticles;
import net.fabricmc.fabric.api.client.particle.v1.*;
import net.minecraft.client.particle.*;

public class ModParticleFactories {
    public static void register(){
        // Fluid Splash
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.MOLTEN_GOLD_SPLASH, WaterSplashParticle.SplashFactory::new);


        // Fluid Dripping
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.DRIPPING_MOLTEN_GOLD, ModBlockLeakParticles.DrippingMoltenGoldFactory::new);


        // Fluid Falling
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.FALLING_MOLTEN_GOLD, ModBlockLeakParticles.FallingMoltenGoldFactory::new);


        // Fluid Landing
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.LANDING_MOLTEN_GOLD, ModBlockLeakParticles.LandingMoltenGoldFactory::new);


        // Fluid Fishing
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.MOLTEN_GOLD_FISHING, FishingParticle.Factory::new);
    }
}
