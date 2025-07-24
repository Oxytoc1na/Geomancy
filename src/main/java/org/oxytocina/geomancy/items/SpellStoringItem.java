package org.oxytocina.geomancy.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;

import java.util.List;
import java.util.Objects;

public class SpellStoringItem extends Item {

    public final int width;
    public final int height;

    public SpellStoringItem(Settings settings, int width, int height) {
        super(settings);
        this.width=width;
        this.height=height;
    }

    public static SpellGrid getOrCreateGrid(ItemStack stack){
        var existing = readGrid(stack);
        if(existing!=null) return existing;
        return createDefaultGrid(stack);
    }

    public int getWidth(){return width;}
    public int getHeight(){return height;}

    protected static SpellGrid createDefaultGrid(ItemStack stack){
        if(!(stack.getItem() instanceof SpellStoringItem storage)) return null;

        SpellGrid grid = new SpellGrid(storage.getWidth(),storage.getHeight());

        writeGrid(stack,grid);
        return grid;
    }

    public static SpellGrid readGrid(ItemStack stack){
        if(stack==null||!stack.hasNbt()) return null;
        var Nbt = stack.getNbt();
        if(!Nbt.contains("spell", NbtElement.COMPOUND_TYPE)) return null;
        var subNbt = Nbt.getCompound("spell");

        return new SpellGrid(stack,subNbt);
    }

    public static void writeGrid(ItemStack stack,SpellGrid grid){
        if(stack==null) return;
        NbtCompound spellCompound = new NbtCompound();
        grid.writeNbt(spellCompound);
        stack.setSubNbt("spell",spellCompound);
    }

    public void cast(ItemStack caster,ItemStack spellstorage, LivingEntity user){
        SpellGrid grid = getOrCreateGrid(spellstorage);
        if(grid==null) return;
        grid.run(caster,spellstorage,user);
    }

    @Override
    public Text getName(ItemStack stack) {
        SpellGrid grid = readGrid(stack);
        return Text.translatable(this.getTranslationKey(stack)).append(Text.literal(" [").append(
                (grid==null?Text.translatable("geomancy.spellstorage.empty"):
                        Objects.equals(grid.name, "") ?Text.translatable("geomancy.spellstorage.unnamed"):
                        Text.literal(grid.name))).append("]").formatted(Formatting.GRAY));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        var grid = readGrid(stack);
        if(grid==null) return;

        for(var comp : grid.components.values()){
            tooltip.add(Text.translatable("geomancy.spellcomponent."+comp.function.identifier.getPath()).formatted(Formatting.GRAY));
        }
    }

}
