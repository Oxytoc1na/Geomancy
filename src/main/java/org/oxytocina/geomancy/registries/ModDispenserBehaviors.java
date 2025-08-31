package org.oxytocina.geomancy.registries;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.blocks.fluids.ModFluids;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.tools.HammerItem;

public class ModDispenserBehaviors {
    public static void register(){

        DispenserBehavior placeFluid = new ItemDispenserBehavior() {
            private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                FluidModificationItem fluidModificationItem = (FluidModificationItem)stack.getItem();
                BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                World world = pointer.getWorld();
                if (fluidModificationItem.placeFluid(null, world, blockPos, null)) {
                    fluidModificationItem.onEmptied(null, world, stack, blockPos);
                    return new ItemStack(Items.BUCKET);
                } else {
                    return this.fallbackBehavior.dispense(pointer, stack);
                }
            }
        };
        DispenserBlock.registerBehavior(ModFluids.MOLTEN_GOLD_BUCKET, placeFluid);

        DispenserBehavior hammerSmithery = new ItemDispenserBehavior() {
            private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                if(!(stack.getItem() instanceof HammerItem hammer)) return this.fallbackBehavior.dispense(pointer, stack);
                BlockPos smitheryPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                World world = pointer.getWorld();
                BlockEntity entity = world.getBlockEntity(smitheryPos);
                if(!(entity instanceof SmitheryBlockEntity smithery)) return this.fallbackBehavior.dispense(pointer, stack);

                float skill = hammer.getSmithingSkill(smithery,null,stack);
                smithery.onHitWithHammer(null,stack,skill);
                return stack;
            }
        };
        DispenserBlock.registerBehavior(ModItems.IRON_HAMMER, hammerSmithery);
        DispenserBlock.registerBehavior(ModItems.GOLDEN_HAMMER, hammerSmithery);
        DispenserBlock.registerBehavior(ModItems.LEAD_HAMMER, hammerSmithery);
        DispenserBlock.registerBehavior(ModItems.MOLYBDENUM_HAMMER, hammerSmithery);
        DispenserBlock.registerBehavior(ModItems.MITHRIL_HAMMER, hammerSmithery);
        DispenserBlock.registerBehavior(ModItems.TITANIUM_HAMMER, hammerSmithery);
        DispenserBlock.registerBehavior(ModItems.OCTANGULITE_HAMMER, hammerSmithery);

    }
}
