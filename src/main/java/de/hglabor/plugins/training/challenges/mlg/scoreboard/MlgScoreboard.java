package de.hglabor.plugins.training.challenges.mlg.scoreboard;

import de.hglabor.plugins.training.challenges.mlg.streaks.StreakPlayer;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardFactory;
import org.bukkit.Bukkit;

public class MlgScoreboard {
    private interface Names {
        String MLG_NAME = "mlgName";
        String STREAK = "streak";
        String HIGH_SCORE = "highscore";
    }

    public static void create(MlgPlayer player) {
        ScoreboardFactory.create(player);
        StreakPlayer streakPlayer = StreakPlayer.get(player);
        ScoreboardFactory.addEntry(player, Names.MLG_NAME, player.getMlgName(),  " MLG", 10);
        ScoreboardFactory.addEntry(player, Names.STREAK, "Streak: ", String.valueOf(streakPlayer.getStreak(player.getMlgName())), 9);
        ScoreboardFactory.addEntry(player, Names.HIGH_SCORE, "Streak High Score: ", String.valueOf(streakPlayer.getHighScore(player.getMlgName())), 8);
    }

    public static void update(MlgPlayer player) {
        if (player.getScoreboard() == null) {
            Bukkit.getLogger().warning("Scoreboard not yet created for player " + player.getName() + ". Creating new one.");
            create(player);
            return;
        }
        StreakPlayer streakPlayer = StreakPlayer.get(player);
        ScoreboardFactory.updateEntry(player, Names.MLG_NAME, player.getMlgName());
        ScoreboardFactory.updateEntry(player, Names.STREAK, "Streak: ", String.valueOf(streakPlayer.getStreak(player.getMlgName())));
        ScoreboardFactory.updateEntry(player, Names.HIGH_SCORE, "Streak High Score: ", String.valueOf(streakPlayer.getHighScore(player.getMlgName())));
    }
}
