package org.oxytocina.geomancy.client.rendering;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;
import org.oxytocina.geomancy.blocks.ModBlocks;

public class ModBlockTransparency {

    public static void register(){
        for(var b : ExtraBlockSettings.CutoutLayerBlocks){
            BlockRenderLayerMap.INSTANCE.putBlock(b, RenderLayer.getCutout());
        }
        for(var b : ExtraBlockSettings.TransparentLayerBlocks){
            BlockRenderLayerMap.INSTANCE.putBlock(b, RenderLayer.getTranslucent());
        }
    }
}
