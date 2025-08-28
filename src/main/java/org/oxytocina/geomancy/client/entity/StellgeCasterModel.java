package org.oxytocina.geomancy.client.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import org.oxytocina.geomancy.client.entity.animation.StellgeCasterAnimations;
import org.oxytocina.geomancy.client.entity.animation.StellgeEngineerAnimations;
import org.oxytocina.geomancy.entity.StellgeCasterEntity;
import org.oxytocina.geomancy.entity.StellgeEngineerEntity;

public class StellgeCasterModel<T extends StellgeCasterEntity> extends SinglePartEntityModel<T> {
    public final static int RING_COUNT = 3;
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart ring1;
    private final ModelPart ring2;
    private final ModelPart ring3;
    private final ModelPart[] rings;
    public StellgeCasterModel(ModelPart root) {
        this.root=root;
        this.head = root.getChild("head");
        this.ring1 = root.getChild("ring1");
        this.ring2 = root.getChild("ring2");
        this.ring3 = root.getChild("ring3");
        this.rings = new ModelPart[RING_COUNT];
        for (int i = 0; i < RING_COUNT; i++) {
            rings[i] = root.getChild("ring"+(i+1));
        }
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(-20, -10).cuboid(-6.0F, -35.0F, -6.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        for (int i = 0; i < RING_COUNT; i++) {
            ModelPartData ring = modelPartData.addChild("ring"+(i+1), ModelPartBuilder.create().uv(-7, 0).cuboid(-8.0F, -2.0F, -8.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
                    .uv(-7, 0).cuboid(-8.0F, -2.0F, 6.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
                    .uv(-3, -10).cuboid(6.0F, -2.0F, -6.0F, 2.0F, 2.0F, 12.0F, new Dilation(0.0F))
                    .uv(-3, -10).cuboid(-8.0F, -2.0F, -6.0F, 2.0F, 2.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        }

        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        ring1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        ring2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        ring3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
    @Override
    public void setAngles(StellgeCasterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw,headPitch);

        this.updateAnimation(entity.idleAnimationState, StellgeCasterAnimations.IDLE,ageInTicks,1f);

        // animate rings
        entity.ringAnimationState.update(ageInTicks, 1f);
        final float height = 10;
        for (int i = 0; i < RING_COUNT; i++) {
            var ring = rings[i];
            float progress = (entity.ringAnimationState.getTimeRunning()/20f*0.01f + (float)i/RING_COUNT)%1;

            float y = (float)Math.sin(progress*Math.PI*2)*height;
            float scale = 0.5f+((float)Math.cos(progress*Math.PI*2)+1)/2f;

            ring.setPivot(ring.pivotX,y,ring.pivotZ);
            ring.xScale *= scale;
            ring.yScale *= scale;
            ring.zScale *= scale;
        }
    }

    private void setHeadAngles(float headYaw, float headPitch){
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * ((float)Math.PI / 180F);
        this.head.pitch = headPitch * ((float)Math.PI / 180F);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }
}
