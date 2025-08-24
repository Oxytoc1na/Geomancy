package org.oxytocina.geomancy.world.dimension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.rendering.dimensions.NullSkyRenderer;

import java.util.OptionalLong;

public class ModDimensions {
    public static final RegistryKey<DimensionOptions> NULL_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            Geomancy.locate("null"));
    public static final RegistryKey<World> NULL_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
            Geomancy.locate("null"));
    public static final RegistryKey<DimensionType> NULL_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Geomancy.locate("null_type"));

    public static void bootstrapType(Registerable<DimensionType> context) {
        context.register(NULL_TYPE, new DimensionType(
                OptionalLong.of(18000), // fixedTime
                false, // hasSkylight
                true, // hasCeiling
                false, // ultraWarm
                true, // natural
                1.0, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                DimensionTypes.OVERWORLD_ID, // effectsLocation
                0.0f, // ambientLight
                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0)));
    }

    public static void register(){
        Registry.register(Registries.CHUNK_GENERATOR, Geomancy.locate("null"), NullChunkGenerator.CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient(){
        DimensionRenderingRegistry.registerSkyRenderer(NULL_LEVEL_KEY, new NullSkyRenderer());
        DimensionRenderingRegistry.registerCloudRenderer(NULL_LEVEL_KEY, c->{});
    }
}