package org.oxytocina.geomancy.client.rendering.capes;

import java.util.*;

public class WorthinessChecker {

    private static final HashMap<UUID, Entry> PLAYER_MAP = new HashMap<>();

    public static CapeType getCapeType(UUID uuid) {
        return Optional.ofNullable(PLAYER_MAP.get(uuid)).map(entry -> entry.capeType).orElse(CapeType.NONE);
    }

    private static void putPlayer(UUID id, CapeType cape) {
        PLAYER_MAP.put(id, new Entry(id, cape));
    }

    public record Entry(UUID playerId, CapeType capeType) {
    }

    public static void init() {
    }

    static {
        // Devs
        putPlayer(Players.OXY, CapeType.DEV);
        putPlayer(Players.MAXI, CapeType.DEV);
    }
}
