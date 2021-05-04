package de.hglabor.plugins.training.settings.data;

import de.hglabor.plugins.training.settings.MlgSettings;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MlgSettingsData implements Serializable {
    public final HashMap<UUID, MlgSettings> mlgSettingsMap;

    public MlgSettingsData(HashMap<UUID, MlgSettings> mlgSettingsMap) {
        this.mlgSettingsMap = mlgSettingsMap;
    }

    public MlgSettingsData(MlgSettingsData loadedData) {
        this.mlgSettingsMap = loadedData.mlgSettingsMap;
    }

    public boolean saveData(String filePath) {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static MlgSettingsData loadData(String filePath) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            MlgSettingsData settings = (MlgSettingsData) in.readObject();
            in.close();
            return settings;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
