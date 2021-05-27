package de.hglabor.plugins.training.challenges.mlg.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MlgPlayerList {
    private static final Map<UUID, MlgPlayer> players = new HashMap<>();
    public static MlgPlayer getMlgPlayer(UUID uuid) {
        return players.get(uuid);
    }
    public static void addMlgPlayer(UUID uuid, MlgPlayer mlgPlayer) {
        players.put(uuid, mlgPlayer);
    }
}
