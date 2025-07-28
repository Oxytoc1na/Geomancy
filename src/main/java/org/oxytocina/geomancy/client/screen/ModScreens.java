package org.oxytocina.geomancy.client.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellstorerBlockEntity;

public class ModScreens {

    public static void register() {
        HandledScreens.register(ModScreenHandlers.SMITHERY_SCREEN_HANDLER, SmitheryScreen::new);
        HandledScreens.register(ModScreenHandlers.SPELLMAKER_SCREEN_HANDLER, SpellmakerScreen::new);
        HandledScreens.register(ModScreenHandlers.SPELLSTORER_SCREEN_HANDLER, SpellstorerScreen::new);
        HandledScreens.register(ModScreenHandlers.SPELLSTORER_ITEM_SCREEN_HANDLER, SpellstorerItemScreen::new);

    }
}
