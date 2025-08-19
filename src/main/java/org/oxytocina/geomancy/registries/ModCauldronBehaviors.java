package org.oxytocina.geomancy.registries;

import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;

import java.util.Map;

import static net.minecraft.block.cauldron.CauldronBehavior.*;

public class ModCauldronBehaviors {

    public static final Map<Item, CauldronBehavior> GOLD_CAULDRON_BEHAVIOR = createMap();
    public static final CauldronBehavior FILL_WITH_GOLD = (state, world, pos, player, hand, stack) -> fillCauldron(
            world, pos, player, hand, stack, ModBlocks.MOLTEN_GOLD_CAULDRON.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY_LAVA
    );

    public static void register(){
        GOLD_CAULDRON_BEHAVIOR.put(
                Items.BUCKET,
                (state, world, pos, player, hand, stack) -> emptyCauldron(
                        state, world, pos, player, hand, stack, new ItemStack(ModFluids.MOLTEN_GOLD_BUCKET), statex -> true, SoundEvents.ITEM_BUCKET_FILL_LAVA
                )
        );
        // overriding gold with other fluids
        registerBucketBehavior(GOLD_CAULDRON_BEHAVIOR);

        EMPTY_CAULDRON_BEHAVIOR.put(ModFluids.MOLTEN_GOLD_BUCKET,FILL_WITH_GOLD);
    }
}
