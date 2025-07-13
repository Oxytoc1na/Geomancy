// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class StellgeEngineerModel extends EntityModel<StellgeEngineer> {
	private final ModelPart stellgeEngineer;
	private final ModelPart copperring1;
	private final ModelPart copperring2;
	private final ModelPart sphere;
	private final ModelPart hand1;
	private final ModelPart hand2;
	public StellgeEngineerModel(ModelPart root) {
		this.stellgeEngineer = root.getChild("stellgeEngineer");
		this.copperring1 = this.stellgeEngineer.getChild("copperring1");
		this.copperring2 = this.stellgeEngineer.getChild("copperring2");
		this.sphere = this.stellgeEngineer.getChild("sphere");
		this.hand1 = this.stellgeEngineer.getChild("hand1");
		this.hand2 = this.stellgeEngineer.getChild("hand2");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData stellgeEngineer = modelPartData.addChild("stellgeEngineer", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData copperring1 = stellgeEngineer.addChild("copperring1", ModelPartBuilder.create().uv(0, 20).cuboid(4.0F, -9.0F, -8.0F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-8.0F, -9.0F, -8.0F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F))
		.uv(40, 0).cuboid(-4.0F, -9.0F, 4.0F, 8.0F, 4.0F, 4.0F, new Dilation(0.0F))
		.uv(40, 8).cuboid(-4.0F, -9.0F, -8.0F, 8.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

		ModelPartData copperring2 = stellgeEngineer.addChild("copperring2", ModelPartBuilder.create().uv(0, 20).cuboid(4.0F, -9.0F, -8.0F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-8.0F, -9.0F, -8.0F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F))
		.uv(40, 0).cuboid(-4.0F, -9.0F, 4.0F, 8.0F, 4.0F, 4.0F, new Dilation(0.0F))
		.uv(40, 8).cuboid(-4.0F, -9.0F, -8.0F, 8.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData sphere = stellgeEngineer.addChild("sphere", ModelPartBuilder.create().uv(0, 40).cuboid(-6.0F, -19.0F, -6.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -16.0F, 0.0F));

		ModelPartData hand1 = stellgeEngineer.addChild("hand1", ModelPartBuilder.create().uv(48, 16).cuboid(-2.0F, -9.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(14.0F, -15.0F, 0.0F));

		ModelPartData hand2 = stellgeEngineer.addChild("hand2", ModelPartBuilder.create().uv(48, 16).cuboid(-2.0F, -9.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-14.0F, -15.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(StellgeEngineer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		stellgeEngineer.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}