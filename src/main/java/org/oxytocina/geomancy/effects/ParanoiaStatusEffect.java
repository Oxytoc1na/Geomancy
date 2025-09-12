package org.oxytocina.geomancy.effects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.HashMap;

public class ParanoiaStatusEffect extends ModStatusEffect {

	public static final HashMap<SoundEvent, SoundCategory> oneshotEvents = new HashMap<>();
	public static final ArrayList<SoundEvent> stepEvents = new ArrayList<>();
	public static final ArrayList<StepEvent> currentStepEvents = new ArrayList<>();

	static{
		addEvent(SoundEvents.BLOCK_CHEST_OPEN,SoundCategory.BLOCKS);
		addEvent(SoundEvents.BLOCK_CHEST_CLOSE,SoundCategory.BLOCKS);
		addEvent(SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,SoundCategory.BLOCKS);
		addEvent(SoundEvents.BLOCK_WOODEN_DOOR_OPEN,SoundCategory.BLOCKS);
		addEvent(SoundEvents.BLOCK_WOODEN_DOOR_CLOSE,SoundCategory.BLOCKS);
		addEvent(SoundEvents.ENTITY_CREEPER_PRIMED,SoundCategory.HOSTILE);
		addEvent(SoundEvents.ENTITY_ZOMBIE_AMBIENT,SoundCategory.HOSTILE);
		addEvent(SoundEvents.ENTITY_SKELETON_AMBIENT,SoundCategory.HOSTILE);
		addEvent(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,SoundCategory.AMBIENT);
		addEvent(SoundEvents.AMBIENT_CAVE.value(),SoundCategory.AMBIENT);
	}
	private static void addEvent(SoundEvent event,SoundCategory category){
		oneshotEvents.put(event,category);
	}
	private static void addStepEvent(SoundEvent event){
		stepEvents.add(event);
	}

	public ParanoiaStatusEffect(StatusEffectCategory statusEffectCategory, int color) {
		super(statusEffectCategory, color);
	}

	@Environment(EnvType.CLIENT)
	public static void tickClient(){
		var playerEntity = MinecraftClient.getInstance().player;
		if(playerEntity==null) return;
		var effectInst = playerEntity.getStatusEffect(ModStatusEffects.PARANOIA);
		if(effectInst==null) return;
		if(!ModStatusEffects.PARANOIA.canApplyUpdateEffect(effectInst.getDuration(),effectInst.getAmplifier())) return;
		World world = playerEntity.getWorld();

		if(Toolbox.random.nextFloat() < 0.7f)
		{
			// oneshot event
			var events = oneshotEvents.keySet().stream().toList();
			SoundEvent event = events.get(Toolbox.random.nextInt(events.size()));

			// position the footstep event around the player
			Vec3d pos = playerEntity.getPos();
			Vec3d offset = Vec3d.fromPolar(0,(float)(Toolbox.random.nextFloat()*Math.PI*2));
			pos.add(offset.multiply(Toolbox.random.nextFloat()*5+5));

			if(world!=null)
				world.playSound(pos.x,pos.y,pos.z,event,oneshotEvents.get(event),1,Toolbox.random.nextFloat()*0.4f+0.8f,true);

		}
		else{
			// footstep event
			StepEvent event = new StepEvent();

			// take the block the player is standing on
			if(playerEntity.supportingBlockPos.isPresent()){
				// player footstep
				if(Toolbox.random.nextFloat() < 0.5f)
				{
					var state = playerEntity.getWorld().getBlockState(playerEntity.supportingBlockPos.get());
					event.event = state.getBlock().getSoundGroup(state).getStepSound();
				}
				// mob footstep
				else
				{
					int mobCase = Toolbox.random.nextInt(2);
					event.event = switch(mobCase){
						case 1 -> SoundEvents.ENTITY_SKELETON_STEP;
						default -> SoundEvents.ENTITY_ZOMBIE_STEP;
					};
				}


				// position the footstep event around the player
				Vec3d pos = playerEntity.getPos();
				Vec3d offset = Vec3d.fromPolar(0,(float)(Toolbox.random.nextFloat()*Math.PI*2));
				Vec3d dir = Vec3d.fromPolar(0,(float)(Toolbox.random.nextFloat()*Math.PI*2));
				pos.add(offset.multiply(Toolbox.random.nextFloat()*5+5));
				event.pos = pos;
				event.dir = dir;
				event.cooldownPerSound = Toolbox.random.nextInt(10,20);
				currentStepEvents.add(event);
			}
		}

		for(var ev : currentStepEvents){
			ev.tick();
		}
		currentStepEvents.removeIf(stepEvent -> !stepEvent.keep());
	}

    @Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		int i = 200 >> amplifier;
		if (i > 0) {
			return duration % i == 0;
		}
		return true;
	}

	public static class StepEvent{
		public int durationLeft = 100;
		public int cooldownPerSound = 20;
		public int cooldown = 0;
		public SoundEvent event;
		public Vec3d pos;
		public Vec3d dir;

		@Environment(EnvType.CLIENT)
		public void tick(){
			if(--cooldown<=0){
				cooldown +=cooldownPerSound;
				// play sound
				if(MinecraftClient.getInstance().world!=null)
					MinecraftClient.getInstance().world.playSound(pos.x,pos.y,pos.z,event,SoundCategory.PLAYERS,0.3f,Toolbox.random.nextFloat()*0.4f+0.8f,true);
				pos.add(dir);
			}
			durationLeft--;
		}

		public boolean keep(){
			return durationLeft >0;
		}
	}
}