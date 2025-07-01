package org.oxytocina.geomancy.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeodeItem extends Item {

    protected final LootTable lootTable;

    public GeodeItem(Settings settings, LootTable lootTable) {
        super(settings);
        ModItems.geodeItems.add(this);
        this.lootTable=lootTable;
    }

    public LootTable getLootTable(){
        return lootTable;
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
