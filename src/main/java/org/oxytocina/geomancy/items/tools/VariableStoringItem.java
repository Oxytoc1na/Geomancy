package org.oxytocina.geomancy.items.tools;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.spells.SpellSignal;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class VariableStoringItem extends Item implements IVariableStoringItem{
    public final int capacity;

    public VariableStoringItem(Settings settings,int capacity) {
        super(settings);
        this.capacity=capacity;
    }

    public static SpellSignal getSignalStatic(ItemStack storage,String name){
        var sigs = getSignalsStatic(storage);
        if(sigs!=null && sigs.containsKey(name)) return sigs.get(name);
        return null;
    }

    public static HashMap<String,SpellSignal> getSignalsStatic(ItemStack storage){
        var nbt = storage.getNbt();
        if(nbt == null || !nbt.contains("signals", NbtElement.COMPOUND_TYPE)) return null;
        var list = nbt.getCompound("signals");
        HashMap<String,SpellSignal> res = new LinkedHashMap<>();
        var keys = list.getKeys();
        for(var key : keys){
            var sig = SpellSignal.fromNBT(list.getCompound(key));
            res.put(key,sig);
        }
        return res;
    }

    public static boolean setSignalStatic(ItemStack storage,SpellSignal signal){
        var list = storage.getOrCreateSubNbt("signals");

        if(signal.type== SpellSignal.Type.None){
            if(list.contains(signal.name)) {
                list.remove(signal.name);
                return true;
            }
            return false;
        }

        // item is full
        if(list.getSize() + (list.contains(signal.name)?0:1) > ((VariableStoringItem)storage.getItem()).capacity) return false;

        var sigNbt = signal.toNBT();
        list.put(signal.name,sigNbt);
        markDirty(storage);
        return true;
    }

    protected static void markDirty(ItemStack stack){

    }

    @Override
    public SpellSignal getSignal(ItemStack stack, String name) {
        return getSignalStatic(stack,name);
    }

    @Override
    public boolean setSignal(ItemStack stack,SpellSignal signal) {
        return setSignalStatic(stack,signal);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        var sigs = getSignalsStatic(stack);

        tooltip.add(Text.translatable("geomancy.varstorage.prefix").formatted(Formatting.GRAY).append(getAccessorPrefix(stack)));
        tooltip.add(Text.translatable("geomancy.varstorage.storage").formatted(Formatting.GRAY).append((sigs!=null?sigs.size():0)+"/"+capacity));

        if(sigs!=null)
            for(var sig : sigs.values()){
                var text = Text.empty().formatted(Formatting.DARK_GRAY).append(sig.toText()).append(" : ").append(sig.toString(SpellContext.ofWorld(world, MinecraftClient.getInstance().player)));
                tooltip.add(text);
            }
    }
}
