package org.oxytocina.geomancy.entity;

import com.mojang.datafixers.types.Func;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.IModMob;

import java.util.function.Function;

public class ModEntityTypes {

    /*
     * Registers our Cube Entity under the ID "entitytesting:cube".
     *
     * The entity is registered under the SpawnGroup#CREATURE category, which is what most animals and passive/neutral mobs use.
     * It has a hitbox size of .75x.75, or 12 "pixels" wide (3/4ths of a block).
     */
    public static final EntityType<StellgeEngineerEntity> STELLGE_ENGINEER = registerMob("stellge_engineer", StellgeEngineerEntity::new,StellgeEngineerEntity::defAttributes,new ExtraEntitySettings().dim(0.75f,1.5f).group(SpawnGroup.MONSTER).spawnEgg(0xFFFFFF,0xFFFFFF));
    public static final EntityType<CasterDelegateEntity> CASTER_DELEGATE = registerEntity("caster_delegate", CasterDelegateEntity::new,new ExtraEntitySettings().dim(0.75f,1.5f).group(SpawnGroup.MISC));


    public static <T extends MobEntity, IModMob> EntityType<T> registerMob(String id, EntityType.EntityFactory<T> factory, @Nullable java.util.function.Supplier<DefaultAttributeContainer.Builder> defaultAttributes, ExtraEntitySettings settings){
        EntityType<T> res = Registry.register(
                Registries.ENTITY_TYPE,
                Geomancy.locate(id),
                EntityType.Builder.create(factory, settings.group).setDimensions(settings.width,settings.height).build(id)
        );

        if(defaultAttributes==null) defaultAttributes = T::createMobAttributes;

        FabricDefaultAttributeRegistry.register(res, defaultAttributes.get());
        settings.setMobEntityType(res);
        settings.apply();
        return res;
    }

    public static <T extends Entity> EntityType<T> registerEntity(String id, EntityType.EntityFactory<T> factory, ExtraEntitySettings settings){
        EntityType<T> res = Registry.register(
                Registries.ENTITY_TYPE,
                Geomancy.locate(id),
                EntityType.Builder.create(factory, settings.group).setDimensions(settings.width,settings.height).build(id)
        );

        settings.apply();
        return res;
    }

    public static void register()
    {

    }
}
