package org.oxytocina.geomancy.client.rendering.armor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.items.armor.OctanguliteArmorItem;
import org.oxytocina.geomancy.util.SimplexNoise;
import org.oxytocina.geomancy.util.Toolbox;

public class OctanguliteArmorModel extends BipedEntityModel<LivingEntity> {
    final EquipmentSlot slot;

    public OctanguliteArmorModel(ModelPart root, EquipmentSlot slot) {
        super(root);
        this.slot = slot;
    }

    public static ModelData getModelData() {
        ModelData data = new ModelData();
        var root = data.getRoot();


        root.addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);

        final float dil = 1f;

        var head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4F, -8.125F, -4F, 8.0F, 8.0F, 8.0F, new Dilation(1f)), ModelTransform.NONE);

        var body = root.addChild("body", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.5F, 0.5F));

        var body_armor = body.addChild("torso_armor", ModelPartBuilder.create()
                .uv(16, 16)
                .cuboid(-4F, 0F, -2F, 8.0F, 12, 4.0F, new Dilation(dil)),
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

        var left_boot = left_leg.addChild("left_boot", ModelPartBuilder.create()
               .uv(0, 16).mirrored().cuboid(-2F, 0, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil)).mirrored(false), ModelTransform.NONE);

        var right_leg = root.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 12.0F, 0.0F));

        var right_leg_armor = right_leg.addChild("right_leg_armor", ModelPartBuilder.create()
                .uv(0, 48).cuboid(-2F, -0F, -2F, 4.0F, 12.0F, 4.0F, new Dilation(0.4f)), ModelTransform.NONE);

        var panty_armor = body.addChild("panty_armor", ModelPartBuilder.create()
                .uv(16, 48).cuboid(-4F, -0F, -2F, 8.0F, 12.0F, 4.0F, new Dilation(dil/2)), ModelTransform.NONE);


        var right_boot = right_leg.addChild("right_boot", ModelPartBuilder.create()
                .uv(0, 16).cuboid(-2F, 0, -2F, 4.0F, 12.0F, 4.0F, new Dilation(dil)), ModelTransform.NONE);
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
        this.hat.copyTransform(head);
    }

    @Override
    public void render(MatrixStack ms, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        renderArmorPart(slot);
        super.render(ms, buffer, light, overlay, r, g, b, a);
    }


    private void renderArmorPart(EquipmentSlot slot) {
        setVisible(false);
        rightLeg.getChild("right_leg_armor").visible = false;
        leftLeg.getChild("left_leg_armor").visible = false;
        body.getChild("panty_armor").visible = false;
        body.getChild("torso_armor").visible = false;
        rightLeg.getChild("right_boot").visible = false;
        leftLeg.getChild("left_boot").visible = false;
        switch (slot) {
            case HEAD -> head.visible = true;
            case CHEST -> {
                body.visible = true;
                rightArm.visible = true;
                leftArm.visible = true;
                body.getChild("torso_armor").visible = true;
            }
            case LEGS -> {
                body.visible = true;
                rightLeg.visible = true;
                leftLeg.visible = true;
                rightLeg.getChild("right_leg_armor").visible = true;
                leftLeg.getChild("left_leg_armor").visible = true;
                body.getChild("panty_armor").visible = true;
            }
            case FEET -> {
                rightLeg.visible = true;
                leftLeg.visible = true;
                rightLeg.getChild("right_boot").visible = true;
                leftLeg.getChild("left_boot").visible = true;
            }
            case MAINHAND, OFFHAND -> {
            }
        }
    }

    public static void renderPartStatic (MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {

        OctanguliteArmorItem armor = (OctanguliteArmorItem) stack.getItem();
        var model = armor.getArmorModel();
        var texture = armor.getArmorTexture(stack, slot);
        contextModel.copyBipedStateTo(model);

        int col = octanguliteNoise(stack,entity);
        var colVel = Toolbox.colorIntToVec(col);

        ModArmorRenderers.renderPart(matrices, vertexConsumers, light, stack, model, texture,colVel.x,colVel.y,colVel.z,1);

    }

    public static int octanguliteNoise(ItemStack stack,LivingEntity entity){
        final float zoom = 0.03f;
        final float speed = 0.01f;
        final int tintIndex = 0;

        float hue = 0;
        float sat = 1;
        float val = 1;

        float baseX, baseY, baseZ;

        if(entity!=null){
            Vec3d pos = entity.getPos();

            baseX = (float)pos.getX();
            baseY = (float)pos.getY();
            baseZ = (float)pos.getZ();
        }
        else if(MinecraftClient.getInstance() != null)
        {
            if(MinecraftClient.getInstance().cameraEntity!=null) {
                Vec3d pos = MinecraftClient.getInstance().cameraEntity.getPos();
                baseX = (float) pos.getX();
                baseY = (float) pos.getY();
                baseZ = (float) pos.getZ();
            }
            else{
                baseX = 0;
                baseY = 0;
                baseZ = 0;
            }


        }
        else{
            baseX = 0;
            baseY = 0;
            baseZ = 0;
        }

        baseX += speed* GeomancyClient.tick;

        float x = zoom*baseX * (1+tintIndex*0.3f) + tintIndex*16;
        float y = zoom*baseY * (1+tintIndex*0.3f) + tintIndex*16;
        float z = zoom*baseZ * (1+tintIndex*0.3f) + tintIndex*16;

        float x2 = zoom*1.5f*((baseX+230) * (1+tintIndex*0.3f) + tintIndex*16);
        float y2 = zoom*1.5f*((baseY+590) * (1+tintIndex*0.3f) + tintIndex*16);
        float z2 = zoom*1.5f*((baseZ+367) * (1+tintIndex*0.3f) + tintIndex*16);

        float x3 = zoom*2f*((baseX+129) * (1+tintIndex*0.3f) + tintIndex*16);
        float y3 = zoom*2f*((baseY+395) * (1+tintIndex*0.3f) + tintIndex*16);
        float z3 = zoom*2f*((baseZ+529) * (1+tintIndex*0.3f) + tintIndex*16);

        hue = (float)(org.oxytocina.geomancy.util.SimplexNoise.noise(x,y,z)+1)/2;
        sat = (float) (1-Math.pow(1F-((SimplexNoise.noise(x2,y2,z2)+1)/2),2));
        val = (float) (1-Math.pow(1F-((SimplexNoise.noise(x3,y3,z3)+1)/2),2));

        float durability = 1;
        if(stack.isDamageable()){
            durability = 1f-(stack.getDamage()/(float)stack.getMaxDamage());
        }
        hue = MathHelper.lerp(durability,0,hue);

        return ModColorizationHandler.hsvToRgb(
                hue,sat,val
        );
    }
}