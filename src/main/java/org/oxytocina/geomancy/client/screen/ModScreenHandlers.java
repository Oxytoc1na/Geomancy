package org.oxytocina.geomancy.client.screen;

import com.mojang.datafixers.util.Function4;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;

public class ModScreenHandlers {
    public static final ExtendedScreenHandlerType<SmitheryScreenHandler> SMITHERY_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Geomancy.MOD_ID, "smithery_block.json"),
                    new ExtendedScreenHandlerType<>(SmitheryScreenHandler::new));

    public static void initialize() {
        SmitheryBlockEntity.SetScreenHandler(SmitheryScreenHandler::new);
    }
}
