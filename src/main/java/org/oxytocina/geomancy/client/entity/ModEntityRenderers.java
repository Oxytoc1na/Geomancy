package org.oxytocina.geomancy.client.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.CasterDelegateEntity;
import org.oxytocina.geomancy.entity.ModEntityTypes;

public class ModEntityRenderers {

    public static final EntityModelLayer MODEL_STELLGE_ENGINEER_LAYER = new EntityModelLayer(Geomancy.locate("stellge_engineer"), "main");
    public static final EntityModelLayer MODEL_STELLGE_CASTER_LAYER = new EntityModelLayer(Geomancy.locate("stellge_caster"), "main");

    public static void register(){
        EntityRendererRegistry.register(ModEntityTypes.STELLGE_ENGINEER, StellgeEngineerRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_STELLGE_ENGINEER_LAYER, StellgeEngineerModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntityTypes.STELLGE_CASTER, StellgeCasterRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_STELLGE_CASTER_LAYER, StellgeCasterModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntityTypes.CASTER_DELEGATE, EmptyEntityRenderer::new);
    }
}
