package org.oxytocina.geomancy.items;

import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.rendering.ModColorizationHandler;
import org.oxytocina.geomancy.util.SimplexNoise;
import org.oxytocina.geomancy.util.Toolbox;

public interface ICustomRarityItem {

    Rarity getRarity();
    static int getRarityColor(Rarity rarity, ItemStack stack, int index){
        return switch (rarity){
            case Rainbow ->Toolbox.colorFromHSV((((Geomancy.CONFIG.epilepsyMode.value()?0:GeomancyClient.tick)+index*5)/20f/2)%1,0.5f,1);
            case Octangulite -> ModColorizationHandler.octanguliteRarityNoise(stack,index);
            case Ancient ->Toolbox.colorFromHSV(46/360f,1, 0.5f + 0.5f*(1f - (((GeomancyClient.tick+30* SimplexNoise.noiseNormalized(index,index,GeomancyClient.tick/300f,2.5234))%30)/30f)));
            default->0xFFFFFFFF;
        };
    }

    static Text colorizeName(Rarity rarity,ItemStack stack, Text t){
        if(rarity==Rarity.None) return t;

        Style style = t.getStyle();
        MutableText res = Text.literal("");
        String content = t.getString();
        for (int i = 0; i < content.length(); i++) {
            String c = content.substring(i,i+1);
            res.append(Text.literal(c).setStyle(style.withColor(getRarityColor(rarity,stack,i))));
        }
        return res;
    }

    default Text colorizeName(ItemStack stack, Text t){
        return colorizeName(getRarity(),stack,t);
    }

    enum Rarity{
        None,
        Rainbow,
        Octangulite,
        Ancient
    }
}
