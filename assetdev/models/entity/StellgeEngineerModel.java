// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class StellgeEngineerModel extends EntityModel<StellgeEngineer> {
	private final ModelPart copperring1;
	private final ModelPart copperring2;
	private final ModelPart sphere;
	private final ModelPart hand1;
	private final ModelPart hand2;
	public StellgeEngineerModel(ModelPart root) {
		this.copperring1 = root.getChild("copperring1");
		this.copperring2 = root.getChild("copperring2");
		this.sphere = root.getChild("sphere");
		this.hand1 = root.getChild("hand1");
		this.hand2 = root.getChild("hand2");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData copperring1 = modelPartData.addChild("copperring1", ModelPartBuilder.create().uv(0, 20).cuboid(4.0F, -2.0F, -8.0F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-8.0F, -2.0F, -8.0F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F))
		.uv(40, 0).cuboid(-4.0F, -2.0F, 4.0F, 8.0F, 4.0F, 4.0F, new Dilation(0.0F))
		.uv(40, 8).cuboid(-4.0F, -2.0F, -8.0F, 8.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 22.0F, 0.0F));

		ModelPartData copperring2 = modelPartData.addChild("copperring2", ModelPartBuilder.create().uv(0, 20).cuboid(4.0F, -2.0F, -8.0F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-8.0F, -2.0F, -8.0F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F))
		.uv(40, 0).cuboid(-4.0F, -2.0F, 4.0F, 8.0F, 4.0F, 4.0F, new Dilation(0.0F))
		.uv(40, 8).cuboid(-4.0F, -2.0F, -8.0F, 8.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 14.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData sphere = modelPartData.addChild("sphere", ModelPartBuilder.create().uv(0, 40).cuboid(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData hand1 = modelPartData.addChild("hand1", ModelPartBuilder.create().uv(48, 16).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(14.0F, 9.0F, 0.0F));

		ModelPartData hand2 = modelPartData.addChild("hand2", ModelPartBuilder.create().uv(48, 16).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-14.0F, 9.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(StellgeEngineer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		copperring1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		copperring2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		sphere.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		hand1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		hand2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}