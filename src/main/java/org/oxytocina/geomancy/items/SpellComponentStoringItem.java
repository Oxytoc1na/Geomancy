package org.oxytocina.geomancy.items;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.jewelry.GemSlot;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;

import java.util.ArrayList;
import java.util.List;

public class SpellComponentStoringItem extends Item {

    public static final ArrayList<SpellComponentStoringItem> List = new ArrayList<>();

    public SpellComponentStoringItem(Settings settings) {
        super(settings);
        List.add(this);
    }

    public static ItemStack createDefaultComponentData(ItemStack stack, SpellBlock block){
        SpellComponent component = new SpellComponent(null,null, block);
        writeComponent(stack,component);
        return stack;
    }

    public static SpellComponent readComponent(ItemStack stack){
        if(stack==null||!stack.hasNbt()) return null;
        var Nbt = stack.getNbt();
        if(!Nbt.contains("component", NbtElement.COMPOUND_TYPE)) return null;
        var subNbt = Nbt.getCompound("component");

        return new SpellComponent(null,subNbt);
    }

    public static void writeComponent(ItemStack stack,SpellComponent component){
        if(stack==null) return;
        NbtCompound componentCompound = new NbtCompound();
        component.writeNbt(componentCompound);
        stack.setSubNbt("component",componentCompound);
    }

    @Override
    public Text getName(ItemStack stack) {
        SpellComponent comp = readComponent(stack);
        return Text.translatable(this.getTranslationKey(stack)).append(Text.literal(" [").append(Text.translatable("geomancy.spellcomponent."+(comp!=null?comp.function.identifier.getPath():"empty")).append("]")).formatted(Formatting.GRAY));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        var comp = readComponent(stack);
        if(comp==null) return;

        tooltip.add(Text.translatable("geomancy.tooltip.spellcomponent."+comp.function.identifier.getPath()));

        for(var input : comp.function.inputs.values()){
            tooltip.add(Text.literal(input.type.toString()+" "+input.name));
        }
        tooltip.add(Text.literal(("->")));
        for(var output : comp.function.outputs.values()){
            tooltip.add(Text.literal(output.type.toString()+" "+output.name));
        }
    }

    public static void populateItemGroup(){
        // Register items to the custom item group.
        ItemGroupEvents.modifyEntriesEvent(ModItems.SPELLS_ITEM_GROUP_KEY).register(itemGroup -> {
            for(SpellComponentStoringItem i : List){

                for(SpellBlock block : SpellBlocks.functions.values())
                {
                    ItemStack item = SpellComponentStoringItem.createDefaultComponentData(i.getDefaultStack(),block);
                    itemGroup.add(item);
                }

            }
        });
    }
}
