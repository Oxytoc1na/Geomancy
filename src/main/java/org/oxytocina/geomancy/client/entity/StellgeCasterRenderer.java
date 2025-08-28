package org.oxytocina.geomancy.client.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.StellgeCasterEntity;
import org.oxytocina.geomancy.entity.StellgeEngineerEntity;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class StellgeCasterRenderer extends MobEntityRenderer<StellgeCasterEntity, StellgeCasterModel<StellgeCasterEntity>> {

    public StellgeCasterRenderer(EntityRendererFactory.Context context) {
        super(context, new StellgeCasterModel<>(context.getPart(ModEntityRenderers.MODEL_STELLGE_CASTER_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(StellgeCasterEntity entity) {
        return Geomancy.locate("textures/entity/stellge_caster/main.png");
    }

    @Override
    public void render(StellgeCasterEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}