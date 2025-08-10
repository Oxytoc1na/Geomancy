package org.oxytocina.geomancy.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.entity.ManaStoringItemData;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.entity.StateSaverAndLoader;
import org.oxytocina.geomancy.networking.packet.C2S.*;
import org.oxytocina.geomancy.networking.packet.S2C.*;
import org.oxytocina.geomancy.util.ManaUtil;
import org.oxytocina.geomancy.util.StellgeUtil;

import java.util.function.Function;

public class ModMessages {

    // server to client
    public static final Identifier MANA_SYNC =              Geomancy.locate("mana_sync");
    public static final Identifier LEAD_POISONING_SYNC =    Geomancy.locate("lead_poisoning_sync");
    public static final Identifier STELLGE_KNOWLEDGE_SYNC =    Geomancy.locate("stellge_knowledge_sync");
    public static final Identifier MADNESS_SYNC =           Geomancy.locate("madness_sync");
    public static final Identifier ITEM_MANA_SYNC =         Geomancy.locate("item_mana_sync");
    public static final Identifier INITIAL_SYNC =           Geomancy.locate("initial_sync");
    public static final Identifier SPELLMAKER_REFRESH =     Geomancy.locate("spellmaker_refresh");
    public static final Identifier CAST_PARTICLES =         Geomancy.locate("cast_particles");

    // client to server

    /*
    // send packet to server
    PacketByteBuf data = PacketByteBufs.create();

        data.writeItemStack(stack);
        data.writeInt(player.getInventory().indexOf(stack));
        data.writeInt(nextIndex);
        ClientPlayNetworking.send(ModMessages.SPELLSTORER_TRY_UPDATE_CASTER, data);
    */
    public static final Identifier CLIENT_JOINED =                      Geomancy.locate("client_joined");
    public static final Identifier SPELLMAKER_TRY_ADD_COMPONENT =       Geomancy.locate("spellmaker_try_add_component");
    public static final Identifier SPELLMAKER_TRY_REMOVE_COMPONENT =    Geomancy.locate("spellmaker_try_remove_component");
    public static final Identifier SPELLMAKER_TRY_CHANGE_TYPE =         Geomancy.locate("spellmaker_try_change_type");
    public static final Identifier SPELLMAKER_TRY_CHANGE_VAR =          Geomancy.locate("spellmaker_try_change_var");
    public static final Identifier SPELLMAKER_TRY_CHANGE_PARAM =        Geomancy.locate("spellmaker_try_change_param");
    public static final Identifier SPELLMAKER_TRY_CHANGE_GRIDNAME =     Geomancy.locate("spellmaker_try_change_gridname");
    public static final Identifier SPELLMAKER_TRY_CHANGE_GRIDLIB =      Geomancy.locate("spellmaker_try_change_lib");
    public static final Identifier SPELLMAKER_TRY_ROTATE_COMPONENT =    Geomancy.locate("spellmaker_try_rotate_component");
    public static final Identifier SPELLSTORER_TRY_UPDATE_CASTER =      Geomancy.locate("spellstorer_try_update_caster");
    public static final Identifier SPELLSTORER_ITEM_TRY_UPDATE_CASTER = Geomancy.locate("spellstorer_item_try_update_caster");
    public static final Identifier CASTER_CHANGE_SELECTED_SPELL =       Geomancy.locate("caster_change_selected_spell");

    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(CLIENT_JOINED, ClientJoinedC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLMAKER_TRY_ADD_COMPONENT, SpellmakerTryAddComponentC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLMAKER_TRY_REMOVE_COMPONENT, SpellmakerTryRemoveComponentC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLMAKER_TRY_CHANGE_TYPE, SpellmakerTryChangeModeC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLMAKER_TRY_CHANGE_VAR, SpellmakerTryChangeVarC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLMAKER_TRY_CHANGE_PARAM, SpellmakerTryChangeParamC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLMAKER_TRY_CHANGE_GRIDNAME, SpellmakerTryChangeGridnameC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLMAKER_TRY_CHANGE_GRIDLIB, SpellmakerTryChangeGridLibC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLMAKER_TRY_ROTATE_COMPONENT, SpellmakerTryRotateComponentC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLSTORER_TRY_UPDATE_CASTER, SpellstorerTryUpdateCasterC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SPELLSTORER_ITEM_TRY_UPDATE_CASTER, SpellstorerItemTryUpdateCasterC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CASTER_CHANGE_SELECTED_SPELL, CasterChangeSelectedSpellC2S::receive);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.execute(() -> {
                try{
                    PlayerData playerState = StateSaverAndLoader.getPlayerState(handler.getPlayer());
                    PacketByteBuf data = PacketByteBufs.create();
                    playerState.writeBuf(data);
                    ServerPlayNetworking.send(handler.getPlayer(), INITIAL_SYNC, data);

                    // send item mana data to player
                    for(var stack : ManaStoringItemData.stackMap.values()){
                        ManaUtil.syncItemMana(handler.getPlayer().getWorld(),stack);
                    }

                    // sync various other things
                    StellgeUtil.syncKnowledge(handler.getPlayer());
                }
                catch (Exception ignored){

                }
            });
        });
    }

    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(MANA_SYNC, ManaSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LEAD_POISONING_SYNC, LeadPoisoningSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(STELLGE_KNOWLEDGE_SYNC, StellgeKnowledgeSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(MADNESS_SYNC, MadnessSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ITEM_MANA_SYNC, ItemManaSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(INITIAL_SYNC, InitialSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SPELLMAKER_REFRESH, SpellmakerRefreshS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(CAST_PARTICLES, CastParticlesS2CPacket::receive);
    }

    public static void sendToAllClients(MinecraftServer server, Identifier id, PacketByteBuf buf){
        for(var player : server.getPlayerManager().getPlayerList()){
            ServerPlayNetworking.send(player,id,buf);
        }
    }

    public static void sendToAllClients(MinecraftServer server, Identifier id,PacketByteBuf buf, Function<ServerPlayerEntity,Boolean> predicate){
        for(var player : server.getPlayerManager().getPlayerList()){
            if(!predicate.apply(player)) continue;
            ServerPlayNetworking.send(player,id,buf);
        }
    }

}
