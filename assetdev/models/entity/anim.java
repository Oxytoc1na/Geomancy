// Save this class in your mod and generate all required imports
/**
 * Made with Blockbench 4.12.5
 * Exported for Minecraft version 1.19 or later with Yarn mappings
 * @author Author
 */
public class StellgeEngineerModelAnimation {
	public static final Animation idle = Animation.Builder.create(8.0F).looping()
		.addBoneAnimation("copperring1", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(10.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 360.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.0F, AnimationHelper.createRotationalVector(10.0F, 720.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("copperring1", new Transformation(Transformation.Targets.SCALE, 
			new Keyframe(0.0F, AnimationHelper.createScalingVector(0.8F, 0.8F, 0.8F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("copperring2", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(4.0F, AnimationHelper.createRotationalVector(10.0F, -360.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(8.0F, AnimationHelper.createRotationalVector(0.0F, -720.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("sphere", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 5.0F, -5.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, -5.0F, 5.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(8.0F, AnimationHelper.createRotationalVector(0.0F, 5.0F, -5.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("sphere", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(8.0F, AnimationHelper.createTranslationalVector(0.0F, -2.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("hand2", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(8.0F, 0.0F, -8.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -5.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(8.0F, AnimationHelper.createRotationalVector(8.0F, 0.0F, -8.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("hand2", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(8.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("hand1", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(4.0F, AnimationHelper.createRotationalVector(8.0F, 0.0F, 8.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(8.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 5.0F), Transformation.Interpolations.CUBIC)
		))
		.addBoneAnimation("hand1", new Transformation(Transformation.Targets.TRANSLATE, 
			new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.CUBIC),
			new Keyframe(8.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
		))
		.build();
}