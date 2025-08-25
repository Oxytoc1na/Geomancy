package org.oxytocina.geomancy.items;

import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.util.Toolbox;

public interface ICustomRarityItem {

    Rarity getRarity();
    default int getRarityColor(ItemStack stack, int index){
        return switch (getRarity()){
            case Rainbow ->Toolbox.colorFromHSV((((Geomancy.CONFIG.epilepsyMode.value()?0:GeomancyClient.tick)+index*5)/20f/2)%1,0.5f,1);
            case Octangulite -> ModColorizationHandler.octanguliteRarityNoise(stack,index);
            default->0xFFFFFFFF;
        };
    }

    default Text colorizeName(ItemStack stack, Text t){
        if(getRarity()==Rarity.None) return t;

        Style style = t.getStyle();
        MutableText res = Text.literal("");
        String content = t.getString();
        for (int i = 0; i < content.length(); i++) {
            String c = content.substring(i,i+1);
            res.append(Text.literal(c).setStyle(style.withColor(getRarityColor(stack,i))));
        }
        return res;
    }

    enum Rarity{
        None,
        Rainbow,
        Octangulite
    }
}
