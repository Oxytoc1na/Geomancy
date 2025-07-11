// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class stellge_engineer extends EntityModel<StellgeEngineerModel> {
	private final ModelPart bb_main;
	public stellge_engineer(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(-21, -14).cuboid(4.0F, -2.0F, -8.0F, 4.0F, 2.0F, 16.0F, new Dilation(0.0F))
		.uv(-21, -14).cuboid(-8.0F, -2.0F, -8.0F, 4.0F, 2.0F, 16.0F, new Dilation(0.0F))
		.uv(-21, -14).cuboid(-4.0F, -2.0F, -8.0F, 8.0F, 2.0F, 4.0F, new Dilation(0.0F))
		.uv(-21, -14).cuboid(-4.0F, -2.0F, 4.0F, 8.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}
	@Override
	public void setAngles(StellgeEngineerModel entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}