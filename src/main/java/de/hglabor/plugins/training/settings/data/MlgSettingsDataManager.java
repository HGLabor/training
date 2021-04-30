package de.hglabor.plugins.training.settings.data;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.settings.MlgSettings;
import org.bukkit.Bukkit;

public class MlgSettingsDataManager {
    private static final String filePath = Training.getInstance().getDataFolder() + "\\mlgSettings.data";
    public static void enable() {
        MlgSettingsData loadedMlgSettingsData = MlgSettingsData.loadData(filePath);
        if (loadedMlgSettingsData == null) {
            return;
        }
        MlgSettings.mlgSettingsMap = new MlgSettingsData(loadedMlgSettingsData).mlgSettingsMap;
    }
    public static void disable() {
        if (!new MlgSettingsData(MlgSettings.mlgSettingsMap).saveData(filePath)) {
            Bukkit.getLogger().warning("Couldn't save mlg settings data to file.");
        }
    }
}
