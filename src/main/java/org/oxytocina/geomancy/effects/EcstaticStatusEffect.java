package org.oxytocina.geomancy.effects;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class EcstaticStatusEffect extends ModStatusEffect {

	public EcstaticStatusEffect(StatusEffectCategory statusEffectCategory, int color) {
		super(statusEffectCategory, color);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		World world = entity.getWorld();

		boolean moving = entity.getVelocity().multiply(1,0,1).length()>0.05f;
		boolean onGround = entity.isOnGround();

		if (onGround && moving && !world.isClient && entity instanceof ServerPlayerEntity playerEntity) {
			playerEntity.jump();
			return;
		}

		if(onGround && moving && world.isClient && entity instanceof ClientPlayerEntity playerEntity){
			playerEntity.jump();
			return;
		}

		entity.setJumping(onGround && moving);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		/*int i = 200 >> amplifier;
		if (i > 0) {
			return duration % i == 0;
		}
		return true;*/

		return true;
	}


	
}