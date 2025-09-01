package org.oxytocina.geomancy.client.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

public class ModScreenHandlers {
    public static final ExtendedScreenHandlerType<SmitheryScreenHandler> SMITHERY_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Geomancy.MOD_ID, "smithery_block.json"),
                    new ExtendedScreenHandlerType<>(SmitheryScreenHandler::new));

    public static final ExtendedScreenHandlerType<SpellmakerScreenHandler> SPELLMAKER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Geomancy.MOD_ID, "spellmaker_block.json"),
                    new ExtendedScreenHandlerType<>(SpellmakerScreenHandler::new));

    public static final ExtendedScreenHandlerType<StorageItemScreenHandler> STORAGE_ITEM_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Geomancy.MOD_ID, "storage_item.json"),
                    new ExtendedScreenHandlerType<>(StorageItemScreenHandler::new));

    public static final ExtendedScreenHandlerType<SoulForgeScreenHandler> SOULFORGE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Geomancy.MOD_ID, "soulforge.json"),
                    new ExtendedScreenHandlerType<>(SoulForgeScreenHandler::new));


    public static void register() {

    }
}
