package de.hglabor.plugins.training.challenges.mlg.streaks.data;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.mlg.streaks.StreakPlayers;

public class DataManager {
    private static final String filePath = Training.getInstance().getDataFolder() + "\\streaks.data";
    public static void enable() {
        Data loadedData = Data.loadData(filePath);
        if (loadedData == null) {
            return;
        }
        StreakPlayers.setStreakPlayers(new Data(loadedData).streakPlayers);
    }
    public static void disable() {
        new Data(StreakPlayers.getStreakPlayers()).saveData(filePath);
    }
}
