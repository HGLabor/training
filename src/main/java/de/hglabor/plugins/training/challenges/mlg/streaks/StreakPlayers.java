package de.hglabor.plugins.training.challenges.mlg.streaks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StreakPlayers {
    private static final Map<UUID, StreakPlayer> streakPlayers = new HashMap<>();
    /** Add a player if not yet added */
    public static void addStreakPlayer(UUID uuid, StreakPlayer streakPlayer) {
        if (!streakPlayers.containsKey(uuid)) {
            streakPlayers.put(uuid, streakPlayer);
        }
    }
    public static StreakPlayer getStreakPlayer(UUID uuid) {
        return streakPlayers.get(uuid);
    }
}
