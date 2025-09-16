package org.oxytocina.geomancy.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.oxytocina.geomancy.registries.ModDamageTypes;

public class ModFallingBlock extends FallingBlock {
    public final int color;
    public ModFallingBlock(Settings settings, int color) {
        super(settings);
        this.color=color;
    }

    @Override
    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return color;
    }

    @Override
    public DamageSource getDamageSource(Entity attacker) {
        return ModDamageTypes.of(attacker.getWorld(),ModDamageTypes.NULL_RUBBLE);
    }
}
