package org.oxytocina.geomancy.client.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;

public class LaunchedBlockEntityRenderer extends FallingBlockEntityRenderer {
    protected LaunchedBlockEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
}
