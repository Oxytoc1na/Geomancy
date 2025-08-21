package org.oxytocina.geomancy.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.oxytocina.geomancy.client.toast.GeomancyToast;
import org.oxytocina.geomancy.client.toast.StellgeKnowledgeToast;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.networking.ModMessages;

public class KeyInputHandler {
    public static final String LANG_CATEGORY_GEOMANCY = "key.category.geomancy";

    public static final String LANG_OPEN_SKILLTREE = "key.geomancy.skilltree";
    public static KeyBinding KEY_OPEN_SKILLTREE;

    public static final String LANG_CAST_1 = "key.geomancy.cast.1";
    public static KeyBinding KEY_CAST_1;
    public static final String LANG_CAST_2 = "key.geomancy.cast.2";
    public static KeyBinding KEY_CAST_2;
    public static final String LANG_CAST_3 = "key.geomancy.cast.3";
    public static KeyBinding KEY_CAST_3;

    public static final String LANG_ACTIVATE_SPELLS = "key.geomancy.activatespells";
    public static KeyBinding KEY_ACTIVATE_SPELLS;

    private static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if(KEY_OPEN_SKILLTREE.wasPressed()){
                GeomancyToast.show(new StellgeKnowledgeToast());
            }
            else if(KEY_CAST_1.wasPressed()) castPressed(0);
            else if(KEY_CAST_2.wasPressed()) castPressed(1);
            else if(KEY_CAST_3.wasPressed()) castPressed(2);
        });
    }

    public static void castPressed(int id){
        var buf = PacketByteBufs.create();
        buf.writeInt(id);
        ClientPlayNetworking.send(ModMessages.CAST_SPELL_PRESSED,buf);
    }

    public static void register(){
        KEY_OPEN_SKILLTREE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                LANG_OPEN_SKILLTREE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                LANG_CATEGORY_GEOMANCY
        ));

        KEY_CAST_1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                LANG_CAST_1,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                LANG_CATEGORY_GEOMANCY
        ));

        KEY_CAST_2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                LANG_CAST_2,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                LANG_CATEGORY_GEOMANCY
        ));

        KEY_CAST_3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                LANG_CAST_3,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                LANG_CATEGORY_GEOMANCY
        ));

        KEY_ACTIVATE_SPELLS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                LANG_ACTIVATE_SPELLS,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_SHIFT,
                LANG_CATEGORY_GEOMANCY
        ));

        registerKeyInputs();
    }

}
