package org.oxytocina.geomancy.client.rendering.armor;

import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.items.armor.materials.MithrilArmorItem;
import org.oxytocina.geomancy.items.armor.materials.OctanguliteArmorItem;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;
import org.oxytocina.geomancy.util.SimplexNoise;
import org.oxytocina.geomancy.util.Toolbox;

public class MithrilArmorModel extends BipedEntityModel<LivingEntity> {
    final EquipmentSlot slot;
    final ModelPart root;

    public MithrilArmorModel(ModelPart root, EquipmentSlot slot) {
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

        var headGem = root.addChild("head_gem", ModelPartBuilder.create()
                .uv(0+64, 0).cuboid(-4F, -8.125F, -4F, 8.0F, 8.0F, 8.0F, new Dilation(dil+0.01f)), ModelTransform.NONE);

        var body = root.addChild("body", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.5F, 0.5F));

        var body_armor = body.addChild("torso_armor", ModelPartBuilder.create()
                .uv(16, 16)
                .cuboid(-4F, 0F, -2F, 8.0F, 12, 4.0F, new Dilation(dil)),
                ModelTransform.NONE);

        var right_arm = root.addChild("right_arm", ModelPartBuilder.create()
                .uv(40, 16).cuboid(-3.0F, -2F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil))
                , ModelTransform.pivot(-4.0F, 2.0F, 0.0F));
        var right_arm_gem = root.addChild("right_arm_gem", ModelPartBuilder.create()
                .uv(40+64, 16).cuboid(-3F, -2F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil+0.01f))
                , ModelTransform.pivot(-4f,2f, 0.0F));

        var left_arm = root.addChild("left_arm", ModelPartBuilder.create()
                .uv(40, 16).mirrored().cuboid(-1.0F, -2.0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil)).mirrored(false),
                ModelTransform.pivot(4.0F, 2.5F, 0.0F));
        var left_arm_gem = root.addChild("left_arm_gem", ModelPartBuilder.create()
                .uv(40+64, 16).mirrored().cuboid(-1F, -2F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil+0.01f)).mirrored(false)
                , ModelTransform.pivot(4,2.5f, 0.0F));

        var left_leg = root.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 12.0F, 0.0F));

        var left_leg_armor = left_leg.addChild("left_leg_armor", ModelPartBuilder.create()
                .uv(0, 48).mirrored().cuboid(-2F, -0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(0.4f)).mirrored(false), ModelTransform.NONE);
        var left_leg_gem = left_leg.addChild("left_leg_gem", ModelPartBuilder.create()
                .uv(0+64, 48).mirrored().cuboid(-2F, -0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(0.4f+0.01f)).mirrored(false), ModelTransform.NONE);

        var left_boot = left_leg.addChild("left_boot", ModelPartBuilder.create()
               .uv(0, 16).mirrored().cuboid(-2F, 0, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil)).mirrored(false), ModelTransform.NONE);
        var left_boot_gem = left_leg.addChild("left_boot_gem", ModelPartBuilder.create()
                .uv(0+64, 16).mirrored().cuboid(-2F, 0, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil+0.01f)).mirrored(false), ModelTransform.NONE);

        var right_leg = root.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 12.0F, 0.0F));

        var right_leg_armor = right_leg.addChild("right_leg_armor", ModelPartBuilder.create()
                .uv(0, 48).cuboid(-2F, -0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(0.4f)), ModelTransform.NONE);
        var right_leg_gem = right_leg.addChild("right_leg_gem", ModelPartBuilder.create()
                .uv(0+64, 48).cuboid(-2F, -0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(0.4f+0.01f)), ModelTransform.NONE);

        var panty_armor = body.addChild("panty_armor", ModelPartBuilder.create()
                .uv(16, 48).cuboid(-4F, -0F, -2F, 8.0F, 12.0F, 4.0F, new Dilation(dil/2)), ModelTransform.NONE);

        var right_boot = right_leg.addChild("right_boot", ModelPartBuilder.create()
                .uv(0, 16).cuboid(-2F, 0, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil)), ModelTransform.NONE);
        var right_boot_gem = right_leg.addChild("right_boot_gem", ModelPartBuilder.create()
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
        var rightArmGem = root.getChild("right_arm_gem");
        rightArmGem.visible = false;
        var leftArmGem = root.getChild("left_arm_gem");
        leftArmGem.visible = false;

        var headGem = root.getChild("head_gem");
        headGem.visible = false;

        var rightLegGem = rightLeg.getChild("right_leg_gem");
        rightLegGem.visible = false;
        var leftLegGem = leftLeg.getChild("left_leg_gem");
        leftLegGem.visible = false;

        var rightBootGem = rightLeg.getChild("right_boot_gem");
        rightBootGem.visible = false;
        var leftBootGem = leftLeg.getChild("left_boot_gem");
        leftBootGem.visible = false;

        switch (slot) {
            case HEAD ->{
                head.visible = true;
                headGem.visible = gem1!=null;
                if(gem1!=null)
                {
                    var col = Toolbox.colorIntToVec(gem1);
                    copyTransform(head,headGem);
                    headGem.render(ms,buffer,light,overlay,col.x,col.y,col.z,a);
                    headGem.visible=false;
                }
            }
            case CHEST -> {
                body.visible = true;
                rightArm.visible = true;
                leftArm.visible = true;
                body.getChild("torso_armor").visible = true;
                rightArmGem.visible = gem1!=null;
                if(gem1!=null)
                {
                    var col = Toolbox.colorIntToVec(gem1);
                    copyTransform(rightArm,rightArmGem);
                    rightArmGem.render(ms,buffer,light,overlay,col.x,col.y,col.z,a);
                    rightArmGem.visible=false;
                }
                leftArmGem.visible = gem2!=null;
                if(gem2!=null)
                {
                    var col = Toolbox.colorIntToVec(gem2);
                    copyTransform(leftArm,leftArmGem);
                    leftArmGem.render(ms,buffer,light,overlay,col.x,col.y,col.z,a);
                    leftArmGem.visible=false;
                }
            }
            case LEGS -> {
                body.visible = true;
                rightLeg.visible = true;
                leftLeg.visible = true;
                rightLeg.getChild("right_leg_armor").visible = true;
                leftLeg.getChild("left_leg_armor").visible = true;
                body.getChild("panty_armor").visible = true;
                leftLegGem.visible = gem1!=null;
                rightLegGem.visible = gem1!=null;
                if(gem1!=null)
                {
                    var col = Toolbox.colorIntToVec(gem1);
                    copyTransform(leftLeg,leftLegGem);
                    leftLegGem.render(ms,buffer,light,overlay,col.x,col.y,col.z,a);
                    leftLegGem.visible=false;

                    copyTransform(rightLeg,rightLegGem);
                    rightLegGem.render(ms,buffer,light,overlay,col.x,col.y,col.z,a);
                    rightLegGem.visible=false;
                }
            }
            case FEET -> {
                rightLeg.visible = true;
                leftLeg.visible = true;
                rightLeg.getChild("right_boot").visible = true;
                leftLeg.getChild("left_boot").visible = true;
                leftBootGem.visible = gem1!=null;
                rightBootGem.visible = gem1!=null;
                if(gem1!=null)
                {
                    var col = Toolbox.colorIntToVec(gem1);
                    copyTransform(leftLeg,leftBootGem);
                    leftBootGem.render(ms,buffer,light,overlay,col.x,col.y,col.z,a);
                    leftBootGem.visible=false;

                    copyTransform(rightLeg,rightBootGem);
                    rightBootGem.render(ms,buffer,light,overlay,col.x,col.y,col.z,a);
                    rightBootGem.visible=false;
                }
            }
            case MAINHAND, OFFHAND -> {
            }
        }
    }

    public static void renderPartStatic (MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {

        MithrilArmorItem armor = (MithrilArmorItem) stack.getItem();
        var model = armor.getArmorModel();
        var texture = armor.getArmorTexture(stack, slot);
        contextModel.copyBipedStateTo(model);

        renderPart(matrices, vertexConsumers, light, stack, model, texture,1,1,1,1);

    }

    static Integer gem1 = null;
    static Integer gem2 = null;

    public static void renderPart(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, Model model, Identifier texture, float r, float g, float b, float a) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), false, stack.hasGlint());
        var gems = IJewelryItem.getSlots(stack);
        gem1 = gems.isEmpty()?null:gems.get(0).getColor();
        gem2 = gems.size()<2?null:gems.get(1).getColor();
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
}
