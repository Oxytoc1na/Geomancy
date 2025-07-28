package org.oxytocina.geomancy.effects;

import net.minecraft.entity.*;
import net.minecraft.entity.effect.*;
import org.jetbrains.annotations.*;

public class ModStatusEffect extends StatusEffect {
	
	public ModStatusEffect(StatusEffectCategory category, int color) {
		super(category, color);
	}
	
	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return false;
	}
	
	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
	
	}
	
	@Override
	public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
	
	}
	
	
}
