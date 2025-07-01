package org.oxytocina.geomancy.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.oxytocina.geomancy.networking.ModMessages;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_GEOMANCY = "key.category.geomancy";
    public static final String KEY_OPEN_SKILLTREE = "key.geomancy.skilltree";

    public static KeyBinding skilltreeKey;

    private static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if(skilltreeKey.wasPressed()){
                ClientPlayNetworking.send(ModMessages.MANA_SYNC_ID, PacketByteBufs.create());
            }
        });
    }

    public static void initialize(){
        skilltreeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_OPEN_SKILLTREE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                KEY_CATEGORY_GEOMANCY
        ));

        registerKeyInputs();
    }

}
