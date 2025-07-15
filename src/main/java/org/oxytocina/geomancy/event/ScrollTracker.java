package org.oxytocina.geomancy.event;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.lwjgl.glfw.GLFW;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.math.MathHelper;
import org.oxytocina.geomancy.util.Toolbox;

public class ScrollTracker {

    public static int delta;

    public static int scrollSize = 1;

    public static void update(){
        delta = 0;
    }

    public static void scroll(double delta){
        ScrollTracker.delta += Toolbox.sign((float)delta);
    }
}