package org.oxytocina.geomancy.fluids;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

public class ModFluids {

    public static FlowableFluid STILL_GOLD;
    public static FlowableFluid FLOWING_GOLD;
    public static Item GOLD_BUCKET;
    public static Block GOLD;

    public static void initialize(){
        STILL_GOLD = Registry.register(Registries.FLUID, new Identifier(Geomancy.MOD_ID, "gold"), new GoldFluid.Still());
        FLOWING_GOLD = Registry.register(Registries.FLUID, new Identifier(Geomancy.MOD_ID, "flowing_gold"), new GoldFluid.Flowing());
        GOLD_BUCKET = Registry.register(Registries.ITEM, new Identifier(Geomancy.MOD_ID, "gold_bucket"),
                new BucketItem(STILL_GOLD, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        GOLD = Registry.register(Registries.BLOCK, new Identifier(Geomancy.MOD_ID, "gold"), new FluidBlock(STILL_GOLD, FabricBlockSettings.copy(Blocks.LAVA)){});
    }
}

