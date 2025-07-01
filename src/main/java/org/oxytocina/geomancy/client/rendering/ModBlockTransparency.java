package org.oxytocina.geomancy.client.rendering;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.oxytocina.geomancy.blocks.ModBlocks;

public class ModBlockTransparency {

    public static void register(){
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OCTANGULITE_SCRAP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OCTANGULITE_ORE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEEPSLATE_OCTANGULITE_ORE, RenderLayer.getCutout());
    }
}
