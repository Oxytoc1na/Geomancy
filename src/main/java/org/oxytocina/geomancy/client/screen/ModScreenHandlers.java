package org.oxytocina.geomancy.client.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellstorerBlockEntity;

public class ModScreenHandlers {
    public static final ExtendedScreenHandlerType<SmitheryScreenHandler> SMITHERY_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Geomancy.MOD_ID, "smithery_block.json"),
                    new ExtendedScreenHandlerType<>(SmitheryScreenHandler::new));

    public static final ExtendedScreenHandlerType<SpellmakerScreenHandler> SPELLMAKER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Geomancy.MOD_ID, "spellmaker_block.json"),
                    new ExtendedScreenHandlerType<>(SpellmakerScreenHandler::new));

    public static final ExtendedScreenHandlerType<SpellstorerScreenHandler> SPELLSTORER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Geomancy.MOD_ID, "spellstorer_block.json"),
                    new ExtendedScreenHandlerType<>(SpellstorerScreenHandler::new));

    public static final ExtendedScreenHandlerType<SpellstorerItemScreenHandler> SPELLSTORER_ITEM_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Geomancy.MOD_ID, "spellstorer_item.json"),
                    new ExtendedScreenHandlerType<>(SpellstorerItemScreenHandler::new));

    // TODO: clean up this mess
    public static void register() {
        SmitheryBlockEntity.SetScreenHandler(SmitheryScreenHandler::new);
        SpellmakerBlockEntity.SetScreenHandler(SpellmakerScreenHandler::new);
        SpellstorerBlockEntity.SetScreenHandler(SpellstorerScreenHandler::new);
    }
}
