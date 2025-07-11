package org.oxytocina.geomancy.entity;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.oxytocina.geomancy.Geomancy;

public class ModEntityAttributes {
    public static final EntityAttribute STELLGE_SPAWN_REINFORCEMENTS = register(
            "stellge.spawn_reinforcements", new ClampedEntityAttribute("attribute.name.stellge.spawn_reinforcements", 0.0, 0.0, 1.0)
    );

    private static EntityAttribute register(String id, EntityAttribute attribute) {
        return Registry.register(Registries.ATTRIBUTE, Geomancy.locate(id), attribute);
    }

    public static void register(){}
}
