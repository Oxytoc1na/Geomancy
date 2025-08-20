package org.oxytocina.geomancy.util;

import net.minecraft.client.font.TextRenderer;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {
    public static List<String> wrapString(String s, int width, TextRenderer renderer){
        List<String> res = new ArrayList<>();

        String[] cutByNewline = s.split("\n");
        if(cutByNewline.length>1){
            for (int i = 0; i < cutByNewline.length; i++) {
                var subres = wrapString(cutByNewline[i],width,renderer);

                res.addAll(subres);
            }
            return res;
        }

        String[] words = s.split(" ");
        String currentLine = "";
        for(int i = 0; i < words.length; i++){
            if(!currentLine.isEmpty() && renderer.getWidth(currentLine+words[i]) > width){
                // split line
                res.add(currentLine);
                currentLine="";
            }

            if(!currentLine.isEmpty()) currentLine+=" ";
            currentLine += words[i];
        }

        if(!currentLine.isEmpty())
            res.add(currentLine);

        return res;
    }
}
