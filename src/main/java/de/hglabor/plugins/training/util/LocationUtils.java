package de.hglabor.plugins.training.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class LocationUtils {
    public final static Location ZERO_DAMAGER = new Location(Bukkit.getWorld("world"), 0, 0, 0);
    public final static Location ZERO_MLG = new Location(Bukkit.getWorld("mlg"), 0, 0, 0);
    public final static Location DAMAGER_SPAWN = Bukkit.getWorld("world").getSpawnLocation();
    public final static Location MLG_SPAWN = Bukkit.getWorld("mlg").getSpawnLocation();
    private LocationUtils() {
    }
}
