package org.oxytocina.geomancy.blocks.fluids;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.util.Toolbox;
import org.oxytocina.geomancy.items.ModItems;

public class ModFluids {

    // RenderHandler storage for compatibility purposes
    public static final Object2ObjectArrayMap<FluidRenderHandler, Fluid[]> HANDLER_MAP = new Object2ObjectArrayMap<>(4);

    public static ModFluid MOLTEN_GOLD = new GoldFluid.Still();
    public static ModFluid FLOWING_MOLTEN_GOLD = new GoldFluid.Flowing();
    public static Item MOLTEN_GOLD_BUCKET;
    public static final int MOLTEN_GOLD_TINT = 0xFCD557; // for falling particle color
    public static final Vector3f MOLTEN_GOLD_COLOR_VEC = Toolbox.colorIntToVec(MOLTEN_GOLD_TINT);
    public static final Identifier MOLTEN_GOLD_OVERLAY_TEXTURE = Geomancy.locate("textures/misc/molten_gold_overlay.png");
    public static final float MOLTEN_GOLD_OVERLAY_ALPHA = 0.6F;

    static{
        registerFluid("molten_gold", MOLTEN_GOLD, FLOWING_MOLTEN_GOLD);
        MOLTEN_GOLD_BUCKET = ModItems.register("gold_bucket",new BucketItem(MOLTEN_GOLD, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));

    }

    public static void register() {

    }

    private static void registerFluid(String name, Fluid stillFluid, Fluid flowingFluid) {
        Registry.register(Registries.FLUID, Geomancy.locate(name), stillFluid);
        Registry.register(Registries.FLUID, Geomancy.locate("flowing_" + name), flowingFluid);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        setupFluidRendering(MOLTEN_GOLD, FLOWING_MOLTEN_GOLD, "molten_gold", 0xFFFFFF);
    }

    @Environment(EnvType.CLIENT)
    private static void setupFluidRendering(final Fluid stillFluid, final Fluid flowingFluid, final String name, int tint) {
        var handler = new SimpleFluidRenderHandler(
                Geomancy.locate("block/" + name + "_still"),
                Geomancy.locate("block/" + name + "_flow"),
                tint
        );

        HANDLER_MAP.put(handler, new Fluid[]{stillFluid, flowingFluid});
        FluidRenderHandlerRegistry.INSTANCE.register(stillFluid, flowingFluid, handler);

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), stillFluid, flowingFluid);
    }
}

