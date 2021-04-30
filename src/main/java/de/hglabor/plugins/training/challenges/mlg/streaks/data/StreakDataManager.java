package de.hglabor.plugins.training.challenges.mlg.streaks.data;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.mlg.streaks.StreakPlayers;
import org.bukkit.Bukkit;

public class StreakDataManager {
    private static final String filePath = Training.getInstance().getDataFolder() + "\\streaks.data";
    public static void enable() {
        StreakData loadedStreakData = StreakData.loadData(filePath);
        if (loadedStreakData == null) {
            return;
        }
        StreakPlayers.setStreakPlayers(new StreakData(loadedStreakData).streakPlayers);
    }
    public static void disable() {
        if (!new StreakData(StreakPlayers.getStreakPlayers()).saveData(filePath)) {
            Bukkit.getLogger().warning("Couldn't save streak data to file.");
        }
    }
}
