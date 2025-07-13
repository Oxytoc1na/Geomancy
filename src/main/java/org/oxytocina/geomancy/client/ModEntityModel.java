package org.oxytocina.geomancy.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.HashMap;
import java.util.Optional;

public abstract class  ModEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
    public ModelPart root;
}
