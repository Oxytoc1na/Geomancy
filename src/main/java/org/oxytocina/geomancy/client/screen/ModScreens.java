package org.oxytocina.geomancy.client.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ModScreens {

    public static void register() {
        HandledScreens.register(ModScreenHandlers.SMITHERY_SCREEN_HANDLER, SmitheryScreen::new);
        HandledScreens.register(ModScreenHandlers.SPELLMAKER_SCREEN_HANDLER, SpellmakerScreen::new);
        HandledScreens.register(ModScreenHandlers.STORAGE_ITEM_SCREEN_HANDLER, StorageItemScreen::new);

    }
}
