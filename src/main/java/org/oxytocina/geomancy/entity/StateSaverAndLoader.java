package org.oxytocina.geomancy.entity;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.oxytocina.geomancy.Geomancy;

public class StateSaverAndLoader extends PersistentState {

    public HashMap<UUID, PlayerData> players = new HashMap<>();
    public HashMap<UUID, ManaStoringItemData> manaStoringItemData = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerData.writeNbt(playerNbt);
            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);

        NbtCompound manaStoringItemDataNbt = new NbtCompound();
        manaStoringItemData.forEach((uuid, data) -> {
            NbtCompound manaDataNbt = new NbtCompound();
            data.writeNbt(manaDataNbt);
            manaStoringItemDataNbt.put(uuid.toString(), manaDataNbt);
        });
        nbt.put("manaStoringItemData", manaStoringItemDataNbt);

        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = PlayerData.fromNbt(playersNbt.getCompound(key));
            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        NbtCompound manaStoringItemDataNbt = tag.getCompound("manaStoringItemData");
        manaStoringItemDataNbt.getKeys().forEach(key -> {
            ManaStoringItemData data = ManaStoringItemData.fromNbt(manaStoringItemDataNbt.getCompound(key));
            UUID uuid = UUID.fromString(key);
            state.manaStoringItemData.put(uuid, data);
        });

        return state;
    }

    public static StateSaverAndLoader createNew() {
        StateSaverAndLoader state = new StateSaverAndLoader();
        state.players = new HashMap<>();
        return state;
    }

    public static StateSaverAndLoader getServerState(World world) {
        if(!(world instanceof ServerWorld serverWorld)) return null;

        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = serverWorld.getServer().getOverworld().getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        StateSaverAndLoader state = persistentStateManager.getOrCreate(StateSaverAndLoader::createFromNbt, StateSaverAndLoader::createNew, Geomancy.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        if(player instanceof ClientPlayerEntity){
            return PlayerData.getOrCreate(player.getUuid());
        }

        StateSaverAndLoader serverState = getServerState(player.getWorld());

        // Either get the player by the uuid, or we don't have data for him yet, make a new player state
        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());

        return playerState;
    }

    public static ManaStoringItemData getManaStoringItemData(World world, UUID uuid, ItemStack stack) {

        if(!(world instanceof ServerWorld serverWorld)) {
            return new ManaStoringItemData(uuid);
        }

        StateSaverAndLoader serverState = getServerState(serverWorld);

        // Either get the player by the uuid, or we don't have data for him yet, make a new player state
        ManaStoringItemData state = serverState.manaStoringItemData.computeIfAbsent(uuid, (uuid1 -> new ManaStoringItemData(uuid,stack)));

        return state;
    }

    public static void setManaStoringItemData(World world,UUID uuid, ManaStoringItemData data) {

        if(!(world instanceof ServerWorld serverWorld)) return;

        StateSaverAndLoader serverState = getServerState(serverWorld);

        serverState.manaStoringItemData.put(uuid,data);
    }
}