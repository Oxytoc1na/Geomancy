package org.oxytocina.geomancy.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String LANG_CATEGORY_GEOMANCY = "key.category.geomancy";
    public static final String LANG_OPEN_SKILLTREE = "key.geomancy.skilltree";

    public static KeyBinding KEY_OPEN_SKILLTREE;

    private static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if(KEY_OPEN_SKILLTREE.wasPressed()){
                //ClientPlayNetworking.send(ModMessages.MANA_SYNC_ID, PacketByteBufs.create());
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
