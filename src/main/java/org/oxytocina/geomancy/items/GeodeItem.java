package org.oxytocina.geomancy.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.List;

public class GeodeItem extends Item {

    protected final Identifier lootTable;

    public GeodeItem(Settings settings, Identifier lootTable) {
        super(settings);
        ModItems.geodeItems.add(this);
        this.lootTable=lootTable;
    }

    public LootTable getLootTable(World world){
        return world instanceof ServerWorld sw ? Toolbox.getLootTable(sw,lootTable) : null;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        tooltip.add(Text.translatable("tooltip.geomancy.geodes").formatted((Formatting.GRAY)));

        super.appendTooltip(stack, world, tooltip, context);
    }

    public int getProgressRequired() {
        return 10;
    }

    public int getBaseDifficulty() {
        return 10;
    }

    public float getDifficultyPerMighty() {
        return 10;
    }
}
