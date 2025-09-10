package org.oxytocina.geomancy.effects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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

	@Environment(EnvType.CLIENT)
	public static void tickClient() {
		var player = MinecraftClient.getInstance().player;
		if(player==null || !player.hasStatusEffect(ModStatusEffects.ECSTATIC)) return;

		boolean moving = player.getVelocity().multiply(1,0,1).length()>0.05f;
		boolean onGround = player.isOnGround();

		if(onGround && moving){
			player.jump();
		}
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

		entity.setJumping(onGround && moving);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}


	
}