package org.oxytocina.geomancy.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.oxytocina.geomancy.util.LeadUtil;
import org.oxytocina.geomancy.util.MadnessUtil;
import org.oxytocina.geomancy.util.StellgeUtil;

import static net.minecraft.server.command.CommandManager.*;
// getString(ctx, "string")


public class ModCommands {

    public static void register(){

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
        dispatcher.register(literal("lead")
                .executes(context -> {
                    PlayerEntity player = context.getSource().getPlayer();
                    if(player==null){
                        return 0;
                    }
                    context.getSource().sendFeedback(() -> Text.literal("lead: "
                            + LeadUtil.getPoisoning(player)
                            +" from items: " +LeadUtil.getPoisoningSpeed(player)
                            +" from environment: " +LeadUtil.getAmbientPoisoning(player)
                    ), false);
                    return 1;
                })
                .then(literal("set")
                        .then(argument("players", EntityArgumentType.players())
                        .then(argument("value", FloatArgumentType.floatArg(0))
                        .executes(context -> {
                            final float value = FloatArgumentType.getFloat(context, "value");
                            for(var p :EntityArgumentType.getPlayers(context,"players")){
                                LeadUtil.setPoisoning(p,value);
                                LeadUtil.syncPoisoning(p);
                                context.getSource().sendFeedback(() -> Text.literal("set lead poisoning of "+p.getEntityName()+" to "+value), false);
                            }
                            return 1;
                        })
                )))
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("madness")
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();
                            if(player==null){
                                return 0;
                            }
                            context.getSource().sendFeedback(() -> Text.literal("madness: "
                                    + MadnessUtil.getMadness(player)
                                    +" from items: " +MadnessUtil.getMadnessSpeed(player)
                                    +" from environment: " +MadnessUtil.getAmbientMadness(player)
                            ), false);
                            return 1;
                        })
                        .then(literal("set")
                                .then(argument("players", EntityArgumentType.players())
                                        .then(argument("value", FloatArgumentType.floatArg(0))
                                                .executes(context -> {
                                                    final float value = FloatArgumentType.getFloat(context, "value");
                                                    for(var p :EntityArgumentType.getPlayers(context,"players")){
                                                        MadnessUtil.setMadness(p,value);
                                                        MadnessUtil.syncMadness(p);
                                                        context.getSource().sendFeedback(() -> Text.literal("set madness of "+p.getEntityName()+" to "+value), false);
                                                    }
                                                    return 1;
                                                })
                                        )))
                ));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("stellge")
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();
                            if(player==null){
                                return 0;
                            }
                            context.getSource().sendFeedback(() -> Text.literal("knowledge: "
                                    + StellgeUtil.getKnowledge(player)
                                    +" from advancements: " +StellgeUtil.getAdvancementKnowledge(player)
                                    +" from items: " +StellgeUtil.getItemKnowledge(player)
                            ), false);
                            return 1;
                        })
                        .then(literal("set")
                                .then(argument("players", EntityArgumentType.players())
                                        .then(argument("value", FloatArgumentType.floatArg(0))
                                                .executes(context -> {
                                                    final float value = FloatArgumentType.getFloat(context, "value");
                                                    for(var p :EntityArgumentType.getPlayers(context,"players")){
                                                        StellgeUtil.setItemKnowledge(p,value);
                                                        StellgeUtil.syncKnowledge(p);
                                                        context.getSource().sendFeedback(() -> Text.literal("set item knowledge of "+p.getEntityName()+" to "+value), false);
                                                    }
                                                    return 1;
                                                })
                                        )))
                ));
    }
}
