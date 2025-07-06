package org.oxytocina.geomancy.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.blocks.ExtraBlockSettings;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.registries.ModFluidTags;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagProvider extends FabricTagProvider<Fluid> {
    public ModFluidTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.FLUID, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        // molten gold
        var moltenGoldBuilder = getOrCreateTagBuilder(ModFluidTags.MOLTEN_GOLD).setReplace(false);
        moltenGoldBuilder.add(ModFluids.MOLTEN_GOLD);
        moltenGoldBuilder.add(ModFluids.FLOWING_MOLTEN_GOLD);

        // swimmable
        var swimmableBuilder = getOrCreateTagBuilder(ModFluidTags.SWIMMABLE_FLUID).setReplace(false);

        // extinguishing
        var extinguishingBuilder = getOrCreateTagBuilder(ModFluidTags.EXTINGUISHING_FLUID).setReplace(false);

        // viscous
        var viscousBuilder = getOrCreateTagBuilder(ModFluidTags.VISCOUS_FLUID).setReplace(false);
        viscousBuilder.add(ModFluids.MOLTEN_GOLD);
        viscousBuilder.add(ModFluids.FLOWING_MOLTEN_GOLD);



    }
}