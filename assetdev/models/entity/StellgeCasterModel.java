// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class StellgeCasterModel extends EntityModel<StellgeCasterEntity> {
	private final ModelPart head;
	private final ModelPart ring1;
	private final ModelPart ring2;
	private final ModelPart ring3;
	public StellgeCasterModel(ModelPart root) {
		this.head = root.getChild("head");
		this.ring1 = root.getChild("ring1");
		this.ring2 = root.getChild("ring2");
		this.ring3 = root.getChild("ring3");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(-20, -10).cuboid(-6.0F, -35.0F, -6.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData ring1 = modelPartData.addChild("ring1", ModelPartBuilder.create().uv(-7, 0).cuboid(-8.0F, -2.0F, -8.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(-7, 0).cuboid(-8.0F, -2.0F, 6.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(-3, -10).cuboid(6.0F, -2.0F, -6.0F, 2.0F, 2.0F, 12.0F, new Dilation(0.0F))
		.uv(-3, -10).cuboid(-8.0F, -2.0F, -6.0F, 2.0F, 2.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData ring2 = modelPartData.addChild("ring2", ModelPartBuilder.create().uv(-7, 0).cuboid(-8.0F, -2.0F, -8.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(-7, 0).cuboid(-8.0F, -2.0F, 6.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(-3, -10).cuboid(6.0F, -2.0F, -6.0F, 2.0F, 2.0F, 12.0F, new Dilation(0.0F))
		.uv(-3, -10).cuboid(-8.0F, -2.0F, -6.0F, 2.0F, 2.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 22.0F, 0.0F));

		ModelPartData ring3 = modelPartData.addChild("ring3", ModelPartBuilder.create().uv(-7, 0).cuboid(-8.0F, -2.0F, -8.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(-7, 0).cuboid(-8.0F, -2.0F, 6.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(-3, -10).cuboid(6.0F, -2.0F, -6.0F, 2.0F, 2.0F, 12.0F, new Dilation(0.0F))
		.uv(-3, -10).cuboid(-8.0F, -2.0F, -6.0F, 2.0F, 2.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 20.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}
	@Override
	public void setAngles(StellgeCasterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		ring1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		ring2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		ring3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}