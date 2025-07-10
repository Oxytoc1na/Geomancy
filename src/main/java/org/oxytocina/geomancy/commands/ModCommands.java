package org.oxytocina.geomancy.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import static net.minecraft.server.command.CommandManager.*;
// getString(ctx, "string")
import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class ModCommands {

    public static void register(){

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
        dispatcher.register(literal("lead")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Called foo without bar"), false);
                    return 1;
                })
                .then(literal("set")
                .then(argument("value", IntegerArgumentType.integer())
                        .executes(context -> {
                            final int value = IntegerArgumentType.getInteger(context, "value");
                            context.getSource().sendFeedback(() -> Text.literal("Called foo with bar"), false);
                            return 1;
                        })
                )
                .then(literal("getAmbient")
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("Called foo with bar"), false);
                            return 1;
                        })
                )
        )));
    }
}
