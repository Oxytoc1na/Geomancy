package org.oxytocina.geomancy.client.event;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.oxytocina.geomancy.util.Toolbox;

@Environment(EnvType.CLIENT)
public class ScrollTracker {

    public static int delta;

    public static void update(){
        delta = 0;
    }

    public static void scroll(double delta){
        ScrollTracker.delta += Toolbox.sign((float)delta);
    }
}