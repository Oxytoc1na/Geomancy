package org.oxytocina.geomancy.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.rendering.ItemsWith3dModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @ModifyVariable(method= "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",at=@At(value="HEAD"),argsOnly = true)
    public BakedModel useItem(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        if(ItemsWith3dModel.LIST.contains(stack.getItem()) && renderMode != ModelTransformationMode.GUI){
            return ((ItemRendererAccessor) this).item$getModels().getModelManager().getModel(new ModelIdentifier(Geomancy.MOD_ID, Registries.ITEM.getId(stack.getItem()).getPath()+"_3d","inventory"));
        }
        return value;
    }
}
