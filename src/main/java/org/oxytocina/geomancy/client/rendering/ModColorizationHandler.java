package org.oxytocina.geomancy.client.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.util.math.BlockPos;
import org.oxytocina.geomancy.util.Toolbox;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;

public class ModColorizationHandler {

    public static void register(){

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                    if (view == null || pos == null) {return 0xFFFFFFFF;} else {return octanguliteNoise(pos,tintIndex,1);}
                }, ModBlocks.OCTANGULITE_SCRAP);
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            if (view == null || pos == null || tintIndex == 0) {return 0xFFFFFFFF;} else {return octanguliteNoise(pos,tintIndex,0.003f);}
        }, ModBlocks.OCTANGULITE_ORE,ModBlocks.DEEPSLATE_OCTANGULITE_ORE);

        // jewelry
        for(JewelryItem jewelry : JewelryItem.List){ColorProviderRegistry.ITEM.register(jewelry::getColor,jewelry);}
    }

    private static int octanguliteNoise(BlockPos pos, int tintIndex, float zoom){
        float x = zoom*(pos.getX() * (1+tintIndex*0.3f) + tintIndex*16);
        float y = zoom*(pos.getY() * (1+tintIndex*0.3f) + tintIndex*16);
        float z = zoom*(pos.getZ() * (1+tintIndex*0.3f) + tintIndex*16);

        return hsvToRgb((float)(org.oxytocina.geomancy.util.SimplexNoise.noise(x,y,z)+1)/2,1,1);

    }

    private static int octangulite(BlockPos pos, int tintIndex, float zoom){
        int x = Math.round(pos.getX() * (1+tintIndex*0.3f) + tintIndex*16);
        int y = Math.round(pos.getY() * (1+tintIndex*0.3f) + tintIndex*16);
        int z = Math.round(pos.getZ() * (1+tintIndex*0.3f) + tintIndex*16);

        int red, green, blue;

        // RAINBOW!
        red = x * 32 + y * 16;
        if ((red & 256) != 0) {
            red = 255 - (red & 255);
        }
        red &= 255;

        green = y * 32 + z * 16;
        if ((green & 256) != 0) {
            green = 255 - (green & 255);
        }
        green &= 255;

        blue = x * 16 + z * 32;
        if ((blue & 256) != 0) {
            blue = 255 - (blue & 255);
        }
        blue &= 255;

        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    public static int hsvToRgb(float hue, float saturation, float value) {

        int h = (int)(hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0: return Toolbox.colorFromRGB(value, t, p);
            case 1: return Toolbox.colorFromRGB(q, value, p);
            case 2: return Toolbox.colorFromRGB(p, value, t);
            case 3: return Toolbox.colorFromRGB(p, q, value);
            case 4: return Toolbox.colorFromRGB(t, p, value);
            case 5: return Toolbox.colorFromRGB(value, p, q);
            default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
    }
}
