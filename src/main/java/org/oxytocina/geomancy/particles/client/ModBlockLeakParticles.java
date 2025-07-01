package org.oxytocina.geomancy.particles.client;

import org.oxytocina.geomancy.registries.*;
import org.oxytocina.geomancy.particles.*;
import net.fabricmc.api.*;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.*;
import net.minecraft.particle.*;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;

@Environment(EnvType.CLIENT)
public class ModBlockLeakParticles {

    public static class LandingLiquidCrystalFactory implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public LandingLiquidCrystalFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Landing(clientWorld, d, e, f, ModFluids.MOLTEN_GOLD);
            blockLeakParticle.setColor(ModFluids.MOLTEN_GOLD_COLOR_VEC.x(), ModFluids.MOLTEN_GOLD_COLOR_VEC.y(), ModFluids.MOLTEN_GOLD_COLOR_VEC.z());
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    public static class FallingLiquidCrystalFactory implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingLiquidCrystalFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            BlockLeakParticle blockLeakParticle = new BlockLeakParticle.ContinuousFalling(clientWorld, d, e, f, ModFluids.MOLTEN_GOLD, ModParticleTypes.LANDING_MOLTEN_GOLD);
            blockLeakParticle.setColor(ModFluids.MOLTEN_GOLD_COLOR_VEC.x(), ModFluids.MOLTEN_GOLD_COLOR_VEC.y(), ModFluids.MOLTEN_GOLD_COLOR_VEC.z());
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    public static class DrippingLiquidCrystalFactory implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public DrippingLiquidCrystalFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            BlockLeakParticle blockLeakParticle = new BlockLeakParticle.Dripping(clientWorld, d, e, f, ModFluids.MOLTEN_GOLD, ModParticleTypes.FALLING_MOLTEN_GOLD);
            blockLeakParticle.setColor(ModFluids.MOLTEN_GOLD_COLOR_VEC.x(), ModFluids.MOLTEN_GOLD_COLOR_VEC.y(), ModFluids.MOLTEN_GOLD_COLOR_VEC.z());
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }
}
