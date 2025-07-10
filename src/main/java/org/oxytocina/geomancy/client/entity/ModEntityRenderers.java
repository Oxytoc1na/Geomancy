package org.oxytocina.geomancy.client.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.ModEntityTypes;

public class ModEntityRenderers {

    public static final EntityModelLayer MODEL_CUBE_LAYER = new EntityModelLayer(Geomancy.locate("cube"), "main");

    public static void register(){
        /*
         * Registers our Cube Entity's renderer, which provides a model and texture for the entity.
         *
         * Entity Renderers can also manipulate the model before it renders based on entity context (EndermanEntityRenderer#render).
         */
        // In 1.17, use EntityRendererRegistry.register (seen below) instead of EntityRendererRegistry.INSTANCE.register (seen above)
        EntityRendererRegistry.register(ModEntityTypes.STELLGE_ENGINEER, StellgeEngineerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_CUBE_LAYER, StellgeEngineerModel::getTexturedModelData);
    }
}
