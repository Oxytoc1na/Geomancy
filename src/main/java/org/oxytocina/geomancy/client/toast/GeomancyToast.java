package org.oxytocina.geomancy.client.toast;

import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.sound.*;
import net.minecraft.client.toast.*;
import net.minecraft.item.*;
import net.minecraft.sound.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.oxytocina.geomancy.Geomancy;

import java.util.*;

@Environment(EnvType.CLIENT)
abstract public class GeomancyToast implements Toast {

    public final Identifier TEXTURE = Geomancy.locate("textures/gui/toasts.png");
    public final ItemStack itemStack;
    public final SoundEvent soundEvent;
    public boolean soundPlayed;

    public GeomancyToast(ItemStack itemStack, SoundEvent soundEvent) {
        this.itemStack = itemStack;
        this.soundEvent = soundEvent;
        this.soundPlayed = false;
    }

    public static void show(GeomancyToast t){
        MinecraftClient.getInstance().getToastManager().add(t);
    }

}
