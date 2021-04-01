package de.hglabor.plugins.training.util;

import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class LocationUtils {
    public final static Location ZERO_DAMAGER = new Location(Bukkit.getWorld("world"), 0, 0, 0);
    public final static Location ZERO_MLG = new Location(Bukkit.getWorld("mlg"), 0, 0, 0);
    public final static Location DAMAGER_SPAWN = Bukkit.getWorld("world").getSpawnLocation();
    public final static Location MLG_SPAWN = Bukkit.getWorld("mlg").getSpawnLocation();
    private LocationUtils() {
    }

    public static void teleportToRespawnPoint(Player player) {
        User user = UserList.INSTANCE.getUser(player);

    }
}
