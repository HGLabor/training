package de.hglabor.plugins.training.challenges.mlg.streaks.data;

import de.hglabor.plugins.training.challenges.mlg.streaks.StreakPlayer;
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

public class StreakData implements Serializable {
    private static transient final long serialVersionUID = -1681012206529286330L;
    public final HashMap<UUID, StreakPlayer> streakPlayers;
    public StreakData(HashMap<UUID, StreakPlayer> streakPlayers) {
        this.streakPlayers = streakPlayers;
    }
    public StreakData(StreakData loadedStreakData) {
        this.streakPlayers = loadedStreakData.streakPlayers;
    }

    public boolean saveData(String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            GZIPOutputStream gzOut = new GZIPOutputStream(fileOut);
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(gzOut);
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static StreakData loadData(String filePath) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            StreakData streakData = (StreakData) in.readObject();
            in.close();
            return streakData;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
