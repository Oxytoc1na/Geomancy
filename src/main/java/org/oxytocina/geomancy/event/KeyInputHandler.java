package org.oxytocina.geomancy.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.oxytocina.geomancy.client.toast.GeomancyToast;
import org.oxytocina.geomancy.client.toast.StellgeKnowledgeToast;
import org.oxytocina.geomancy.items.ModItems;

public class KeyInputHandler {
    public static final String LANG_CATEGORY_GEOMANCY = "key.category.geomancy";
    public static final String LANG_OPEN_SKILLTREE = "key.geomancy.skilltree";

    public static KeyBinding KEY_OPEN_SKILLTREE;

    private static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if(KEY_OPEN_SKILLTREE.wasPressed()){
                GeomancyToast.show(new StellgeKnowledgeToast());
            }
        });
    }

    public static void register(){
        KEY_OPEN_SKILLTREE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                LANG_OPEN_SKILLTREE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                LANG_CATEGORY_GEOMANCY
        ));

        registerKeyInputs();
    }

}
