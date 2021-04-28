package de.hglabor.plugins.training.challenges.mlg.streaks;

import de.hglabor.plugins.training.challenges.mlg.scoreboard.MlgPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StreakPlayer {
    private Map<String, Long> streaks;
    private Map<String, Long> highScores;

    public StreakPlayer() {
        this.streaks = new HashMap<>();
        this.highScores = new HashMap<>();
    }

    public static StreakPlayer get(MlgPlayer player) {
        return get(player.getPlayer());
    }

    public static StreakPlayer get(Player player) {
        return StreakPlayers.getStreakPlayer(player.getUniqueId());
    }

    public void increaseStreak(String mlgName) {
        long newStreak = getStreak(mlgName)+1;
        if (newStreak > getHighScore(mlgName)) {
            setHighScore(mlgName, newStreak);
        }
        streaks.put(mlgName, newStreak);
    }

    public void resetStreak(String mlgName) {
        streaks.put(mlgName, 0L);
    }

    public long getStreak(String mlgName) {
        return streaks.getOrDefault(mlgName, 0L);
    }

    public long getHighScore(String mlgName) {
        return highScores.getOrDefault(mlgName, 0L);
    }

    public void setHighScore(String mlgName, long highScore) {
        highScores.put(mlgName, highScore);
    }
}
