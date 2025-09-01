package org.oxytocina.geomancy.mixin.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.profiler.Profiler;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.rendering.ItemsWith3dModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow
    protected abstract void addModel(ModelIdentifier modelId);

    @Inject(method="<init>",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 3, shift = At.Shift.AFTER))
    public void add3dItems(BlockColors blockColors, Profiler profiler, Map jsonUnbakedModels, Map blockStates, CallbackInfo ci){
        for(Item item : ItemsWith3dModel.LIST)
            this.addModel(new ModelIdentifier(Geomancy.MOD_ID, Registries.ITEM.getId(item).getPath()+"_3d","inventory"));
    }
}
