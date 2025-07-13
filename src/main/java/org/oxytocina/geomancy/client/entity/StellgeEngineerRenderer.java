package org.oxytocina.geomancy.client.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.StellgeEngineerEntity;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class StellgeEngineerRenderer extends MobEntityRenderer<StellgeEngineerEntity, StellgeEngineerModel<StellgeEngineerEntity>> {

    public StellgeEngineerRenderer(EntityRendererFactory.Context context) {
        super(context, new StellgeEngineerModel<>(context.getPart(ModEntityRenderers.MODEL_STELLGE_ENGINEER_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(StellgeEngineerEntity entity) {
        return Geomancy.locate("textures/entity/stellge_engineer/main.png");
    }

    @Override
    public void render(StellgeEngineerEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}