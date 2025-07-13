package org.oxytocina.geomancy.util;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import org.oxytocina.geomancy.client.ModEntityModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AnimationHelper {
    public static void animate(ModEntityModel<?> model, Animation animation, long runningTime, float scale, Vector3f tempVec) {
        float runningSeconds = getRunningSeconds(animation, runningTime);

        for (Map.Entry<String, List<Transformation>> entry : animation.boneAnimations().entrySet()) {
            Optional<ModelPart> optional = model.root.hasChild(entry.getKey()) ? Optional.of(model.root.getChild(entry.getKey())) : Optional.empty();
            List<Transformation> list = entry.getValue();
            optional.ifPresent(part -> list.forEach(transformation -> {
                Keyframe[] keyframes = transformation.keyframes();
                int lastKeyframeIndex = Math.max(0, MathHelper.binarySearch(0, keyframes.length, index -> runningSeconds <= keyframes[index].timestamp()) - 1);
                int nextKeyframeIndex = Math.min(keyframes.length - 1, lastKeyframeIndex + 1);
                Keyframe lastKeyframe = keyframes[lastKeyframeIndex];
                Keyframe nextKeyframe = keyframes[nextKeyframeIndex];
                float secondsSinceLastKeyframe = runningSeconds - lastKeyframe.timestamp();
                float progressToNextKeyframe;
                if (nextKeyframeIndex != lastKeyframeIndex) {
                    progressToNextKeyframe = MathHelper.clamp(secondsSinceLastKeyframe / (nextKeyframe.timestamp() - lastKeyframe.timestamp()), 0.0F, 1.0F);
                } else {
                    progressToNextKeyframe = 0.0F;
                }

                nextKeyframe.interpolation().apply(tempVec, progressToNextKeyframe, keyframes, lastKeyframeIndex, nextKeyframeIndex, scale);
                transformation.target().apply(part, tempVec);
            }));
        }
    }

    private static float getRunningSeconds(Animation animation, long runningTime) {
        float f = (float)runningTime / 1000.0F;
        return animation.looping() ? f % animation.lengthInSeconds() : f;
    }
}
