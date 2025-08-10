package org.oxytocina.geomancy.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.PlayerData;

import java.util.List;

public class StellgeUtil {

    public static MutableText stellgify(MutableText t){
        return t.setStyle(t.getStyle().withFont(Geomancy.locate("stellgian")));
    }

    public static MutableText stellgify(MutableText t, float knowledgeRequired){
        if(MinecraftClient.getInstance()==null||MinecraftClient.getInstance().player==null) return t;
        return stellgify(t,knowledgeRequired, MinecraftClient.getInstance().player);
    }

    public static MutableText stellgify(MutableText t, float requiredKnowledge, PlayerEntity pe) {
        float knowledge = getKnowledge(pe);
        return stellgify(t,requiredKnowledge,knowledge);
    }

    public static MutableText stellgify(MutableText t, float requiredKnowledge, float knowledge){
        float knowledgeFraction = knowledge/requiredKnowledge;
        if(knowledgeFraction<=0) return stellgify(t);
        if(knowledgeFraction>=1) return t;

        var tString = t.getString();
        final MutableText resText = Text.literal("");
        t.asOrderedText().accept((index, style, codePoint) -> {
            var charText = Text.literal(tString.substring(index,index+1)).setStyle(style);
            if(Toolbox.seededRandom(index*473).nextFloat()>knowledgeFraction)
                charText = stellgify(charText);
            resText.append(charText);
            return true;
        });
        return resText;
    }

    public static float getKnowledge(PlayerEntity player){
        return PlayerData.from(player).stellgianKnowledge;
    }

}
