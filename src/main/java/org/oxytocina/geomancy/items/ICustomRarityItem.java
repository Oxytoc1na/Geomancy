package org.oxytocina.geomancy.items;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.util.Toolbox;

public interface ICustomRarityItem {

    Rarity getRarity();
    default int getRarityColor(int index){
        return switch (getRarity()){
            case Rainbow ->Toolbox.colorFromHSV(((GeomancyClient.tick+index*5)/20f/2)%1,1,1);
            default->0xFFFFFFFF;
        };
    }

    default MutableText colorizeName(MutableText t){
        Style style = t.getStyle();
        MutableText res = Text.literal("");
        String content = t.getString();
        for (int i = 0; i < content.length(); i++) {
            String c = content.substring(i,i+1);
            res.append(Text.literal(c).setStyle(style.withColor(getRarityColor(i))));
        }
        return res;
    }

    enum Rarity{
        Rainbow
    }
}
