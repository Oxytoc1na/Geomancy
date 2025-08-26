package org.oxytocina.geomancy.client.rendering.armor;

import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.items.armor.CastingArmorItem;
import org.oxytocina.geomancy.items.armor.MithrilArmorItem;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.util.Toolbox;

public class CastingArmorModel extends BipedEntityModel<LivingEntity> {
    final EquipmentSlot slot;
    final ModelPart root;

    public CastingArmorModel(ModelPart root, EquipmentSlot slot) {
        super(root);
        this.slot = slot;
        this.root=root;
    }

    public static ModelData getModelData() {
        ModelData data = new ModelData();
        var root = data.getRoot();


        root.addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);

        final float dil = 1f;

        var head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4F, -8.125F, -4F, 8.0F, 8.0F, 8.0F, new Dilation(dil)), ModelTransform.NONE);

        var headOverlay = root.addChild("head_overlay", ModelPartBuilder.create()
                .uv(0+64, 0).cuboid(-4F, -8.125F, -4F, 8.0F, 8.0F, 8.0F, new Dilation(dil+0.01f)), ModelTransform.NONE);

        var body = root.addChild("body", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.5F, 0.5F));

        var body_armor = body.addChild("torso_armor", ModelPartBuilder.create()
                        .uv(16, 16)
                        .cuboid(-4F, 0F, -2F, 8.0F, 12, 4.0F, new Dilation(dil)),
                ModelTransform.NONE);

        var body_overlay = body.addChild("torso_overlay", ModelPartBuilder.create()
                        .uv(16+64, 16)
                        .cuboid(-4F, 0F, -2F, 8.0F, 12, 4.0F, new Dilation(dil+0.01f)),
                ModelTransform.NONE);

        var right_arm = root.addChild("right_arm", ModelPartBuilder.create()
                        .uv(40, 16).cuboid(-3.0F, -2F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil))
                , ModelTransform.pivot(-4.0F, 2.0F, 0.0F));
        var left_arm = root.addChild("left_arm", ModelPartBuilder.create()
                        .uv(40, 16).mirrored().cuboid(-1.0F, -2.0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil)).mirrored(false),
                ModelTransform.pivot(4.0F, 2.5F, 0.0F));

        var left_leg = root.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 12.0F, 0.0F));

        var left_leg_armor = left_leg.addChild("left_leg_armor", ModelPartBuilder.create()
                .uv(0, 48).mirrored().cuboid(-2F, -0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(0.4f)).mirrored(false), ModelTransform.NONE);
        var left_leg_gem = left_leg.addChild("left_leg_overlay", ModelPartBuilder.create()
                .uv(0+64, 48).mirrored().cuboid(-2F, -0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(0.4f+0.01f)).mirrored(false), ModelTransform.NONE);

        var left_boot = left_leg.addChild("left_boot", ModelPartBuilder.create()
                .uv(0, 16).mirrored().cuboid(-2F, 0, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil)).mirrored(false), ModelTransform.NONE);
        var left_boot_gem = left_leg.addChild("left_boot_overlay", ModelPartBuilder.create()
                .uv(0+64, 16).mirrored().cuboid(-2F, 0, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil+0.01f)).mirrored(false), ModelTransform.NONE);

        var right_leg = root.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 12.0F, 0.0F));

        var right_leg_armor = right_leg.addChild("right_leg_armor", ModelPartBuilder.create()
                .uv(0, 48).cuboid(-2F, -0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(0.4f)), ModelTransform.NONE);
        var right_leg_gem = right_leg.addChild("right_leg_overlay", ModelPartBuilder.create()
                .uv(0+64, 48).cuboid(-2F, -0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(0.4f+0.01f)), ModelTransform.NONE);

        var panty_armor = body.addChild("panty_armor", ModelPartBuilder.create()
                .uv(16, 48).cuboid(-4F, -0F, -2F, 8.0F, 12.0F, 4.0F, new Dilation(dil/2)), ModelTransform.NONE);

        var right_boot = right_leg.addChild("right_boot", ModelPartBuilder.create()
                .uv(0, 16).cuboid(-2F, 0, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil)), ModelTransform.NONE);
        var right_boot_gem = right_leg.addChild("right_boot_overlay", ModelPartBuilder.create()
                .uv(0+64, 16).cuboid(-2F, 0, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil+0.01f)), ModelTransform.NONE);


        return data;
    }

    @Override
    public void animateModel(LivingEntity livingEntity, float f, float g, float h) {
        super.animateModel(livingEntity, f, g, h);
    }

    @Override
    public void setAngles(LivingEntity entity, float f, float g, float h, float i, float j) {
        if (!(entity instanceof ArmorStandEntity stand)) {
            super.setAngles(entity, f, g, h, i, j);
            copyTransform(rightArm,root.getChild("right_arm_gem"));
            copyTransform(leftArm,root.getChild("left_arm_gem"));
            copyTransform(rightLeg,rightLeg.getChild("right_leg_gem"));
            copyTransform(leftLeg,leftLeg.getChild("left_leg_gem"));
            copyTransform(rightLeg,rightLeg.getChild("right_boot_gem"));
            copyTransform(leftLeg,leftLeg.getChild("left_boot_gem"));
            copyTransform(head,root.getChild("head_gem"));


            return;
        }

        this.head.pitch = ((float) Math.PI / 180F) * stand.getHeadRotation().getPitch();
        this.head.yaw = ((float) Math.PI / 180F) * stand.getHeadRotation().getYaw();
        this.head.roll = ((float) Math.PI / 180F) * stand.getHeadRotation().getRoll();
        this.head.setPivot(0.0F, 1.0F, 0.0F);
        this.body.pitch = ((float) Math.PI / 180F) * stand.getBodyRotation().getPitch();
        this.body.yaw = ((float) Math.PI / 180F) * stand.getBodyRotation().getYaw();
        this.body.roll = ((float) Math.PI / 180F) * stand.getBodyRotation().getRoll();
        this.leftArm.pitch = ((float) Math.PI / 180F) * stand.getLeftArmRotation().getPitch();
        this.leftArm.yaw = ((float) Math.PI / 180F) * stand.getLeftArmRotation().getYaw();
        this.leftArm.roll = ((float) Math.PI / 180F) * stand.getLeftArmRotation().getRoll();
        this.rightArm.pitch = ((float) Math.PI / 180F) * stand.getRightArmRotation().getPitch();
        this.rightArm.yaw = ((float) Math.PI / 180F) * stand.getRightArmRotation().getYaw();
        this.rightArm.roll = ((float) Math.PI / 180F) * stand.getRightArmRotation().getRoll();
        this.leftLeg.pitch = ((float) Math.PI / 180F) * stand.getLeftLegRotation().getPitch();
        this.leftLeg.yaw = ((float) Math.PI / 180F) * stand.getLeftLegRotation().getYaw();
        this.leftLeg.roll = ((float) Math.PI / 180F) * stand.getLeftLegRotation().getRoll();
        this.leftLeg.setPivot(1.9F, 11.0F, 0.0F);
        this.rightLeg.pitch = ((float) Math.PI / 180F) * stand.getRightLegRotation().getPitch();
        this.rightLeg.yaw = ((float) Math.PI / 180F) * stand.getRightLegRotation().getYaw();
        this.rightLeg.roll = ((float) Math.PI / 180F) * stand.getRightLegRotation().getRoll();
        this.rightLeg.setPivot(-1.9F, 11.0F, 0.0F);
        copyTransform(rightArm,root.getChild("right_arm_gem"));
        copyTransform(leftArm,root.getChild("left_arm_gem"));
        copyTransform(rightLeg,rightLeg.getChild("right_leg_gem"));
        copyTransform(leftLeg,leftLeg.getChild("left_leg_gem"));
        copyTransform(rightLeg,rightLeg.getChild("right_boot_gem"));
        copyTransform(leftLeg,leftLeg.getChild("left_boot_gem"));
        copyTransform(head,root.getChild("head_gem"));

        this.hat.copyTransform(head);
    }

    @Override
    public void render(MatrixStack ms, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        renderArmorPart(slot,ms,buffer,light,overlay,r,g,b,a);
        super.render(ms, buffer, light, overlay, r, g, b, a);
    }


    private void renderArmorPart(EquipmentSlot slot,MatrixStack ms, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        setVisible(false);
        rightLeg.getChild("right_leg_armor").visible = false;
        leftLeg.getChild("left_leg_armor").visible = false;
        body.getChild("panty_armor").visible = false;
        body.getChild("torso_armor").visible = false;
        rightLeg.getChild("right_boot").visible = false;
        leftLeg.getChild("left_boot").visible = false;
        body.getChild("torso_overlay").visible = false;
        var bodyOverlay = body.getChild("torso_overlay");
        bodyOverlay.visible = false;

        var headOverlay = root.getChild("head_overlay");
        headOverlay.visible = false;

        var rightLegOverlay = rightLeg.getChild("right_leg_overlay");
        rightLegOverlay.visible = false;
        var leftLegOverlay = leftLeg.getChild("left_leg_overlay");
        leftLegOverlay.visible = false;

        var rightBootOverlay = rightLeg.getChild("right_boot_overlay");
        rightBootOverlay.visible = false;
        var leftBootOverlay = leftLeg.getChild("left_boot_overlay");
        leftBootOverlay.visible = false;

        switch (slot) {
            case HEAD ->{
                head.visible = true;
                renderPartColorless(headOverlay,root.getChild("head"),ms,buffer,light,overlay,a);
                headOverlay.visible = true;
            }
            case CHEST -> {
                body.visible = true;
                rightArm.visible = true;
                leftArm.visible = true;
                body.getChild("torso_armor").visible = true;
                renderPartColorless(bodyOverlay,body.getChild("torso_armor"),ms,buffer,light,overlay,a);
            }
            case LEGS -> {
                body.visible = true;
                rightLeg.visible = true;
                leftLeg.visible = true;
                rightLeg.getChild("right_leg_armor").visible = true;
                leftLeg.getChild("left_leg_armor").visible = true;
                body.getChild("panty_armor").visible = true;
                renderPartColorless(leftLegOverlay,leftLeg,ms,buffer,light,overlay,a);
                renderPartColorless(rightLegOverlay,rightLeg,ms,buffer,light,overlay,a);
            }
            case FEET -> {
                rightLeg.visible = true;
                leftLeg.visible = true;
                rightLeg.getChild("right_boot").visible = true;
                leftLeg.getChild("left_boot").visible = true;
                renderPartColorless(leftBootOverlay,leftLeg.getChild("left_boot"),ms,buffer,light,overlay,a);
                renderPartColorless(rightBootOverlay,rightLeg.getChild("right_boot"),ms,buffer,light,overlay,a);
            }
            case MAINHAND, OFFHAND -> {
            }
        }
    }

    public static void renderPartStatic (MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {

        CastingArmorItem armor = (CastingArmorItem) stack.getItem();
        var model = armor.getArmorModel();
        var texture = armor.getArmorTexture(stack, slot);
        contextModel.copyBipedStateTo(model);

        int col = OctanguliteArmorModel.octanguliteNoise(stack,entity);
        var colVel = Toolbox.colorIntToVec(col);

        renderPart(matrices, vertexConsumers, light, stack, model, texture,colVel.x,colVel.y,colVel.z,1);

    }

    public static void renderPart(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, Model model, Identifier texture, float r, float g, float b, float a) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), false, stack.hasGlint());
        var gems = IJewelryItem.getSlots(stack);
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, r,g,b,a);
    }

    private static void copyTransform(ModelPart from, ModelPart to){
        to.yaw = from.yaw;
        to.pitch = from.pitch;
        to.roll = from.roll;
        to.pivotX = from.pivotX;
        to.pivotY = from.pivotY;
        to.pivotZ = from.pivotZ;
    }

    private static void renderPartColorless(ModelPart part,ModelPart parent,MatrixStack ms, VertexConsumer buffer,int light, int overlay, float a){
        copyTransform(parent,part);
        part.visible=true;
        part.render(ms,buffer,light,overlay,1,1,1,a);
        part.visible=false;
    }
}