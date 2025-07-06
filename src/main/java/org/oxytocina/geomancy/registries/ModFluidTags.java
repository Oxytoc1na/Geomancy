package org.oxytocina.geomancy.registries;

import net.minecraft.fluid.*;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.*;
import org.oxytocina.geomancy.Geomancy;

public class ModFluidTags {

    public static final TagKey<Fluid> SWIMMABLE_FLUID = of("swimmable_fluid");
    public static final TagKey<Fluid> VISCOUS_FLUID = of("viscous_fluid");
    public static final TagKey<Fluid> EXTINGUISHING_FLUID = of("extinguishing_fluid");

    public static final TagKey<Fluid> MOLTEN_GOLD = of("molten_gold");

    private static TagKey<Fluid> of(String id) {
        return TagKey.of(Registries.FLUID.getKey(), Geomancy.locate(id));
    }
}