package org.oxytocina.geomancy.registries;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.oxytocina.geomancy.Geomancy;

public class ModBiomeTags {

    public static final TagKey<Biome> HAS_DWARVEN_REMNANTS = of( "has_dwarven_remnants");
    public static final TagKey<Biome> HAS_OCTANGULA = of( "has_octangula");
    public static final TagKey<Biome> HAS_DIGSITE = of( "has_digsite");
    public static final TagKey<Biome> VPB_LOW = of( "vpb_low");
    public static final TagKey<Biome> VPB_LOWER = of( "vpb_lower");
    public static final TagKey<Biome> VPB_LOWEST = of( "vpb_lowest");
    public static final TagKey<Biome> VPB_NONE = of( "vpb_none");
    public static final TagKey<Biome> VPB_HIGH = of( "vpb_high");
    public static final TagKey<Biome> VPB_HIGHER = of( "vpb_higher");
    public static final TagKey<Biome> VPB_HIGHEST = of( "vpb_highest");
    public static final TagKey<Biome> VPB_INSANE = of("vpb_insane");

    private static TagKey<Biome> of(String id) {
        return TagKey.of(RegistryKeys.BIOME, Geomancy.locate(id));
    }
}